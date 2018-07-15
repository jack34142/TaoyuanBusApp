package com.imge.yeezbus.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.imge.yeezbus.DetailActivity;
import com.imge.yeezbus.R;
import com.imge.yeezbus.adapter.MainListAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RouteListFragment extends Fragment {
    private ListView listView;
    private List<String[]> list;
    private MainListAdapter adapter;

    public RouteListFragment() {
        // Required empty public constructor
    }

    /*設定view元件*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.route_list, container, false);
        listView = v.findViewById(R.id.routeList_listView);
        return v;
    }

    /*元件賦值，與其他操作*/
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new MainListAdapter(getContext(), list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(myListener);
    }

    public void setData(List<String[]> list){
        this.list = list;
    }

    AdapterView.OnItemClickListener myListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String[] id_name = adapter.getId_Name(position);

            Intent detail_intent = new Intent(getContext(), DetailActivity.class);
            detail_intent.putExtra("route_id",id_name[0]);
            detail_intent.putExtra("route_name",id_name[1]);
            startActivity(detail_intent);
        }
    };


}
