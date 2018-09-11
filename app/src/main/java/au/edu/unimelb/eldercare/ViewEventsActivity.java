package au.edu.unimelb.eldercare;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ViewEventsActivity extends AppCompatActivity {
    private HashMap<String, Event> allEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the screen on open
        setContentView(R.layout.event_ui);

        DatabaseReference eventDB = FirebaseDatabase.getInstance().getReference().child("events");
        eventDB.addChildEventListener(eventsListListener);

    }

    ChildEventListener eventsListListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Event newEvent = dataSnapshot.getValue(Event.class);
            allEvents.put(dataSnapshot.getKey(), newEvent);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Event changedEvent = dataSnapshot.getValue(Event.class);
            allEvents.put(dataSnapshot.getKey(), changedEvent);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            allEvents.remove(dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("EventsUI", "fail to get list of events");
        }
    };
}
