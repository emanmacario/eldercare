package au.edu.unimelb.eldercare.user;

import java.util.HashMap;

public class User {

    private String displayName;
    private String email;
    private String userType;
    private String connectedUserID;
    private HashMap<String, String> registeredEventId;

    public User(){
        this.registeredEventId = new HashMap<>();
    }

    public User(String displayName, String email, String userType, String connectedUserID){
        this.displayName = displayName;
        this.email = email;
        this.userType = userType;
        this.connectedUserID = connectedUserID;
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
    public HashMap<String, String> getRegisteredEventId() {
        return registeredEventId;
    }

    public void setRegisteredEventId(HashMap<String, String> registeredEventId){
        this.registeredEventId = registeredEventId;
    }

    public String registerEvent(String eventId, String registerState){
        return this.registeredEventId.put(eventId, registerState);
    }

    public String unregisterEvent(String eventId){
        return this.registeredEventId.remove(eventId);
    }

    public String getConnectedUserID() {
        return connectedUserID;
    }

    public void setConnectedUserID(String connectedUserID) {
        this.connectedUserID = connectedUserID;
    }
}
