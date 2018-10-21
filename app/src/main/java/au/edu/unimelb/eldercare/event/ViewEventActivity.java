package au.edu.unimelb.eldercare.event;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;

import au.edu.unimelb.eldercare.HomeActivity;
import au.edu.unimelb.eldercare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

/**
 * Provides UI services for viewing an event
 */
public class ViewEventActivity extends EditEventActivity {

    private String userId;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addEventText.setText(R.string.eventButton);

        eventNameTextbox.setEnabled(false);
        eventDescriptionTextbox.setEnabled(false);
        dateButton.setEnabled(false);
        timeButton.setEnabled(false);
        maxUserTextbox.setEnabled(false);

        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userRef = mDatabase.child("users").child(userId);

        alterActivityByUserJoinState();

    }

    @Override
    protected void submitNewEvent(View view) {
        if (isRegistered()) {
            unregister();
        } else {
            register();
            askAddEventToCalendar(view);
        }
        alterActivityByUserJoinState();
    }

    private boolean isRegistered() {
        return event.registeredUserId.get(userId) != null;
    }

    /**
     * Shows the location of the event using the GoogleMaps API
     * @param view
     */
    @Override
    public void onClickLocation(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("location", location);
        intent.putExtra("locationName", event.locationName);
        startActivity(intent);
    }

    /**
     * Based on whether the user is joining or leaving event, show appropriate alert
     */
    private void alterActivityByUserJoinState() {
        if (isRegistered()) {
            submitEventButton.setText(R.string.eventUnregister);
            alertTitleText = "Confirm Unregister";
            confirmText = "Are you sure you want to unregister this event?";
        } else {
            submitEventButton.setText(R.string.eventJoin);
            alertTitleText = "Confirm Join";
            confirmText = "Are you sure you want to join this event?";
        }
    }

    private void register() {
        String state = "register";
        eventRef.child("registeredUserId").child(userId).setValue(state);
        userRef.child("registeredEventId").child(event.eventId).setValue(state);

        event.registerUser(userId, state);
    }

    private void unregister() {
        eventRef.child("registeredUserId").child(userId).removeValue();
        userRef.child("registeredEventId").child(event.eventId).removeValue();

        event.unregisterUser(userId);
    }

    /**
     * Prompt the user to add the event to their personal calendar. Will allow for notifications.
     * @param view
     */
    private void askAddEventToCalendar(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Add event to calendar").setMessage("Do you want to add this event to your calendar?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                addEventToCalendar();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    /**
     * Opens the calendar application on the phone, pre-filling the fields to add the event
     */
    private void addEventToCalendar() {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.startingTime)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.startingTime)
                .putExtra(CalendarContract.Events.TITLE, event.eventName)
                .putExtra(CalendarContract.Events.DESCRIPTION, event.eventDescription)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, event.locationName);
        startActivity(intent);
    }

}
