package au.edu.unimelb.eldercare.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import au.edu.unimelb.eldercare.R;

public class ViewEventsActivity extends AppCompatActivity {
    protected HashMap<String, Event> allEvents;
    protected LayoutInflater inflater;
    protected ViewGroup container;
    protected DatabaseReference eventDB = FirebaseDatabase.getInstance().getReference().child("events");
    protected ChildEventListener childEventListener = new eventsListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_event_ui);

        allEvents = new HashMap<>();
        eventDB.addChildEventListener(childEventListener);

        container = findViewById(R.id.eventBoxContainer);
        inflater = this.getLayoutInflater();

    }

    public class eventsListener implements ChildEventListener{

        protected Button viewButton;

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Event newEvent = dataSnapshot.getValue(Event.class);
            allEvents.put(dataSnapshot.getKey(), newEvent);

            View newBox = inflater.inflate(R.layout.event_box,
                                          container,
                                          false);
            container.addView(newBox);
            if (!filter(newEvent)) {
                newBox.setVisibility(View.GONE);
            }

            newBox.setTag(dataSnapshot.getKey());

            TextView eventNameText = newBox.findViewById(R.id.eventNameText);
            eventNameText.setText(newEvent.eventName);

            viewButton = newBox.findViewById(R.id.ViewEventButton);
            ButtonClickListener onClickListener = getOnClickListener();
            onClickListener.setEvent(newEvent);
            viewButton.setOnClickListener(onClickListener);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Event changedEvent = dataSnapshot.getValue(Event.class);
            allEvents.put(dataSnapshot.getKey(), changedEvent);

            View changedBox = container.findViewWithTag(dataSnapshot.getKey());
            TextView eventNameText = changedBox.findViewById(R.id.eventNameText);
            eventNameText.setText(changedEvent.eventName);

            viewButton = changedBox.findViewById(R.id.ViewEventButton);
            ButtonClickListener onClickListener = getOnClickListener();
            onClickListener.setEvent(changedEvent);
            viewButton.setOnClickListener(onClickListener);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            allEvents.remove(dataSnapshot.getKey());
            View removedBox = container.findViewWithTag(dataSnapshot.getKey());
            removedBox.setVisibility(View.GONE);
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e(this.getClass().getSimpleName(), "fail to get list of events");
            finish();
        }

        protected Boolean filter(Event newEvent){
            return true;
        }

        public class ButtonClickListener implements View.OnClickListener {

            protected Event event;
            protected Class activity = ViewEventActivity.class;

            public void setEvent(Event event){
                this.event = event;
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewEventsActivity.this, activity);
                intent.putExtra("event", this.event);
                startActivity(intent);
            }
        }

        public ButtonClickListener getOnClickListener(){
            return new ButtonClickListener();
        }
    }
}