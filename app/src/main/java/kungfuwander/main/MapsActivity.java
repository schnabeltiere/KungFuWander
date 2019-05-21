package kungfuwander.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 69;
    private final String TAG = getClass().getName();
    private GoogleMap mMap;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private String bestProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            updateUserLocation();
            // here fetch of friends if wanted
            FireBaseHelper.fetchLoggedInUserHikes(hikes -> {
                hikes.forEach(this::markPathOfHiking);
                hikes.forEach(this::markAreaOfHiking);
            });
        }
    }

    private void markPathOfHiking(Hiking hiking){
        // Instantiates a new Polyline object and adds points to define a rectangle
        PolylineOptions rectOptions = new PolylineOptions()
                .addAll(hiking.locationsAsLatLng())
                .color(Color.RED);

        // Get back the mutable Polyline
        Polyline polyline = mMap.addPolyline(rectOptions);
    }

    private void markAreaOfHiking(Hiking hiking) {
        PolygonOptions rectOptions = new PolygonOptions()
                .addAll(hiking.locationsAsLatLng())
                .fillColor(Color.LTGRAY)
                .strokeColor(Color.DKGRAY);

        // Get back the mutable Polygon
        Polygon polygon = mMap.addPolygon(rectOptions);
    }

    private void updateUserLocation() {
        locationManager = getSystemService(LocationManager.class);
        if (locationManager == null) {
            finish();
        }

        // only for debugging
        infoAboutAllProviders();

        bestProvider = getBestProvider();
        locationListener = new MyLocationListener();
    }

    private String getBestProvider() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String bestProvider = locationManager.getBestProvider(criteria, true);

        Log.d(TAG, "Best bestProvider : " + bestProvider);
        return bestProvider;
    }

    private void infoAboutAllProviders() {
        // get all providers
        List<String> providers = locationManager.getAllProviders();
        for (String name : providers) {
            boolean enabled = locationManager.isProviderEnabled(name);
            Log.d(TAG, "Name: " + name + " --- isProviderEnabled(): " + enabled + "\n");

            if (!enabled) {
                continue;
            }

            LocationProvider lp = locationManager.getProvider(name);
            Log.d(TAG, "   requiresCell(): " + lp.requiresCell() + "\n");
            Log.d(TAG, "   requiresNetwork(): " + lp.requiresNetwork() + "\n");
            Log.d(TAG, "   requiresSatellite(): " + lp.requiresSatellite() + "\n\n");
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if ((requestCode == REQUEST_PERMISSION_ACCESS_FINE_LOCATION) &&
                (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            updateUserLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(bestProvider, 3000, 0, locationListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
