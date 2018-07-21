package com.imge.yeezbus.CatchUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.imge.yeezbus.bean.StopDetailBean;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CatchUtils {
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
     * List.get(0) = List;              經過此站的公車路線
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

    public static boolean setFavorite(Context context, String category_name, String routeId){
        SharedPreferences sp = context.getSharedPreferences("Favorite", Context.MODE_PRIVATE);
        List<String> routeId_list = getFavorite(context, category_name);

        if(routeId_list.contains(routeId)){
            return false;
        }else{
            routeId_list.add(routeId);
        }

        String s = routeId_list.toString();
        s = s.substring(1, s.length()-1);
        sp.edit().putString(category_name, s).commit();
        return true;
    }

    public static List<String> getFavorite(Context context, String category_name){
        SharedPreferences sp = context.getSharedPreferences("Favorite", Context.MODE_PRIVATE);

        String routeId_str = sp.getString(category_name, "");
        if(routeId_str.equals("")){
            return new ArrayList<>();
        }else{
            return new ArrayList<>(Arrays.asList(routeId_str.split(", ")));
        }
    }

    public static void deleteFavoriteItem(Context context, String category_name, String routeId){
        SharedPreferences sp = context.getSharedPreferences("Favorite", Context.MODE_PRIVATE);
        List<String> routeId_list = getFavorite(context, category_name);

        if(routeId_list.contains(routeId)){
            int index = routeId_list.indexOf(routeId);
            routeId_list.remove(index);
        }else{
            return;
        }

        String s = routeId_list.toString();
        s = s.substring(1, s.length()-1);
        sp.edit().putString(category_name, s).commit();
    }

    public static void deleteFavorite(Context context, String category_name){
        SharedPreferences sp = context.getSharedPreferences("Favorite", Context.MODE_PRIVATE);
        sp.edit().remove(category_name).commit();
    }


    // direct = 0 最後, -1 = 下移, 1 上移
    public static void setFavoriteSort(Context context, String category_name, int direct){
        SharedPreferences sp = context.getSharedPreferences("FavoriteSort", Context.MODE_PRIVATE);
        List<String> sort_list = getFavoriteSort(context);

        if(direct == 1 || direct == -1){
            int index = sort_list.indexOf(category_name);
            sort_list.remove(index);
            sort_list.add(index - direct, category_name);
        }else if(sort_list.contains(category_name)){
            Toast.makeText(context,"重複的分類名稱",Toast.LENGTH_SHORT).show();
            return;
        }else{
            sort_list.add(category_name);
        }

        String s = sort_list.toString();
        s = s.substring(1,s.length()-1);

        sp.edit().putString("favorite_sort",s).commit();
    }

    public static List<String> getFavoriteSort(Context context){
        SharedPreferences sp = context.getSharedPreferences("FavoriteSort", Context.MODE_PRIVATE);
        String favorite_sort = sp.getString("favorite_sort","");

        if(favorite_sort == ""){
            return new ArrayList<>();
        }else{
            List<String> sort_list = new ArrayList<String>(Arrays.asList(favorite_sort.split(", ")));
            return sort_list;
        }
    }

    public static void deleteFavoriteSort(Context context, String category_name){
        SharedPreferences sp = context.getSharedPreferences("FavoriteSort", Context.MODE_PRIVATE);
        List<String> sort_list = getFavoriteSort(context);

        if(sort_list.size() == 1){
            sp.edit().remove("favorite_sort").commit();
        }else{
            int index = sort_list.indexOf(category_name);
            sort_list.remove(index);
            String s = sort_list.toString();
            s = s.substring(1,s.length()-1);
            sp.edit().putString("favorite_sort",s).commit();
        }

        deleteFavorite(context,category_name);
    }





}
