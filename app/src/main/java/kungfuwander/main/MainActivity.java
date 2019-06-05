package kungfuwander.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import kungfuwander.main.fragments.CompareFragment;
import kungfuwander.main.fragments.CurrentHikeFragment;
import kungfuwander.main.fragments.MapsFragment;
import kungfuwander.main.fragments.ProfileFragment;
import kungfuwander.main.fragments.WeatherFragment;
import kungfuwander.main.weather.Common;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static FirebaseUser currentFirebaseUser;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private CoordinatorLayout coordinatorLayout;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private Fragment fragment_currenthike = new CurrentHikeFragment();
    private Fragment fragment_map = new MapsFragment();
    private Fragment fragment_profil = new ProfileFragment();
    private Fragment fragment_weather = new WeatherFragment();
    private Fragment fragment_statistic = new CompareFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);


        BottomNavigationView nav = findViewById(R.id.navigation);

        nav.setOnNavigationItemSelectedListener(this);



        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        buildLocationRequest();
                        buildLocationCallBack();

                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();
        loadFragment(fragment_currenthike);

    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                super.onLocationResult(locationResult);
                Common.current_lcation = locationResult.getLastLocation();



                Log.d("Location", locationResult.getLastLocation().getLatitude()+ "/" + locationResult.getLastLocation().getLongitude());
            }
        };
    }


    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(50000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setSmallestDisplacement(10.0f);
    }
    private boolean loadFragment(Fragment f) {
        if (f != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
        return true;
        }
      return false;
    }
        @Override
        public boolean onNavigationItemSelected (@NonNull MenuItem menuItem){

            switch (menuItem.getItemId()) {
                case R.id.navigation_currentHike:return loadFragment(fragment_currenthike);
                case R.id.navigation_weather:return loadFragment(fragment_weather);
                case R.id.navigation_map:return loadFragment(fragment_map);
                case R.id.navigation_recentHikes: return loadFragment(fragment_statistic);
                case R.id.navigation_profile:return loadFragment(fragment_profil);
            }
            return loadFragment(fragment_currenthike);
        }
    }


