package com.imge.yeezbus.model;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyVolley {
    private static MyVolley instance;
    private Context context;
    private RequestQueue requestQueue;

    // 單例
    private MyVolley(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    // 物件方法不同步，避免效能的浪費
    public static MyVolley getInstance(Context context){
        // 只在第一次執行時同步，有效節省效能
        if (instance == null){
            synchronized (MyVolley.class){
                if(instance == null){
                    instance = new MyVolley(context);
                }
            }
        }

        return instance;
    }

    // 取得 RequestQueue 物件
    private RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
        }

        return requestQueue;
    }

    // 將 Request 加入隊列
    public <T>void addToRequestQue(Request<T> request){
        getRequestQueue().add(request);
    }
}
