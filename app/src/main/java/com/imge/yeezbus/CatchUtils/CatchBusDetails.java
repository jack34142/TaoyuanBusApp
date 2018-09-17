package com.imge.yeezbus.CatchUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.imge.yeezbus.bean.StopDetailBean;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatchBusDetails {

    public static void setRouteNameZh(Context context, Map<String,String> routeNameZh_map){
        SharedPreferences sp = context.getSharedPreferences("RouteNameZh", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        for(String key : routeNameZh_map.keySet()){
            editor.putString(key, routeNameZh_map.get(key));
        }

        editor.commit();
    }

    public static Map<String,String> getRouteNameZh(Context context){
        SharedPreferences sp = context.getSharedPreferences("RouteNameZh", Context.MODE_PRIVATE);

        return (Map<String,String>)sp.getAll();
    }

    public static void setStopDetail(Context context, Map<String,List> stopDetail_map){
        List stopDetail_list = new ArrayList();

        for(String key: stopDetail_map.keySet()){
            List stopDetail = stopDetail_map.get(key);
            Map map = new HashMap();
            map.put("Id",key);
            map.put("routeId",stopDetail.get(0));
            map.put("nameZh",stopDetail.get(1));
            map.put("latitude",stopDetail.get(2));
            map.put("longitude",stopDetail.get(3));
            stopDetail_list.add(map);
        }

        Gson gson = new Gson();
        String stopDetail_str = gson.toJson(stopDetail_list);
//        Log.d("test",stopDetail_str);

        SharedPreferences sp = context.getSharedPreferences("StopDetail", Context.MODE_PRIVATE);
        sp.edit().putString("stopDetail_str",stopDetail_str).commit();
    }


    /**
     * 格式：
     * Map.get( stopId ) = List;
     * List.get(0) = Set;              經過此站的公車路線
     * List.get(1) = String;            此站的中文名稱
     * List.get(2) = String;            此站的緯度
     * List.get(3) = String;            此站的經度
     *
     * 範例：
     * Map.get( stopId ) = [
     *      [5071, 5014, 5015, 5023, 5020, 50221, 5021, 3200, 5073, 5022, 5016, 5069],
     *      煉油廠,
     *      25.03167,
     *      121.303686
     * ]
     * */
    public static Map<String,List> getStopDetail(Context context){
        SharedPreferences sp = context.getSharedPreferences("StopDetail", Context.MODE_PRIVATE);

        String stopDetail_str =  sp.getString("stopDetail_str","");
        Gson gson = new Gson();

        if( !stopDetail_str.equals("") ){
            Map<String,List> stopDetail_map = new HashMap<>();

            try{
                JSONArray jsonArray = new JSONArray(stopDetail_str);

                int len_jsonArray = jsonArray.length();
                for(int i=0; i<len_jsonArray; i++){
                    List stopDetail = new ArrayList();

                    StopDetailBean stopDetailBean = gson.fromJson(jsonArray.getJSONObject(i).toString(),StopDetailBean.class);
                    stopDetail.add(stopDetailBean.getRouteId());
                    stopDetail.add(stopDetailBean.getNameZh());
                    stopDetail.add(stopDetailBean.getLatitude());
                    stopDetail.add(stopDetailBean.getLongitude());

                    stopDetail_map.put(stopDetailBean.getId(), stopDetail);
                }
                return stopDetail_map;

            }catch (Exception e){
                Log.e("CatchUtils","getStopDetail() 解析 json 失敗");
                e.getStackTrace();
            }
        }

        return null;
    }
}
