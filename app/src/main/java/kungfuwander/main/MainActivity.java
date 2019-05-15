package kungfuwander.main;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.app.Activity;
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
                case R.id.navigation_home:
                    fragment = new ProfilFragment();
                    break;

                case R.id.navigation_dashboard:
                    fragment = new FriendsFragment();
                    break;

                case R.id.navigation_notifications:
                    fragment = new HomeFragment();
            }
            return loadFragment(fragment);
        }
    }


