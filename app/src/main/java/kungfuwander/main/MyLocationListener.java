package kungfuwander.main;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {
    private final String TAG = getClass().getName();

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged()\n");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled()\n");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled()\n");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "\nonLocationChanged()\n");
        if (location != null) {
//                    moveMapToLocation(location);
            // TODO: 09.04.2019 userName from login
            MyLocation myLocation = new MyLocation(location);
            // TODO: 16.05.2019 change this to getDisplayName
            myLocation.setUserName(MainActivity.currentFirebaseUser.getEmail());
            Log.d(TAG, "The E-Mail of the User: " + MainActivity.currentFirebaseUser.getEmail());

            // TODO: 16.05.2019 move map here
            // add to database
            Log.d(TAG, "User location: " + location.toString());
        }
    }
}
