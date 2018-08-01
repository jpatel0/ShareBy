package com.zero.shareby;

public class UserDetails {
    private String name;
    private String uid;
    private double latitude;
    private double longitude;
    private String phone;
    private String photoUrl;

    public UserDetails(){
        name=null;
        uid=null;
        latitude=0.0;
        longitude=0.0;
        phone=null;
        photoUrl=null;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
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
