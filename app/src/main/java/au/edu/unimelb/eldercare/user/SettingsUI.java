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

import au.edu.unimelb.eldercare.R;

public class SettingsUI extends AppCompatActivity {

    private TextView currentDisplayName;
    private TextView currentEmailAddress;
    private TextView currentConnectedUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the screen on open
        setContentView(R.layout.settings_ui);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        currentDisplayName = findViewById(R.id.currentDisplayName);
        currentEmailAddress = findViewById(R.id.CurrentEmailAddress);
        currentConnectedUser = findViewById(R.id.CurrentConnectedUserSettings);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                //Sets the TextView strings based on values in the database
                String dName = user.getDisplayName();
                currentDisplayName.setText(dName);

                String email = user.getEmail();
                currentEmailAddress.setText(email);

                String connectedUser = user.getConnectedUserID();
                currentConnectedUser.setText(connectedUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void openChangeDNameActivity(View view){
        Intent intent = new Intent(SettingsUI.this, ChangeDNameActivity.class);
        startActivity(intent);
    }

    public void openChangeEmailActivity(View view){
        Intent intent = new Intent(SettingsUI.this, ChangeEmailActivity.class);
        startActivity(intent);
    }

    public void openChangeUserTypeActivity(View view){
        Intent intent = new Intent(SettingsUI.this, SelectUserTypeActivity.class);
        startActivity(intent);
    }

    public void openChangeConnectedUserActivity(View view){
        Intent intent = new Intent(SettingsUI.this, ChangeConnectedUserActivity.class);
        startActivity(intent);
    }

    public void openChangeUserBioActivity(View view){
        Intent intent = new Intent(SettingsUI.this, ChangeUserBioActivity.class);
        startActivity(intent);
    }

}
