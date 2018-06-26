package com.imge.yeezbus.bean;

public class ComeTimeBean {


    /**
     * StopID : 2130
     * SID : 64667
     * StopName : 今日飯店
     * GoBack : 1
     * seqNo : 1
     * IVRNO : 2130
     * EXTVoiceNo : 2130
     * comeTime : 19:05
     * comeCarid :
     * schId : 5022_1_20180620190500
     * carId :
     * Value : null
     * ests : []
     */

    private String StopName;
    private int GoBack;
    private String comeTime;
    private String Value;

    public String getStopName() {
        return StopName;
    }

    public void setStopName(String StopName) {
        this.StopName = StopName;
    }

    public int getGoBack() {
        return GoBack;
    }

    public void setGoBack(int GoBack) {
        this.GoBack = GoBack;
    }

    public String getComeTime() {
        return comeTime;
    }

    public void setComeTime(String comeTime) {
        this.comeTime = comeTime;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String Value) {
        this.Value = Value;
    }
}
