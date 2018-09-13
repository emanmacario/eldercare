package au.edu.unimelb.eldercare;

import com.bumptech.glide.Glide;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.Calendar;
import java.util.TimeZone;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import android.text.format.DateUtils;
import android.text.format.DateFormat;
import java.util.Date;

public class MessagingActivity extends AppCompatActivity {

    // Static variables and constants
    private static final String TAG = "MessagingActivity";
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    // Firebase instance variables
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<Message, SentMessageViewHolder> mFirebaseAdapter;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    // Instance variables
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;


    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;
        TextView nameText;

        public SentMessageViewHolder(View view) {
            super(view);
            this.messageText = (TextView) view.findViewById(R.id.text_message_body);
            this.timeText = (TextView) view.findViewById(R.id.text_message_time);
            this.nameText = (TextView) view.findViewById(R.id.text_message_name);
        }

        public void bind(Message message) {
            this.messageText.setText(message.getText());
            this.nameText.setText(message.getSenderDisplayName());
            this.timeText.setText(createTimeString(message.getTime()));
        }
    }

    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;
        TextView nameText;
        ImageView profileImage;

        public ReceivedMessageViewHolder(View view) {
            super(view);
            this.messageText = (TextView) view.findViewById(R.id.text_message_body);
            this.timeText = (TextView) view.findViewById(R.id.text_message_time);
            this.nameText = (TextView) view.findViewById(R.id.text_message_name);
            this.profileImage = (ImageView) view.findViewById(R.id.image_message_profile);
        }

        public void bind(Message message) {
            this.messageText.setText(message.getText());
            this.nameText.setText(message.getSenderDisplayName());
            this.timeText.setText(createTimeString(message.getTime()));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        // Initialise Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // Check if user is authenticated
        if (mFirebaseUser == null) {
            /*
            // Not signed in, return to login activity
            Intent intent = new Intent(this, HomeScreen.class); // TODO: Replace HomeScreen activity with SignInActivity
            startActivity(intent);
            finish();
            */
            mUsername = "anonymous";
            mPhotoUrl = null;
        } else {
            // Signed in, get user information
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageRecyclerView.setHasFixedSize(false);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        //mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageEditText = (EditText) findViewById(R.id.edittext_chatbox);
        mSendButton = (Button) findViewById(R.id.button_chatbox_send);

        // Set reference to the Firebase database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

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

        // TODO: Make static constants for magic strings
        DatabaseReference messagesReference = mDatabaseReference.child("messages");

        // Configure the Firebase Recycler Adapter
        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(messagesReference, parser)
                        // .setLifecycleOwner(this)
                        .build();

        // Create the Firebase Recycler Adapter
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, SentMessageViewHolder>(options) {
            @NonNull
            @Override
            public SentMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                Log.w(TAG, "onCreateViewHolder called");

                // Create a new instance of the MessageViewHolder. In this case, we
                // will use a custom layout called R.layout.message for each item
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.item_message_received, parent, false);
                return new SentMessageViewHolder(view);

                /*
                if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
                    View view = inflater.inflate(R.layout.item_message_received, parent, false);
                    return new ReceivedMessageViewHolder(view);
                } else {
                    View view = inflater.inflate(R.layout.item_message_received, parent, false);
                    return new SentMessageViewHolder(view);
                }*/

            }

            @Override
            protected void onBindViewHolder(@NonNull SentMessageViewHolder holder, int position,
                                            @NonNull Message message) {

                Log.w(TAG, "onBindViewHolder called");

                // Hide the progress bar
                //mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                // Bind the Message object to the MessageViewHolder
                holder.bind(message);
                holder.messageText.setVisibility(TextView.VISIBLE);
                holder.timeText.setVisibility(TextView.VISIBLE);
                holder.nameText.setVisibility(TextView.VISIBLE);
                /*
                if (getItemViewType(message) == VIEW_TYPE_MESSAGE_SENT) {
                    SentMessageViewHolder sentHolder = (SentMessageViewHolder) holder;
                    sentHolder.bind(message);
                } else {
                    ReceivedMessageViewHolder receivedHolder = (ReceivedMessageViewHolder) holder;
                    receivedHolder.bind(message);
                }
                */
            }

            @Override
            public void onDataChanged() {
                Log.w(TAG, "ADDED MESSAGE TO DATABASE");
            }

            /*
            private int getItemViewType(Message message) {
                if (message.getSenderId().equals(mFirebaseUser.getUid())) {
                    return VIEW_TYPE_MESSAGE_SENT;
                }
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
            */
        };

        Log.w(TAG, "SIZE OF SNAPSHOTS ARRAY: " + mFirebaseAdapter.getItemCount());

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        assert(mFirebaseAdapter != null);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);



        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user's input text message
                String text = mMessageEditText.getText().toString();

                // Get current UNIX time
                long time = System.currentTimeMillis() / 1000L;

                // Create the message and push it to the database
                Message message = new Message("1234", mUsername, text, "fakeimageurl", "fakephotourl", time);
                mDatabaseReference.child("messages").push().setValue(message);

                // Clear the input field
                mMessageEditText.setText("");
            }
        });
    }


    /*
     * https://stackoverflow.com/questions/50467814/tasksnapshot-getdownloadurl-is-deprecated
     * @param storageReference reference to the Firebase real-time database
     * @param uri the URI storing the image to upload
     * @param key the key
     */

    /*
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
                    Message message =
                            new Message(null, mUsername, mPhotoUrl, downloadUri.toString());
                    mDatabaseReference.child("messages").child(key).setValue(message);
                } else {
                    Log.w(TAG, "Image upload task was not successful", task.getException());
                }
            }
        });
    }
    */

    // Set event listener to monitor changes to the Firebase query
    @Override
    protected void onStart() {
        Log.w(TAG, "STARTED LISTENING");
        mFirebaseAdapter.startListening();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.w(TAG, "STOPPED LISTENING");
        mFirebaseAdapter.stopListening();
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.w(TAG, "STOPPED LISTENING");
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.w(TAG, "STARTED LISTENING");
        mFirebaseAdapter.startListening();
        super.onResume();
    }

    private static String createTimeString(long time) {
        String timeString;
        time *= 1000L;
        Date date = new Date(time);

        if (DateUtils.isToday(time)) {
            timeString = "Today\n" + DateFormat.format("h:mm a", date).toString();
        } else {
            timeString = DateFormat.format("dd/MM/yy\nhh:mm a", date).toString();
        }
        return timeString;
    }
}