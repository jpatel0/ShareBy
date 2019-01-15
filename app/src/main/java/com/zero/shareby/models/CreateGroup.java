package com.zero.shareby.models;

public class CreateGroup {
    private String grpName;
    private int memberCount;
    private double radius;
    private double latitude;
    private double longitude;

    public CreateGroup(String gName,int mCount,double rad,double lat,double lng){
        grpName=gName;
        memberCount=mCount;
        radius=rad;
        latitude=lat;
        longitude=lng;
    }

    public String getGrpName() {
        return grpName;
    }

    public void setGrpName(String grpName) {
        this.grpName = grpName;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
