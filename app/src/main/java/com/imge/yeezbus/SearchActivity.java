package com.imge.yeezbus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.imge.yeezbus.adapter.SearchListAdapter;
import com.imge.yeezbus.tools.KeyBoard;
import com.imge.yeezbus.tools.MyFavorite;

public class SearchActivity extends AppCompatActivity {
    private EditText editText;
    private Button search_ok;
    private RadioGroup radioGroup;
    private RadioButton radio_routeName;
    private RadioButton radio_stopName;
    private SearchListAdapter adapter;
    private ListView search_list;
    private TextView title_stopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("搜尋");

        editText = findViewById(R.id.search_editText);
        search_ok = findViewById(R.id.search_ok);
        radioGroup = findViewById(R.id.search_radioGroup);
        radio_routeName = findViewById(R.id.radio_routeName);
        radio_stopName = findViewById(R.id.radio_stopName);
        search_list = findViewById(R.id.search_listView);
        title_stopName = findViewById(R.id.search_title_stopName);

        radio_routeName.setChecked(true);
        search_ok.setOnClickListener(myOkListener);
        search_list.setOnItemClickListener(myListener);
        search_list.setOnItemLongClickListener(myLongListener);
    }

    AdapterView.OnItemClickListener myListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String[] id_name = adapter.getId_Name(position);

            Intent detail_intent = new Intent(SearchActivity.this, DetailActivity.class);
            detail_intent.putExtra("route_id",id_name[0]);
            detail_intent.putExtra("route_name",id_name[1]);
            startActivity(detail_intent);
        }
    };

    AdapterView.OnItemLongClickListener myLongListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
            PopupMenu popupMenu = new PopupMenu(SearchActivity.this, view);
            popupMenu.inflate(R.menu.main_list_operation);
            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String routeId = String.valueOf(adapter.getItemId(position));
//                    Log.e("test", CategoryNameSinTon.getInstence().getCategoryName());
                    switch (item.getItemId()){
                        case R.id.main_add:
                            new MyFavorite(SearchActivity.this, routeId).setFavorite();
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

    View.OnClickListener myOkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            KeyBoard.hideKeyBoard(SearchActivity.this);

            String data = editText.getText().toString();
            data = data.trim();
            if(data.equals("")){
                Toast.makeText(SearchActivity.this,"請輸入內容", Toast.LENGTH_SHORT).show();
                return;
            }

            switch(radioGroup.getCheckedRadioButtonId()){
                case R.id.radio_routeName:
                    title_stopName.setVisibility(View.GONE);
                    adapter = new SearchListAdapter(SearchActivity.this, data, 1);
                    break;
                case R.id.radio_stopName:
                    title_stopName.setVisibility(View.VISIBLE);
                    adapter = new SearchListAdapter(SearchActivity.this, data, 2);
                    break;
                default:
                    break;
            }
            search_list.setAdapter(adapter);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }



}
