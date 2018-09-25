package au.edu.unimelb.eldercare.event;

import android.os.Bundle;
import android.view.View;

import au.edu.unimelb.eldercare.R;

public class ViewEventActivity extends EditEventActivity {

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
        submitEventButton.setVisibility(View.GONE);
    }
}
