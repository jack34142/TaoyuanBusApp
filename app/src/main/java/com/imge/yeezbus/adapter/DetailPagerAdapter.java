package com.imge.yeezbus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.imge.yeezbus.R;
import java.util.List;

public class DetailPagerAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;
    List<List<String[]>> list;

    public DetailPagerAdapter(Context context, List<List<String[]>> list) {
        super();
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View v = inflater.inflate(R.layout.route_list,container,false);

        ListView listView = v.findViewById(R.id.routeList_listView);
        ListAdapter adapter = new DetailListAdapter(context,list.get(position));
        listView.setAdapter(adapter);
        container.addView(v);

        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "去程";
            case 1:
                return "返程";
            default:
                return "";
        }
    }
}
