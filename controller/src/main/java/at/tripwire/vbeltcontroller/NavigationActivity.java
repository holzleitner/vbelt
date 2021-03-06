package at.tripwire.vbeltcontroller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import at.tripwire.vbeltcontroller.model.Step;
import at.tripwire.vbeltcontroller.networking.ActionBroadcaster;
import at.tripwire.vbeltcontroller.routing.RouteFacade;
import at.tripwire.vbeltcontroller.utils.Utils;

@EActivity(R.layout.activity_navigation)
public class NavigationActivity extends AppCompatActivity {

    public static final String EXTRA_DESTINATION = "at.tripwire.vbeltcontroller.extra.destination";

    private static final long UPDATE_INTERVAL = 1000; // in milliseconds

    private static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2; // in milliseconds

    @ViewById(R.id.location)
    protected TextView locationTextView;

    @ViewById(R.id.steps)
    protected TextView stepsTextView;

    @ViewById(R.id.next_step)
    protected TextView nextStepTextView;

    @Bean
    protected RouteFacade routeFacade;

    @Bean
    protected ActionBroadcaster actionBroadcaster;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationRequest locationRequest;

    private List<Step> steps;

    private Step currentStep;

    private int lastSentDistance = 0;

    @AfterViews
    protected void init() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            // check permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 42);
            }
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // start location updates
        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            actionBroadcaster.connect();
        } catch (SecurityException e) {
            Log.e(getString(R.string.app_name), "Failed to request the current location.", e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop location updates
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        actionBroadcaster.disconnect();
    }

    @Background
    protected void loadRoutePoints(Location currentLocation) {
        LatLng destination = routeFacade.getCoordinates(getIntent().getStringExtra(EXTRA_DESTINATION));
        if (destination != null) {
            steps = routeFacade.getPoints(
                    Double.toString(currentLocation.getLatitude()), Double.toString(currentLocation.getLongitude()),
                    Double.toString(destination.latitude), Double.toString(destination.longitude));
            showRoutePoints();
        } else {
            finishActivity();
        }
    }

    @UiThread
    protected void finishActivity() {
        Toast.makeText(this, R.string.destination_not_found, Toast.LENGTH_SHORT).show();
        finish();
    }

    @UiThread
    protected void showRoutePoints() {
        if (steps != null) {
            StringBuilder builder = new StringBuilder();
            for (Step step : steps) {
                builder.append(step.getLatitude());
                builder.append(", ");
                builder.append(step.getLongitude());
                builder.append(": ");
                builder.append(step.getManeuver());
                builder.append("\n");
            }
            Log.i(this.getString(R.string.app_name), "steps: " + builder.toString());
            stepsTextView.setText(builder.toString());
        }
    }

    @UiThread
    protected void updateUI(Location location) {
        locationTextView.setText("latitude: " + location.getLatitude() + ", longitude: " + location.getLongitude());
    }

    @UiThread
    protected void showNextStep(double distance) {
        nextStepTextView.setText("distance: " + String.format("%.1f", distance) + "m, maneuver: " + currentStep.getManeuver());
    }

    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location currentLocation = locationResult.getLastLocation();
            updateUI(currentLocation);

            if (steps == null) {
                loadRoutePoints(currentLocation);
            } else {
                calculateAndPublicize(currentLocation);
            }
        }
    };

    private void calculateAndPublicize(Location currentLocation) {
        double distance = getMinDistance(currentLocation);
        int payload = Utils.normalize(distance);

        Log.i(this.getString(R.string.app_name), "distance: " + distance + ", normalized: " + payload + ", maneuver: " + currentStep.getManeuver());
        showNextStep(distance);
        showRoutePoints();

        if (payload != -1) {
            if (payload != lastSentDistance) {
                actionBroadcaster.publish(currentStep.getManeuver(), String.valueOf(payload));
            }
            lastSentDistance = payload;
        }
        if (distance < 5) {
            steps.remove(currentStep);
            lastSentDistance = 0;
        }
    }

    private double getMinDistance(Location currentLocation) {
        Location nearest = new Location("nearest");
        double minDistance = Double.MAX_VALUE;
        for (Step step : steps) {
            nearest.setLatitude(step.getLatitude());
            nearest.setLongitude(step.getLongitude());
            double distance = currentLocation.distanceTo(nearest);
            if (distance < minDistance) {
                minDistance = distance;
                currentStep = step;
            }
        }
        return minDistance;
    }
}
