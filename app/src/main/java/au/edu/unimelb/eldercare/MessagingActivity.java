package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import au.edu.unimelb.eldercare.helpers.TimeUtil;
import au.edu.unimelb.eldercare.messaging.Message;
import au.edu.unimelb.eldercare.messaging.MessageViewHolder;
import au.edu.unimelb.eldercare.messaging.ReceivedMessageViewHolder;
import au.edu.unimelb.eldercare.messaging.SentMessageViewHolder;

public class MessagingActivity extends AppCompatActivity {

    // Static variables and constants
    private static final String TAG = "MessagingActivity";
    private static final String MESSAGES_CHILD = "messages";
    private static final String ANONYMOUS_NAME = "anonymous";
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    // Firebase instance variables
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> mFirebaseAdapter;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    // User instance variables
    private String mUsername;
    private String mPhotoUrl;

    // UI instance variables
    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;


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
            } else {
                mPhotoUrl = null;
            }
        }

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

        // Configure the Firebase Recycler Adapter
        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(mDatabaseReference.child(MESSAGES_CHILD), parser)
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
                mDatabaseReference.child(MESSAGES_CHILD).push().setValue(message);

                // Clear the input field
                mMessageEditText.setText("");
            }
        });
    }

    // TODO: Complete this method in next iteration to send images
    private void storeImage(final StorageReference storageReference, Uri uri, final String key) {

        // Create new task to aynchronously upload from content URI to this storage reference
        UploadTask uploadTask = storageReference.putFile(uri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Could not get download URL");
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Message message = new Message(); // TODO: Complete proper message object instantiation
                    mDatabaseReference.child(MESSAGES_CHILD).child(key).setValue(message);
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