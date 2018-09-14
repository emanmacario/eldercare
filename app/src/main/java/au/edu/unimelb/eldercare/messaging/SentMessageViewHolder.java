package au.edu.unimelb.eldercare.messaging;

import android.view.View;
import android.widget.TextView;

import au.edu.unimelb.eldercare.R;
import au.edu.unimelb.eldercare.helpers.TimeUtil;

public class SentMessageViewHolder extends MessageViewHolder {

    private TextView messageText;
    private TextView timeText;

    public SentMessageViewHolder(View view) {
        super(view);
        this.messageText = (TextView) view.findViewById(R.id.text_message_body);
        this.timeText = (TextView) view.findViewById(R.id.text_message_time);
    }

    @Override
    public void bind(Message message) {
        this.messageText.setText(message.getText());
        this.timeText.setText(TimeUtil.createTimeString(message.getTime()));
    }
}