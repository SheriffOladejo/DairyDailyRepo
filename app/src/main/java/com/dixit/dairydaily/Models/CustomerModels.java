package com.dixit.dairydaily.Models;

public class CustomerModels {
    public CustomerModels(String phone_number, String name, String address, int id, String status){
        this.phone_number = phone_number;
        this.name = name;
        this.address = address;
        this.id = id;
        this.status = status;
    }

    public CustomerModels(){}

    private String phone_number;

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    private String name;
    private String address;
    private int id;
    private String status;
}
