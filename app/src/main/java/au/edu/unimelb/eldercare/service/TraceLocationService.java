package au.edu.unimelb.eldercare.service;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Provides services around location tracking
 */
public class TraceLocationService {

    private static TraceLocationService instance;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    private DatabaseReference mDatabase;

    private TraceLocationService() {
        Log.d(this.getClass().getSimpleName(), "creating service");

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000).setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase = userDatabase.child(user.getUid());

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(this.getClass().getSimpleName(), "location changed");
                if (locationResult == null) {
                    Log.d(this.getClass().getSimpleName(), "locationResult is null");
                    return;
                }
                Log.d(this.getClass().getSimpleName(), locationResult.getLastLocation().toString());
                uploadLocation(locationResult.getLastLocation());
            }
        };
    }

    /**
     * Returns (and, if necessary, creates) the singleton location tracing service
     * @return The singleton location service
     */
    public static TraceLocationService getTraceLocationService() {
        if (instance == null) {
            instance = new TraceLocationService();
        }
        return instance;
    }

    /**
     * Commences tracing for the current user
     * @param context The global Android context
     */
    public void startTracing(Context context) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);


        try {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d(this.getClass().getSimpleName(), "getLastLocation");
                        Log.d(this.getClass().getSimpleName(), location.toString());
                        uploadLocation(location);
                    } else {
                        Log.d(this.getClass().getSimpleName(), "no location!! make a fake one");
                        location = new Location("");
                        location.setLatitude(-38);
                        location.setLongitude(145);
                        uploadLocation(location);
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e(this.getClass().getSimpleName(), "permission plz");
        }

        restartTracing();
    }

    private void restartTracing() {
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            Log.d(this.getClass().getSimpleName(), "requesting update");
        } catch (SecurityException e) {
            Log.e(this.getClass().getSimpleName(), "permission plz");
        }
    }

    /**
     * Stops tracing for the current user
     */
    public void stopTracing() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void uploadLocation(final Location location) {
        HashMap<String, Double> locationMap = new HashMap<>();
        locationMap.put("latitude", location.getLatitude());
        locationMap.put("longitude", location.getLongitude());

        mDatabase.child("location").setValue(locationMap);

        Log.d(this.getClass().getSimpleName(), "updating DB");
    }
}
