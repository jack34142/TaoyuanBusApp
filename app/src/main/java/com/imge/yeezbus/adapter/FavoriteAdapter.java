package com.imge.yeezbus.adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.imge.yeezbus.CatchUtils.CatchUtils;
import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends BaseAdapter {
    Context context;
    List<String> category_name;

    public FavoriteAdapter(Context context) {
        super();
        this.context = context;

        category_name = CatchUtils.getFavoriteSort(context);
        if(category_name.isEmpty()){
            category_name = new ArrayList<>();
            CatchUtils.setFavoriteSort(context,"我的最愛",0);
            category_name = CatchUtils.getFavoriteSort(context);
        }
    }

    @Override
    public int getCount() {
        return category_name.size();
    }

    @Override
    public Object getItem(int position) {
        return category_name.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        TextView tv = new TextView(context);
        tv.setText(category_name.get(position));
        tv.setTextSize(18);
        tv.setPadding(22,20,20,20);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }
}
