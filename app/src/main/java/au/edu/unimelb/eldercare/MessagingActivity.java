package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.content.SharedPreferences;
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


public class MessagingActivity extends AppCompatActivity {

    // Static variables and constants
    private static final String TAG = "MessagingActivity";

    // Firebase instance variables
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> mFirebaseAdapter;
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


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;

        public MessageViewHolder(View view) {
            super(view);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        // Initialise Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // Set reference to the Firebase database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Check if user is authenticated
        if (mFirebaseUser == null) {
            // Not signed in, return to login activity
            Intent intent = new Intent(this, HomeScreen.class); // TODO: Replace HomeScreen activity with SignInActivity
            startActivity(intent);
            finish();
        } else {
            // Signed in, get user information
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

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
                        .setQuery(mDatabaseReference.child("messages"), parser) // TODO: Make static constants for magic strings
                        .build();

        // Create the Firebase Recycler Adapter
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message model) {
                // Bind the Message object to the MessageViewHolder

            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the MessageViewHolder. In this case, we
                // will use a custom layout called R.layout.message for each item
                // TODO: Finish implementing how MessageViewHolder will be inflated
                return null;
            }
        };
    }


    /**
     * https://stackoverflow.com/questions/50467814/tasksnapshot-getdownloadurl-is-deprecated
     * @param storageReference reference to the Firebase real-time database
     * @param uri the URI storing the image to upload
     * @param key the key
     */
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

    // Set event listener to monitor changes to the Firebase query
    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAdapter.stopListening();
    }
}