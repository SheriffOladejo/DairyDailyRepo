package com.dixit.dairydaily.Models;

public class CustomerReportModel {
    private String date;
    private String fat;
    private String snf;
    private String weight;
    private String amount;
    private String shift;

    public CustomerReportModel() {
    }

    public CustomerReportModel(String date, String fat, String snf, String weight, String amount, String shift) {
        this.date = date;
        this.fat = fat;
        this.snf = snf;
        this.weight = weight;
        this.amount = amount;
        this.shift = shift;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}
