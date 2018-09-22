package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventsUI extends AppCompatActivity {

    private DatabaseReference eventDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the screen on open
        setContentView(R.layout.event_ui);
    }

    public void openAddEventsUI(View view){
        Intent intent = new Intent(EventsUI.this, AddEventActivity.class);
        startActivity(intent);
    }


    public void openEditEventsUI(View view){
        Intent intent = new Intent(EventsUI.this, ViewOwnEventActivity.class);
        startActivity(intent);
    }

    public void openViewEventsUI(View view){
        Intent intent = new Intent(EventsUI.this, ViewEventsActivity.class);
        startActivity(intent);
    }

}
