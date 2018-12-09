package com.zero.shareby.chats;

public class Chat {
    private String sentBy,receivedBy,message;
    private long timestamp;
    boolean belongsToCurrentUser,isDivider;

    public Chat() {
    }

    public boolean isDivider() {
        return isDivider;
    }

    public void setDivider(boolean divider) {
        isDivider = divider;
    }

    public Chat(String sentBy, String receivedBy, String message, long timestamp) {
        this.sentBy = sentBy;
        this.receivedBy = receivedBy;
        this.message = message;
        this.timestamp = timestamp;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }

    public void setBelongsToCurrentUser(boolean belongsToCurrentUser) {
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
