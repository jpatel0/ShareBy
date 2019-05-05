package com.zero.shareby.models;

public class Post {
    private String refKey,reqUid,sharedUid,repliedUid,repliedName,name,title,desc;
    private int priority,status,type;
    private long timestamp;
/*
type: 0-question
      1-req item
priority: 0-new user
          1-item post normal
          2-urgent item req
 */
    public Post(){
    }

    public Post(String uid1,String uid2,String n,String tit,String des,int prior,int stat,int type){
        reqUid=uid1;
        sharedUid=uid2;
        name=n;
        title=tit;
        desc=des;
        priority=prior;
        status=stat;
        this.type = type;
        timestamp=System.currentTimeMillis();
    }

    public Post(String u,String n){
        reqUid=u;
        name=n;
        title="Welcome "+name+" as the new member";
        priority=0;
        timestamp=System.currentTimeMillis();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRepliedUid() {
        return repliedUid;
    }

    public void setRepliedUid(String repliedUid) {
        this.repliedUid = repliedUid;
    }

    public String getRepliedName() {
        return repliedName;
    }

    public void setRepliedName(String repliedName) {
        this.repliedName = repliedName;
    }

    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }

    public String getRefKey() {
        return refKey;
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
