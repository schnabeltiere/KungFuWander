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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;

import im.delight.android.location.SimpleLocation;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = getClass().getName();
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // here fetch of friends if wanted
        FireBaseHelper.fetchLoggedInUserHikes(hikes -> {
            hikes.forEach(this::markPathOfHiking);
//            hikes.forEach(this::markAreaOfHiking);
        });
    }

    private void markPathOfHiking(Hiking hiking) {
        // TODO: 22.05.2019 add what date the hiking was on click
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

        // TODO: 22.05.2019 replace this with actual position or default if unknown
        // the current position is zagreb, croatia
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(45.815399,15.966568)));
    }

}
