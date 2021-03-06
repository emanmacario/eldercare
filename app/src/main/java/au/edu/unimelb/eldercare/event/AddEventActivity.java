package au.edu.unimelb.eldercare.event;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import au.edu.unimelb.eldercare.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Provides UI services for creating new events
 */
public class AddEventActivity extends AppCompatActivity {

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private int currentYear;
    private int currentMonth;
    private int currentDay;
    private int currentHour;
    EditText eventNameTextbox;
    EditText eventDescriptionTextbox;
    EditText maxUserTextbox;
    Button dateButton;
    Button timeButton;
    private String eventDate;
    private String eventTime;
    private final int PLACE_PICKER_REQUEST = 1;
    private String locationName;
    LatLng location;
    LatLngBounds openLocation = null;
    TextView locationText;
    String confirmText = "Are you sure you want to submit this event?";
    String alertTitleText = "Confirm Submit";
    DatabaseReference eventRef = mDatabase.child("events").push();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.add_event_ui);


        eventNameTextbox = findViewById(R.id.eventNameTextbox);
        eventDescriptionTextbox = findViewById(R.id.eventDescriptionTextbox);
        maxUserTextbox = findViewById(R.id.maxUserTextbox);

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

        locationText = findViewById(R.id.selectedLocation);

        //eventDate and eventTime must be in yyyy-mm-dd hh:mm:ss
        eventDate = currentDate;
        eventTime = currentTime + ":00";
    }

    /**
     * Gets the values entered in fields, creates an event and adds it to the database
     * @param view
     */
    void submitNewEvent(View view) {
        String eventName = eventNameTextbox.getText().toString();

        Long startingTime = Timestamp.valueOf(eventDate + " " + eventTime).getTime();

        HashMap<String, Double> eventLocation = new HashMap<>();
        eventLocation.put("latitude", location.latitude);
        eventLocation.put("longitude", location.longitude);

        Event newEvent = new Event(eventName, startingTime, eventLocation);

        newEvent.eventId = eventRef.getKey();
        newEvent.startingTime = startingTime;
        newEvent.eventDescription = eventDescriptionTextbox.getText().toString();
        newEvent.maxUser = Integer.parseInt(maxUserTextbox.getText().toString());
        newEvent.creator = FirebaseAuth.getInstance().getCurrentUser().getUid();
        newEvent.locationName = locationName;

        eventRef.setValue(newEvent);

        finish();
    }

    /**
     * Opens the datePickerDialog and allows the user to change the date of the event
     * @param view
     */
    public void onClickDate(View view) {
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

    /**
     * Opens the timePickerDialog and allows the user to change the time of the event
     * @param view
     */
    public void onClickTime(View view) {
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

    /**
     * Checks if the event is filled out correctly, checks for confirmation and also
     * allows the user to add the event to their phone's calendar
     * @param view
     */
    public void onClickSubmit(View view) {
        //Checks that the event is filled correctly
        if (someFieldMissing()) {
            Toast toast = Toast.makeText(this, "Some Field is Empty", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        //Sets up the confirmation alert
        final View currentView = view;
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(alertTitleText).setMessage(confirmText);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                submitNewEvent(currentView);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    /**
     * Builds a place picker to allow the user to set the location of the event
     * via the GoogleMaps API
     * @param view
     * @throws GooglePlayServicesNotAvailableException
     * @throws GooglePlayServicesRepairableException
     */
    public void onClickLocation(View view) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        if (openLocation != null) {
            builder.setLatLngBounds(openLocation);
        }
        Intent placePickerIntent = builder.build(this);
        placePickerIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(placePickerIntent, PLACE_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                locationName = place.getName().toString();
                locationText.setText(locationName);
                location = place.getLatLng();
            }
        }
    }

    /**
     * Checks all fields to see that they are filled
     * @return
     */
    private boolean someFieldMissing() {
        boolean isMissing = false;
        if (isEmptyField(eventNameTextbox)) {
            isMissing = true;
        }
        if (isEmptyField(eventDescriptionTextbox)) {
            isMissing = true;
        }
        if (location == null) {
            isMissing = true;
        }
        if (isEmptyField(maxUserTextbox)) {
            isMissing = true;
        }
        return isMissing;
    }

    private boolean isEmptyField(EditText editText) {
        return editText.getText().toString().equals("");
    }
}
