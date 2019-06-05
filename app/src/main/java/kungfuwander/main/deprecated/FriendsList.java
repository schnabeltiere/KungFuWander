package kungfuwander.main.deprecated;

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
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import kungfuwander.main.R;
import kungfuwander.main.beans.User;
import kungfuwander.main.deprecated.CompareFriends;
import kungfuwander.main.helper.FirebaseHelper;
import kungfuwander.main.helper.FriendsListAdapter;

public class FriendsList extends AppCompatActivity {

    public static final String UID_COMPARE = "uid_compare";
    private FriendsListAdapter adapter;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        FirebaseHelper.fetchAllUsers(this::setUpListView);
    }

    private boolean compareWithUser(int position) {
        User user = users.get(position);

        // Get the layout inflater
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View alertView = inflater.inflate(R.layout.alert_about_user, null);

        // set default price
        TextView tvUserName = alertView.findViewById(R.id.atvUserName);
        TextView tvHikingSince = alertView.findViewById(R.id.atvUserHikingSince);
        TextView tvNHikes = alertView.findViewById(R.id.atvUserNHikings);

        // TODO: 19.05.2019 change this to only display nCounts
        // otherwise waste of queries if we just want number of hikes
        FirebaseHelper.fetchSpecificUserHikes(user.getUid(), hikes -> {
            tvUserName.setText("Challenger: " + user.getName());
            tvHikingSince.setText("Hike since: " + user.getUid());
            tvNHikes.setText("Here comes nHikings..." + hikes.size());
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

    private void setUpListView(List<User> users) {
        // first attempt was to just read all users from authentification database
        // this is not possible - at least i didn't find any way.
        // so just list all users from users database - consider creating database at login for user
        ListView listView = findViewById(R.id.listViewUsers);
        this.users = users;

        adapter = new FriendsListAdapter(this, 0, users);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> compareWithUser(position));
        listView.setOnItemLongClickListener((parent, view, position, id) -> showAddUserDialog(position));
        // do something with long click listener
    }

    private boolean showAddUserDialog(int position) {
        // TODO: 22.05.2019 can't add himself
        User user = users.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("Dou you want to add him as your friend?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    FirebaseHelper.addFriendToLoggedInUser(user);
                    FirebaseHelper.addLoggedInUserAsFriend(user);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User wants everything back to normal - do nothing
                });

        builder.create().show();
        return true;
    }

}
