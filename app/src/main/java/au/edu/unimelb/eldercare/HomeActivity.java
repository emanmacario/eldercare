package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import au.edu.unimelb.eldercare.event.EventsUI;
import au.edu.unimelb.eldercare.service.AuthenticationService;
import au.edu.unimelb.eldercare.user.SettingsUI;
import au.edu.unimelb.eldercare.user.UserProfileUI;
import au.edu.unimelb.eldercare.user.UserSearchUI;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Create the Sinch Client for the current authenticated user
        VoiceCallService sinchService = VoiceCallService.getInstance();
        sinchService.buildSinchClient(this);
    }

    public void openFriends(View view) {
        // TODO: Complete this and open friends list intent
    }

    public void openConnectedUser(View view) {
        // TODO: Complete this and open connected user intent
    }

    public void openUserProfileUI(View view) {
        String userId = AuthenticationService.getAuthenticationService().getUser().getUid();
        Intent intent = new Intent(HomeActivity.this, UserProfileUI.class);
        intent.putExtra("targetUser", userId);
        startActivity(intent);
    }

    public void openSettings(View view){
        Intent intent = new Intent(HomeActivity.this, SettingsUI.class);
        startActivity(intent);
    }

    public void openEvents(View view) {
        Intent intent = new Intent(HomeActivity.this, EventsUI.class);
        startActivity(intent);
    }

    public void openUserSearch(View view) {
        Intent intent = new Intent(HomeActivity.this, UserSearchUI.class);
        startActivity(intent);
    }

}
