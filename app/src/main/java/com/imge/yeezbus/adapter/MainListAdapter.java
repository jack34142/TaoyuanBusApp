package com.imge.yeezbus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.imge.yeezbus.CatchUtils.CatchUtils;
import com.imge.yeezbus.R;
import com.imge.yeezbus.comparator.ListIntegerComparator;
import com.imge.yeezbus.comparator.ListStringComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainListAdapter extends BaseAdapter{
    LayoutInflater inflater;
    private Map<String,String> routeNameZh_map;
    private List<String[]> list;

    public MainListAdapter(Context context, List<String[]> list) {
        super();
        inflater = LayoutInflater.from(context);
        routeNameZh_map = CatchUtils.getRouteNameZh(context);

        List<Integer> bus_one, bus_two, bus_three, bus_four;
        List<Integer> bus_two_val;
        List<String> bus_three_val;

        bus_one = new ArrayList<>();
        bus_two = new ArrayList<>();
        bus_three = new ArrayList<>();
        bus_four = new ArrayList<>();
        bus_two_val = new ArrayList<>();
        bus_three_val = new ArrayList<>();

        int len_list = list.size();
        for (int i=0; i<len_list; i++){
            String value = list.get(i)[1];
            if(value == null || value.equals("")){
                continue;
            }else if(value.equals("-3")){
                bus_four.add(i);
            }else if(value.length() == 5){
                bus_three.add(i);
                bus_three_val.add(value);
            }else if(value.equals("即將進站")){
                bus_one.add(i);
            }else{
                int value_int = Integer.parseInt(value);
                bus_two.add(i);
                bus_two_val.add(value_int);
            }
        }

        ListIntegerComparator comparator_two = new ListIntegerComparator(bus_two_val);
        List<Integer> indexes_two = comparator_two.createIndexArray();
        Collections.sort(indexes_two,comparator_two);

        ListStringComparator comparator_three = new ListStringComparator(bus_three_val);
        List<Integer> indexes_three = comparator_three.createIndexArray();
        Collections.sort(indexes_three, comparator_three);

        List<String[]> list_sort = new ArrayList<>();
        for(int i : bus_one){
            list_sort.add(list.get(i));
        }

        for(int i : indexes_two){
            list_sort.add(list.get(bus_two.get(i)));
        }

        for(int i : indexes_three){
            list_sort.add(list.get(bus_three.get(i)));
        }

        for(int i : bus_four){
            list_sort.add(list.get(i));
        }

        this.list = list_sort;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.parseInt(list.get(position)[0]);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        v = inflater.inflate(R.layout.route_list_item, null);
        TextView value = v.findViewById(R.id.mainList_value);
        TextView nameZh = v.findViewById(R.id.mainList_nameZh);
        TextView nextStop = v.findViewById(R.id.mainList_nextStop);

        String string_value = list.get(position)[1];
        String string_nextStop = list.get(position)[2];

        if(string_value.equals("-3")){
            value.setText("末班已過");
            nextStop.setText("");
        }else if(string_value.length() >= 4){
            value.setText(string_value);

            if(string_nextStop == null){
                nextStop.setText("尚未發車");
            }else{
                nextStop.setText(string_nextStop);
            }
        }else{
            value.setText(string_value+" 分");
            nextStop.setText(string_nextStop);
        }

        nameZh.setText(routeNameZh_map.get(list.get(position)[0]));

        return v;
    }
}
