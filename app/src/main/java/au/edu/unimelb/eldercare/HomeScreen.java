package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import au.edu.unimelb.eldercare.event.EventsUI;
import au.edu.unimelb.eldercare.service.AuthenticationService;
import au.edu.unimelb.eldercare.service.TraceLocationService;
import au.edu.unimelb.eldercare.user.SettingsUI;
import au.edu.unimelb.eldercare.user.UserProfileUI;
import au.edu.unimelb.eldercare.user.UserSearchUI;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        // Create the Sinch Client for the current authenticated user
        VoiceCallService sinchService = VoiceCallService.getInstance();
        sinchService.buildSinchClient(this);
    }

    public void openUserProfileUI(View view) {
        String userId = AuthenticationService.getAuthenticationService().getUser().getUid();
        Intent intent = new Intent(HomeScreen.this, UserProfileUI.class);
        intent.putExtra("targetUser", userId);
    	startActivity(intent);
    }

    public void openSettings(View view){
        Intent intent = new Intent(HomeScreen.this, SettingsUI.class);
        startActivity(intent);
    }

    //When Frequent Contacts button is clicked, this opens the frequent contacts page
    public void openFrequentContacts(View view){
        //Intent intent = new Intent(HomeScreen.this, FrequentContactsUI.class);
        Intent intent = new Intent(HomeScreen.this, VoiceCallActivity.class);
        startActivity(intent);
    }

    //When Events button is clicked, this opens the events page
    public void openEvents(View view) {
        Intent intent = new Intent(HomeScreen.this, EventsUI.class);
        startActivity(intent);
    }

    public void openMessaging(View view) {
        Intent intent = new Intent(HomeScreen.this, MessagingActivity.class);
        startActivity(intent);
    }

    public void openUserSearch(View view) {
        Intent intent = new Intent(HomeScreen.this, UserSearchUI.class);
        startActivity(intent);
    }

    public void openMap(View view){
        Intent intent = new Intent(HomeScreen.this, MapActivity.class);
        startActivity(intent);
    }

    public void openVoiceCall(View view){
        Intent intent = new Intent(HomeScreen.this, VoiceCallActivity.class);
        startActivity(intent);
    }

}
