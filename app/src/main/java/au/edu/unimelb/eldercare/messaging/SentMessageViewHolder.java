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

import au.edu.unimelb.eldercare.R;
import au.edu.unimelb.eldercare.helpers.TimeUtil;

public class SentMessageViewHolder extends MessageViewHolder {

    private static final String TAG = "SentMessageViewHolder";

    private TextView messageText;
    private TextView timeText;
    private ImageView messageImage;

    public SentMessageViewHolder(View view) {
        super(view);
        this.messageText = (TextView) view.findViewById(R.id.text_message_body);
        this.timeText = (TextView) view.findViewById(R.id.text_message_time);
        this.messageImage = (ImageView) view.findViewById(R.id.image_message_view);
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
            if (imageUrl.startsWith("gs://")) {
                StorageReference storageReference
                        = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

                storageReference.getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
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
            timeText.setVisibility(TextView.VISIBLE);
        }
    }
}