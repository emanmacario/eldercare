package au.edu.unimelb.eldercare;

import java.util.HashMap;

public class User {

    private String displayName;
    private String email;
    private HashMap<String, String> registeredEventId;

    public User(){
        this.registeredEventId = new HashMap<>();
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
}
