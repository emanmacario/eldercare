package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private Button callButton;
    private Button acceptButton;
    private Button declineButton;
    private TextView callState;
    private EditText recipientIdEditText;
    private String recipientUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_call);

        // Get the current Firebase user
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Set the call state text view
        callState = (TextView) findViewById(R.id.call_state);

        // Instantiate a SinchClient using the SinchClientBuilder
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(mFirebaseUser.getUid()) // TODO: Replace id with mFirebaseUser.getUid();
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
        callButton = (Button) findViewById(R.id.button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make a call or hang up depending on whether or not the call is null
                if (call == null) {
                    CallClient callClient = sinchClient.getCallClient();
                    call = callClient.callUser(recipientUserId);
                    call.addCallListener(new SinchCallListener());
                    callButton.setText("End Call");
                } else {
                    call.hangup();
                }
            }
        });

        // Set the accept and decline call buttons
        acceptButton = (Button) findViewById(R.id.button_accept);
        declineButton = (Button) findViewById(R.id.button_decline);

        acceptButton.setVisibility(Button.GONE);
        declineButton.setVisibility(Button.GONE);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (call == null) {
                    Log.d(TAG, "ERROR, ACCEPTING NULL CALL");
                    return;
                }
                call.answer();
                call.addCallListener(new SinchCallListener());
                callButton.setText("End Call");
                //acceptButton.setVisibility(Button.GONE);
                //declineButton.setVisibility(Button.GONE);
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (call != null) {
                    call.hangup();
                    acceptButton.setVisibility(Button.GONE);
                    declineButton.setVisibility(Button.GONE);
                }
            }
        });

        recipientIdEditText = (EditText) findViewById(R.id.edittext_recipientid);
        recipientIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Set the new recipient user id
                recipientUserId = recipientIdEditText.getText().toString();

                // Only enable call button if a string is entered
                if (charSequence.toString().trim().length() > 0) {
                    callButton.setEnabled(true);
                } else {
                    callButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
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
            callButton.setText("Call");
            callState.setText("");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            // Incoming call was picked up. Make volume buttons
            // control the volume of the phone call while connected
            Log.d(TAG, "onCallEstablished called");
            callState.setText("CONNECTED");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

            // TODO: If progress tone is played, it should be stopped here
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            // Call is currently being made (i.e. ringing)
            Log.d(TAG, "onCallProgressing called");
            callState.setText("RINGING ...");

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
            Log.d(TAG, "onIncomingCall called");

            // TODO: Start playing ringtone

            call = incomingCall;
            acceptButton.setVisibility(Button.VISIBLE);
            declineButton.setVisibility(Button.VISIBLE);


            // TODO: Stop playing ringtone
        }
    }
}