package au.edu.unimelb.eldercare;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ViewEventActivity extends EditEventActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addEventText.setText("EVENT");

        eventNameTextbox.setEnabled(false);
        eventDescriptionTextbox.setEnabled(false);
        dateButton.setEnabled(false);
        timeButton.setEnabled(false);
        ((Button)findViewById(R.id.openMapButton)).setVisibility(View.GONE);
        maxUserTextbox.setEnabled(false);
        submitEventButton.setVisibility(View.GONE);
    }
}
