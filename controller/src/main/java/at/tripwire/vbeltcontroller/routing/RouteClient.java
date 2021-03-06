package at.tripwire.vbeltcontroller.routing;

import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Rest;
import org.springframework.http.converter.StringHttpMessageConverter;

@Rest(rootUrl = "https://maps.googleapis.com/maps/api/directions", converters = {StringHttpMessageConverter.class})
public interface RouteClient {

    @Get("/json?origin={srcLat},{srcLon}&destination={destLat},{destLon}&mode=bicycling")
    String getPoints(@Path String srcLat, @Path String srcLon, @Path String destLat, @Path String destLon);
}
