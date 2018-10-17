package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.unimelb.eldercare.helpers.TimeUtil;
import au.edu.unimelb.eldercare.messaging.Message;
import au.edu.unimelb.eldercare.messaging.MessageViewHolder;
import au.edu.unimelb.eldercare.messaging.ReceivedMessageViewHolder;
import au.edu.unimelb.eldercare.messaging.SentMessageViewHolder;
import au.edu.unimelb.eldercare.service.UserService;
import au.edu.unimelb.eldercare.user.User;
import au.edu.unimelb.eldercare.usersearch.UserAccessor;

public class MessagingActivity extends AppCompatActivity implements UserAccessor {

    // Static variables and constants
    private static final String TAG = "MessagingActivity";
    private static final String MESSAGES_CHILD = "messages";
    private static final String ANONYMOUS_NAME = "anonymous";
    private static final String LOADING_IMAGE_URL = "gs://comp30022colombia.appspot.com/spinningwheel.gif";
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int REQUEST_IMAGE = 3;

    // Firebase instance variables
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> mFirebaseAdapter;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    // User instance variables
    private String mUsername;
    private String mCurrentUserId;
    private String mChatUserId;
    private String mPhotoUrl;

    // UI instance variables
    private Button mSendButton;
    private ImageButton mAddImageButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText mMessageEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);



        // Initialise Firebase instance variables
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Check if user is authenticated
        if (mFirebaseUser == null) {
            // Not signed in, return to login activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Signed in, get user information
            if (mFirebaseUser.getDisplayName() != null) {
                mUsername = mFirebaseUser.getDisplayName();
            } else {
                mUsername = ANONYMOUS_NAME;
            }

            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        mCurrentUserId = mFirebaseUser.getUid();
        mChatUserId = getIntent().getStringExtra("targetUser");
        UserService.getInstance().getSpecificUser(mCurrentUserId, this);

        mDatabaseReference.child("chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(mChatUserId)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", 0);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("chat/" + mCurrentUserId + "/" + mChatUserId, chatAddMap);
                    chatUserMap.put("chat/" + mChatUserId + "/" + mCurrentUserId, chatAddMap);

                    mDatabaseReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d(TAG, databaseError.getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageRecyclerView.setHasFixedSize(false);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mSendButton = (Button) findViewById(R.id.button_chatbox_send);
        mMessageEditText = (EditText) findViewById(R.id.edittext_chatbox);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mAddImageButton = (ImageButton) findViewById(R.id.button_image_add);
        mAddImageButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        // Create a parser to parse new messages in the database into a Message object
        SnapshotParser<Message> parser = new SnapshotParser<Message>() {
            @NonNull
            @Override
            public Message parseSnapshot(@NonNull DataSnapshot snapshot) {
                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    message.setId(snapshot.getKey());
                }
                return message;
            }
        };


        DatabaseReference messagesReference = mDatabaseReference.child("messages").child(mCurrentUserId).child(mChatUserId);

        // Configure the Firebase Recycler Adapter
        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(messagesReference, parser) //mDatabaseReference.child(MESSAGES_CHILD)
                        .build();

        // Create the Firebase Recycler Adapter
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {
            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                Log.d(TAG, "onCreateViewHolder called");
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());

                // Create a new instance of a view holder. In this case, we
                // will use custom layouts for each sent or received message item
                if (viewType == VIEW_TYPE_MESSAGE_SENT) {
                    View view = inflater.inflate(R.layout.item_message_sent, parent, false);
                    return new SentMessageViewHolder(view);
                } else {
                    View view = inflater.inflate(R.layout.item_message_received, parent, false);
                    return new ReceivedMessageViewHolder(view);
                }
            }

            @Override
            public void onBindViewHolder(@NonNull MessageViewHolder holder, int position,
                                         @NonNull Message message) {
                Log.d(TAG, "onBindViewHolder called");
                holder.bind(message);
            }

            @Override
            public void onDataChanged() {
                Log.d(TAG, "Child added to 'messages'");
            }

            @Override
            public int getItemViewType(int position) {

                String senderId = getItem(position).getSenderId();
                String mFirebaseUserId = mFirebaseUser.getUid();

                if (senderId.equals(mFirebaseUserId)) {
                    return VIEW_TYPE_MESSAGE_SENT;
                }
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        };


        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int messageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                // If the recycler view is initially being loaded or the user is
                // at the bottom of the list, scroll to the bottom of the list
                // to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (messageCount - 1)
                        && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user's input text message and current time in UNIX time
                String text = mMessageEditText.getText().toString();
                long time = TimeUtil.getCurrentTime();

                // Create the message and push it to the database
                Message message = new Message(mFirebaseUser.getUid(), mUsername, text, null, mPhotoUrl, time);
                //mDatabaseReference.child(MESSAGES_CHILD).push().setValue(message);


                String currentUserReference = "messages/" + mCurrentUserId + "/" + mChatUserId;
                String chatUserReference = "messages/" + mChatUserId + "/" + mCurrentUserId;

                DatabaseReference userMessagePush = mDatabaseReference.child("messages")
                        .child(mCurrentUserId).child(mChatUserId).push();

                String pushId = userMessagePush.getKey();

                Map messageUserMap = new HashMap();
                messageUserMap.put(currentUserReference + "/" + pushId, message);
                messageUserMap.put(chatUserReference + "/" + pushId, message);

                mDatabaseReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.d(TAG, databaseError.getMessage());
                        }
                    }
                });

                // Clear the input field
                mMessageEditText.setText("");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Message tempMessage =
                            new Message(mFirebaseUser.getUid(), mUsername, null, LOADING_IMAGE_URL,
                                    mPhotoUrl, TimeUtil.getCurrentTime());

                    // mDatabaseReference.child(MESSAGES_CHILD).push()
                    mDatabaseReference.child("messages").child(mCurrentUserId).child(mChatUserId).push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError,
                                                       @NonNull DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        .getReference(mFirebaseUser.getUid())
                                                        .child(key)
                                                        .child(uri.getLastPathSegment());
                                        storeImage(storageReference, uri, key);
                                    } else {
                                        Log.w(TAG, "Unable to write temporary message to database",
                                                databaseError.toException());
                                    }
                                }
                            });
                }
            }
        }
    }

    @Override
    public void userListLoaded(List<User> users) {
        // Not used
    }

    @Override
    public void userLoaded(User user) {
        mPhotoUrl = user.getDisplayPhoto();
    }

    private void storeImage(final StorageReference storageReference, Uri uri, final String key) {
        // Create new task to asynchronously upload from content URI to this storage reference
        UploadTask uploadTask = storageReference.putFile(uri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Could not get download URL");
                    if (task.getException() != null) {
                        throw task.getException();
                    }
                }
                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Message message =
                            new Message(mFirebaseUser.getUid(), mFirebaseUser.getDisplayName(),
                                    null, downloadUri.toString(), mPhotoUrl,
                                    TimeUtil.getCurrentTime());
                    //mDatabaseReference.child(MESSAGES_CHILD).child(key).setValue(message);
                    mDatabaseReference.child("messages").child(mCurrentUserId).child(mChatUserId)
                            .child(key).setValue(message);
                    mDatabaseReference.child("messages").child(mChatUserId).child(mCurrentUserId)
                            .child(key).setValue(message);
                } else {
                    Log.w(TAG, "Image upload task was not successful", task.getException());
                }
            }
        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "Started listening");
        mFirebaseAdapter.startListening();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped listening");
        mFirebaseAdapter.stopListening();
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "Stopped listening");
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "Started listening");
        mFirebaseAdapter.startListening();
        super.onResume();
    }
}