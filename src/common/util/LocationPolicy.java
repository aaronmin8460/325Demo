package common.util;

public class LocationPolicy {

    private double latitude;

    private double longitude;

    private double allowedRadius;

    public LocationPolicy(double latitude, double longitude, double allowedRadius) {

        this.latitude = latitude;

        this.longitude = longitude;

        this.allowedRadius = allowedRadius;

    }

    public boolean isWithinRange() {

        // TODO: Implement range check logic

        return false;

    }

    // getters and setters

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getAllowedRadius() { return allowedRadius; }

    public void setAllowedRadius(double allowedRadius) { this.allowedRadius = allowedRadius; }

}