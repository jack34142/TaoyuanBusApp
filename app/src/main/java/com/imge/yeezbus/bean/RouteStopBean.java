package com.imge.yeezbus.bean;

public class RouteStopBean {
    /**
     * Id : 2130
     * routeId : 5022
     * nameZh : 今日飯店
     * seqNo : 1
     * pgp : 0
     * terminal : 0
     * districtId : 1
     * GoBack : 1
     * latitude : 24.990867
     * longitude : 121.314336
     * EXTVoiceNO : 2130
     * SID : 64667
     * ivrno : 2130
     */

    private String Id;
    private String routeId;
    private String nameZh;
    private String latitude;
    private String longitude;

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
