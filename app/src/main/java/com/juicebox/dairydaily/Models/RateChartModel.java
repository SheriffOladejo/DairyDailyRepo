package com.juicebox.dairydaily.Models;

public class RateChartModel {
    private String fat, rate;

    public RateChartModel() {
    }

    public RateChartModel(String fat, String rate) {
        this.fat = fat;
        this.rate = rate;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
