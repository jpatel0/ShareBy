package com.zero.shareby.chats;

public class Chat {
    private String sentBy,receivedBy,message;
    private long timestamp;

    public Chat() {
    }

    public Chat(String sentBy, String receivedBy, String message, long timestamp) {
        this.sentBy = sentBy;
        this.receivedBy = receivedBy;
        this.message = message;
        this.timestamp = timestamp;
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
