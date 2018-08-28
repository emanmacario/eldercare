package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class HomeScreen extends AppCompatActivity {

    private TextView mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the screen on open
        setContentView(R.layout.home_screen);

        mTextMessage = findViewById(R.id.message);
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

}