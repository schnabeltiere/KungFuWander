package kungfuwander.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.stream.Collectors;

public class FriendsList extends AppCompatActivity {

    public static final String UID_COMPARE = "uid_compare";
    private List<String> items;
    private List<UserBean> userBeans;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        listView = findViewById(R.id.listViewUsers);

        FireBaseHelper.fetchAllUsers(this::displayAllUsers);
        listView.setOnItemLongClickListener((parent, view, position, id) -> compareWithUser(position));
    }

    private boolean compareWithUser(int position) {
        UserBean user = userBeans.get(position);

        // Get the layout inflater
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View alertView = inflater.inflate(R.layout.alert_about_user, null);

        // set default price
        TextView tvUserName = alertView.findViewById(R.id.atvUserName);
        TextView tvHikingSince = alertView.findViewById(R.id.atvUserHikingSince);
        TextView tvNHikings = alertView.findViewById(R.id.atvUserNHikings);

        // TODO: 19.05.2019 change this to only display nCounts
        // otherwise waste of queries if we just want number of hikings
        FireBaseHelper.fetchSpecificUserHikes(user.getUid(), hikings -> {
            tvUserName.setText("Challenger: " + user.getName());
            tvHikingSince.setText("Hiking since: " + user.getUid());
            tvNHikings.setText("Here comes nHikings..." + hikings.size());
        });

        new AlertDialog.Builder(this)
                .setView(alertView)
                .setPositiveButton("Compare", (dialog, which) -> {
                    Intent intent = new Intent(this, CompareFriends.class);
                    intent.putExtra(UID_COMPARE, user.getUid());
                    startActivity(intent);
                })
                .show();
        return true;
    }

    private void displayAllUsers(List<UserBean> users) {
        // first attempt was to just read all users from authentification
        // this is not possible - at least i didn't find any way.
        // so just list all users from users database - consider creating database at login for user
        userBeans = users;
        items = users.stream()
                .map(UserBean::toString)
                .collect(Collectors.toList());

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);

        listView.setAdapter(itemsAdapter);
    }

}
