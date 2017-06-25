package at.tripwire.vbeltcontroller;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;
import com.google.maps.RoadsApi;
import com.google.maps.model.SnappedPoint;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.rest.spring.annotations.RestService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@EBean(scope = EBean.Scope.Singleton)
public class RouteFacade {

    private static final int PAGE_SIZE_LIMIT = 100;

    private static final int PAGINATION_OVERLAP = 5;

    @RestService
    protected RouteClient routeClient;

    @RootContext
    protected Context context;

    private GeoApiContext geoApiContext;

    @AfterInject
    protected void init() {
        geoApiContext = new GeoApiContext();
        geoApiContext.setApiKey(context.getString(R.string.google_maps_web_services_key));
    }

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
            return snapToRoads(geoPoints);
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

    private List<LatLng> snapToRoads(List<LatLng> geoPoints) {
        List<LatLng> snappedPoints = new ArrayList<>();

        int offset = 0;
        while (offset < geoPoints.size()) {
            if (offset > 0) {
                offset -= PAGINATION_OVERLAP;
            }
            int lowerBound = offset;
            int upperBound = Math.min(offset + PAGE_SIZE_LIMIT, geoPoints.size());

            List<LatLng> subList = geoPoints.subList(lowerBound, upperBound);
            com.google.maps.model.LatLng[] page = new com.google.maps.model.LatLng[upperBound - lowerBound];
            for (int i = 0; i < subList.size(); i++) {
                LatLng latLng = subList.get(i);
                page[i] = new com.google.maps.model.LatLng(latLng.latitude, latLng.longitude);
            }

            SnappedPoint[] points = new SnappedPoint[0];
            try {
                points = RoadsApi.snapToRoads(geoApiContext, page).await();
            } catch (Exception e) {
                Log.e(context.getString(R.string.app_name), "Failed to use the road api.", e);
            }
            boolean passedOverlap = false;
            for (SnappedPoint point : points) {
                if (offset == 0 || point.originalIndex >= PAGINATION_OVERLAP - 1) {
                    passedOverlap = true;
                }
                if (passedOverlap) {
                    snappedPoints.add(new LatLng(point.location.lat, point.location.lng));
                }
            }
            offset = upperBound;
        }
        return snappedPoints;
    }
}
