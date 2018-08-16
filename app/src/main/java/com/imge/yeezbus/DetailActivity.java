package com.imge.yeezbus;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.imge.yeezbus.adapter.DetailPagerAdapter;
import com.imge.yeezbus.model.MyVolley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Intent intent;
    private PagerAdapter detailPagerAdapter;

    private String route_id;
    private String route_name;
    private StringRequest request;
    private boolean isUpdate;
    private int goBack = 0;
    private int nowPage = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            route_id = bundle.getString("route_id");
            route_name = bundle.getString("route_name");
            setTitle(route_name);
            goBack = bundle.getInt("goBack");
        }

        progressBar = findViewById(R.id.detail_progressBar);
        tabLayout = findViewById(R.id.detail_tabLayout);
        viewPager = findViewById(R.id.detail_viewPager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(goBack);

        isUpdate = false;
        getDetail(route_id);
    }

    public void getDetail(final String route_id){
        String json_url = "http://apidata.tycg.gov.tw/OPD-io/bus4/GetEstimateTime.json?routeIds="+route_id;
        request = new StringRequest(json_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.d("test",response);
                List<List<String[]>> go_back = new ArrayList<>();
                List<String[]> go_detail = new ArrayList<>();
                List<String[]> back_detail = new ArrayList<>();
                try{
                    JSONObject json_obj = new JSONObject(response);
                    JSONArray json_ary = json_obj.getJSONArray(route_id);

                    int len_json_ary = json_ary.length();
                    for (int i=0; i<len_json_ary; i++){
                        JSONObject detail = json_ary.getJSONObject(i);
                        String[] comeTime_stopName = new String[2];

                        String value = detail.getString("Value");
                        if(value.equals("null") || value.equals("-3")){
                            comeTime_stopName[0] = detail.getString("comeTime");
                        }else{
                            comeTime_stopName[0] = detail.getString("Value")+" 分";
                        }

                        comeTime_stopName[1] = detail.getString("StopName");

                        switch (detail.getString("GoBack")){
                            case "1":
                                go_detail.add(comeTime_stopName);
                                break;
                            case "2":
                                back_detail.add(comeTime_stopName);
                                break;
                            default:
                                break;
                        }
                    }

                    go_back.add(go_detail);
                    go_back.add(back_detail);

                    if(nowPage == -1){
                        nowPage = goBack;
                    }else{
                        nowPage = tabLayout.getSelectedTabPosition();
                    }
                    Log.e("test",String.valueOf(goBack));
                    detailPagerAdapter = new DetailPagerAdapter(DetailActivity.this, go_back);
                    viewPager.setAdapter(detailPagerAdapter);
                    progressBar.setVisibility(View.GONE);
                    isUpdate = true;
                    viewPager.setCurrentItem(nowPage);

                }catch (Exception e){
                    Log.e("DetailActivity","getDetail() 解析json失敗");
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DetailActivity","getDetail() 的 Response.ErrorListener()");
                error.printStackTrace();
                MyVolley.getInstance(DetailActivity.this).addToRequestQue(request);
            }
        });

        MyVolley.getInstance(DetailActivity.this).addToRequestQue(request);
    }

    public void updateNow(){
        if(isUpdate == true){
            getDetail(route_id);
            isUpdate = false;
        }else{
            Toast.makeText(DetailActivity.this,"請稍候再試。",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "更新");
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case 0:
                updateNow();
                break;
            default:
                break;
        }
        return true;
    }

}
