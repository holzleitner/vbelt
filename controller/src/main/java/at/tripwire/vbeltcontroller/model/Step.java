package at.tripwire.vbeltcontroller.model;

public class Step {

    private double latitude;

    private double longitude;

    private String maneuver;

    public Step(double latitude, double longitude, String maneuver) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.maneuver = maneuver;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }
}
