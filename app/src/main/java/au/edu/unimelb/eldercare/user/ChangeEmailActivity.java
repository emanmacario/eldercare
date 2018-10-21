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
import au.edu.unimelb.eldercare.service.UserAccessor;
import au.edu.unimelb.eldercare.service.UserService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.List;

import static au.edu.unimelb.eldercare.helpers.EmailValidator.isEmailValid;

public class ChangeEmailActivity extends AppCompatActivity implements UserAccessor{

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

        //Gets current User
        String mUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserService.getInstance().getSpecificUser(mUser, this);

        //Get User and Database reference
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(this.user.getUid());
    }

    /**
     * Updates a users email address on the database
     *
     * @param view
     */
    public void updateEmailAddress(View view) {
        //Get the Text entered in the EditText
        String newEmail = newEmailAddress.getText().toString();

        //Validate Email Address
        if (!isEmailValid(newEmail)) {
            Toast toast = Toast.makeText(ChangeEmailActivity.this, "@string/invalid_email", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        //If valid email, change on database
        mDatabase.child("email").setValue(newEmail);
    }

    @Override
    public void userListLoaded(List<User> users) {
        //not used
    }

    @Override
    public void userLoaded(User value) {
        String mEmail = value.getEmail();
        currentEmailAddress.setText(mEmail);
    }
}
