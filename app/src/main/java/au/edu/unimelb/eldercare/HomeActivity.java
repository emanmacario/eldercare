package au.edu.unimelb.eldercare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import au.edu.unimelb.eldercare.event.EventsUI;
import au.edu.unimelb.eldercare.service.AuthenticationListener;
import au.edu.unimelb.eldercare.service.AuthenticationService;
import au.edu.unimelb.eldercare.service.TraceLocationService;
import au.edu.unimelb.eldercare.service.VoiceCallService;

import com.directions.route.*;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.edu.unimelb.eldercare.service.UserService;
import au.edu.unimelb.eldercare.user.OtherUserProfileActivity;
import au.edu.unimelb.eldercare.user.SelectUserTypeActivity;
import au.edu.unimelb.eldercare.user.SettingsUI;
import au.edu.unimelb.eldercare.user.User;
import au.edu.unimelb.eldercare.user.UserProfileUI;
import au.edu.unimelb.eldercare.user.UserSearchUI;
import au.edu.unimelb.eldercare.service.UserAccessor;


import static au.edu.unimelb.eldercare.service.AuthenticationService.RC_SIGN_IN;

public class HomeActivity extends AppCompatActivity implements UserAccessor, AuthenticationListener, OnMapReadyCallback, RoutingListener {

    // Class constants
    private static final String TAG = "MainActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    private static final String latitude = "latitude";
    private static final String longitude = "longitude";
    private static final String connectedUser = "ConnectedUser";
    private static final String location = "location";

    // Firebase instance variables
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private DatabaseReference mUsersRef;

    // Google Maps API instance variables
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient = null;
    private EditText searchAddress = null;
    private LatLng eventLocation = null;
    private List<Polyline> polylines;

    // User Tracking instance variables
    private Boolean userTracking;
    private TraceLocationService traceLocationService;
    private String ConnectedUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mDatabase.child("users");
        user = AuthenticationService.getAuthenticationService().getUser();
        if (user == null) {
            AuthenticationService.getAuthenticationService().startAuthentication(this);
        }

