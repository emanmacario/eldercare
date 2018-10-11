package au.edu.unimelb.eldercare;

import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class ActiveCallActivity extends AppCompatActivity {

    private static final String TAG = ActiveCallActivity.class.getSimpleName();

    private String mCallId;
    private VoiceCallService mSinchService;

    private TextView mCallState;
    private TextView mCallDuration;
    private TextView mCallerName;
    private Button mEndCallButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_call);

        mCallState = (TextView) findViewById(R.id.callState);
        mCallDuration = (TextView) findViewById(R.id.callDuration);
        mCallerName = (TextView) findViewById(R.id.remoteUser);
        mEndCallButton = (Button) findViewById(R.id.hangupButton);

        mEndCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endCall();
            }
        });

        // Get the id of the current active call
        mSinchService = VoiceCallService.getInstance();
        mCallId = getIntent().getStringExtra("CALL_ID");
        Call call = mSinchService.getCall(mCallId);

        if (call != null) {
            call.addCallListener(new SinchCallListener());
            mCallerName.setText("Other caller here");
            mCallState.setText(call.getState().toString());
        } else {
            Log.e(TAG, "Started with invalid call, aborting");
            finish();
        }
    }

    private void endCall() {
        Call call = mSinchService.getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call endedCall) {
            // Call ended by either party. Reset button text and
            // make volume buttons go back to controlling ringer volume
            Log.d(TAG, "onCallEnded called");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            // Incoming call was picked up. Make volume buttons
            // control the volume of the phone call while connected
            Log.d(TAG, "onCallEstablished called");
            mCallState.setText("CONNECTED");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

            // TODO: If progress tone is played, it should be stopped here
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            // Call is currently being made (i.e. ringing)
            Log.d(TAG, "onCallProgressing called");
            mCallState.setText("RINGING ...");

            // TODO: Add progress tone here to indicate outgoing call is being made
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Don't worry about this right now
            Log.d(TAG, "onShouldSendPushNotification called");
        }
    }
}
