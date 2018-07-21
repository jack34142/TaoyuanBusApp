package com.imge.yeezbus.tools;

import android.os.Message;

import com.imge.yeezbus.MainActivity;

public class CategoryNameSinTon {
    public static CategoryNameSinTon instance;
    private String categoryName;

    private CategoryNameSinTon() {
        super();
        categoryName = "附近的公車";
    }

    public static CategoryNameSinTon getInstence() {
        if (instance == null) {
            synchronized (CategoryNameSinTon.class) {
                if (instance == null) {
                    instance = new CategoryNameSinTon();
                }
            }
        }
        return instance;
    }

    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    public String getCategoryName(){
        return categoryName;
    }


}
