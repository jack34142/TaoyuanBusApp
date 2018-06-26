package com.imge.yeezbus.bean;

import java.util.Set;

public class StopDetailBean {
    /**
     * latitude : 24.945516
     * nameZh : 平鎮區公所
     * Id : 6750
     * longitude : 121.21799
     * routeId : ["6750"]
     */

    private String latitude;
    private String nameZh;
    private String Id;
    private String longitude;
    private Set<String> routeId;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Set<String> getRouteId() {
        return routeId;
    }

    public void setRouteId(Set<String> routeId) {
        this.routeId = routeId;
    }
}
