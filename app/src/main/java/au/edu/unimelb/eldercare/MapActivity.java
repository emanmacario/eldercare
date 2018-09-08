package au.edu.unimelb.eldercare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity{

    private static final String TAG = "MapActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }

    private void getLocationPermission(){

    }
}
