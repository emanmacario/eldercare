package au.edu.unimelb.eldercare.messaging;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import au.edu.unimelb.eldercare.R;
import au.edu.unimelb.eldercare.helpers.TimeUtil;

public class ReceivedMessageViewHolder extends MessageViewHolder {

    private TextView messageText;
    private TextView timeText;
    private TextView nameText;
    private ImageView profileImage;
    private ImageView messageImage;

    public ReceivedMessageViewHolder(View view) {
        super(view);
        this.messageText = (TextView) view.findViewById(R.id.text_message_body);
        this.timeText = (TextView) view.findViewById(R.id.text_message_time);
        this.nameText = (TextView) view.findViewById(R.id.text_message_name);
        this.profileImage = (ImageView) view.findViewById(R.id.image_message_profile);
    }

    @Override
    public void bind(Message message) {
        this.messageText.setText(message.getText());
        this.nameText.setText(message.getSenderDisplayName());
        this.timeText.setText(TimeUtil.createTimeString(message.getTime()));
    }
}