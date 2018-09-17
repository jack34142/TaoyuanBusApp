package com.imge.yeezbus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.imge.yeezbus.CatchUtils.CatchBusDetails;
import com.imge.yeezbus.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<String> routeId_list;
    List<String> routeName_list;
    List<String> stopName_list;

    public SearchListAdapter(Context context, String data, int type) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);

        switch(type){
            case 1:
                search_routeName(data);
                break;
            case 2:
                search_stopName(data);
                break;
            default:
                break;
        }
    }

    @Override
    public int getCount() {
        return routeId_list.size();
    }

    @Override
    public Object getItem(int position) {
        return routeName_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.parseInt(routeId_list.get(position));
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        v = inflater.inflate(R.layout.search_list_item, null);
        TextView tv_routeName = v.findViewById(R.id.search_routeName);
        tv_routeName.setText(routeName_list.get(position));

        if(stopName_list!=null){
            TextView tv_stopName = v.findViewById(R.id.search_stopName);
            tv_stopName.setVisibility(View.VISIBLE);
            tv_stopName.setText(stopName_list.get(position));
        }
        return v;
    }


    public String[] getId_Name(int position){
        String[] id_name = new String[2];
        id_name[0] = routeId_list.get(position);
        id_name[1] = routeName_list.get(position);
        return id_name;
    }

    public void search_routeName(String data){
        Map<String, String> routeNameZh = CatchBusDetails.getRouteNameZh(context);

        routeId_list = new ArrayList<>();
        routeName_list = new ArrayList<>();

        for(String routeId : routeNameZh.keySet()){
            if(routeNameZh.get(routeId).indexOf(data) >= 0){
//                Log.e("test",routeNameZh.get(routeId));
                routeId_list.add(routeId);
                routeName_list.add(routeNameZh.get(routeId));
            }
        }

        if(routeId_list.isEmpty()){
            Toast.makeText(context, "查無資料", Toast.LENGTH_SHORT).show();
        }
    }

    public void search_stopName(String data){
        Map<String, List> stopDetail = CatchBusDetails.getStopDetail(context);

        stopName_list = new ArrayList<>();
        routeId_list = new ArrayList<>();
        routeName_list = new ArrayList<>();

        for(String stopId : stopDetail.keySet()){
            String stopNameZh = (String) stopDetail.get(stopId).get(1);
            if(stopNameZh.indexOf(data) >= 0){
//                Log.e("test",stopDetail.get(stopId).get(0).toString());
                for(String routeId : (Set<String>)stopDetail.get(stopId).get(0)){
                    if (!routeId_list.contains(routeId)){
                        stopName_list.add(stopNameZh);
                        routeId_list.add(routeId);
                    }
                }
            }
        }

        if(routeId_list.isEmpty()){
            Toast.makeText(context, "查無資料", Toast.LENGTH_SHORT).show();
        }else{
            Map<String, String> routeNameZh = CatchBusDetails.getRouteNameZh(context);
            for(String routeId : routeId_list){
                routeName_list.add(routeNameZh.get(routeId));
            }
        }
    }

}
