package com.dixit.dairydaily.Models;

public class PaymentRegisterModel {
    private int id;
    private String fat;
    private String snf;
    private String weight;
    private String amount;
    private String date;
    private String shift;
    private String name;

    public PaymentRegisterModel() {
    }

    public PaymentRegisterModel(int id, String fat, String snf, String weight, String amount, String date, String shift, String name) {
        this.id = id;
        this.fat = fat;
        this.snf = snf;
        this.weight = weight;
        this.amount = amount;
        this.date = date;
        this.shift = shift;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
