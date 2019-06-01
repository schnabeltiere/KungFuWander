package kungfuwander.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapterListViewFriends extends ArrayAdapter<String> {
    private List<String> items;
    private LayoutInflater inflater;
    private int layoutId;

    public AdapterListViewFriends(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.items = items;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = resource;
    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View v = (view == null) ? inflater.inflate(R.layout.adapter_friends_listview,null) : view;
        ((TextView)v.findViewById(R.id.textViewFriendName)).setText("TEST");
        ((TextView)v.findViewById(R.id.textViewFriendEMail)).setText("TEST");

        return v;
    }
}
