package au.edu.unimelb.eldercare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;

public class VoiceCall extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_call);

        // Instantiate a SinchClient using the SinchClientBuilder.
        android.content.Context context = this.getApplicationContext();
        SinchClient sinchClient = Sinch.getSinchClientBuilder().context(context)
                .applicationKey("0a9ff560-c6ed-4d85-a4ab-1143c50eb1ae")
                .applicationSecret("BqjIJOCIhEynByd5ApgSoA==")
                .environmentHost("clientapi.sinch.com")
                .userId("")
                .build();


    }
}
