package android.bignerdranch.com.nerdlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends ListFragment {
    private static final String TAG = "NerdLauncherFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager manager = getActivity().getPackageManager();
        List<ResolveInfo> activities = manager.queryIntentActivities(startupIntent, 0);

        Log.i(TAG, "I've found " + activities.size() + " activities.");

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo lhs, ResolveInfo rhs) {
                return String.CASE_INSENSITIVE_ORDER.compare(
                        lhs.loadLabel(manager).toString(),
                        rhs.loadLabel(manager).toString()
                );
            }
        });

        ArrayAdapter<ResolveInfo> adapter = new ArrayAdapter<ResolveInfo>(
                getActivity(), android.R.layout.activity_list_item, activities) {
            public View getView(int pos, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
                    convertView = inflater.inflate(android.R.layout.activity_list_item, parent, false);
                }
                ResolveInfo info = getItem(pos);
                TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
                textView.setText(info.loadLabel(manager));
                ImageView imageView = (ImageView) convertView.findViewById(android.R.id.icon);
                imageView.setImageDrawable(info.loadIcon(manager));
                return convertView;
            }
        };

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ResolveInfo resolveInfo = (ResolveInfo) l.getAdapter().getItem(position);
        ActivityInfo activityInfo = resolveInfo.activityInfo;

        if (activityInfo == null) return;

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }
}
