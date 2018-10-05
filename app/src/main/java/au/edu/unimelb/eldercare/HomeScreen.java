package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import au.edu.unimelb.eldercare.event.EventsUI;
import au.edu.unimelb.eldercare.user.SettingsUI;
import au.edu.unimelb.eldercare.user.UserProfileUI;
import au.edu.unimelb.eldercare.user.UserSearchUI;

public class HomeScreen extends AppCompatActivity {

    private TextView mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the screen on open
        setContentView(R.layout.home_screen);

        mTextMessage = findViewById(R.id.message);
    }

    public void openUserProfileUI(View view){
        Intent intent = new Intent(HomeScreen.this, UserProfileUI.class);
    	startActivity(intent);
    }

    //When Settings button is clicked, this opens the settings page
    public void openSettings(View view){
        Intent intent = new Intent(HomeScreen.this, SettingsUI.class);
        startActivity(intent);
    }

    //When Frequent Contacts button is clicked, this opens the frequent contacts page
    public void openFrequentContacts(View view){
        Intent intent = new Intent(HomeScreen.this, FrequentContactsUI.class);
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

    public void openVoiceCall(View view) {
        Intent intent = new Intent(HomeScreen.this, VoiceCallActivity.class);
        startActivity(intent);
    }

}
