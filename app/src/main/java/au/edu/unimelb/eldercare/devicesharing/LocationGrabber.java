package au.edu.unimelb.eldercare.devicesharing;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import au.edu.unimelb.eldercare.user.User;

public class LocationGrabber {

    Location UserLocation;
    private static LocationGrabber instance;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

    public static LocationGrabber getLocationGrabber(){
        if(instance == null){
            instance = new LocationGrabber();
        }
        return instance;
    }

    private Location getConnectedUsersLocation(String email){

        mDatabase.orderByChild("email").equalTo(email).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);

                double Latitude = user.getLatitude();
                double Longitude = user.getLongitude();

                Location location = new Location("");
                location.setLatitude(Latitude);
                location.setLongitude(Longitude);

                UserLocation = location;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return UserLocation;
    }
}
