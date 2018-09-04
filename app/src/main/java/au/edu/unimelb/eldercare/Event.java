package au.edu.unimelb.eldercare;

import java.util.ArrayList;

class Event {

    protected final int eventId;
    protected String eventName;
    protected String eventDescription;
    protected Long startingTime;
    //private someType location;
    private ArrayList<Integer> registeredUserId;
    private int maxUser;

    //TODO: add location to param
    Event (String eventName, Long startingTime){
        this.eventId = 0;  //TODO: find a way to get current max
        this.eventName = eventName;
        this.startingTime = startingTime;
        //this.location = location;
    }

    boolean registerUser(int userId){
        return this.registeredUserId.add(userId);
    }

    boolean unregisterUser(int userId){
        return this.registeredUserId.remove(new Integer(userId));
    }

    /*
    void changeLocation(someType location){
        this.location = location
    }*/

    Long changeStartingTime(Long startingTime){
        this.startingTime = startingTime;
        return startingTime;
    }

    int changeMaximumUser(int maxUser){
        this.maxUser = maxUser;
        return maxUser;
    }

    String changeEventDescription(String eventDescription){
        this.eventDescription = eventDescription;
        return eventDescription;
    }
}
