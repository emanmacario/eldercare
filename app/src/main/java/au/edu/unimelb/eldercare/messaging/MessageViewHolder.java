package au.edu.unimelb.eldercare.messaging;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * The base view holder class from which each new
 * concrete message view holder class extends
 */
public abstract class MessageViewHolder extends RecyclerView.ViewHolder {

    MessageViewHolder(View view) {
        super(view);
    }

    /**
     * Binds message object contents to different
     * item view components in the view holder
     * @param message the message to bind
     */
    public abstract void bind(Message message);
}
