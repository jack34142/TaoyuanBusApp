package com.imge.yeezbus.adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.imge.yeezbus.CatchUtils.CatchUtils;
import com.imge.yeezbus.R;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends BaseAdapter {
    Context context;
    List<String> category_name;
    LayoutInflater inflater;

    public FavoriteAdapter(Context context) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);

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
//        v = inflater.inflate(R.layout.main_favorite_item, null);

//        TextView tv = v.findViewById(R.id.favorite_item);
//        tv.setText(category_name.get(position));

        TextView tv = new TextView(context);
        tv.setText(category_name.get(position));
        tv.setTextSize(17);
        tv.setPadding(22,18,20,18);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

}
