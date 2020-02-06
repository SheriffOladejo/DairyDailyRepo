package com.juicebox.dairydaily.Models;

public class ReceiveCashModel {
    private String date, title, credit, debit;
    private int id;

    public ReceiveCashModel() {
    }

    public ReceiveCashModel(String date, String title, String credit, String debit, int id) {
        this.date = date;
        this.title = title;
        this.credit = credit;
        this.debit = debit;
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getDebit() {
        return debit;
    }

    public void setDebit(String debit) {
        this.debit = debit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
