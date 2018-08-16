package com.imge.yeezbus.model;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class MyWindowSize {
    /**
     * 範例：
     *      dialog_wait = new Dialog(MainActivity.this);
     *      dialog_wait.setContentView(R.layout.dialog_wait);
     *      dialog_wait.setCancelable(false);
     *
     *      MyWindowSize myWindowSize = new MyWindowSize(MainActivity.this);
     *      WindowManager.LayoutParams p = myWindowSize.setSize(-1, 0.85);
     *      dialog_wait.getWindow().setAttributes(p);
     * */
    private int windowWidth;
    private int windowHeight;

    public MyWindowSize(Context context) {
        super();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        windowWidth = d.widthPixels;
        windowHeight = d.heightPixels;
    }

    // 輸入 -1 即 WRAP_CONTENT
    // 輸入 -2 即 MATCH_PARENT
    public WindowManager.LayoutParams setSize(double height_rate, double width_rate){
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();

        if(height_rate == -1){
            p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }else if(height_rate == -2){
            p.height = WindowManager.LayoutParams.MATCH_PARENT;
        }else{
            p.height = (int) (windowHeight * height_rate);
        }

        if(width_rate == -1){
            p.width = WindowManager.LayoutParams.WRAP_CONTENT;
        }else if(width_rate == -2){
            p.width = WindowManager.LayoutParams.MATCH_PARENT;
        }else{
            p.width = (int) (windowWidth * width_rate);
        }

        return p;
    }

}
