package au.edu.unimelb.eldercare;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.HashMap;

public class AddEventActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private int currentYear;
    private int currentMonth;
    private int currentDay;
    private int currentHour;
    private Button dateButton;
    private Button timeButton;
    private String eventDate;
    private String eventTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_ui);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentHour = calendar.get(Calendar.HOUR);

        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);
        String currentDate = String.format("%d-%d-%d", currentYear, currentMonth + 1, currentDay);
        String currentTime = String.format("%02d:00", currentHour);
        dateButton.setText(currentDate);
        timeButton.setText(currentTime);

        //eventDate and eventTime must be in yyyy-mm-dd hh:mm:ss
        eventDate = currentDate;
        eventTime = currentTime + ":00";
    }

    public void submitNewEvent(View view){
        String eventName = ((EditText) findViewById(R.id.eventNameTextbox)).getText().toString();
        DatabaseReference newEventRef = mDatabase.child("events").push();

        Log.e("AddEventActivity", eventDate + " " + eventTime);
        Timestamp startingTime = Timestamp.valueOf(eventDate + " " + eventTime);
        //HashMap<String, Double> location = getLocationFromView(); //TODO: write getLocationFromView
        HashMap<String, Double> location = new HashMap<>();
        location.put("latitude", 2d);
        location.put("longitude", 3d);

        Event newEvent = new Event(eventName, startingTime, location);
        newEvent.eventId = newEventRef.getKey();
        newEvent.startingTime = startingTime;
        newEvent.location = location;
        newEvent.eventDescription = ((EditText) findViewById(R.id.eventDescriptionTextbox)).getText().toString();
        newEvent.maxUser = Integer.parseInt( ( (EditText) findViewById(R.id.maxUserTextbox) ).getText().toString() );

        newEventRef.setValue(newEvent);
    }

    public void onClickDate(View view){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String newDate = String.format("%d-%d-%d", year, month + 1, dayOfMonth);
                dateButton.setText(newDate);
                eventDate = newDate;
            }
        }, currentYear, currentMonth, currentDay);
        datePickerDialog.show();
    }

    public void onClickTime(View view){
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                String newTime = String.format("%02d:%02d", hour, minute);
                timeButton.setText(newTime);
                eventTime = newTime + ":00";
            }
        }, currentHour, 0, true);
        timePickerDialog.show();
    }

}
