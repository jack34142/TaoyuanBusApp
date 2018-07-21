package com.imge.yeezbus.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.imge.yeezbus.CatchUtils.CatchUtils;
import com.imge.yeezbus.DetailActivity;
import com.imge.yeezbus.MainActivity;
import com.imge.yeezbus.R;
import com.imge.yeezbus.adapter.MainListAdapter;
import com.imge.yeezbus.tools.CategoryNameSinTon;
import com.imge.yeezbus.tools.MyFavorite;

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
        listView.setOnItemLongClickListener(myLongListener);
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

    AdapterView.OnItemLongClickListener myLongListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            popupMenu.inflate(R.menu.main_list_operation);
            popupMenu.show();

            // 如果不是 附近的公車 模式，不要顯示加到最愛，並顯示刪除最愛
            if(!CategoryNameSinTon.getInstence().getCategoryName().equals("附近的公車")){
                Menu menu = popupMenu.getMenu();
                menu.findItem(R.id.main_add).setVisible(false);
                menu.findItem(R.id.main_delete).setVisible(true);
            }

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String routeId = String.valueOf(adapter.getItemId(position));
                    Log.e("test", CategoryNameSinTon.getInstence().getCategoryName());
                    switch (item.getItemId()){
                        case R.id.main_add:
                            new MyFavorite(getContext(), routeId).setFavorite();
                            break;
                        case R.id.main_delete:
                            CatchUtils.deleteFavoriteItem(getContext(), CategoryNameSinTon.getInstence().getCategoryName(), routeId);
                            adapter.removeView(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getContext(),"刪除成功", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });

            return true;
        }
    };


}
