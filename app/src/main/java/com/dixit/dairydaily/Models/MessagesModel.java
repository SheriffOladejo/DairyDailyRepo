package com.dixit.dairydaily.Models;

public class MessagesModel {
    private String message, time, status;

    public MessagesModel() {
    }

    public MessagesModel(String message, String time, String status) {
        this.message = message;
        this.time = time;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
