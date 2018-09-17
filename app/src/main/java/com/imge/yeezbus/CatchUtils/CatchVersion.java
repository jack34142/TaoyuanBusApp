package com.imge.yeezbus.CatchUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.imge.yeezbus.config.Config;

public class CatchVersion {

    // 更新到最新版本號
    public static void setVersionCode(Context context){
        SharedPreferences sp = context.getSharedPreferences("VersionCode", Context.MODE_PRIVATE);

        int versionCode = Config.getVersionCode(context);
//        Log.e("set last versionCode", String.valueOf(versionCode));

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("versionCode", versionCode);
        editor.commit();
    }

    // 取得上次更新時的版本號
    public static int getVersionCode(Context context){
        SharedPreferences sp = context.getSharedPreferences("VersionCode", Context.MODE_PRIVATE);

        // 上次更新時的版本號
        int lastVersionCode = sp.getInt("versionCode", 0);
//        Log.e("last versionCode", String.valueOf(lastVersionCode));
        return sp.getInt("versionCode", 0);
    }
}
