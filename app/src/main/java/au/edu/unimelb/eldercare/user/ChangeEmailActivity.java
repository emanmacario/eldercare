package au.edu.unimelb.eldercare.user;

import android.os.Bundle;
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
import au.edu.unimelb.eldercare.user.User;

public class ChangeEmailActivity extends AppCompatActivity {

    TextView currentEmailAddress;
    EditText newEmailAddress;
    FirebaseUser user;
    DatabaseReference mDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets correct layout file
        setContentView(R.layout.change_email_activity);

        currentEmailAddress = findViewById(R.id.CurrentEmail);
        newEmailAddress = findViewById(R.id.NewEmail);

        //Get User and Database reference
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(this.user.getUid());

        //Set Current Email Address
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Assigns the current users email address to the TextView
                User user = dataSnapshot.getValue(User.class);
                String Email = user.getEmail();
                currentEmailAddress.setText(Email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateEmailAddress(View view){
        //Still have to make sure that the string in the EditText is an Email
        String newEmail = newEmailAddress.getText().toString();
        mDatabase.child("email").setValue(newEmail);
    }
}
