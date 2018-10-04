package au.edu.unimelb.eldercare.event;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import au.edu.unimelb.eldercare.R;

public class ViewEventActivity extends EditEventActivity {

    private String userId;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addEventText.setText("EVENT");

        eventNameTextbox.setEnabled(false);
        eventDescriptionTextbox.setEnabled(false);
        dateButton.setEnabled(false);
        timeButton.setEnabled(false);
        findViewById(R.id.openMapButton).setVisibility(View.GONE);
        maxUserTextbox.setEnabled(false);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = mDatabase.child("users").child(userId);

        alterActivityByUserJoinState();

        //TODO: remove this after implementing map
        findViewById(R.id.openMapButton).setVisibility(View.GONE);
    }

    @Override
    protected void submitNewEvent(View view){
        if(isRegistered()){
            unregister();
        }else{
            register();
        }
        alterActivityByUserJoinState();
    }

    private boolean isRegistered(){
        return event.registeredUserId.get(userId) != null? true: false;
    }

    private void alterActivityByUserJoinState(){
        if(isRegistered()){
            submitEventButton.setText("Unregister");
            alertTitleText = "Confirm Unregister";
            confirmText = "Are you sure you want to unregister this event?";
        }else{
            submitEventButton.setText("Join");
            alertTitleText = "Confirm Join";
            confirmText = "Are you sure you want to join this event?";
        }
    }

    private void register(){
        String state = "register";
        eventRef.child("registeredUserId").child(userId).setValue(state);
        userRef.child("registeredEventId").child(event.eventId).setValue(state);

        event.registerUser(userId, state);
    }

    private void unregister(){
        eventRef.child("registeredUserId").child(userId).removeValue();
        userRef.child("registeredEventId").child(event.eventId).removeValue();

        event.unregisterUser(userId);
    }
}
