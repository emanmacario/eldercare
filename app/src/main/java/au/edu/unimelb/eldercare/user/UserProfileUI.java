package au.edu.unimelb.eldercare.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import au.edu.unimelb.eldercare.R;
import au.edu.unimelb.eldercare.service.UserAccessor;
import au.edu.unimelb.eldercare.service.UserService;
import de.hdodenhof.circleimageview.CircleImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

public class UserProfileUI extends AppCompatActivity implements UserAccessor {

    private static final String TAG = "UserProfileUI";

    private TextView mDisplayName;
    private TextView mUserBio;
    private CircleImageView mDisplayPhoto;
    private String mCurrentUserId;

    // Firebase References
    //FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_ui);

        String profileUserId = getIntent().getStringExtra("targetUser");
        UserService.getInstance().getSpecificUser(profileUserId, this);

        mDisplayName = findViewById(R.id.UserProfileHeading);
        mUserBio = findViewById(R.id.UserBio);
        mDisplayPhoto = findViewById(R.id.ProfilePicture);
        Button changePictureButton = findViewById(R.id.ChangeDisplayPictureButton);

        changePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start picker to get image for cropping and then
                // use the image in cropping activity
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(UserProfileUI.this);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                // Get the uri
                Uri resultUri = result.getUri();

                final StorageReference pictureStorageReference
                        = FirebaseStorage.getInstance().getReference()
                        .child("display-pictures")
                        .child(mCurrentUserId + ".jpg");

                // Create new task to asynchronously upload from content URI to this storage reference
                UploadTask uploadTask = pictureStorageReference.putFile(resultUri);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Could not get download URL");
                            if (task.getException() != null) {
                                throw task.getException();
                            }
                        }
                        // Continue with the task to get the download URL
                        return pictureStorageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String downloadUri = task.getResult().toString();
                            DatabaseReference databaseReference
                                    = FirebaseDatabase.getInstance().getReference();


                            databaseReference.child("users").child(mCurrentUserId).child("displayPhoto")
                                    .setValue(downloadUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Image upload was successful");
                                    }
                                }
                            });
                        } else {
                            Log.w(TAG, "Image upload task was not successful", task.getException());
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Log.d(TAG, result.getError().toString());

            }
        }
    }

    public void openAboutActivity(View view) {
        Intent intent = new Intent(UserProfileUI.this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void userListLoaded(List<User> users) {
        // Not used
    }

    @Override
    public void userLoaded(User user) {
        mCurrentUserId = user.getUserId();

        String displayName = user.getDisplayName();
        mDisplayName.setText(displayName);

        String userBioString = user.getUserBio();
        mUserBio.setText(userBioString);

        String displayPhotoUrl = user.getDisplayPhoto();
        if (displayPhotoUrl != null) {
            Glide.with(getApplicationContext())
                    .load(displayPhotoUrl)
                    .into(mDisplayPhoto);
        }
    }
}
