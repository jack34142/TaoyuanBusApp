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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.imge.yeezbus.CatchUtils.CatchUtils;
import com.imge.yeezbus.adapter.MainFragmentPagetAdapter;
import com.imge.yeezbus.model.MyWindowSize;
import com.imge.yeezbus.tools.CategoryNameSinTon;
import com.imge.yeezbus.tools.CountDown;
import com.imge.yeezbus.tools.MyBusTools;
import com.imge.yeezbus.tools.MyFavorite;
import com.imge.yeezbus.tools.MyGpsTools;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView tv_count;
    private Dialog dialog_wait;
    public static TextView dialog_wait_tv;
    private ProgressBar wait;

    public static Handler handler;
    private MyBusTools myBusTools;
    private CountDown countDown;

    private FragmentManager fragmentManager;
    private MainFragmentPagetAdapter adapter;

    Map<String,String> routeNameZh_map;
    Map<String,List> stopDetail_map;
    List<String> stop_distance_sort;        // get( index ) = 附近的站點，index 越小，站點的距離越近
    Map<String,Set<String>> nearStop_map;       // get( 站名 ) = 經過此站的 routeId_set
    List<List<String[]>> goBack_list;
    List<String> routeId_list;
    private int mode = 0;       //0 = 附近站點, 1 = 最愛的站點
    private long firstTime=0;       //记录用户首次点击返回键的时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("附近的公車");

        setHandler();
        setDialog();
        myBusTools = new MyBusTools(MainActivity.this);

        tv_count = findViewById(R.id.main_count);
        wait = findViewById(R.id.main_progressBar);

        fragmentManager = getSupportFragmentManager();
        viewPager = findViewById(R.id.main_viewPager);
        tabLayout = findViewById(R.id.main_tabLayout);
        tabLayout.setupWithViewPager(viewPager);        // 用來同步 viewPager 與 tabLayout

        getRouteNameZh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(countDown != null){
            countDown.setPause(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(countDown != null){
            countDown.setPause(false);
        }
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

                        // 消除切換模式的時出現的 progressBar
                        wait.setVisibility(View.GONE);
                        // 切換模式時修改 標題
                        setTitle(CategoryNameSinTon.getInstence().getCategoryName());

                        countDown.resetCount();
                        break;
                    case 3:
                        setCount(msg.arg1);

                        if (msg.arg1 == 0){
                            download_info();
                        }
                        break;
                    case 4:
                        dialog_wait.dismiss();
                        viewPager.removeAllViews();
                        countDown.resetCount();

                        // 消除切換模式的時出現的 progressBar
                        wait.setVisibility(View.GONE);
                        // 切換模式時修改 標題
                        setTitle(CategoryNameSinTon.getInstence().getCategoryName());

                        switch (mode){
                            case 0:
                                Toast.makeText(MainActivity.this,"找不到附近站點，或目前不處於桃園地區。",Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(MainActivity.this,"你還沒有加入最愛的站點哦。",Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                        break;
                    case 5:
                        // 判斷當前模式
                        if(getTitle().equals(CategoryNameSinTon.getInstence().getCategoryName())){
                            Toast.makeText(MainActivity.this,"已為此模式。",Toast.LENGTH_SHORT).show();
                        }else{
                            mode = msg.arg1;
                            wait.setVisibility(View.VISIBLE);
                            countDown.updateNow();
                        }
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

        countDown = new CountDown();
        countDown.start();
    }

    public void setCount(final int count){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (count == 0){
                    tv_count.setText("更新中 ...");
                }else{
                    tv_count.setText("更新倒數 "+count+" 秒");
                }
            }
        });
    }

    public void download_info(){
        //取得附近站點
        new Thread(){
            @Override
            public void run() {
                super.run();
                List list = myBusTools.getNearStop(stopDetail_map);
                stop_distance_sort = (List<String>) list.get(0);

                Set<String> routeId_set;
                if (mode == 0){
                    // 整合 routeId_map 變成 set
                    nearStop_map = (Map<String, Set<String>>) list.get(1);
                    routeId_set = myBusTools.deal_routeId(nearStop_map);
                }else {
                    routeId_list = CatchUtils.getFavorite(MainActivity.this, CategoryNameSinTon.getInstence().getCategoryName());
                    routeId_set = myBusTools.deal_routeId(routeId_list);
                }

                if(routeId_set != null){
                    // 取得經過此站的路線資料
                    myBusTools.getJson_comeTime(routeId_set, stop_distance_sort);
                }
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.update:
                countDown.updateNow();
                break;
            case R.id.favorite:
                new MyFavorite(MainActivity.this).setFavorite();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                long secondTime=System.currentTimeMillis();
                if(secondTime-firstTime>2000){
                    Toast.makeText(MainActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                    firstTime=secondTime;
                    return true;
                }else{
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
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
