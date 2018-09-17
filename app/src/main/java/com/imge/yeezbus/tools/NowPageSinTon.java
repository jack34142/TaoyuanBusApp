package com.imge.yeezbus.tools;

public class NowPageSinTon {
    public static NowPageSinTon instance;
    private int nowPage;

    private NowPageSinTon() {
        super();
        nowPage = 0;
    }

    public static NowPageSinTon getInstence() {
        if (instance == null) {
            synchronized (NowPageSinTon.class) {
                if (instance == null) {
                    instance = new NowPageSinTon();
                }
            }
        }
        return instance;
    }

    public void setNowPage(int nowPage){
        this.nowPage = nowPage;
    }

    public int getNowPage(){
        return nowPage;
    }




}
