package com.zero.shareby;

public class Post {
    private String reqUid,sharedUid,name,title,desc;
    private int priority,status;
    private long timestamp;

    public Post(){
    }

    public Post(String uid1,String uid2,String n,String tit,String des,int prior,int stat){
        reqUid=uid1;
        sharedUid=uid2;
        name=n;
        title=tit;
        desc=des;
        priority=prior;
        status=stat;
        timestamp=System.currentTimeMillis();
    }

    public Post(String u,String n){
        reqUid=u;
        name=n;
        title="Welcome "+n+" as the new member";
        priority=0;
        timestamp=System.currentTimeMillis();
    }

    public String getReqUid() {
        return reqUid;
    }

    public void setReqUid(String reqUid) {
        this.reqUid = reqUid;
    }

    public String getSharedUid() {
        return sharedUid;
    }

    public void setSharedUid(String sharedUid) {
        this.sharedUid = sharedUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
