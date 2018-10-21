package au.edu.unimelb.eldercare.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import au.edu.unimelb.eldercare.R;
import au.edu.unimelb.eldercare.service.UserAccessor;
import au.edu.unimelb.eldercare.service.UserService;

import static au.edu.unimelb.eldercare.helpers.EmailValidator.isEmailValid;

public class ChangeConnectedUserActivity extends AppCompatActivity implements UserAccessor {

    //On Screen Texts
    private TextView currentConnectedUser;
    private EditText newConnectedUser;
    //Firebase Variables
    private FirebaseUser user;
    private DatabaseReference mDatabaseCurrUser;
    private DatabaseReference mDatabaseUsers;
    //Current User type
    private String currentUserType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets correct layout file
        setContentView(R.layout.change_connected_user_activity);

        //on screen texts
        currentConnectedUser = findViewById(R.id.CurrentConnectedUser);
        newConnectedUser = findViewById(R.id.NewConnectedUser);

        //Gets the current user
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserService.getInstance().getSpecificUser(userID, this);

        //Gets Database Reference
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseCurrUser = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
    }

    public void updateConnectedUser(View view) {
        //Get the text entered in the EditText
        String newConnectedUserEmail = newConnectedUser.getText().toString();

        //Checks that the email entered is a valid email
        if (!isEmailValid(newConnectedUserEmail)) {
            Toast toast = Toast.makeText(ChangeConnectedUserActivity.this, R.string.invalid_email, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        //Queries the database for the user with the entered email address
        mDatabaseUsers.orderByChild("email").equalTo(newConnectedUserEmail).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //get connected user type
                String connectedUserType = dataSnapshot.child("userType").getValue(String.class);
                //check that it's different to the current user's type
                if (connectedUserType.equals(currentUserType)) {
                    Toast toast = Toast.makeText(ChangeConnectedUserActivity.this, R.string.same_user_types, Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                //Grabs the Uid and sets that as the connected user in the current users database reference
                String ConnectedUserUid = dataSnapshot.getKey();
                mDatabaseCurrUser.child("ConnectedUser").setValue(ConnectedUserUid);

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

    @Override
    public void userListLoaded(List<User> users) {
        //not used
    }

    @Override
    public void userLoaded(User value) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        String ConnectedUserID = value.getConnectedUser();
        mDatabase.child(ConnectedUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User ConnectedUser = dataSnapshot.getValue(User.class);
                String ConnectedUserName = ConnectedUser.getDisplayName();
                currentConnectedUser.setText(ConnectedUserName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
