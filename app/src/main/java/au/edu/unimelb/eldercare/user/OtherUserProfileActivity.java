package au.edu.unimelb.eldercare.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;

import java.util.List;

import au.edu.unimelb.eldercare.ActiveCallActivity;
import au.edu.unimelb.eldercare.MessagingActivity;
import au.edu.unimelb.eldercare.R;
import au.edu.unimelb.eldercare.VoiceCallActivity;
import au.edu.unimelb.eldercare.VoiceCallService;
import au.edu.unimelb.eldercare.service.UserService;
import au.edu.unimelb.eldercare.usersearch.UserAccessor;
import de.hdodenhof.circleimageview.CircleImageView;

public class OtherUserProfileActivity extends AppCompatActivity implements UserAccessor {

    TextView userProfileName;
    TextView userProfileBio;
    CircleImageView userDisplayPhoto;
    Button userAboutButton;
    Button userAddFriendButton;
    Button userCallButton;
    Button userMessageButton;

    private String mDisplayName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_user_profile_activity);

        // Get the id of the user being looked at
        final String profileUserId = getIntent().getStringExtra("targetUser");
        UserService.getInstance().getSpecificUser(profileUserId, this);

        userProfileName = findViewById(R.id.OtherUserDisplayName);
        userProfileBio = findViewById(R.id.OtherUserBio);
        userAboutButton = findViewById(R.id.OtherUserAboutButton);
        userAddFriendButton = findViewById(R.id.addFriendButtonUserProfile);
        userCallButton = findViewById(R.id.CallOtherUserButton);
        userMessageButton = findViewById(R.id.MessageOtherUserButton);
        userDisplayPhoto = findViewById(R.id.UserProfilePicture);


        userCallButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Try to make the call
                try {
                    VoiceCallService sinchService = VoiceCallService.getInstance();
                    Call call = sinchService.callUser(profileUserId);
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
                    ActivityCompat.requestPermissions(OtherUserProfileActivity.this,
                            new String[] {e.getRequiredPermission()}, 0);
                }
            }
        });

        userMessageButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtherUserProfileActivity.this, MessagingActivity.class);
                intent.putExtra("targetUser", profileUserId);
                intent.putExtra("displayName", mDisplayName);
                startActivity(intent);
            }
        });
    }

    @Override
    public void userListLoaded(List<User> users) {
        // Not used
    }

    @Override
    public void userLoaded(User value) {
        mDisplayName = value.getDisplayName();
        userProfileName.setText(mDisplayName);

        String userBioString = value.getUserBio();
        userProfileBio.setText(userBioString);

        String displayPhotoUrl = value.getDisplayPhoto();
        if (displayPhotoUrl != null) {
            Glide.with(getApplicationContext())
                    .load(displayPhotoUrl)
                    .into(userDisplayPhoto);
        }
    }
}
