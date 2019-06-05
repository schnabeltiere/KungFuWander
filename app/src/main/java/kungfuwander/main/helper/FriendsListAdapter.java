package kungfuwander.main.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kungfuwander.main.R;
import kungfuwander.main.beans.User;

public class FriendsListAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private List<User> users;
    private Filter filter;

    public FriendsListAdapter(Context context, int resource, List<User> users) {
        super(context, resource, users);
        this.mContext = context;
        this.users = users;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new AppFilter<>(users);
        return filter;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_view_friends, parent, false);

        User currentUser = users.get(position);

        TextView tvName = listItem.findViewById(R.id.lvUserName);
        TextView tvUid = listItem.findViewById(R.id.lvUserUid);

        tvName.setText(currentUser.getName());
        tvUid.setText(currentUser.getUid());

        return listItem;
    }

    private class AppFilter<T> extends Filter {

        private ArrayList<T> sourceObjects;

        AppFilter(List<T> objects) {
            sourceObjects = new ArrayList<>();
            synchronized (this) {
                sourceObjects.addAll(objects);
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence chars) {
            String filterSeq = chars.toString().toLowerCase();
            FilterResults result = new FilterResults();

            if (filterSeq.length() > 0) { // user typed something
                ArrayList<T> filter = new ArrayList<>();

                for (T object : sourceObjects) {
                    // the filtering itself:
                    if (object.toString().toLowerCase().contains(filterSeq))
                        filter.add(object);
                }
                result.count = filter.size();
                result.values = filter;
            } else { // search is empty
                // add all objects
                synchronized (this) {
                    result.values = sourceObjects;
                    result.count = sourceObjects.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.
            ArrayList<T> filtered = (ArrayList<T>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filtered.size(); i < l; i++)
                add((User) filtered.get(i));
            notifyDataSetInvalidated();
        }
    }
}
