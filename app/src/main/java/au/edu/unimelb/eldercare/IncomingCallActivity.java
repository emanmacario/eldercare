package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;
import com.sinch.android.rtc.calling.CallState;

import java.util.List;

import au.edu.unimelb.eldercare.service.UserService;
import au.edu.unimelb.eldercare.user.User;
import au.edu.unimelb.eldercare.usersearch.UserAccessor;

public class IncomingCallActivity extends AppCompatActivity implements UserAccessor {

    private static final String TAG = IncomingCallActivity.class.getSimpleName();

    private VoiceCallService mSinchService;
    private String mCallId;
    private String mRemoteUserId;

    private View.OnClickListener mClickListener;
    private TextView mRemoteUserDisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.answerButton:
                        answerClicked();
                        break;
                    case R.id.declineButton:
                        declineClicked();
                        break;
                    default:
                        break;
                }
            }
        };

        Button answer = (Button) findViewById(R.id.answerButton);
        Button decline = (Button) findViewById(R.id.declineButton);
        answer.setOnClickListener(mClickListener);
        decline.setOnClickListener(mClickListener);

        mSinchService = VoiceCallService.getInstance();
        mCallId = getIntent().getStringExtra("CALL_ID"); // TODO: refactor string into constant somewhere
        Call call = mSinchService.getCall(mCallId);
        call.addCallListener(new SinchCallListener());

        mRemoteUserDisplayName = (TextView) findViewById(R.id.remoteUser);
        mRemoteUserId = call.getRemoteUserId();
        UserService.getInstance().getSpecificUser(mRemoteUserId, this);
    }

    @Override
    public void userListLoaded(List<User> users) {
        // Not used
    }

    @Override
    public void userLoaded(User user) {
        mRemoteUserDisplayName.setText(user.getDisplayName());
    }

    private void answerClicked() {
        Call call = mSinchService.getCall(mCallId);
        if (call != null) {
            try {
                call.answer();
                Intent intent = new Intent(this, ActiveCallActivity.class);
                intent.putExtra("CALL_ID", mCallId);
                startActivity(intent);
                finish();
            } catch (MissingPermissionException e) {
                ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
            }
        } else {
            finish();
        }
    }

    private void declineClicked() {
        Call call = mSinchService.getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call call) {
            Log.d(TAG, "Call ended");
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You may now answer the call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }
}
