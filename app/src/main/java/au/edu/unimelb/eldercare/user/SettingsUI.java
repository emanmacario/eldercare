package au.edu.unimelb.eldercare.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import au.edu.unimelb.eldercare.R;
import au.edu.unimelb.eldercare.service.UserAccessor;
import au.edu.unimelb.eldercare.service.UserService;

public class SettingsUI extends AppCompatActivity implements UserAccessor{

    private TextView currentDisplayName;
    private TextView currentEmailAddress;
    private TextView currentConnectedUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the screen on open
        setContentView(R.layout.settings_ui);

        String mUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserService.getInstance().getSpecificUser(mUser, this);

        currentDisplayName = findViewById(R.id.currentDisplayName);
        currentEmailAddress = findViewById(R.id.CurrentEmailAddress);
        currentConnectedUser = findViewById(R.id.CurrentConnectedUserSettings);


    }

    public void openChangeDNameActivity(View view) {
        Intent intent = new Intent(SettingsUI.this, ChangeDNameActivity.class);
        startActivity(intent);
    }

    public void openChangeEmailActivity(View view) {
        Intent intent = new Intent(SettingsUI.this, ChangeEmailActivity.class);
        startActivity(intent);
    }

    public void openChangeUserTypeActivity(View view) {
        Intent intent = new Intent(SettingsUI.this, SelectUserTypeActivity.class);
        startActivity(intent);
    }

    public void openChangeConnectedUserActivity(View view) {
        Intent intent = new Intent(SettingsUI.this, ChangeConnectedUserActivity.class);
        startActivity(intent);
    }

    public void openChangeUserBioActivity(View view) {
        Intent intent = new Intent(SettingsUI.this, ChangeUserBioActivity.class);
        startActivity(intent);
    }

    @Override
    public void userListLoaded(List<User> users) {
        //not used
    }

    @Override
    public void userLoaded(User value) {
        String mUserName = value.getDisplayName();
        currentDisplayName.setText(mUserName);

        String mEmail = value.getEmail();
        currentEmailAddress.setText(mEmail);

        String mConnectedUser = value.getConnectedUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mConnectedUser);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String mConnectedUserName = user.getDisplayName();
                currentConnectedUser.setText(mConnectedUserName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
