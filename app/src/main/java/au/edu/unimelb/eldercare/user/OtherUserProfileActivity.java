package au.edu.unimelb.eldercare.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import au.edu.unimelb.eldercare.R;

public class OtherUserProfileActivity extends AppCompatActivity {

    TextView UserProfileName;
    TextView UserProfileBio;

    Button UserAboutButton;
    Button UserPhotosButton;
    Button UserAddFriendButton;

    FirebaseUser user;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.other_user_profile_activity);

        UserProfileName = findViewById(R.id.OtherUserDisplayName);
        UserProfileBio = findViewById(R.id.OtherUserBio);
        UserAboutButton = findViewById(R.id.OtherUserAboutButton);
        UserPhotosButton = findViewById(R.id.OtherUserPhotoButton);
        UserAddFriendButton = findViewById(R.id.addFriendButtonUserProfile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
