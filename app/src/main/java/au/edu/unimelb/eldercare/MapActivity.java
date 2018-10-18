package au.edu.unimelb.eldercare;

import android.Manifest;
import android.content.Context;
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

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.edu.unimelb.eldercare.service.TraceLocationService;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    protected EditText searchAddress = null;
    protected LatLng eventLocation = null;


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
            }
            else {
                mMap.clear();
            }
        }
    }

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient = null;

    //Firebase
    private FirebaseUser user;
    private DatabaseReference mDatabase;

    //User Tracking
    private Boolean userTracking;
    private TraceLocationService traceLocationService;

    //Strings
    private final String latitude = "latitude";
    private final String longitude = "longitude";
    private final String connectedUser = "ConnectedUser";
    private final String location = "location";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //route lines
        polylines = new ArrayList<>();

        getLocationPermission();

        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        this.mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //TODO: Add a toggle button
        userTracking = false;
        traceLocationService = TraceLocationService.getTraceLocationService();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(userTracking){
                    trackUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String locationName = extras.getString("locationName");
            eventLocation = extras.getParcelable("location");
            if (searchAddress == null) {
                searchAddress = findViewById(R.id.searchAddress);
            }
            searchAddress.setText(locationName);
            this.route(searchAddress);
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        if(mFusedLocationProviderClient == null) {
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

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }

    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        assert mapFragment != null;
        mapFragment.getMapAsync(MapActivity.this);
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
                            for (int i = 0; i < grantResults.length; i++) {
                                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
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


    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    @Override
    public void onRoutingFailure(RouteException e) {
        // The Routing request failed
        //progressDialog.dismiss();
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }

    }

    //protected Location mLastLocation;
    //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

    public void route(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        final Task location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Log.d(TAG, "onComplete: found location!");
                    Location currentLocation = (Location) task.getResult();

                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                            DEFAULT_ZOOM);

                    try {
                        getRoute(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.d(TAG, "onComplete: current location is null");
                    Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Hides Keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    private void getRoute(LatLng startLatLng) throws IOException {

        //get the users inputted location
        if (searchAddress == null) {
            searchAddress = findViewById(R.id.searchAddress);
        }

        LatLng end;
        if(eventLocation == null) {
            //get the latitude and longitude for the search terms location
            Geocoder gc = new Geocoder(this);
            List<Address> list = gc.getFromLocationName(searchAddress.getText().toString(), 1);
            Address add = list.get(0);
            String locality = add.getLocality();
            Toast.makeText(getApplicationContext(), locality, Toast.LENGTH_LONG).show();

            double lat = add.getLatitude();
            double lon = add.getLongitude();

            //use the lat and long to and create a route from the users current location to destination
            end = new LatLng(lat, lon);
        }else{
            end = eventLocation;
        }

        String apiKey = "";
        ApplicationInfo ai = null;
        try {
            ai = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            apiKey = bundle.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(end, startLatLng)
                .key(apiKey)
                .build();
        routing.execute();
    }

    //TODO clean up & comment code and fix layout

    @Override
    public void onRoutingStart() {

    }


    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
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

    public void toggleUserTracking(View view){
        userTracking = !userTracking;
        onMapReady(mMap);
    }

    public void trackUser(){
        traceLocationService.startTracing(MapActivity.this);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear();
                //Get the connected users email
                String ConnectedUsersUid = dataSnapshot.child(user.getUid()).child(connectedUser).getValue(String.class);
                if(ConnectedUsersUid != null){
                    //Using the connected users Uid, grab their location
                    double lat = dataSnapshot.child(ConnectedUsersUid).child(location).child(latitude).getValue(double.class);
                    double lon = dataSnapshot.child(ConnectedUsersUid).child(location).child(longitude).getValue(double.class);

                    //Place a marker on the map at the connected user's location
                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Hello World"));
                }
                else{
                    Toast noConnectedUserToast = Toast.makeText(MapActivity.this, "No User to Track", Toast.LENGTH_LONG);
                    noConnectedUserToast.show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}