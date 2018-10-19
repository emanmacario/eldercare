package au.edu.unimelb.eldercare;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import au.edu.unimelb.eldercare.event.EventsUI;
import au.edu.unimelb.eldercare.service.AuthenticationListener;
import au.edu.unimelb.eldercare.service.AuthenticationService;
import au.edu.unimelb.eldercare.user.SelectUserTypeActivity;
import au.edu.unimelb.eldercare.user.SettingsUI;
import au.edu.unimelb.eldercare.user.User;
import au.edu.unimelb.eldercare.user.UserProfileUI;
import au.edu.unimelb.eldercare.user.UserSearchUI;

import static au.edu.unimelb.eldercare.service.AuthenticationService.RC_SIGN_IN;

public class HomeActivity extends AppCompatActivity implements AuthenticationListener {

    // Firebase variables
    private FirebaseUser user;
    private DatabaseReference mDatabase;

    // Google Maps API variables
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = AuthenticationService.getAuthenticationService().getUser();
        if (user == null) {
            AuthenticationService.getAuthenticationService().startAuthentication(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            AuthenticationService.getAuthenticationService().handleAuthenticationRequestCallback(resultCode, data, this);
        }
    }

    @Override
    public void userAuthenticated(final FirebaseUser user) {
        this.user = user;
        //Note, have to check if the user already exists so that their data doesn't get overridden
        //every time they login
        DatabaseReference userRef = mDatabase.child("users").child(this.user.getUid());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    //Once a user is authenticated and they don't already exist,
                    //create a new user on the database
                    writeNewUser(user.getUid(), user.getDisplayName(), user.getEmail());
                    //Also need to select user type so go to this activity
                    Intent intent = new Intent(HomeActivity.this, SelectUserTypeActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        userRef.addListenerForSingleValueEvent(eventListener);

        // Create the Sinch Client for the current authenticated user
        VoiceCallService sinchService = VoiceCallService.getInstance();
        sinchService.buildSinchClient(this);
    }

    @Override
    public void authenticationFailed(IdpResponse response) {
        AuthenticationService.getAuthenticationService().startAuthentication(this);
    }


    /**
     * Function creates a new User and creates the user on the realtime database
     * @param userId
     * @param name
     * @param email
     */
    private void writeNewUser(String userId, String name, String email){
        User user = new User(name, email, "", "");
        mDatabase.child("users").child(userId).setValue(user);
    }

    // Google maps implementation

    private void init(){
        Button mapBtn = (Button) findViewById(R.id.MapButton);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    //check if device can use maps
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //resolvable error occured
            Log.d(TAG, "isServicesOK: a fixable error occured");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this, "You cant make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void openFriends(View view) {
        // TODO: Complete this and open friends list intent
    }

    public void openConnectedUser(View view) {
        // TODO: Complete this and open connected user intent
    }

    public void openUserProfileUI(View view) {
        String userId = AuthenticationService.getAuthenticationService().getUser().getUid();
        Intent intent = new Intent(HomeActivity.this, UserProfileUI.class);
        intent.putExtra("targetUser", userId);
        startActivity(intent);
    }

    public void openSettings(View view){
        Intent intent = new Intent(HomeActivity.this, SettingsUI.class);
        startActivity(intent);
    }

    public void openEvents(View view) {
        Intent intent = new Intent(HomeActivity.this, EventsUI.class);
        startActivity(intent);
    }

    public void openUserSearch(View view) {
        Intent intent = new Intent(HomeActivity.this, UserSearchUI.class);
        startActivity(intent);
    }

}
