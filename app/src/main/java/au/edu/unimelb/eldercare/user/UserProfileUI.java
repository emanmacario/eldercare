package au.edu.unimelb.eldercare.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import au.edu.unimelb.eldercare.R;
import au.edu.unimelb.eldercare.usersearch.UserAccessor;
import au.edu.unimelb.eldercare.usersearch.UserService;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class UserProfileUI extends AppCompatActivity implements UserAccessor {

    private TextView DisplayName;
    private TextView userBio;

    // Firebase References
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the screen on open
        setContentView(R.layout.user_profile_ui);

        DisplayName = findViewById(R.id.UserProfileHeading);
        userBio = findViewById(R.id.UserBio);

        String profileUserId = getIntent().getStringExtra("targetUser");
        UserService.getInstance().getSpecificUser(profileUserId, this);
    }

    public void openAboutActivity(View view){
        Intent intent = new Intent(UserProfileUI.this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void userListLoaded(List<User> users) {
        // Not used
    }

    @Override
    public void userLoaded(User value) {
        String userName = value.getDisplayName();
        DisplayName.setText(userName);
        //User Bio
        String userBioString = value.getUserBio();
        userBio.setText(userBioString);
    }
}
