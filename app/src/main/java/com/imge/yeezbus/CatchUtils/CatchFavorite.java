package com.imge.yeezbus.CatchUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.imge.yeezbus.bean.StopDetailBean;
import com.imge.yeezbus.config.Config;

import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatchFavorite {

    // 在 favorite 中 name == category_name 加入 routeId
    public static boolean setFavorite(Context context, String category_name, Map<String, List<Boolean>> routeId_goBack){
        SharedPreferences sp = context.getSharedPreferences("Favorite", Context.MODE_PRIVATE);
        Map<String, List<Boolean>> favorite_map = getFavorite(context, category_name);

        for(String routeId : routeId_goBack.keySet()){
//            if( favorite_map.containsKey(routeId) ){
//                return false;
//            }else{
                favorite_map.putAll(routeId_goBack);
//            }
        }

        String s = "";
        if(!favorite_map.isEmpty()){
            s = favorite_map.toString();
            s = s.substring(1, s.length()-2);
        }

        sp.edit().putString(category_name, s).commit();
        return true;
    }

    // 取得 favorite 中 name == category_name 的 routeId List
    public static Map<String, List<Boolean>> getFavorite(Context context, String category_name){
        SharedPreferences sp = context.getSharedPreferences("Favorite", Context.MODE_PRIVATE);

        String routeId_str = sp.getString(category_name, "");
        if(routeId_str.equals("")){
            return new HashMap<>();
        }else{
            Map<String, List<Boolean>> favorite_map = new HashMap<>();

            String[] routeId_goBack_ary = routeId_str.split("], ");
            for(String routeId_goBack : routeId_goBack_ary){
                String[] key_value = routeId_goBack.split("=\\[");

                String value = key_value[1].toString();
                String[] value_ary = value.split(", ");
                List<Boolean> goBack_list = new ArrayList<>();
                goBack_list.add(Boolean.parseBoolean(value_ary[0]));
                goBack_list.add(Boolean.parseBoolean(value_ary[1]));

                favorite_map.put(key_value[0], goBack_list);
            }

            return favorite_map;
        }
    }

    // 重新命名 favorite
    public static void renameFavorite(Context context, String category_name, String new_category_name){
        SharedPreferences sp = context.getSharedPreferences("Favorite", Context.MODE_PRIVATE);

        if(!renameFavoriteSort(context, category_name, new_category_name)){
            Toast.makeText(context,"重複的分類名稱",Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, List<Boolean>> favorite_map = getFavorite(context, category_name);
        copyFavorite(context, new_category_name, favorite_map);
        sp.edit().remove(category_name).commit();
    }

    // 複製 favorite 中 name == category_name 的資料 (rename 時會用到)
    private static void copyFavorite(Context context, String category_name, Map<String, List<Boolean>> routeId_goBack){
        SharedPreferences sp = context.getSharedPreferences("Favorite", Context.MODE_PRIVATE);

        String s = "";
        if(!routeId_goBack.isEmpty()){
            s = routeId_goBack.toString();
            s = s.substring(1, s.length()-2);
        }

        sp.edit().putString(category_name, s).commit();
    }

    // 刪除 favorite 中 name == category_name 的其中一個 routeId
    public static void deleteFavoriteItem(Context context, String category_name, String routeId, int nowPage){
        SharedPreferences sp = context.getSharedPreferences("Favorite", Context.MODE_PRIVATE);
        Map<String, List<Boolean>> favorite_map = getFavorite(context, category_name);

        if(favorite_map.containsKey(routeId)){
            if(nowPage == 0 && favorite_map.get(routeId).get(1)){
                List<Boolean> goBack_list = new ArrayList<>();
                goBack_list.add(false);
                goBack_list.add(true);
                favorite_map.put(routeId, goBack_list);
            }else if(nowPage == 1 && favorite_map.get(routeId).get(0)){
                List<Boolean> goBack_list = new ArrayList<>();
                goBack_list.add(true);
                goBack_list.add(false);
                favorite_map.put(routeId, goBack_list);
            }else{
                favorite_map.remove(routeId);
            }

        }else{
            return;
        }

        String s = "";
        if(!favorite_map.isEmpty()){
            s = favorite_map.toString();
            s = s.substring(1, s.length()-2);
        }
        sp.edit().putString(category_name, s).commit();
    }

    // 刪除 favorite 中 name == category_name 的資料
    public static void deleteFavorite(Context context, String category_name){
        SharedPreferences sp = context.getSharedPreferences("Favorite", Context.MODE_PRIVATE);
        sp.edit().remove(category_name).commit();

        deleteFavoriteSort(context,category_name);
    }

    // 在 favoriteSort 中加入新的 name
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

    // 取得 favoriteSort 所有 name (有排序)
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

    // 刪除 請用上面的方法
    // 刪除 favoriteSort 中 name == category_name
    private static void deleteFavoriteSort(Context context, String category_name){
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
    }

    // 重命名 請用上面的方法
    // 重命名 favoriteSort 中 name == category_name
    private static boolean renameFavoriteSort(Context context, String category_name, String new_category_name){
        SharedPreferences sp = context.getSharedPreferences("FavoriteSort", Context.MODE_PRIVATE);
        List<String> sort_list = getFavoriteSort(context);

        if(sort_list.contains(new_category_name)){
            return false;
        }else{
            int index = sort_list.indexOf(category_name);
            sort_list.set(index, new_category_name);
        }

        String s = sort_list.toString();
        s = s.substring(1,s.length()-1);
        sp.edit().putString("favorite_sort",s).commit();
        return true;
    }





}
