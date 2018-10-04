package au.edu.unimelb.eldercare.user;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import au.edu.unimelb.eldercare.R;

public class ChangeConnectedUserActivity extends AppCompatActivity{

    //On Screen Texts
    TextView currentConnectedUser;
    EditText newConnectedUser;
    //Firebase Variables
    FirebaseUser user;
    DatabaseReference mDatabaseCurrUser;
    DatabaseReference mDatabaseUsers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets correct layout file
        setContentView(R.layout.change_connected_user_activity);

        currentConnectedUser = findViewById(R.id.CurrentConnectedUser);
        newConnectedUser = findViewById(R.id.NewConnectedUser);

        this.user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseCurrUser = FirebaseDatabase.getInstance().getReference().child("users").child(this.user.getUid());
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");

        //Set Current Connected User Text
        mDatabaseCurrUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Gets the user object that is the current user
                User user = dataSnapshot.getValue(User.class);
                //Grabs the value of connected user from the database and sets it to the object
                user.setConnectedUserID(dataSnapshot.child("ConnectedUser").getValue(String.class));
                //Uses this to set the value of the TextView
                String ConnectedUserID = user.getConnectedUserID();
                currentConnectedUser.setText(ConnectedUserID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateConnectedUser(View view){
        final String newConnectedUserEmail = newConnectedUser.getText().toString();

        //Queries the database for the user with the entered email address
        mDatabaseUsers.orderByChild("email").equalTo(newConnectedUserEmail).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Gets the user object associated with the email
                User user = dataSnapshot.getValue(User.class);
                //Grabs the display name and sets that as the connected user in the current users database reference
                String ConnectedUserName = user.getDisplayName();
                mDatabaseCurrUser.child("ConnectedUser").setValue(ConnectedUserName);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
