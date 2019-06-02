package kungfuwander.main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static FirebaseUser currentFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);

        BottomNavigationView nav = findViewById(R.id.navigation);
        nav.setOnNavigationItemSelectedListener(this);
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
            Fragment fragment = null;

            switch (menuItem.getItemId()) {
                case R.id.navigation_weather:
                    fragment = new Fragment_Weather();
                    break;

                case R.id.navigation_map:
                    startActivity(new Intent(this, MapsActivity.class));
                    break;

                case R.id.navigation_currentHike:
                    fragment = new Fragment_CurrentHike();
                    break;

                case R.id.navigation_recentHikes:
                    fragment = new Fragment_RecentHikes();
                    break;

                case R.id.navigation_profile:
                    fragment = new Fragment_Profile();
                    break;
            }
            return loadFragment(fragment);
        }
    }


