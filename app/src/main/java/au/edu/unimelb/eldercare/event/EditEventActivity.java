package au.edu.unimelb.eldercare.event;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.sql.Timestamp;

import au.edu.unimelb.eldercare.R;

public class EditEventActivity extends AddEventActivity{

    protected Event event;

    protected TextView addEventText;
    protected Button submitEventButton;

    protected ChildEventListener childEventListener = new registeredUserListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        event = intent.getParcelableExtra("event");
        Timestamp eventTime = new Timestamp(event.startingTime);

        addEventText = findViewById(R.id.addEventText);
        submitEventButton = findViewById(R.id.submitEventButton);

        addEventText.setText("EDIT EVENT");
        eventNameTextbox.setText(event.eventName);
        eventDescriptionTextbox.setText(event.eventDescription);
        dateButton.setText((new SimpleDateFormat("yyyy-MM-dd")).format(eventTime));
        timeButton.setText((new SimpleDateFormat("hh:mm")).format(eventTime));
        locationText.setText(event.locationName);
        maxUserTextbox.setText(String.valueOf(event.maxUser));
        submitEventButton.setText("Edit");

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(event.location.get("latitude"), event.location.get("longitude")));

        openLocation = builder.build();

        alertTitleText = "Confirm Edit";
        confirmText = "Are you sure you want to edit this event?";

        eventRef = mDatabase.child("events").child(event.eventId).getRef();

        eventRef.child("registeredUserId").addChildEventListener(childEventListener);
    }

    public class registeredUserListener implements ChildEventListener {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String registerState = dataSnapshot.getValue(String.class);
            String userId = dataSnapshot.getKey();

            event.registerUser(userId, registerState);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            onChildAdded(dataSnapshot, s);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String userId = dataSnapshot.getKey();

            event.unregisterUser(userId);
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e(this.getClass().getSimpleName(), "fail to get list of registered user");
            finish();
        }
    }
}
