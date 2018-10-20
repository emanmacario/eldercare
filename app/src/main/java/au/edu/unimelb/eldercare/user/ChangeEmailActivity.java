package au.edu.unimelb.eldercare.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import au.edu.unimelb.eldercare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import static au.edu.unimelb.eldercare.helpers.EmailValidifier.isEmailValid;

public class ChangeEmailActivity extends AppCompatActivity {

    private TextView currentEmailAddress;
    private EditText newEmailAddress;
    private FirebaseUser user;
    private DatabaseReference mDatabase;

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

    /**
     * Updates a users email address on the database
     * @param view
     */
    public void updateEmailAddress(View view){
        //Get the Text entered in the EditText
        String newEmail = newEmailAddress.getText().toString();

        //Validate Email Address
        if(!isEmailValid(newEmail)){
            Toast toast = Toast.makeText(ChangeEmailActivity.this, "@string/invalid_email", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        //If valid email, change on database
        mDatabase.child("email").setValue(newEmail);
    }
}
