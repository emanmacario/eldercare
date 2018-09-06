package au.edu.unimelb.eldercare;

public class Message {

    private String id;
    private String name;
    private String text;
    private String photoUrl;
    private String imageUrl;

    public Message(String name, String text, String imageUrl, String photoUrl) {
        this.name = name;
        this.text = text;
        this.imageUrl = imageUrl;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
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

    public String getText() {
        return text;
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