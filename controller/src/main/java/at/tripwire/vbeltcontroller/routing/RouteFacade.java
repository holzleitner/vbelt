package at.tripwire.vbeltcontroller.routing;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.rest.spring.annotations.RestService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.tripwire.vbeltcontroller.R;
import at.tripwire.vbeltcontroller.model.Step;

@EBean(scope = EBean.Scope.Singleton)
public class RouteFacade {

    @RestService
    protected RouteClient routeClient;

    @RootContext
    protected Context context;

    public List<Step> getPoints(String srcLat, String srcLon, String destLat, String destLon) {
        String content = routeClient.getPoints(srcLat, srcLon, destLat, destLon);

        try {
            JSONObject responseJson = new JSONObject(content);
            String status = responseJson.getString("status");

            if (!"OK".equals(status)) {
                return null;
            }

            JSONArray routes = responseJson.getJSONArray("routes");
            List<Step> geoPoints = new ArrayList<>();

            for (int i = 0; i < routes.length(); i++) {
                JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");
                for (int l = 0; l < legs.length(); l++) {
                    JSONArray steps = legs.getJSONObject(l).getJSONArray("steps");
                    for (int s = 0; s < steps.length(); s++) {
                        JSONObject step = steps.getJSONObject(s);
                        JSONObject endLocation = step.getJSONObject("end_location");
                        Double lat = endLocation.getDouble("lat");
                        Double lng = endLocation.getDouble("lng");
                        String maneuver = "center";
                        if (step.has("maneuver")) {
                            maneuver = step.getString("maneuver");
                            if (maneuver != null && maneuver.startsWith("turn-")) {
                                maneuver = maneuver.substring(5);
                            }
                        }
                        geoPoints.add(new Step(lat, lng, maneuver));
                    }
                }
            }
            return geoPoints;
        } catch (JSONException e) {
            Log.e(context.getString(R.string.app_name), "Error loading route points.", e);
        }
        return null;
    }

    public LatLng getCoordinates(String address) {
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                return new LatLng(latitude, longitude);
            }
        } catch (IOException e) {
            Log.e(context.getString(R.string.app_name), "Failed to get the coordinates.", e);
        }
        return null;
    }
}
