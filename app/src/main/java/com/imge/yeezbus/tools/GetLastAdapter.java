package com.imge.yeezbus.tools;

import com.imge.yeezbus.adapter.MainListAdapter;

public class GetLastAdapter {
    public static MainListAdapter adapter_go;
    public static MainListAdapter adapter_back;

    public static MainListAdapter getAdapter(int goBack){
        switch(goBack){
            case 0:
                return adapter_go;
            case 1:
                return adapter_back;
            default:
                return null;
        }
    }

    public static void setAdapter(MainListAdapter adapter, int goBack){
        switch(goBack){
            case 0:
                adapter_go = adapter;
                break;
            case 1:
                adapter_back = adapter;
                break;
        }
    }
}
