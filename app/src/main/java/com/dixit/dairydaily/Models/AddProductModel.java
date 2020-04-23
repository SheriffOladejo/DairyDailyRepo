package com.dixit.dairydaily.Models;

public class AddProductModel {
    private String product_name;
    private String rate;

    public AddProductModel() {
    }

    public AddProductModel(String product_name, String rate) {
        this.product_name = product_name;
        this.rate = rate;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
