package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import au.edu.unimelb.eldercare.service.AuthenticationListener;
import au.edu.unimelb.eldercare.service.AuthenticationService;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import static au.edu.unimelb.eldercare.service.AuthenticationService.RC_SIGN_IN;

public class MainActivity extends AppCompatActivity implements AuthenticationListener {

    private TextView mTextMessage;
    private FirebaseUser user;
    private DatabaseReference mDatabase;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
                case R.id.navigation_home_screen:
                    mTextMessage.setText(R.string.HomeText);
                    startActivity(new Intent(MainActivity.this, HomeScreen.class));
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the screen on open
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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
                    Intent intent = new Intent(MainActivity.this, SelectUserTypeActivity.class);
                    startActivity(intent);
                }
                else{
                    //User already has user type, go straight to home screen
                    Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        userRef.addListenerForSingleValueEvent(eventListener);

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
        User user = new User(name, email, "");
        mDatabase.child("users").child(userId).setValue(user);
    }

}
