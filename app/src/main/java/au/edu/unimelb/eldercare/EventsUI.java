package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class EventsUI extends AppCompatActivity {

    private TextView mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the screen on open
        setContentView(R.layout.event_ui);

        mTextMessage = findViewById(R.id.message);
    }

    //When Events button is clicked, this opens the events page
    public void openAddEventsUI(View view){
        Intent intent = new Intent(EventsUI.this, AddEventActivity.class);
        startActivity(intent);
    }
}
