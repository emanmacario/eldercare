package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.sql.Timestamp;

public class EditEventActivity extends AddEventActivity{

    protected Event event;

    protected TextView addEventText;
    protected EditText eventNameTextbox;
    protected EditText eventDescriptionTextbox;
    protected EditText maxUserTextbox;
    protected Button submitEventButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        event = intent.getParcelableExtra("event");
        Timestamp eventTime = new Timestamp(event.startingTime);

        addEventText = findViewById(R.id.addEventText);
        eventNameTextbox = findViewById(R.id.eventNameTextbox);
        eventDescriptionTextbox = findViewById(R.id.eventDescriptionTextbox);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);
        maxUserTextbox = findViewById(R.id.maxUserTextbox);
        submitEventButton = findViewById(R.id.submitEventButton);

        addEventText.setText("EDIT EVENT");
        eventNameTextbox.setText(event.eventName);
        eventDescriptionTextbox.setText(event.eventDescription);
        dateButton.setText((new SimpleDateFormat("yyyy-MM-dd")).format(eventTime));
        timeButton.setText((new SimpleDateFormat("hh:mm")).format(eventTime));
        //location.setText //TODO: fix location stuff
        maxUserTextbox.setText(String.valueOf(event.maxUser));
        submitEventButton.setText("Edit");

        confirmText = "Are you sure you want to edit this event?";
    }

    @Override
    public DatabaseReference getEventRef(){
        return mDatabase.child("events").child(event.eventId).getRef();
    }
}
