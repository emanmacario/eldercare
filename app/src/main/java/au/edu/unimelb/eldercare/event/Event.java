package au.edu.unimelb.eldercare.event;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

class Event implements Parcelable{

    public String eventId;
    public String eventName;
    public String eventDescription;
    public Long startingTime;
    public HashMap<String, Double> location;
    public HashMap<String, String> registeredUserId;
    public int maxUser;
    public String creator;

    public Event(){
        this.registeredUserId = new HashMap<String, String>();
    }

    public Event(String eventName, Long startingTime, HashMap<String, Double> location){
        this.eventName = eventName;
        this.startingTime = startingTime;
        this.location = location;
        this.registeredUserId = new HashMap<String, String>();
    }

    public String registerUser(String userId, String state){
        return this.registeredUserId.put(userId, state);
    }

    public String unregisterUser(String userId){
        return this.registeredUserId.remove(userId);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString(){
        return String.format("eventId: %s, eventName: %s, eventDescription: %s, startingTime: %d, " +
                        "location: %s, registeredUserId: %s, maxUser: %d, creator: %s",
                eventId, eventName, eventDescription, startingTime, location,
                registeredUserId, maxUser, creator);
    }

    @SuppressWarnings("unchecked")
    protected Event(Parcel in) {
        eventId = in.readString();
        eventName = in.readString();
        eventDescription = in.readString();
        startingTime = in.readLong();
        location = (HashMap<String, Double>) in.readSerializable();
        registeredUserId = (HashMap<String, String>) in.readSerializable();
        maxUser = in.readInt();
        creator = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventId);
        dest.writeString(eventName);
        dest.writeString(eventDescription);
        dest.writeLong(startingTime);
        dest.writeSerializable(location);
        dest.writeSerializable (registeredUserId);
        dest.writeInt(maxUser);
        dest.writeString(creator);
    }


    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}