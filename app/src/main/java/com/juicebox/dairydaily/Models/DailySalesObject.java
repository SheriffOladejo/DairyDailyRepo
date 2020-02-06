package com.juicebox.dairydaily.Models;

public class DailySalesObject {
    private int id;
    private String buyerName;
    private double weight;
    private double amount;
    private double rate;
    private String shift;
    private String date;
    private double debit;
    private double credit;
    private int unique_id;

    public DailySalesObject() {
    }

    public DailySalesObject(int id, String buyerName, double weight, double amount, double rate, String shift, String date, double debit, double credit, int unique_id) {
        this.id = id;
        this.buyerName = buyerName;
        this.weight = weight;
        this.amount = amount;
        this.rate = rate;
        this.shift = shift;
        this.date = date;
        this.debit = debit;
        this.credit = credit;
        this.unique_id = unique_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public int getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(int unique_id) {
        this.unique_id = unique_id;
    }
}