        getLocationPermission();
        polylines = new ArrayList<>();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        userTracking = false;

        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (userTracking) {
                    trackUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String locationName = extras.getString("locationName");
            eventLocation = extras.getParcelable("location");
            if (searchAddress == null) {
                searchAddress = findViewById(R.id.searchAddress);
            }
            searchAddress.setText(locationName);
            this.route(searchAddress);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            AuthenticationService.getAuthenticationService().handleAuthenticationRequestCallback(resultCode, data, this);
        }
    }

    @Override
    public void userAuthenticated(final FirebaseUser user) {
        this.user = user;

        // Check if user already exists so that their data is not overridden on every single login
        DatabaseReference userRef = mDatabase.child("users").child(this.user.getUid());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Once new user is authenticated, add them to the database
                    writeNewUser(user.getUid(), user.getDisplayName(), user.getEmail());
                    // Allow the new authenticated user to select their user type
                    Intent intent = new Intent(HomeActivity.this, SelectUserTypeActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        userRef.addListenerForSingleValueEvent(eventListener);

        // Create the Sinch Client for the current authenticated user
        VoiceCallService sinchService = VoiceCallService.getInstance();
        sinchService.buildSinchClient(this);

        // Initialise the Trace Location Service
        traceLocationService = TraceLocationService.getTraceLocationService();
        traceLocationService.startTracing(getApplicationContext());

        // Load connected user details
        UserService.getInstance().getSpecificUser(this.user.getUid(), this);
    }

    @Override
    public void userListLoaded(List<User> users) {
        // Not used
    }

    @Override
    public void userLoaded(User value) {
        ConnectedUser = value.getConnectedUser();
    }

    @Override
    public void authenticationFailed(IdpResponse response) {
        AuthenticationService.getAuthenticationService().startAuthentication(this);
    }

    /**
     * Writes a new user to the Firebase Realtime Database
     *
     * @param userId The id of the authenticated user
     * @param displayName The name of the user
     * @param email The email of the user
     */
    private void writeNewUser(String userId, String displayName, String email) {
        mUsersRef.child(userId).child("displayName").setValue(displayName);
        mUsersRef.child(userId).child("email").setValue(email);
        mUsersRef.child(userId).child("userType").setValue("");
        mUsersRef.child(userId).child("ConnectedUser").setValue("");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            if (userTracking) {
                trackUser();
            } else {
                mMap.clear();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        if (mFusedLocationProviderClient == null) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())
                            );

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(HomeActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }

    }

    private void moveCamera(LatLng latLng) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, HomeActivity.DEFAULT_ZOOM));
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        assert mapFragment != null;
        mapFragment.getMapAsync(HomeActivity.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            for (int grantResult1 : grantResults) {
                                if (grantResult1 != PackageManager.PERMISSION_GRANTED) {
                                    mLocationPermissionsGranted = false;
                                    Log.d(TAG, "onRequestPermissionsResult: permission failed");
                                    return;
                                }
                            }
                            Log.d(TAG, "onRequestPermissionsResult: permission granted");
                            mLocationPermissionsGranted = true;
                            //initialize our map
                            initMap();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        // The Routing request failed
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressWarnings("unchecked")
    private void route(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final Task location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Log.d(TAG, "onComplete: found location!");
                    Location currentLocation = (Location) task.getResult();

                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())
                    );

                    try {
                        getRoute(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.d(TAG, "onComplete: current location is null");
                    Toast.makeText(HomeActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getRoute(LatLng startLatLng) throws IOException {
        // Get the users inputted location
        if (searchAddress == null) {
            searchAddress = findViewById(R.id.searchAddress);
        }

        LatLng end;
        if (eventLocation == null) {
            // Get the latitude and longitude for the search terms location
            Geocoder gc = new Geocoder(this);
            List<Address> list = gc.getFromLocationName(searchAddress.getText().toString(), 1);
            Address add = list.get(0);
            String locality = add.getLocality();
            Toast.makeText(getApplicationContext(), locality, Toast.LENGTH_LONG).show();

            double lat = add.getLatitude();
            double lon = add.getLongitude();

            // Use the lat and long to and create a route from the users current location to destination
            end = new LatLng(lat, lon);
        } else {
            end = eventLocation;
        }

        String apiKey = "";
        ApplicationInfo ai;
        try {
            ai = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            apiKey = bundle.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.WALKING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(end, startLatLng)
                .key(apiKey)
                .build();
        routing.execute();
    }

    @Override
    public void onRoutingStart() {
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        // Add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {
            // In case of more than five alternative routes
            int colorIndex = i % COLORS.length;
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
        }
    }

    @Override
    public void onRoutingCancelled() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();
    }

    public void toggleUserTracking(View view) {
        userTracking = !userTracking;
        onMapReady(mMap);
    }

    private void trackUser() {
        traceLocationService.startTracing(HomeActivity.this);
        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear();
                // Get the connected user's email
                String ConnectedUsersUid = dataSnapshot.child(user.getUid()).child(connectedUser).getValue(String.class);
                if (ConnectedUsersUid != null) {
                    // Using the connected users Uid, grab their location
                    double lat = dataSnapshot.child(ConnectedUsersUid).child(location).child(latitude).getValue(double.class);
                    double lon = dataSnapshot.child(ConnectedUsersUid).child(location).child(longitude).getValue(double.class);

                    // Place a marker on the map at the connected user's location
                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Hello World"));
                } else {
                    Toast noConnectedUserToast = Toast.makeText(HomeActivity.this, "No User to Track", Toast.LENGTH_LONG);
                    noConnectedUserToast.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void openFriends(View view) {
    }

    public void openConnectedUser(View view) {
        if (ConnectedUser != null) {
            Intent intent = new Intent(HomeActivity.this, OtherUserProfileActivity.class);
            intent.putExtra("targetUser", ConnectedUser);
            startActivity(intent);
        } else {
            Toast toast = Toast.makeText(HomeActivity.this, "No Connected User", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void openUserProfileUI(View view) {
        String userId = AuthenticationService.getAuthenticationService().getUser().getUid();
        Intent intent = new Intent(HomeActivity.this, UserProfileUI.class);
        intent.putExtra("targetUser", userId);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(HomeActivity.this, SettingsUI.class);
        startActivity(intent);
    }

    public void openEvents(View view) {
        Intent intent = new Intent(HomeActivity.this, EventsUI.class);
        startActivity(intent);
    }

    public void openUserSearch(View view) {
        Intent intent = new Intent(HomeActivity.this, UserSearchUI.class);
        startActivity(intent);
    }
}