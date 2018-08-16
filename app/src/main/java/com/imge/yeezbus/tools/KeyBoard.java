package com.imge.yeezbus.tools;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class KeyBoard {

    public static void hideKeyBoard(Context context){
        Activity activity = (Activity) context;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }


}
