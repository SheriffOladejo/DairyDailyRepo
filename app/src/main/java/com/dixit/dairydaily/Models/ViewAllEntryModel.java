package com.dixit.dairydaily.Models;

public class ViewAllEntryModel {
    private String date;
    private String session;
    private String fat;
    private String snf;
    private String rate;
    private String weight;
    private String amount;
    private String bonus;

    public ViewAllEntryModel() {
    }

    public ViewAllEntryModel(String date, String session, String fat, String snf, String rate, String weight, String amount, String bonus) {
        this.date = date;
        this.session = session;
        this.fat = fat;
        this.snf = snf;
        this.rate = rate;
        this.weight = weight;
        this.amount = amount;
        this.bonus = bonus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getSnf() {
        return snf;
    }

    public void setSnf(String snf) {
        this.snf = snf;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }
}
