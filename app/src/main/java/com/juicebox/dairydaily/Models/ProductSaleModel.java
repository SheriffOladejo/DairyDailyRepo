package com.juicebox.dairydaily.Models;

public class ProductSaleModel {
    private int id;
    private String name, product_name, units, amount;

    public ProductSaleModel() {
    }

    public ProductSaleModel(int id, String name, String product_name, String units, String amount) {
        this.id = id;
        this.name = name;
        this.product_name = product_name;
        this.units = units;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
