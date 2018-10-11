package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallState;

public class IncomingCallActivity extends AppCompatActivity {

    private static final String TAG = IncomingCallActivity.class.getSimpleName();

    private VoiceCallService mSinchService;
    private String mCallId;

    private View.OnClickListener mClickListener;
    private TextView mRemoteUser;

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
        answer.setOnClickListener(mClickListener);
        Button decline = (Button) findViewById(R.id.declineButton);
        decline.setOnClickListener(mClickListener);

        mSinchService = VoiceCallService.getInstance();
        mCallId = getIntent().getStringExtra("CALL_ID"); // TODO: refactor string into constant somewhere
        Call call = mSinchService.getCall(mCallId);
        mRemoteUser = (TextView) findViewById(R.id.remoteUser);
        mRemoteUser.setText(call.getRemoteUserId());
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
        } else if (call.getState().equals(CallState.ENDED)) {
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You may now answer the call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }
}
