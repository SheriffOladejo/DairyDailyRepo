package com.example.dairydaily.Others;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.widget.Toast;

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

    // Milk rate table
    private static final String rate_table = "Rate_Table";
    private static final String COL1 = "Rate";

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
        String createRateTable = "CREATE TABLE " + rate_table + "(Rate INTEGER)";
        db.execSQL(createRateTable);
        db.execSQL(createBuyerTable);
        db.execSQL(createSellerTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + buyers_table);
        db.execSQL("DROP TABLE IF EXISTS " + sellers_table);
        db.execSQL("DROP TABLE IF EXISTS " + rate_table);
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

    // Gets all the buyers in the buyers table
    public Cursor getAllBuyers(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + buyers_table;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    // Delete a buyer from database
    public void deleteBuyer(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + buyers_table + " WHERE " + BTCOL1 + " = '" + id + "'";
        db.execSQL(query);
    }

    // Returns the name of a buyer with id passed
    public String getBuyerName(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + BTCOL2 + " FROM " + buyers_table + " WHERE " + BTCOL1 + " ='" + id + "'";
        Cursor data = db.rawQuery(query, null);
        return data.getString(1);
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
                ", " + BTCOL2 + " = '" + name + "', " + BTCOL3 + " = '" + phone_number + "', " +
                BTCOL4 + " = '" + address + "', " + BTCOL5 + " = '" + status + "' WHERE " + BTCOL1 + " = '" + old_id +"'";
        db.execSQL(query);
    }

    // Gets all the sellers in the sellers table
    public Cursor getAllSellers(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + sellers_table;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    // Updates seller info
    public void updateSeller(int old_id, int new_id, String name, String phone_number, String address, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + sellers_table + " SET " + STCOL1 + " = '" + new_id + "'" +
                ", " + STCOL2 + " = '" + name + "', " + STCOL3 + " = '" + phone_number + "', " +
                STCOL4 + " = '" + address + "', " + STCOL5 + " = '" + status + "' WHERE " + STCOL1 + " = '" + old_id +"'";
        db.execSQL(query);
    }

    // Returns the name of a seller with id passed
    public String getSellerName(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + BTCOL2 + " FROM " + sellers_table + " WHERE " + STCOL1 + " ='" + id + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                return data.getString(0);
            }
        }
        return "";
    }

    // Returns the id of a seller given name and phone number
    public int getSellerId(String name, String phone_number){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + STCOL1 + " FROM " + sellers_table + " WHERE " + STCOL2
                + " ='" + name + "' AND " + STCOL3 + " ='" + phone_number + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                int id = data.getInt(0);
                return id;
            }
        }
        return 0;
    }

    // Sets the rate value
    public boolean setRate(int rate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Rate", rate);
        long result = db.insert(rate_table, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    // Updates the rate value
    public void updateRate(int rate){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + rate_table + " SET " + COL1 +
                " = '" + rate + "'";
        db.execSQL(query);
    }

    // Returns the rate value
    public int getRate(){
        SQLiteDatabase db = this.getWritableDatabase();
        int rate = 0;
        String query = "SELECT " + COL1 + " FROM " + rate_table;
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() == 0){
            Toast.makeText(context, "No rate price", Toast.LENGTH_SHORT).show();
        }
        else{
            while(data.moveToNext()){
                rate = data.getInt(0);
            }
        }
        return rate;
    }
}
