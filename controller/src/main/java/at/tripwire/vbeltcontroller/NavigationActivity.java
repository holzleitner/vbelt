package at.tripwire.vbeltcontroller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;

@EActivity(R.layout.activity_navigation)
public class NavigationActivity extends AppCompatActivity {

    public static final String EXTRA_DESTINATION = "at.tripwire.vbeltcontroller.extra.destination";

    @ViewById(R.id.location)
    protected TextView locationTextView;

    @ViewById(R.id.points)
    protected TextView pointsTextView;

    @RestService
    protected MapsClient mapsClient;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @AfterViews
    protected void init() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            // check permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 42);
            }
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // start location updates
        try {
            fusedLocationProviderClient.requestLocationUpdates(LocationRequest.create(), locationCallback, null);
        } catch (SecurityException e) {
            Log.e(getString(R.string.app_name), "Failed to request the current location.", e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop location updates
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location currentLocation = locationResult.getLastLocation();
            loadRoutePoints(currentLocation);
            updateUI(currentLocation);
        }
    };

    @Background
    protected void loadRoutePoints(Location currentLocation) {
        String route = mapsClient.getRoutePoints(Double.toString(currentLocation.getLatitude()), Double.toString(currentLocation.getLongitude()), "48.2643454", "13.9280544");
        showPoints(route);
    }

    @UiThread
    protected void showPoints(String route) {
        pointsTextView.setText(route);
    }

    @UiThread
    protected void updateUI(Location location) {
        locationTextView.setText(location.toString());
    }
}
