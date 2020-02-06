package com.juicebox.dairydaily.Models;

public class DailyBuyObject {
    private int id;
    private String SellerName;
    private double weight;
    private double rate;
    private double amount;
    private String shift;
    private double fat;
    private double snf;
    private String date;
    private String type;
    private int Unique_Id;

    public DailyBuyObject() {
    }

    public DailyBuyObject(int id, String sellerName, double weight, double rate, double amount, String shift, double fat, double snf, String date, String type, int unique_Id) {
        this.id = id;
        SellerName = sellerName;
        this.weight = weight;
        this.rate = rate;
        this.amount = amount;
        this.shift = shift;
        this.fat = fat;
        this.snf = snf;
        this.date = date;
        this.type = type;
        Unique_Id = unique_Id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSellerName() {
        return SellerName;
    }

    public void setSellerName(String sellerName) {
        SellerName = sellerName;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getSnf() {
        return snf;
    }

    public void setSnf(double snf) {
        this.snf = snf;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUnique_Id() {
        return Unique_Id;
    }

    public void setUnique_Id(int unique_Id) {
        Unique_Id = unique_Id;
    }
}
