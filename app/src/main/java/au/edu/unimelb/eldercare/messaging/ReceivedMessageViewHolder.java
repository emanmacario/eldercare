package au.edu.unimelb.eldercare.messaging;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import au.edu.unimelb.eldercare.R;
import au.edu.unimelb.eldercare.helpers.TimeUtil;
import au.edu.unimelb.eldercare.service.UserService;
import au.edu.unimelb.eldercare.user.User;
import au.edu.unimelb.eldercare.service.UserAccessor;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

/**
 * Handles display of incoming messages
 */
public class ReceivedMessageViewHolder extends MessageViewHolder implements UserAccessor {

    private TextView messageText;
    private TextView timeText;
    private TextView nameText;
    private CircleImageView profileImage;
    private RoundedImageView messageImage;

    public ReceivedMessageViewHolder(View view) {
        super(view);
        this.messageText = view.findViewById(R.id.text_message_body);
        this.timeText = view.findViewById(R.id.text_message_time);
        this.nameText = view.findViewById(R.id.text_message_name);
        this.profileImage = view.findViewById(R.id.image_message_profile);
        this.messageImage = view.findViewById(R.id.image_message_view);
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
            String TAG = "ReceivedMessageViewHolder";
            MessagingUtilities.handleImageMessage(message, imageUrl, messageImage, TAG);
            messageImage.setVisibility(ImageView.VISIBLE);
            messageText.setVisibility(TextView.GONE);
            timeText.setVisibility(TextView.GONE);
        }
    }

    @Override
    public void userListLoaded(List<User> users) {
        // Not used
    }

    @Override
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




