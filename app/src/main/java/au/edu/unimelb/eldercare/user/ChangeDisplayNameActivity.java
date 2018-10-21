package au.edu.unimelb.eldercare.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import au.edu.unimelb.eldercare.R;
import au.edu.unimelb.eldercare.service.UserAccessor;
import au.edu.unimelb.eldercare.service.UserService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.List;

public class ChangeDisplayNameActivity extends AppCompatActivity implements UserAccessor{

    private TextView currentDisplayName;
    private EditText newDisplayName;
    private FirebaseUser user;
    private DatabaseReference mDatabase;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets correct layout file
        setContentView(R.layout.change_d_name_activity);

        currentDisplayName = findViewById(R.id.DisplayName);
        newDisplayName = findViewById(R.id.NewDisplayName);

        //Gets the current User
        String mUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserService.getInstance().getSpecificUser(mUser, this);

        //Get User and Database reference
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(this.user.getUid());
    }

    /**
     * Updates the display name on the database
     *
     * @param view
     */
    public void updateDisplayName(View view) {
        String newDName = newDisplayName.getText().toString();
        mDatabase.child("displayName").setValue(newDName);
    }

    @Override
    public void userListLoaded(List<User> users) {
        //not used
    }

    @Override
    public void userLoaded(User value) {
        String mUserName = value.getDisplayName();
        currentDisplayName.setText(mUserName);
    }
}
