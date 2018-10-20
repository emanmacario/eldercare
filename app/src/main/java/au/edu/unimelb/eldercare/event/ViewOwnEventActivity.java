package au.edu.unimelb.eldercare.event;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import au.edu.unimelb.eldercare.R;

/**
 * Provides UI services for viewing events you own
 */
public class ViewOwnEventActivity extends ViewEventsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.childEventListener = new OwnEventsListener();
        super.onCreate(savedInstanceState);
        ((TextView) findViewById(R.id.allEventText)).setText(R.string.eventCreatedList);
    }

    public class OwnEventsListener extends EventsListener {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            super.onChildAdded(dataSnapshot, s);
            viewButton.setText(R.string.eventEditButton);
        }

        @Override
        protected Boolean filter(Event newEvent) {
            return newEvent.creator.equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        public class EditButtonClickListener extends ButtonClickListener {
            @Override
            public void onClick(View view) {
                super.activity = EditEventActivity.class;
                super.onClick(view);
            }
        }

        @Override
        public ButtonClickListener getOnClickListener() {
            return new EditButtonClickListener();
        }
    }
}
