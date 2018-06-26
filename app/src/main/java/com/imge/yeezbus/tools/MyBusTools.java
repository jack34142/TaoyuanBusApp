package com.imge.yeezbus.tools;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.imge.yeezbus.CatchUtils.CatchUtils;
import com.imge.yeezbus.MainActivity;
import com.imge.yeezbus.bean.ComeTimeBean;
import com.imge.yeezbus.bean.RouteStopBean;
import com.imge.yeezbus.model.MyVolley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyBusTools {
    Context context;
    StringRequest request;
    int count;

    public MyBusTools(Context context) {
        super();
        this.context = context;
    }

    // 取得路線的中文名稱
    public void getJson_RouteNameZh(){
        String json_str = "https://data.tycg.gov.tw/opendata/datalist/datasetMeta/download?id=d7a0513d-1a91-4ae6-a06f-fbf83190ab2a&rid=8cbcf170-8641-4a0d-8fe8-256a36f4c6cb";

        request = new StringRequest(json_str, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    //暫時解決 volley 的中文亂碼問題
                    response = new String(response.getBytes("iso-8859-1"), "UTF-8");
                }catch (Exception e){
                    Log.e("MyBusTools","getRouteNameZh() 中文編碼錯誤");
                };
//                Log.d("MyBusTools","getRouteNameZh()："+response);

                Map<String,String> map = new HashMap<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");

                    int len_jsonArray = jsonArray.length();
                    for(int i=0; i<len_jsonArray; i++){
                        jsonObject = jsonArray.getJSONObject(i);
                        map.put(
                                jsonObject.getString("ID"),
                                jsonObject.getString("nameZh")+" "+jsonObject.getString("ddesc")
                        );
                    }
                }catch (Exception e){
                    Log.e("MyBusTools","getRouteNameZh() 解析JSON錯誤");
                }

                CatchUtils.setRouteNameZh(context, map);
                MainActivity.handler.sendEmptyMessage(0);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MyBusTools","getRouteNameZh() 的 Response.ErrorListener()");
                error.printStackTrace();
                MyVolley.getInstance(context).addToRequestQue(request);
            }
        });

        MyVolley.getInstance(context).addToRequestQue(request);
    }


    // 整理所有的 站點資料 包括：Id, routeId, nameZh, latitude, longitude
    Map<String,List> stopDetail_map;
    public void getJson_stopDetail(Set<String> routeId_set){
        String json_url = "http://apidata.tycg.gov.tw/OPD-io/bus4/GetStop.json?routeIds=";

        final int len_routeId_set = routeId_set.size();
        count = 0;
        final List<RouteStopBean> list = new ArrayList<>();
        stopDetail_map = new HashMap<>();

        for (String val : routeId_set) {
            request = new StringRequest(json_url + val, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    int progress = (++count)*100/len_routeId_set;
                    MainActivity.dialog_wait_tv.setText("正在下載各站點的資料 "+progress+"%");
//                    Log.d("進度", ++count + " / " + len_routeId_set);
                    Gson gson = new Gson();

                    try{
                        JSONArray jsonArray = new JSONArray(response);
                        int len_jsonArray = jsonArray.length();
                        List stopDetail;
                        for (int i=0; i<len_jsonArray; i++){
                            RouteStopBean routeStopBean = gson.fromJson(jsonArray.getJSONObject(i).toString(), RouteStopBean.class);

                            Set<String> stop_route;
                            if( stopDetail_map.keySet().contains(routeStopBean.getId()) ){
                                stopDetail = stopDetail_map.get(routeStopBean.getId());
                                stop_route = (Set<String>) stopDetail.get(0);

                                stop_route.add(routeStopBean.getRouteId());
                                stopDetail.set(0,stop_route);

                            }else {
                                stopDetail = new ArrayList<>();
                                stop_route = new HashSet<>();
                                stop_route.add(routeStopBean.getRouteId());
                                stopDetail.add(stop_route);
                                stopDetail.add(routeStopBean.getNameZh());
                                stopDetail.add(routeStopBean.getLatitude());
                                stopDetail.add(routeStopBean.getLongitude());
                            }

                            stopDetail_map.put(routeStopBean.getId(),stopDetail);
                        }

                        if(count == len_routeId_set){
                            CatchUtils.setStopDetail(context, stopDetail_map);
                            MainActivity.handler.sendEmptyMessage(1);
                        }
                    }catch (Exception e){
                        Log.e("MyBusTools","getJson_stopDetail() 解析 json 失敗");
                        e.printStackTrace();
                    };
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("MyBusTools","getJson_stopDetail() 的 Response.ErrorListener()");
                    error.printStackTrace();
                    MyVolley.getInstance(context).addToRequestQue(request);
                }
            });
            MyVolley.getInstance(context).addToRequestQue(request);
        }
    }

    // List.get(0) = List           站名 (已按照你的位置排序)，也是 map 的索引值
    // List.get(1) = Map            key = 站名，value = routeId_set
    public List getNearStop(Map<String,List> stopDetail_map){
        Double[] myPos;
        do{
            myPos = MyGpsTools.getMyPos();
            try{
                Thread.sleep(500);
            }catch (Exception e){}
        }while (myPos == null);

        Map<String,Set<String>> nearStop_map = new HashMap<>();
        Map<Double, String> stop_distance = new HashMap<>();
        List<String> stop_distance_sort = new ArrayList<>();
        List<Double> distance_list = new ArrayList<>();
        for (String key : stopDetail_map.keySet()){
            List stopDetail = stopDetail_map.get(key);
            Double stop_latitude = Double.parseDouble((String)stopDetail.get(2));
            Double stop_longtitude = Double.parseDouble((String)stopDetail.get(3));

            // 一度 大約是 100 公里
            Double distance = Math.sqrt(
                    Math.pow(stop_latitude - myPos[0], 2) +
                    Math.pow(stop_longtitude - myPos[1], 2)
            ) * 100;

            if(distance < 0.25){
                String stopNameZh = (String)stopDetail.get(1);
                Set<String> routeId_set;
                Set<String> routeIds = (Set<String>)stopDetail.get(0);

                if( nearStop_map.keySet().contains(stopNameZh) ){
                    routeId_set = nearStop_map.get(stopNameZh);

                    routeId_set.addAll(routeIds);
                    nearStop_map.put(stopNameZh, routeId_set);
                }else{
                    stop_distance.put(distance, stopNameZh);
                    distance_list.add(distance);

                    routeId_set = routeIds;
                    nearStop_map.put(stopNameZh, routeId_set);
                }
            }
        }

        Collections.sort(distance_list);
        int len_distance_list = distance_list.size();
        for (int i=0; i<len_distance_list; i++){
            stop_distance_sort.add(stop_distance.get(distance_list.get(i)));
        }
//        Log.d("test",stop_distance_sort.toString());
        List list = new ArrayList();
        list.add(stop_distance_sort);
        list.add(nearStop_map);
        return list;
    }

    Set<String> routeId_set;
    public void getJson_comeTime(Map<String,Set<String>> nearStop_map, final List<String> stop_distance_sort){
        routeId_set = new HashSet<>();
        for (String key : nearStop_map.keySet()){
            routeId_set.addAll( nearStop_map.get(key) );
        }

        if (routeId_set.isEmpty()){
            return;
        }

        String routeIds = routeId_set.toString();
        routeIds = routeIds.substring(1,routeIds.length()-1);
        routeIds = routeIds.replace(", ",",");
//        Log.d("test", routeIds);

        String json_url = "http://apidata.tycg.gov.tw/OPD-io/bus4/GetEstimateTime.json?routeIds=" + routeIds;
        request = new StringRequest(json_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                List<String[]> go_list = new ArrayList<>();
                List<String[]> back_list = new ArrayList<>();

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    for(String key : routeId_set){
                        JSONArray jsonArray = jsonObject.getJSONArray(key);
                        String[] comeTime_go_ary = new String[3];
                        String[] comeTime_back_ary = new String[3];
                        Map<String, String> value_go_map = new HashMap<>();
                        Map<String, String> value_back_map = new HashMap<>();

                        comeTime_go_ary[0] = comeTime_back_ary[0] = key;
                        int len_jsonArray = jsonArray.length();
                        for (int i=0; i<len_jsonArray; i++){
                            ComeTimeBean comeTimeBean = gson.fromJson(jsonArray.getJSONObject(i).toString(), ComeTimeBean.class);

                            switch (comeTimeBean.getGoBack()){
                                case 1:
                                    if(comeTime_go_ary[2]==null){
                                        if(!comeTimeBean.getValue().equals("null") && !comeTimeBean.getValue().equals("-3")){
                                            comeTime_go_ary[2] = comeTimeBean.getStopName();
                                        }
                                    }

                                    if (comeTimeBean.getValue().equals("null")){
                                        value_go_map.put(comeTimeBean.getStopName(), comeTimeBean.getComeTime());
                                    }else{
                                        value_go_map.put(comeTimeBean.getStopName(), comeTimeBean.getValue());
                                    }

                                    break;
                                case 2:
                                    if(comeTime_back_ary[2]==null){
                                        if(!comeTimeBean.getValue().equals("null") && !comeTimeBean.getValue().equals("-3")){
                                            comeTime_back_ary[2] = comeTimeBean.getStopName();
                                        }
                                    }

                                    if (comeTimeBean.getValue().equals("null")){
                                        value_back_map.put(comeTimeBean.getStopName(), comeTimeBean.getComeTime());
                                    }else{
                                        value_back_map.put(comeTimeBean.getStopName(), comeTimeBean.getValue());
                                    }

                                    break;
                                default:
                                    break;
                            }
                        }

                        for (String s : stop_distance_sort){
                            if(value_go_map.keySet().contains(s)){
                                if(comeTime_go_ary[2] == null){
                                    comeTime_go_ary[1] = value_go_map.get(s);
                                }else if(!comeTime_go_ary[2].equals(s)){
                                    comeTime_go_ary[1] = value_go_map.get(s);
                                }else{
                                    comeTime_go_ary[1] = "即將進站";
                                }
                                break;
                            }
                        }

                        for (String s : stop_distance_sort){
                            if(value_back_map.keySet().contains(s)){
                                if(comeTime_back_ary[2] == null){
                                    comeTime_back_ary[1] = value_back_map.get(s);
                                }else if(!comeTime_back_ary[2].equals(s)){
                                    comeTime_back_ary[1] = value_back_map.get(s);
                                }else{
                                    comeTime_back_ary[1] = "即將進站";
                                }
                                break;
                            }
                        }

                        go_list.add(comeTime_go_ary);
                        back_list.add(comeTime_back_ary);
                    }

                    List<List<String[]>> list = new ArrayList<>();
                    list.add(go_list);
                    list.add(back_list);
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = list;
                    MainActivity.handler.handleMessage(msg);

                }catch (Exception e){
                    Log.e("MyBusTools","getJson_comeTime() 解析 json 失敗");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MyBusTools","getJson_comeTime() 的 Response.ErrorListener()");
                error.printStackTrace();
                MyVolley.getInstance(context).addToRequestQue(request);
            }
        });

        MyVolley.getInstance(context).addToRequestQue(request);
    }


}
