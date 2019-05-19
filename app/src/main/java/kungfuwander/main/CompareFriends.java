package kungfuwander.main;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.stream.Collectors;

public class CompareFriends extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_friends);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        FireBaseHelper.fetchAllUsers(this::displayAllUsers);
    }

    private void displayAllUsers(List<UserBean> users) {
        // first attempt was to just read all users from authentification
        // this is not possible - at least i didn't find any way.
        // so just list all users from users database - consider creating database at login for user
        List<String> items = users.stream()
                .map(UserBean::toString)
                .collect(Collectors.toList());

        ListView view = findViewById(R.id.listViewUsers);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);

        view.setAdapter(itemsAdapter);
    }

}
