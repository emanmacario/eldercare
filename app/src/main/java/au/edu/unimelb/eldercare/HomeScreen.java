package au.edu.unimelb.eldercare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    public void openUserProfileUI(){
        
    }

}