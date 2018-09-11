package au.edu.unimelb.eldercare;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

class Event{

    public String eventId;
    public String eventName;
    public String eventDescription;
    public Timestamp startingTime;
    public HashMap<String, Double> location;
    public ArrayList<Integer> registeredUserId;
    public int maxUser;


    public Event(String eventName, Timestamp startingTime, HashMap<String, Double> location){
        this.eventName = eventName;
        this.startingTime = startingTime;
        this.location = location;
    }


    public boolean registerUser(int userId){
        return this.registeredUserId.add(userId);
    }

    public boolean unregisterUser(int userId){
        return this.registeredUserId.remove(Integer.valueOf(userId));
    }

}
