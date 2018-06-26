package com.imge.yeezbus;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.imge.yeezbus.CatchUtils.CatchUtils;
import com.imge.yeezbus.adapter.MainFragmentPagetAdapter;
import com.imge.yeezbus.model.MyWindowSize;
import com.imge.yeezbus.tools.MyBusTools;
import com.imge.yeezbus.tools.MyGpsTools;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    MyBusTools myBusTools;
    public static Handler handler;
    FragmentManager fragmentManager;
    MainFragmentPagetAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    Map<String,String> routeNameZh_map;
    Map<String,List> stopDetail_map;
    List<String> stop_distance_sort;        // get( index ) = 附近的站點，index 越小，站點的距離越近
    Map<String,Set<String>> nearStop_map;       // get( 站名 ) = 經過此站的 routeId_set
    List<List<String[]>> goBack_list;
    int count = 0;
    private TextView tv_count;
    private Dialog dialog_wait;
    public static TextView dialog_wait_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setHandler();
        setDialog();
        myBusTools = new MyBusTools(MainActivity.this);

        tv_count = findViewById(R.id.main_count);

        fragmentManager = getSupportFragmentManager();
        viewPager = findViewById(R.id.main_viewPager);
        tabLayout = findViewById(R.id.main_tabLayout);
        tabLayout.setupWithViewPager(viewPager);        // 用來同步 viewPager 與 tabLayout
        getRouteNameZh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        count = 2*60*60;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        count = 0;
    }

    public void setHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what){
                    case 0:     // 取得路線中文名後，取得站點資料
                        dialog_wait.dismiss();
                        if(routeNameZh_map.isEmpty()){
                            routeNameZh_map = CatchUtils.getRouteNameZh(MainActivity.this);
                        }

                        getStopDetails();
                        break;
                    case 1:     // 取得站點資料後，找到附近的站點
                        dialog_wait.dismiss();
                        if(stopDetail_map == null){
                            stopDetail_map = CatchUtils.getStopDetail(MainActivity.this);
                        }

                        new MyGpsTools(MainActivity.this);      // 設定 gps 的 listener
                        getNearStop();      // 取得附近站點，並取得經過此站的路線資料
                        break;
                    case 2:
                        dialog_wait.dismiss();

                        int nowPage = tabLayout.getSelectedTabPosition();
                        goBack_list = (List<List<String[]>>)msg.obj;

                        adapter = new MainFragmentPagetAdapter(fragmentManager, goBack_list);
                        viewPager.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        // 修正Adapter更新後，自動跳轉至第一頁的問題
                        viewPager.setCurrentItem(nowPage);
                        count = 20;
                        break;
                    case 3:
                        setCount();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public void setDialog(){
        dialog_wait = new Dialog(MainActivity.this);
        dialog_wait.setContentView(R.layout.dialog_wait);
        dialog_wait.setCancelable(false);

        MyWindowSize myWindowSize = new MyWindowSize(MainActivity.this);
        WindowManager.LayoutParams p = myWindowSize.setSize(-1,0.85);
        dialog_wait.getWindow().setAttributes(p);

        dialog_wait_tv = dialog_wait.findViewById(R.id.dialog_wait_tv);

//        dialog_wait.show();
    }

    public void getRouteNameZh(){
        routeNameZh_map = CatchUtils.getRouteNameZh(MainActivity.this);
        if(routeNameZh_map.isEmpty()){
            dialog_wait_tv.setText("正在下載路線中文名稱");
            dialog_wait.show();
//            Log.d("true", routeNameZh_map.toString());

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    myBusTools.getJson_RouteNameZh();
                }
            }.start();
        }else{
//            Log.d("false", routeNameZh.toString());
            handler.sendEmptyMessage(0);
        }
    }

    public void getStopDetails(){
        stopDetail_map = CatchUtils.getStopDetail(MainActivity.this);
        if(stopDetail_map == null){
            dialog_wait_tv.setText("正在下載各站點的資料");
            dialog_wait.show();
//            Log.d("true", "null");

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    myBusTools.getJson_stopDetail(routeNameZh_map.keySet());
                }
            }.start();
        }else{
            handler.sendEmptyMessage(1);
        }
    }

    public void getNearStop(){
        dialog_wait_tv.setText("正在取得附近路線資料");
        dialog_wait.show();

        new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    try{
                        List list = myBusTools.getNearStop(stopDetail_map);
                        stop_distance_sort = (List<String>) list.get(0);
                        nearStop_map = (Map<String,Set<String>>) list.get(1);

                        while(count > 0){
                            // 設定倒數計時
                            handler.sendEmptyMessage(3);
                            Thread.sleep(1000);
                        }

                        // 取得經過此站的路線資料
                        myBusTools.getJson_comeTime(nearStop_map, stop_distance_sort);

                        while(count == 0){
                            handler.sendEmptyMessage(3);
                            Thread.sleep(500);
                        }

                    }catch (Exception e){}
                }
            }
        }.start();
    }

    public void setCount(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (count == 0){
                    tv_count.setText("更新中 ...");
                }else if(count <= 20){
                    tv_count.setText("更新倒數 "+count+" 秒");
                    count--;
                }else{}
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // requestCode == 1 是 MyGpsTools 設置的，用來檢查 Gps 權限
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dialog_wait.dismiss();
                recreate();
                Toast.makeText(MainActivity.this, "gps權限已取得", Toast.LENGTH_SHORT).show();
            } else { // if permission is not granted
                Toast.makeText(MainActivity.this, "若不提供GPS權限，將無法使用本產品。", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);     // gps 設置頁面
//                activity.startActivityForResult(intent,0);         //此为设置完成后返回到获取界面
            }
        }
    }

}
