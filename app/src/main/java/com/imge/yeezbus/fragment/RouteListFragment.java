package com.imge.yeezbus.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.imge.yeezbus.CatchUtils.CatchFavorite;
import com.imge.yeezbus.DetailActivity;
import com.imge.yeezbus.R;
import com.imge.yeezbus.adapter.MainListAdapter;
import com.imge.yeezbus.tools.CategoryNameSinTon;
import com.imge.yeezbus.tools.GetLastAdapter;
import com.imge.yeezbus.tools.MyFavorite;
import com.imge.yeezbus.tools.NowPageSinTon;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RouteListFragment extends Fragment {
    private ListView listView;
    private List<String[]> list;
    private MainListAdapter adapter;
    private Context context;
    private int goBack;

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
        context = getContext();

        adapter = new MainListAdapter(context, list);
        GetLastAdapter.setAdapter(adapter, goBack);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(myListener);
        listView.setOnItemLongClickListener(myLongListener);
    }

    public void setData(List<String[]> list, int goBack){
        this.list = list;
        this.goBack = goBack;
    }

    AdapterView.OnItemClickListener myListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String[] id_name = adapter.getId_Name(position);

            Intent detail_intent = new Intent(context, DetailActivity.class);
            detail_intent.putExtra("route_id",id_name[0]);
            detail_intent.putExtra("route_name",id_name[1]);
            detail_intent.putExtra("goBack",goBack);
            startActivity(detail_intent);
        }
    };

    AdapterView.OnItemLongClickListener myLongListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
            final String routeId = String.valueOf(adapter.getItemId(position));
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.inflate(R.menu.main_list_operation);
            popupMenu.show();

            // 如果不是 附近的公車 模式，顯示刪除最愛
            if(!CategoryNameSinTon.getInstence().getCategoryName().equals("附近的公車")){
                Menu menu = popupMenu.getMenu();
//                menu.findItem(R.id.main_add).setVisible(false);
                menu.findItem(R.id.main_delete).setVisible(true);
            }

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

//                    Log.e("test", CategoryNameSinTon.getInstence().getCategoryName());
                    switch (item.getItemId()){
                        case R.id.main_add:
                            new MyFavorite(context, routeId).setFavorite();
                            break;
                        case R.id.main_delete:
                            CatchFavorite.deleteFavoriteItem(context, CategoryNameSinTon.getInstence().getCategoryName(), routeId, NowPageSinTon.getInstence().getNowPage());
                            adapter = GetLastAdapter.getAdapter(goBack);
                            adapter.removeView(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(context,"刪除成功", Toast.LENGTH_SHORT).show();
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
