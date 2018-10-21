package au.edu.unimelb.eldercare.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import au.edu.unimelb.eldercare.R;

/**
 * Provides UI services for event management
 */
public class EventsUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the screen on open
        setContentView(R.layout.event_ui);
    }

    public void openAddEventsUI(View view) {
        Intent intent = new Intent(EventsUI.this, AddEventActivity.class);
        startActivity(intent);
    }


    public void openEditEventsUI(View view) {
        Intent intent = new Intent(EventsUI.this, ViewOwnEventActivity.class);
        startActivity(intent);
    }

    public void openViewEventsUI(View view) {
        Intent intent = new Intent(EventsUI.this, ViewEventsActivity.class);
        startActivity(intent);
    }

}
