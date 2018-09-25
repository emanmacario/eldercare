package au.edu.unimelb.eldercare;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

public class ViewOwnEventActivity extends ViewEventsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.childEventListener = new ownEventsListener();
        super.onCreate(savedInstanceState);
        ((TextView)findViewById(R.id.allEventText)).setText("List of created event");
    }

    public class ownEventsListener extends eventsListener{
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            super.onChildAdded(dataSnapshot, s);
            viewButton.setText("Edit");
        }

        @Override
        protected Boolean filter(Event newEvent){
            return newEvent.creator.equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        public class EditButtonClickListener extends ButtonClickListener{
            @Override
            public void onClick(View view) {
                super.activity = EditEventActivity.class;
                super.onClick(view);
            }
        }

        @Override
        public ButtonClickListener getOnClickListener(){
            return new EditButtonClickListener();
        }
    }
}
