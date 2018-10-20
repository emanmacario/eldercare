package au.edu.unimelb.eldercare.messaging;

/**
 * The Message class, which is used for
 * communication between two or more users
 */
public class Message {

    private String id;
    private String senderId;
    private String senderDisplayName;
    private String text;
    private String photoUrl;
    private String imageUrl;
    private long time;

    @SuppressWarnings("unused")
    // Default constructor needed for Firebase
    public Message() {
    }

    /**
     * Create a new message
     * @param senderId The uid of the sender
     * @param senderDisplayName The friendly name of the sender
     * @param text The text of the message (can be null)
     * @param imageUrl The URL to an image attached to the message
     * @param photoUrl (Deprecated) The URL to a photo attached to the message
     * @param time The UNIX epoch time the message was sent
     */
    public Message(String senderId,
                   String senderDisplayName,
                   String text,
                   String imageUrl,
                   String photoUrl,
                   long time) {
        this.senderId = senderId;
        this.senderDisplayName = senderDisplayName;
        this.text = text;
        this.imageUrl = imageUrl;
        this.photoUrl = photoUrl;
        this.time = time;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return this.senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public void setSenderDisplayName(String senderDisplayName) {
        this.senderDisplayName = senderDisplayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getText() {
        return this.text;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
