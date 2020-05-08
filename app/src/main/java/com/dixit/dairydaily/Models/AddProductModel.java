package com.dixit.dairydaily.Models;

public class AddProductModel {
    private String id;
    private String product_name;
    private String rate;

    public AddProductModel() {
    }

    public AddProductModel(String id, String product_name, String rate) {
        this.id = id;
        this.product_name = product_name;
        this.rate = rate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
