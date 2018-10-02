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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import au.edu.unimelb.eldercare.R;

public class ChangeConnectedUserActivity extends AppCompatActivity{

    TextView currentConnectedUser;
    EditText newConnectedUser;
    FirebaseUser user;
    DatabaseReference mDatabaseCurrUser;
    DatabaseReference mDatabaseUsers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets correct layout file
        setContentView(R.layout.change_connected_user_activity);

        currentConnectedUser = findViewById(R.id.ConnectedUser);
        newConnectedUser = findViewById(R.id.NewConnectedUser);

        this.user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseCurrUser = FirebaseDatabase.getInstance().getReference().child("users").child(this.user.getUid());
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");

        //Set Current Connected User Text
        mDatabaseCurrUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Assigns the current connected user display name to the TextView
                User user = dataSnapshot.getValue(User.class);
                String ConnectedUserID = user.getConnectedUserID();
                currentConnectedUser.setText(ConnectedUserID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateConnectedUser(View view){
        String newConnectedUserEmail = newConnectedUser.getText().toString();

        //Get the user based on entered email
        mDatabaseUsers.orderByChild("email").equalTo(newConnectedUserEmail);
    }
}
