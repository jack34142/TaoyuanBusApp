package com.imge.yeezbus.tools;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.imge.yeezbus.CatchUtils.CatchFavorite;
import com.imge.yeezbus.MainActivity;
import com.imge.yeezbus.R;
import com.imge.yeezbus.adapter.FavoriteAdapter;
import com.imge.yeezbus.model.MyWindowSize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFavorite {
    Context context;
    private Dialog favorite_dialog;
    private LinearLayout favorite_form;
    private ListView favorite_list;
    private Button favorite_new;
    private Button favorite_cancel;
    private EditText favorite_editText;
    private Button favorite_ok;
    private FavoriteAdapter adapter;
    private Button favorite_near;
    private RadioGroup favorite_radio;
    private RadioButton radio_go, radio_back, radio_both;

    private String route_id;
    private String newName;
    private int nowPage = NowPageSinTon.getInstence().getNowPage();

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
        favorite_radio = favorite_dialog.findViewById(R.id.favorite_radio);
        radio_go = favorite_dialog.findViewById(R.id.radio_go);
        radio_back = favorite_dialog.findViewById(R.id.radio_back);
        radio_both = favorite_dialog.findViewById(R.id.radio_both);

        if(route_id != null){
            favorite_near.setVisibility(View.GONE);
            TextView title = favorite_dialog.findViewById(R.id.favorite_title);
            title.setText("請選擇一個分類");
            radio_both.setVisibility(View.VISIBLE);
        }else{
            favorite_near.setOnClickListener(myNearListener);
        }

        switch (nowPage){
            case 0:
                radio_go.setChecked(true);
                break;
            case 1:
                radio_back.setChecked(true);
                break;
        }

        adapter = new FavoriteAdapter(context);
        favorite_list.setAdapter(adapter);
        favorite_list.setOnItemClickListener(myListener);
        favorite_list.setOnItemLongClickListener(myLongListener);

        favorite_new.setOnClickListener(myNewListener);
        favorite_cancel.setOnClickListener(myCancelListener);
        favorite_ok.setOnClickListener(myOkListener);

        favorite_radio.setOnCheckedChangeListener(myRadioListener);
        favorite_dialog.show();
    }

    RadioGroup.OnCheckedChangeListener myRadioListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch(checkedId){
                case R.id.radio_go:
                    nowPage = 0;
                    break;
                case R.id.radio_back:
                    nowPage = 1;
                    break;
                default:
                    break;
            }
        }
    };

    AdapterView.OnItemClickListener myListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String category_name = (String) adapter.getItem(position);

            if(route_id != null){
                Map<String, List<Boolean>> routeId_goBack = new HashMap<>();
                List<Boolean> goBack_list = new ArrayList<>();

                if(radio_go.isChecked()){
                    goBack_list.add(true);
                    goBack_list.add(false);
                }else if(radio_back.isChecked()){
                    goBack_list.add(false);
                    goBack_list.add(true);
                }else{
                    goBack_list.add(true);
                    goBack_list.add(true);
                }
                routeId_goBack.put(route_id, goBack_list);

                favorite_dialog.dismiss();

                if(CatchFavorite.setFavorite(context, category_name, routeId_goBack)){
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
                NowPageSinTon.getInstence().setNowPage(nowPage);
                MainActivity.handler.handleMessage(msg);
            }
        }
    };

    AdapterView.OnItemLongClickListener myLongListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
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
                    if(item.getItemId() == R.id.favorite_rename){
                        getNewName(view);
                        return true;
                    }

                    switch (item.getItemId()){
                        case R.id.favorite_up:
                            CatchFavorite.setFavoriteSort(context, (String) adapter.getItem(position),1);
                            break;
                        case R.id.favorite_down:
                            CatchFavorite.setFavoriteSort(context, (String) adapter.getItem(position),-1);
                            break;
                        case R.id.favorite_delete:
                            CatchFavorite.deleteFavorite(context, (String) adapter.getItem(position));
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
            NowPageSinTon.getInstence().setNowPage(nowPage);
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
            category_name = category_name.trim();
            if(category_name.equals("")){
                Toast.makeText(context,"請輸入內容", Toast.LENGTH_SHORT).show();
                return;
            }

            CatchFavorite.setFavoriteSort(context,category_name,0);

            favorite_dialog.dismiss();
            setFavorite();
        }
    };

    public void getNewName(View view){
        favorite_dialog.dismiss();

//        String old_name = ((TextView)view.findViewById(R.id.favorite_item)).getText().toString();
        final String old_name = ((TextView)view).getText().toString();

        final Dialog rename_dialog = new Dialog(context);
        rename_dialog.setContentView(R.layout.main_favorite_rename);
        MyWindowSize myWindowSize = new MyWindowSize(context);
        WindowManager.LayoutParams p = myWindowSize.setSize(-1,0.85);
        rename_dialog.getWindow().setAttributes(p);
        TextView rename_title = rename_dialog.findViewById(R.id.rename_title);
        rename_title.setText(old_name);
        rename_dialog.show();

        Button rename_ok = rename_dialog.findViewById(R.id.rename_ok);
        Button rename_cancel = rename_dialog.findViewById(R.id.rename_cancel);
        final EditText rename_editText = rename_dialog.findViewById(R.id.rename_editText);

        rename_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rename_dialog.dismiss();
                setFavorite();
            }
        });

        rename_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newName = rename_editText.getText().toString();

                newName = newName.trim();
                if(newName.equals("")){
                    Toast.makeText(context,"請輸入內容", Toast.LENGTH_SHORT).show();
                    return;
                }

                CatchFavorite.renameFavorite(context, old_name, newName);
                rename_dialog.dismiss();
                setFavorite();
            }
        });
    }
}
