package au.edu.unimelb.eldercare.devicesharing;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import au.edu.unimelb.eldercare.user.User;

public class LocationGrabber {

    private static LocationGrabber instance;
    DatabaseReference mDatabase;
    private double Latitude;
    private double Longitude;
    private LatLng location;

    public LocationGrabber(){
        Latitude = 0;
        Longitude = 0;
        location = new LatLng(Latitude, Longitude);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    }

    public void setConnectedUsersLocation(String email){

        mDatabase.orderByChild("email").equalTo(email).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                Latitude = user.getLatitude();
                Longitude = user.getLongitude();
                location = new LatLng(Latitude, Longitude);
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
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
