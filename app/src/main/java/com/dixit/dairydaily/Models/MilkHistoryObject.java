package com.dixit.dairydaily.Models;

public class MilkHistoryObject {
    private String session;
    private String date;
    private String amount;
    private String weight;
    private String fat;

    public MilkHistoryObject() {
    }

    public MilkHistoryObject(String session, String date, String amount, String weight, String fat) {
        this.session = session;
        this.date = date;
        this.amount = amount;
        this.weight = weight;
        this.fat = fat;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }
}
