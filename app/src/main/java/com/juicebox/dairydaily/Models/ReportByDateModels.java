package com.juicebox.dairydaily.Models;

public class ReportByDateModels {
    private String date;
    private String rate;
    private String weight;
    private String amount;
    private String shift;

    public ReportByDateModels() {
    }

    public ReportByDateModels(String date, String rate, String weight, String amount, String shift) {
        this.date = date;
        this.rate = rate;
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

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}
