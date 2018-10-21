package au.edu.unimelb.eldercare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class FrequentContactsUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the screen on open
        setContentView(R.layout.frequent_contacts_ui);

        TextView mTextMessage = findViewById(R.id.message);
    }

}
