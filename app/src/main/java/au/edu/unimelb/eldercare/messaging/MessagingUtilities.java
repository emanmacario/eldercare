package au.edu.unimelb.eldercare.messaging;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

class MessagingUtilities {
    static void handleImageMessage(Message message, String imageUrl, final RoundedImageView messageImage, final String tag) {
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
                                Log.w(tag, "Getting download URL was not successful",
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
    }
}
