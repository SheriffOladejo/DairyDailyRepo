package com.dixit.dairydaily.Models;

public class ReceiveCashModel {
    private String date, title, credit, debit;
    private int id;
    private int unique_Id;

    public ReceiveCashModel() {
    }

    public ReceiveCashModel(String date, String title, String credit, String debit, int id, int unique_Id) {
        this.date = date;
        this.title = title;
        this.credit = credit;
        this.debit = debit;
        this.id = id;
        this.unique_Id = unique_Id;
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

    public int getUnique_Id() {
        return unique_Id;
    }

    public void setUnique_Id(int unique_Id) {
        this.unique_Id = unique_Id;
    }
}
