package kungfuwander.main.fragments;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import kungfuwander.main.R;
import kungfuwander.main.beans.Hike;
import kungfuwander.main.helper.FirebaseHelper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getName();
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
        FirebaseHelper.fetchLoggedInUserHikes(hikes -> {
            hikes.forEach(this::markPathOfHiking);
//            hikes.forEach(this::markAreaOfHiking);
        });
    }

    private void markPathOfHiking(Hike hike) {
        // TODO: 22.05.2019 add what date the hike was on click
        // Instantiates a new Polyline object and adds points to define a rectangle
        PolylineOptions rectOptions = new PolylineOptions()
                .addAll(hike.locationsAsLatLng())
                .color(Color.RED);

        // Get back the mutable Polyline
        Polyline polyline = mMap.addPolyline(rectOptions);
    }

    private void markAreaOfHiking(Hike hike) {
        PolygonOptions rectOptions = new PolygonOptions()
                .addAll(hike.locationsAsLatLng())
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
