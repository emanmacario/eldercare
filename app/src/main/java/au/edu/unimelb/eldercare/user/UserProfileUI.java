package au.edu.unimelb.eldercare.user;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import au.edu.unimelb.eldercare.R;

public class UserProfileUI extends AppCompatActivity {

    private TextView mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the screen on open
        setContentView(R.layout.user_profile_ui);

        mTextMessage = findViewById(R.id.message);
    }

}