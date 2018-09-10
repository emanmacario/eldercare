package au.edu.unimelb.eldercare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddEventActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_ui);

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void submitNewEvent(View view){
        String eventName = ((EditText) findViewById(R.id.eventNameTextbox)).getText().toString();

        //int eventId = getEventId();//TODO: write getEventId
        int eventId = 5;
        //Long startingTime = getStartingTimeFromView(); //TODO: write getStartingTimeFromView
        Long startingTime = 1L;
        //float[] location = getLocationFromView(); //TODO: write getLocationFromView
        HashMap<String, Double> location = new HashMap<>();
        location.put("latitude", 2d);
        location.put("longitude", 2d);

        Event newEvent = new Event(eventName, startingTime, location);
        newEvent.eventId = eventId;
        newEvent.startingTime = startingTime;
        newEvent.location = location;
        newEvent.eventDescription = ((EditText) findViewById(R.id.eventDescriptionTextbox)).getText().toString();
        newEvent.maxUser = Integer.parseInt( ( (EditText) findViewById(R.id.maxUserTextbox) ).getText().toString() );

        newEvent.uploadEvent(mDatabase.child("events"));
    }
}
