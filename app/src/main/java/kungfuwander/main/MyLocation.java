package kungfuwander.main;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class MyLocation {

    private double latitude;
    private double longitude;
    private String userName = "default_user";
    private long millisSince1970;

    public MyLocation(){
        millisSince1970 = System.currentTimeMillis();
    }
    public MyLocation(Location location){
        millisSince1970 = System.currentTimeMillis();

        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }
    public MyLocation(LatLng latLng){
        millisSince1970 = System.currentTimeMillis();

        latitude = latLng.latitude;
        longitude = latLng.longitude;
    }

    public MyLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getMillisSince1970() {
        return millisSince1970;
    }

    public void setMillisSince1970(long millisSince1970) {
        this.millisSince1970 = millisSince1970;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
}
