package com.juicebox.dairydaily.Models;

public class ReceiveCashListModel {
    private String id, name, weight, due;

    public ReceiveCashListModel() {
    }

    public ReceiveCashListModel(String id, String name, String weight, String due) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.due = due;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }
}
