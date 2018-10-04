package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;
import java.util.List;

public class VoiceCallActivity extends AppCompatActivity {

    private static String TAG = "VoiceCallActivity";

    private FirebaseUser mFirebaseUser;
    private SinchClient sinchClient;
    private Call call;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_call);

        // Get the current Firebase user
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Set hardcoded recipient id for debugging
        final String recipientUserId = "b";

        // Instantiate a SinchClient using the SinchClientBuilder
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId("a") // TODO: Replace id with mFirebaseUser.getUid();
                .applicationKey("0a9ff560-c6ed-4d85-a4ab-1143c50eb1ae") // TODO: Don't commit keys
                .applicationSecret("BqjIJOCIhEynByd5ApgSoA==")
                .environmentHost("clientapi.sinch.com")
                .build();

        // Verify manifest in runtime during development
        // This can be removed when application is ready for release
        sinchClient.checkManifest(); // TODO: Delete this

        // Enable app-app calling, then start the client
        sinchClient.setSupportCalling(true);
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

        // Set button properties
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make a call or hang up depending on whether or not the call is null
                if (call == null) {
                    CallClient callClient = sinchClient.getCallClient();
                    call = callClient.callUser(recipientUserId);
                    call.addCallListener(new SinchCallListener());
                    button.setText("End Call");
                } else {
                    call.hangup();
                }
            }
        });
    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            // Call ended by either party. Reset button text and
            // make volume buttons go back to controlling ringer volume
            Log.d(TAG, "onCallEnded called");
            call = null;
            button.setText("Call");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            // Incoming call was picked up. Make volume buttons
            // control the volume of the phone call while connected
            Log.d(TAG, "onCallEstablished called");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

            // TODO: If progress tone is played, it should be stopped here
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            // Call is currently being made (i.e. ringing)
            Log.d(TAG, "onCallProgressing called");
            // TODO: Add progress tone here to indicate outgoing call is being made
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Don't worry about this right now
            Log.d(TAG, "onShouldSendPushNotification called");
        }
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            // Pick up the call
            Log.d(TAG, "onIncomingCall called");

            // TODO: Start playing ringtone

            call = incomingCall;
            call.answer();
            call.addCallListener(new SinchCallListener());

            // TODO: Stop playing ringtone

            button.setText("End Call");

            Intent intent = new Intent(VoiceCallActivity.this, MessagingActivity.class);
            startActivity(intent);

        }
    }
}