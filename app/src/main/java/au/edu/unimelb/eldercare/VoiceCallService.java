package au.edu.unimelb.eldercare;

import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;

import au.edu.unimelb.eldercare.service.AuthenticationService;
import au.edu.unimelb.eldercare.user.User;


/**
 * Provides voice call services for ElderCare
 */
public class VoiceCallService {

    private static final String TAG = "VoiceCallService";
    private static final String APP_KEY = "0a9ff560-c6ed-4d85-a4ab-1143c50eb1ae";
    private static final String APP_SECRET = "BqjIJOCIhEynByd5ApgSoA==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    private Context context;
    private SinchClient sinchClient;
    private String displayName;

    private static VoiceCallService instance;

    /**
     * Get (and initialise, if required) the singleton Voice Call Service
     *
     * @return The singleton Voice Call Service
     */
    public static VoiceCallService getInstance() {
        if (instance == null) {
            instance = new VoiceCallService();
        }
        return instance;
    }

    /**
     * Builds a Sinch Client for a given authenticated user
     * @param context the context from which to build the Sinch Client
     */
    public void buildSinchClient(Context context) {
        this.context = context;

        // Check if Sinch Client is already built
        if (sinchClient != null) {
            return;
        }

        // Get id of current authenticated user
        FirebaseUser currentUser = AuthenticationService.getAuthenticationService().getUser();
        String userId = currentUser.getUid();

        // Instantiate a SinchClient using the SinchClientBuilder
        sinchClient = Sinch.getSinchClientBuilder()
                .context(context) // TODO: Make sure this works with parameterised context
                .userId(userId)
                .applicationKey(APP_KEY) // TODO: Don't commit keys
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();

        // Verify manifest in runtime during development
        // This can be removed when application is ready for release
        sinchClient.checkManifest(); // TODO: Delete this

        // Enable app-app calling, then start the client
        sinchClient.setSupportCalling(true);
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();

        // Add a call client listener to handle incoming calls
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
    }

    public SinchClient getSinchClient() {
        if (sinchClient == null) {
            return null;
        }
        return sinchClient;
    }

    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            Log.d(TAG, "Incoming call");

            Intent intent = new Intent(context, IncomingCallActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("CALL_ID", call.getCallId());

            context.startActivity(intent);
        }
    }

    public Call callUser(String userId) {
        if (sinchClient == null) {
            return null;
        }
        return sinchClient.getCallClient().callUser(userId);
    }

    public String getDisplayName(String userId) {

        DatabaseReference databaseReference
                = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                displayName = dataSnapshot.child("displayName").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return displayName;
    }

    public Call getCall(String callId) {
        return sinchClient.getCallClient().getCall(callId);
    }
}
