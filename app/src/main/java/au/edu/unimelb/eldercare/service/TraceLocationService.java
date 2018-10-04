package au.edu.unimelb.eldercare.service;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class TraceLocationService {

    private static TraceLocationService instance;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    private DatabaseReference databaseUserLocationReference;

    private TraceLocationService(){
        Log.d(this.getClass().getSimpleName(), "creating service");

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000).setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseUserLocationReference = userDatabase.child(currentUser).child("location").getRef();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                uploadLocation(locationResult.getLastLocation());
            };
        };
    }

    public static TraceLocationService getTraceLocationService() {
        if (instance == null) {
            instance = new TraceLocationService();
        }
        return instance;
    }

    public void startTracing(Context context){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        try {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                uploadLocation(location);
                            }else{
                                Log.d(this.getClass().getSimpleName(), "no location!! make a fake one");
                                location = new Location("");
                                location.setLatitude(3);
                                location.setLongitude(5);
                                uploadLocation(location);
                            }
                        }
                    });
        } catch (SecurityException e){
            Log.e(this.getClass().getSimpleName(), "permission plz");
        }

        restartTracing();
    }



    public void restartTracing(){
        try{
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            Log.d(this.getClass().getSimpleName(), "requesting update");
        }catch (SecurityException e){
            Log.e(this.getClass().getSimpleName(), "permission plz");
        }
    }

    public void stopTracing(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


    //should be private
    public void uploadLocation(Location location){
        HashMap<String, Double> locationMap = new HashMap<>();
        locationMap.put("latitude", location.getLatitude());
        locationMap.put("longitude", location.getLongitude());

        databaseUserLocationReference.setValue(locationMap);
        Log.d(this.getClass().getSimpleName(), "updating DB");
    }
}
