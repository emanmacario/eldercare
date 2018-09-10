package au.edu.unimelb.eldercare;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

class Event {

    public int eventId;
    public String eventName;
    public String eventDescription;
    public Long startingTime;
    public HashMap<String, Double> location;
    public ArrayList<Integer> registeredUserId;
    public int maxUser;


    public Event(String eventName, Long startingTime, HashMap<String, Double> location){
        this.eventId = 0;  //TODO: find a way to get current max
        this.eventName = eventName;
        this.startingTime = startingTime;
        this.location = location;
    }

    public Event(String jsonEventData) throws JSONException{
        this(new JSONObject(jsonEventData));
    }


    public Event(JSONObject jsonEventData) throws JSONException{
        this.eventId = jsonEventData.getInt("event_id");
        this.eventName = jsonEventData.getString("event_name");
        this.eventDescription = jsonEventData.getString("event_description");
        this.startingTime = jsonEventData.getLong("starting_time");
        //this.location = (someType) jsonEventData.get("location");  //TODO: fix location
        this.registeredUserId = Utilities.jsonGetIntArrayList(jsonEventData, "registered_user_id");
        this.maxUser = jsonEventData.getInt("max_user");
    }

    /*
    public JSONObject toJSON() throws JSONException{
        JSONObject jsonEventData = new JSONObject();
        jsonEventData.put("event_id", this.eventId);
        jsonEventData.put("event_name", this.eventName);
        jsonEventData.put("event_description", this.eventDescription);
        jsonEventData.put("starting_time", this.startingTime);
        //jsonEventData.put("location", this.location);  //TODO: fix location
        jsonEventData.put("registered_user_id", this.registeredUserId);
        jsonEventData.put("max_user", this.maxUser);
        return jsonEventData;
    }*/

    public boolean registerUser(int userId){
        return this.registeredUserId.add(userId);
    }

    public boolean unregisterUser(int userId){
        return this.registeredUserId.remove(Integer.valueOf(userId));
    }

    /*
    public ArrayList<Double>  changeLocation(ArrayList<Double>  location){
        this.location = location
    }

    public Long changeStartingTime(Long startingTime){
        this.startingTime = startingTime;
        return startingTime;
    }

    public int changeMaximumUser(int maxUser){
        this.maxUser = maxUser;
        return maxUser;
    }

    public String changeEventDescription(String eventDescription){
        this.eventDescription = eventDescription;
        return eventDescription;
    }*/

    public void uploadEvent(DatabaseReference eventDB){
        DatabaseReference thisEvent = eventDB.child(Integer.toString(this.eventId));
        thisEvent.child("event_id").setValue(this.eventId);
        thisEvent.child("event_name").setValue(this.eventName);
        thisEvent.child("event_description").setValue(this.eventDescription);
        thisEvent.child("starting_time").setValue(this.startingTime);
        thisEvent.child("location").setValue(this.location);
        thisEvent.child("registered_user_id").setValue(this.registeredUserId);
        thisEvent.child("max_user").setValue(this.maxUser);
    }
}
