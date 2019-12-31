package com.example.dairydaily.Others;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DairyDaily";

    // Buyer table variables
    private static final String buyers_table = "Buyer_Table";
    private static final String BTCOL1 = "ID";
    private static final String BTCOL2 = "Name";
    private static final String BTCOL3 = "PhoneNumber";
    private static final String BTCOL4 = "Address";
    private static final String BTCOL5 = "Status";

    // Seller table variables
    private static final String sellers_table = "Seller_Table";
    private static final String STCOL1 = "ID";
    private static final String STCOL2 = "Name";
    private static final String STCOL3 = "PhoneNumber";
    private static final String STCOL4 = "Address";
    private static final String STCOL5 = "Status";

    private Context context;

    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createBuyerTable = "CREATE TABLE " + buyers_table + " (ID INTEGER, " +
                "Name TEXT, PhoneNumber TEXT, Address TEXT, Status TEXT)";
        String createSellerTable = "CREATE TABLE " + sellers_table + " (ID INTEGER, " +
                "Name TEXT, PhoneNumber TEXT, Address TEXT, Status TEXT)";
        db.execSQL(createBuyerTable);
        db.execSQL(createSellerTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + buyers_table);
        db.execSQL("DROP TABLE IF EXISTS " + sellers_table);
    }

    // Adds a new buyer to buyers table
    public boolean addBuyer(int id, String name, String phoneNumber, String address, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BTCOL1, id);
        contentValues.put(BTCOL2, name);
        contentValues.put(BTCOL3, phoneNumber);
        contentValues.put(BTCOL4, address);
        contentValues.put(BTCOL5, status);

        long result = db.insert(buyers_table, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    // Gets all the data in the buyers table
    public Cursor getAllBuyers(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + buyers_table;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    // Delete a seller from database
    public void deleteSeller(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + sellers_table + " WHERE " + STCOL1 + " = '" + id + "'";
        db.execSQL(query);
    }

    // Adds a new seller to seller's table
    public boolean addSeller(int id, String name, String phoneNumber, String address, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STCOL1, id);
        contentValues.put(STCOL2, name);
        contentValues.put(STCOL3, phoneNumber);
        contentValues.put(STCOL4, address);
        contentValues.put(STCOL5, status);

        long result = db.insert(sellers_table, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    // Updates buyer info
    public void updateBuyer(int old_id, int new_id, String name, String phone_number, String address, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + buyers_table + " SET " + BTCOL1 + " = '" + new_id + "'" +
                " AND " + BTCOL2 + " = '" + name + "' AND " + BTCOL3 + " = '" + phone_number + "' AND " +
                BTCOL4 + " = '" + address + "' AND " + BTCOL5 + " = '" + status + "' WHERE " + BTCOL1 + " = '" + old_id +"'";
        db.execSQL(query);
    }

    // Gets all the data in the sellers table
    public Cursor getAllSellers(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + sellers_table;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    // Delete a buyer from database
    public void deleteBuyer(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + buyers_table + " WHERE " + BTCOL1 + " = '" + id + "'";
        db.execSQL(query);
    }

    // Updates seller info
    public void updateSeller(int old_id, int new_id, String name, String phone_number, String address, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + sellers_table + " SET " + STCOL1 + " = '" + new_id + "'" +
                " AND " + STCOL2 + " = '" + name + "' AND " + STCOL3 + " = '" + phone_number + "' AND " +
                STCOL4 + " = '" + address + "' AND " + STCOL5 + " = '" + status + "' WHERE " + STCOL1 + " = '" + old_id +"'";
        db.execSQL(query);
    }
}
