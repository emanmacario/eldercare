package au.edu.unimelb.eldercare.event;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Contains information for events
 */
class Event implements Parcelable {

    public String eventId;
    public String eventName;
    public String eventDescription;
    public Long startingTime;
    public String locationName;
    public HashMap<String, Double> location;
    public HashMap<String, String> registeredUserId;
    public int maxUser;
    public String creator;

    // Default constructure required for Firebase
    @SuppressWarnings("unused")
    public Event() {

    }

    /**
     * Create a new event object without persisting to the database
     *
     * @param eventName    The name of the event
     * @param startingTime The time (in UNIX epoch) the event will start
     * @param location     The latitude and longitude of the event
     */
    public Event(String eventName, Long startingTime, HashMap<String, Double> location) {
        this.eventName = eventName;
        this.startingTime = startingTime;
        this.location = location;
        this.registeredUserId = new HashMap<>();
    }

    void registerUser(String userId, String state) {
        this.registeredUserId.put(userId, state);
    }

    void unregisterUser(String userId) {
        this.registeredUserId.remove(userId);
    }

    //for debug only
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("eventId: %s, eventName: %s, eventDescription: %s, startingTime: %d, " +
                        "locationName: %s, location: %s, registeredUserId: %s, maxUser: %d, " +
                        "creator: %s",
                eventId, eventName, eventDescription, startingTime, locationName,
                location, registeredUserId, maxUser, creator);
    }

    @SuppressWarnings("unchecked")
    private Event(Parcel in) {
        eventId = in.readString();
        eventName = in.readString();
        eventDescription = in.readString();
        startingTime = in.readLong();
        locationName = in.readString();
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
        dest.writeString(locationName);
        dest.writeSerializable(location);
        dest.writeSerializable(registeredUserId);
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
