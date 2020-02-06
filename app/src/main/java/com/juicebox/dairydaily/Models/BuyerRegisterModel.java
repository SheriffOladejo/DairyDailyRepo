package com.juicebox.dairydaily.Models;

public class BuyerRegisterModel {
    private String Name;
    private String weight;
    private String amount;
    private int id;

    public BuyerRegisterModel() {
    }

    public BuyerRegisterModel(String name, String weight, String amount, int id) {
        Name = name;
        this.weight = weight;
        this.amount = amount;
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
