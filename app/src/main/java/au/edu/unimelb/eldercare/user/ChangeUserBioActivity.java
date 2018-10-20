package au.edu.unimelb.eldercare.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import au.edu.unimelb.eldercare.R;

public class ChangeUserBioActivity extends AppCompatActivity {

    private TextView editBioHeading;
    private EditText newUserBio;
    private Button confirmButton;

    //Firebase references
    private FirebaseUser user;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set correct layout
        setContentView(R.layout.change_user_bio_activity);

        editBioHeading = findViewById(R.id.EditUserBioHeading);
        newUserBio = findViewById(R.id.EditBioEditText);
        confirmButton = findViewById(R.id.EditUserBioButton);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(this.user.getUid());
    }


    /**
     * Updates the Users Bio on the database
     *
     * @param view
     */
    public void updateUserBio(View view) {
        //get the text entered in the EditText
        String newUserBioString = newUserBio.getText().toString();

        //Check that bio is less than 200 characters
        int userBioLength = newUserBioString.length();
        if (userBioLength > 200) {
            Toast toast = Toast.makeText(ChangeUserBioActivity.this, "@string/bio_too_long", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        //If length is <=200, set on database
        mDatabase.child("userBio").setValue(newUserBioString);

        //Send user to profile to see the new bio
        Intent intent = new Intent(ChangeUserBioActivity.this, UserProfileUI.class);
        startActivity(intent);
    }
}
