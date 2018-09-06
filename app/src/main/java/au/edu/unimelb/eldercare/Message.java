package au.edu.unimelb.eldercare;

public class Message {

    private String id;
    private String name;
    private String message;
    private String photoUrl;
    private String imageUrl;

    // Default constructor needed for Firebase
    public Message() {
    }

    public Message(String name, String message, String imageUrl, String photoUrl) {
        this.name = name;
        this.message = message;
        this.imageUrl = imageUrl;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getMessage() {
        return message;
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
}