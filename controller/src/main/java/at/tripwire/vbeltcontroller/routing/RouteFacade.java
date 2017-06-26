package at.tripwire.vbeltcontroller.routing;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.rest.spring.annotations.RestService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import at.tripwire.vbeltcontroller.R;

@EBean(scope = EBean.Scope.Singleton)
public class RouteFacade {

    @RestService
    protected RouteClient routeClient;

    @RootContext
    protected Context context;

    public List<LatLng> getPoints(String srcLat, String srcLon, String destLat, String destLon) {
        String content = routeClient.getPoints(srcLat, srcLon, destLat, destLon);

        try {
            JSONObject responseJson = new JSONObject(content);
            String status = responseJson.getString("status");

            if (!"OK".equals(status)) {
                return null;
            }

            JSONArray routes = responseJson.getJSONArray("routes");
            List<LatLng> geoPoints = new ArrayList<>();

            for (int i = 0; i < routes.length(); i++) {
                JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");
                for (int l = 0; l < legs.length(); l++) {
                    JSONArray steps = legs.getJSONObject(l).getJSONArray("steps");
                    for (int s = 0; s < steps.length(); s++) {
                        JSONObject step = steps.getJSONObject(s);
                        String points = step.getJSONObject("polyline").getString("points");
                        geoPoints.addAll(decodePoly(points));
                    }
                }
            }
            return geoPoints;
        } catch (JSONException e) {
            Log.e(context.getString(R.string.app_name), "Error loading route points.", e);
        }
        return null;
    }

    private static List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }
        return poly;
    }
}
