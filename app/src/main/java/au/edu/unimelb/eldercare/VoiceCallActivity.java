package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.speech.tts.Voice;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sinch.android.rtc.MissingPermissionException;
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

    private Button callButton;
    private EditText recipientIdEditText;
    private String recipientUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_call);

        // Set button properties
        callButton = (Button) findViewById(R.id.button);
        callButton.setEnabled(false);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                VoiceCallService sinchService = VoiceCallService.getInstance();

                // Try to make the call
                try {
                    Call call = sinchService.callUser(recipientUserId);
                    if (call == null) {
                        // Service failed for some reason, show a Toast message and abort
                        Toast.makeText(getApplicationContext(), "Unable to make a call",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    String callId = call.getCallId();
                    Intent intent = new Intent(getApplicationContext(), ActiveCallActivity.class);
                    intent.putExtra("CALL_ID", callId);
                    startActivity(intent);
                } catch (MissingPermissionException e) {
                    ActivityCompat.requestPermissions(VoiceCallActivity.this, new String[]{e.getRequiredPermission()}, 0);
                }
            }
        });

        recipientIdEditText = (EditText) findViewById(R.id.edittext_recipientid);
        recipientIdEditText.addTextChangedListener(new TextWatcher() {
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You may now place a call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }
}