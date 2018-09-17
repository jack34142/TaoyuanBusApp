package com.imge.yeezbus.tools;

public class CategoryNameSinTon {
    public static CategoryNameSinTon instance;
    private String categoryName;

    private CategoryNameSinTon() {
        super();
        categoryName = "";
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
