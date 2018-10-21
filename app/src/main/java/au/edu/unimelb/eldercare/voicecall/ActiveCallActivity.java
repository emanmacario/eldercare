package au.edu.unimelb.eldercare.voicecall;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import au.edu.unimelb.eldercare.R;
import au.edu.unimelb.eldercare.helpers.TimeUtil;
import au.edu.unimelb.eldercare.service.UserService;
import au.edu.unimelb.eldercare.service.VoiceCallService;
import au.edu.unimelb.eldercare.user.User;
import au.edu.unimelb.eldercare.service.UserAccessor;
import com.bumptech.glide.Glide;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ActiveCallActivity extends AppCompatActivity implements UserAccessor {

    private static final String TAG = ActiveCallActivity.class.getSimpleName();

    private String mCallId;
    private Timer mTimer;
    private VoiceCallService mSinchService;

    private TextView mCallState;
    private TextView mCallDuration;
    private TextView mCallerName;
    private CircleImageView mCallerDisplayPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_call);

        mCallState = findViewById(R.id.callState);
        mCallDuration = findViewById(R.id.callDuration);
        mCallerName = findViewById(R.id.remoteUser);
        mCallerDisplayPhoto = findViewById(R.id.displayPicture);
        ImageButton mEndCallButton = findViewById(R.id.hangupButton);

        mCallDuration.setVisibility(TextView.GONE);
        mEndCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endCall();
            }
        });

        mSinchService = VoiceCallService.getInstance();
        mCallId = getIntent().getStringExtra(VoiceCallService.CALL_ID);
        Call call = mSinchService.getCall(mCallId);

        if (call != null) {
            call.addCallListener(new SinchCallListener());
            String remoteUserId = call.getRemoteUserId();
            UserService.getInstance().getSpecificUser(remoteUserId, this);
            mCallState.setText(call.getState().toString());
            mTimer = new Timer();
        } else {
            Log.e(TAG, "Started with invalid call, aborting");
            finish();
        }
    }

    @Override
    public void userListLoaded(List<User> users) {
        // Not used
    }

    @Override
    public void userLoaded(User user) {
        mCallerName.setText(user.getDisplayName());

        String displayPhotoUrl = user.getDisplayPhoto();
        if (displayPhotoUrl != null) {
            Glide.with(getApplicationContext())
                    .load(displayPhotoUrl)
                    .into(mCallerDisplayPhoto);
        }
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
            endCall();
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            Log.d(TAG, "onCallEstablished called");

            // Incoming call was picked up. Make volume buttons
            // control the volume of the phone call while connected
            mCallState.setText("CONNECTED");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

            // Set timer to update call duration on a separate thread
            mCallDuration.setVisibility(TextView.VISIBLE);
            mTimer.schedule(new UpdateCallDurationTask(), 0, 600);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            Log.d(TAG, "onCallProgressing called");

            // Call is currently being made (i.e. ringing)
            mCallState.setText("CALLING");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
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
