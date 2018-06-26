package com.imge.yeezbus.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.route_list, container, false);
        listView = v.findViewById(R.id.routeList_listView);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new MainListAdapter(getContext(), list);
        listView.setAdapter(adapter);
    }

    public void setData(List<String[]> list){
        this.list = list;
    }
}
