package com.imge.yeezbus.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class MyGpsTools implements LocationListener {
    Activity activity;
    static LocationManager locationManager;
    static String provider;

    public MyGpsTools(Activity activity) {
        super();
        this.activity = activity;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        // 請求 gps 使用權限
        boolean permission_fine, permission_coarse;
        permission_fine = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        permission_coarse = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

        if (permission_fine && permission_coarse) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // NETWORK_PROVIDER 速度較快
        provider = LocationManager.GPS_PROVIDER;
        // 執行時先抓一次 GPS
        locationManager.requestLocationUpdates(provider, 0, 0, this);

        // 為了節省耗電量，搜尋 GPS 的頻率 不要太高，移動 10 m以上，或是 60 秒，才偵測一次。
//        locationManager.removeUpdates(this);
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 10, this);

    }

    public static Double[] getMyPos(){
        try {
            Location location = locationManager.getLastKnownLocation(provider);

            return new Double[]{location.getLatitude(), location.getLongitude()};
        }catch (SecurityException e){

        }catch (Exception e){       // 如果取得位置失敗，先停個 50 豪秒
            Log.e("MyGpsTools","getMyPos() 取得當前位置失敗");
        }
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
//        Toast.makeText(activity,location.getLatitude()+" , "+location.getLongitude(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(activity, "GPS正常運作", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(activity, "請開啟GPS功能", Toast.LENGTH_SHORT).show();
    }
}
