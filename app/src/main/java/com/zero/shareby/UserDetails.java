package com.zero.shareby;

public class UserDetails {
    private String name;
    private String uid;
    private String latitude;
    private String longitude;
    private String phone;

    public UserDetails(){
        name="";
        uid="";
        latitude="";
        longitude="";
        phone="";
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setPhone(String phone){
        this.phone=phone;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }


    public String getPhone() {
        return phone;
    }
}
