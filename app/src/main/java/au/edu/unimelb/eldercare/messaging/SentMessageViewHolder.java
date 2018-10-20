package au.edu.unimelb.eldercare.messaging;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import au.edu.unimelb.eldercare.R;
import au.edu.unimelb.eldercare.helpers.TimeUtil;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Handles display of outgoing messages
 */
public class SentMessageViewHolder extends MessageViewHolder {

    private static final String TAG = "SentMessageViewHolder";

    private TextView messageText;
    private TextView timeText;
    private RoundedImageView messageImage;

    public SentMessageViewHolder(View view) {
        super(view);
        this.messageText = view.findViewById(R.id.text_message_body);
        this.timeText = view.findViewById(R.id.text_message_time);
        this.messageImage = view.findViewById(R.id.image_message_view);
    }

    @Override
    public void bind(final Message message) {

        if (message.getText() != null) {

            this.messageText.setText(message.getText());
            this.timeText.setText(TimeUtil.createTimeString(message.getTime()));
            messageImage.setVisibility(ImageView.GONE);
            messageText.setVisibility(TextView.VISIBLE);

        } else if (message.getImageUrl() != null) {

            String imageUrl = message.getImageUrl();
            MessagingUtilities.handleImageMessage(message, imageUrl, messageImage, TAG);
            messageImage.setVisibility(ImageView.VISIBLE);
            messageText.setVisibility(TextView.GONE);
            timeText.setVisibility(TextView.VISIBLE);
        }
    }
}
