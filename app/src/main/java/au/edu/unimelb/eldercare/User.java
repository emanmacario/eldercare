package au.edu.unimelb.eldercare;

public class User {

    private String displayName;
    private String email;
    private String userType;

    public User(){
    }

    public User(String displayName, String email, String userType){
        this.displayName = displayName;
        this.email = email;
        this.userType = userType;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) { this.userType = userType; }
}
