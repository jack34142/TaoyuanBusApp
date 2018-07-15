package com.imge.yeezbus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.imge.yeezbus.R;
import java.util.List;

public class DetailListAdapter extends BaseAdapter {
    List<String[]> route_detail;
    LayoutInflater inflater;

    public DetailListAdapter(Context context, List<String[]> route_detail) {
        super();
        this.route_detail = route_detail;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return route_detail.size();
    }

    @Override
    public Object getItem(int position) {
        return route_detail.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        v = inflater.inflate(R.layout.detail_list_item,null);

        TextView comeTime = v.findViewById(R.id.detail_comeTime);
        TextView stopName = v.findViewById(R.id.detail_stopName);

        String[] comeTime_stopName = route_detail.get(position);
        comeTime.setText(comeTime_stopName[0]);
        stopName.setText(comeTime_stopName[1]);

        return v;
    }
}
