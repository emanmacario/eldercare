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
import com.sinch.android.rtc.calling.CallState;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import au.edu.unimelb.eldercare.helpers.TimeUtil;

public class ActiveCallActivity extends AppCompatActivity {

    private static final String TAG = ActiveCallActivity.class.getSimpleName();

    private String mCallId;
    private Timer mTimer;
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

        mSinchService = VoiceCallService.getInstance();
        mCallId = getIntent().getStringExtra("CALL_ID");
        Call call = mSinchService.getCall(mCallId);

        if (call != null) {
            call.addCallListener(new SinchCallListener());
            mCallerName.setText(call.getRemoteUserId());
            mCallState.setText(call.getState().toString());
        } else {
            Log.e(TAG, "Started with invalid call, aborting");
            finish();
        }

        mTimer = new Timer();
    }

    private void endCall() {
        Call call = mSinchService.getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        mTimer.cancel();
        finish();
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call endedCall) {
            // Call ended by either party. Reset button text and
            // make volume buttons go back to controlling ringer volume
            Log.d(TAG, "onCallEnded called");
            mCallState.setText("ENDED");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            mTimer.cancel();
            finish();
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            // Incoming call was picked up. Make volume buttons
            // control the volume of the phone call while connected
            Log.d(TAG, "onCallEstablished called");
            mCallState.setText("CONNECTED");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

            // Set timer to update call duration on a separate thread
            mTimer.schedule(new UpdateCallDurationTask(), 0, 600);

            // TODO: If progress tone is played, it should be stopped here
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            // Call is currently being made (i.e. ringing)
            Log.d(TAG, "onCallProgressing called");
            mCallState.setText("CALLING");

            // TODO: Add progress tone here to indicate outgoing call is being made
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Don't worry about this right now
            Log.d(TAG, "onShouldSendPushNotification called");
        }
    }

    private class UpdateCallDurationTask extends TimerTask {
        @Override
        public void run() {
            ActiveCallActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    private void updateCallDuration() {
        Call call = mSinchService.getCall(mCallId);
        if (call != null) {
            String timeDurationString = TimeUtil.formatTimeDuration(call.getDetails().getDuration());
            mCallDuration.setText(timeDurationString);
        }
    }
}
