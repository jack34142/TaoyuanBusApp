package com.imge.yeezbus.tools;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.imge.yeezbus.CatchUtils.CatchUtils;
import com.imge.yeezbus.MainActivity;
import com.imge.yeezbus.R;
import com.imge.yeezbus.adapter.FavoriteAdapter;
import com.imge.yeezbus.adapter.MainListAdapter;

import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyFavorite {
    Context context;
    private Dialog favorite_dialog;
    private LinearLayout favorite_form;
    private ListView favorite_list;
    private Button favorite_new;
    private Button favorite_cancel;
    private EditText favorite_editText;
    private Button favorite_ok;
    private ListAdapter adapter;
    private Button favorite_near;

    private String route_id;

    public MyFavorite(Context context) {
        super();
        this.context = context;
    }

    public MyFavorite(Context context, String route_id) {
        super();
        this.context = context;
        this.route_id = route_id;
    }

    public void setFavorite(){
        favorite_dialog = new Dialog(context);
        favorite_dialog.setContentView(R.layout.main_favorite);

        favorite_form = favorite_dialog.findViewById(R.id.favorite_form);
        favorite_list = favorite_dialog.findViewById(R.id.favorite_list);
        favorite_new = favorite_dialog.findViewById(R.id.favorite_new);
        favorite_cancel = favorite_dialog.findViewById(R.id.favorite_cancel);
        favorite_editText = favorite_dialog.findViewById(R.id.favorite_editText);
        favorite_ok = favorite_dialog.findViewById(R.id.favorite_ok);
        favorite_near = favorite_dialog.findViewById(R.id.favorite_near);

        if(route_id != null){
            favorite_near.setVisibility(View.GONE);
            TextView title = favorite_dialog.findViewById(R.id.favorite_title);
            title.setText("請選擇一個分類");
        }else{
            favorite_near.setOnClickListener(myNearListener);
        }


        adapter = new FavoriteAdapter(context);
        favorite_list.setAdapter(adapter);
        favorite_list.setOnItemClickListener(myListener);
        favorite_list.setOnItemLongClickListener(myLongListener);

        favorite_new.setOnClickListener(myNewListener);
        favorite_cancel.setOnClickListener(myCancelListener);
        favorite_ok.setOnClickListener(myOkListener);

        favorite_dialog.show();
    }

    AdapterView.OnItemClickListener myListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String category_name = (String) adapter.getItem(position);

            if(route_id != null){
                favorite_dialog.dismiss();
                if(CatchUtils.setFavorite(context, category_name, route_id)){
                    Toast.makeText(context,"加到最愛成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,"已經是最愛了", Toast.LENGTH_SHORT).show();
                }
            }else{
                favorite_dialog.dismiss();

                CategoryNameSinTon.getInstence().setCategoryName(category_name);
                Message msg = new Message();
                msg.what = 5;
                msg.arg1 = 1;
                MainActivity.handler.handleMessage(msg);
            }
        }
    };

    AdapterView.OnItemLongClickListener myLongListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.inflate(R.menu.favorite_operation);

            Menu menu = popupMenu.getMenu();
            if(position == adapter.getCount()-1){
                menu.findItem(R.id.favorite_down).setVisible(false);
            }
            if(position == 0){
                menu.findItem(R.id.favorite_up).setVisible(false);
            }

            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.favorite_up:
                            CatchUtils.setFavoriteSort(context, (String) adapter.getItem(position),1);
                            break;
                        case R.id.favorite_down:
                            CatchUtils.setFavoriteSort(context, (String) adapter.getItem(position),-1);
                            break;
                        case R.id.favorite_delete:
                            CatchUtils.deleteFavoriteSort(context, (String) adapter.getItem(position));
                            break;
                        default:
                            return true;
                    }

                    favorite_dialog.dismiss();
                    setFavorite();
                    return true;
                }
            });
            return true;
        }
    };

    View.OnClickListener myNearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            favorite_dialog.dismiss();
            CategoryNameSinTon.getInstence().setCategoryName("附近的公車");
            Message msg = new Message();
            msg.what = 5;
            msg.arg1 = 0;
            MainActivity.handler.handleMessage(msg);
        }
    };

    View.OnClickListener myNewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(favorite_form.getVisibility() == View.GONE){
                favorite_form.setVisibility(View.VISIBLE);
            }else{
                favorite_form.setVisibility(View.GONE);
            }
        }
    };

    View.OnClickListener myCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            favorite_dialog.cancel();
        }
    };

    View.OnClickListener myOkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String category_name = favorite_editText.getText().toString();
            CatchUtils.setFavoriteSort(context,category_name,0);

            favorite_dialog.dismiss();
            setFavorite();
        }
    };



}
