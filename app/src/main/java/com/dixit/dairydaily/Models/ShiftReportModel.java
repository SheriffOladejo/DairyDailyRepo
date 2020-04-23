package com.dixit.dairydaily.Models;

public class ShiftReportModel {
    private int id;
    private String weight;
    private String amount;
    private String fat;
    private String snf;
    private String name;
    private String rate;

    public ShiftReportModel() {
    }

    public ShiftReportModel(int id, String weight, String amount, String fat, String snf, String name, String rate) {
        this.id = id;
        this.weight = weight;
        this.amount = amount;
        this.fat = fat;
        this.snf = snf;
        this.name = name;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
