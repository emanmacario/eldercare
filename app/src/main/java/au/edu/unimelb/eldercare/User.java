package au.edu.unimelb.eldercare;

public class User {

    private String displayName;
    private String email;

    public User(){
    }

    public User(String displayName, String email){
        this.displayName = displayName;
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
