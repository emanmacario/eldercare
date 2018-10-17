package au.edu.unimelb.eldercare.messaging;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import au.edu.unimelb.eldercare.R;
import au.edu.unimelb.eldercare.helpers.TimeUtil;
import au.edu.unimelb.eldercare.service.UserService;
import au.edu.unimelb.eldercare.user.User;
import au.edu.unimelb.eldercare.usersearch.UserAccessor;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReceivedMessageViewHolder extends MessageViewHolder implements UserAccessor {

    private static String TAG = "ReceivedMessageViewHolder";

    private TextView messageText;
    private TextView timeText;
    private TextView nameText;
    private CircleImageView profileImage;
    private RoundedImageView messageImage;

    public ReceivedMessageViewHolder(View view) {
        super(view);
        this.messageText = (TextView) view.findViewById(R.id.text_message_body);
        this.timeText = (TextView) view.findViewById(R.id.text_message_time);
        this.nameText = (TextView) view.findViewById(R.id.text_message_name);
        this.profileImage = (CircleImageView) view.findViewById(R.id.image_message_profile);
        this.messageImage = (RoundedImageView) view.findViewById(R.id.image_message_view);
    }

    @Override
    public void bind(Message message) {

        UserService.getInstance().getSpecificUser(message.getSenderId(), this);

        this.timeText.setText(TimeUtil.createTimeString(message.getTime()));
        this.nameText.setVisibility(TextView.VISIBLE);

        if (message.getText() != null) {

            this.messageText.setText(message.getText());
            messageImage.setVisibility(ImageView.GONE);
            messageText.setVisibility(TextView.VISIBLE);
            timeText.setVisibility(TextView.VISIBLE);

        } else if (message.getImageUrl() != null) {

            String imageUrl = message.getImageUrl();
            if (imageUrl.startsWith("gs://")) {
                StorageReference storageReference
                        = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

                storageReference.getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    assert(task.getResult() != null);
                                    String downloadUrl = task.getResult().toString();
                                    Glide.with(messageImage.getContext())
                                            .load(downloadUrl)
                                            .into(messageImage);
                                } else {
                                    Log.w(TAG, "Getting download URL was not successful",
                                            task.getException());
                                }
                            }
                        }
                );
            } else {
                Glide.with(messageImage.getContext())
                        .load(message.getImageUrl())
                        .into(messageImage);
            }
            messageImage.setVisibility(ImageView.VISIBLE);
            messageText.setVisibility(TextView.GONE);
            timeText.setVisibility(TextView.GONE);
        }
    }

    @Override
    public void userListLoaded(List<User> users) {
        // Not used
    }

    public void userLoaded(User user) {
        // Dynamically display the sending user's display
        // name and profile display image, as they may be updated
        nameText.setText(user.getDisplayName());

        String profileImageUrl = user.getDisplayPhoto();
        if (profileImageUrl != null) {
            Glide.with(messageImage.getContext())
                    .load(profileImageUrl)
                    .into(profileImage);
        }
    }
}




