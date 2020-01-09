package com.juicebox.dairydaily.Others;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.juicebox.dairydaily.Models.DailySalesObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DairyDaily";

    private static final String TAG = "DbHelper";

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

    // Milk Sale tables
    private static final String milk_sale_table = "MilkSale";
    private static final String Sale_COL1 = "ID";
    private static final String Sale_COL2 = "BuyerName";
    private static final String Sale_COL3 = "Weight";
    private static final String Sale_COL4 = "Amount";
    private static final String Sale_COL5 = "Rate";
    private static final String Sale_COL6 = "Shift";
    private static final String Sale_COL7 = "Date";
    private static final String Sale_COL8 = "Debit";
    private static final String Sale_COL9 = "Credit";

    // SNF tables
    private static final String SNF_table = "SNF_Table";
    private static final String Buffalo_SNF = "Buffalo_SNF";
    private static final String[] SNFcolumns = {"SNF76", "SNF77", "SNF78", "SNF79", "SNF80", "SNF81", "SNF82", "SNF83",
                                             "SNF84", "SNF85", "SNF86", "SNF87", "SNF88", "SNF89", "SNF90", "SNF91",
                                                "SNF92", "SNF93", "SNF94", "SNF95"};
    private static final String fat_column = "FatColumn";

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
        String createRateTable = "CREATE TABLE " + rate_table + "(Rate TEXT)";
        String createSNFTable = "CREATE TABLE " + SNF_table + " (FatColumn TEXT, " +
                "SNF76 TEXT, SNF77 TEXT, SNF78 TEXT, SNF79 TEXT, SNF80 TEXT, SNF81 TEXT, " +
                "SNF82 TEXT, SNF83 TEXT, SNF84 TEXT, SNF85 TEXT, SNF86 TEXT, SNF87 TEXT, SNF88 TEXT, SNF89 TEXT, " +
                "SNF90 TEXT, SNF91 TEXT, SNF92 TEXT, SNF93 TEXT, SNF94 TEXT, SNF95 TEXT)";
        String createBuffaloSNFTable = "CREATE TABLE " + Buffalo_SNF + " (FatColumn TEXT, " +
                "SNF76 TEXT, SNF77 TEXT, SNF78 TEXT, SNF79 TEXT, SNF80 TEXT, SNF81 TEXT, " +
                "SNF82 TEXT, SNF83 TEXT, SNF84 TEXT, SNF85 TEXT, SNF86 TEXT, SNF87 TEXT, SNF88 TEXT, SNF89 TEXT, " +
                "SNF90 TEXT, SNF91 TEXT, SNF92 TEXT, SNF93 TEXT, SNF94 TEXT, SNF95 TEXT)";
        String createSalesTable = "CREATE TABLE " + milk_sale_table + " (ID INTEGER, " +
                "BuyerName TEXT, Weight String, Amount String, Rate String, Shift TEXT, Date TEXT, Credit String, Debit String)";

        db.execSQL(createSNFTable);
        db.execSQL(createRateTable);
        db.execSQL(createBuyerTable);
        db.execSQL(createSellerTable);
        db.execSQL(createBuffaloSNFTable);
        db.execSQL(createSalesTable);
        //context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + buyers_table);
        db.execSQL("DROP TABLE IF EXISTS " + sellers_table);
        db.execSQL("DROP TABLE IF EXISTS " + rate_table);
        db.execSQL("DROP TABLE IF EXISTS " + SNF_table);
        db.execSQL("DROP TABLE IF EXISTS " + Buffalo_SNF);
        db.execSQL("DROP TABLE IF EXISTS " + milk_sale_table);
    }

    // Add sales data to sales table
    public boolean addSalesEntry(int id, String name, double weight, double amount, double rate, String shift, String date
                                    ,double debit, double credit){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("BuyerName", name);
        contentValues.put("Weight", String.valueOf(weight));
        contentValues.put("Amount", String.valueOf(amount));
        contentValues.put("Rate", String.valueOf(rate));
        contentValues.put("Shift", shift);
        contentValues.put("Date", date);
        contentValues.put("Debit", String.valueOf(debit));
        contentValues.put("Credit", String.valueOf(credit));

        long result = db.insert(milk_sale_table, null, contentValues);
        return result != -1;
    }

    // Get sales data
    public Cursor getSalesData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + milk_sale_table;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    // Get Sales data for a day
    public ArrayList<DailySalesObject> getDailySalesData(String dateToGet){
        ArrayList<DailySalesObject> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + milk_sale_table;
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                if(data.getString(6).equals(dateToGet)){
                    int id = data.getInt(0);
                    String name = data.getString(1);
                    String weight = data.getString(2);
                    String amount = data.getString(3);
                    String rate = data.getString(4);
                    String shift = data.getString(5);
                    String date = data.getString(6);
                    String debit = data.getString(7);
                    String credit = data.getString(8);
                    DailySalesObject object = new DailySalesObject(id, name, Double.valueOf(weight), Double.valueOf(amount),
                                            Double.valueOf(rate), shift, date, Double.valueOf(debit), Double.valueOf(credit));
                    list.add(object);
                }
            }
        }
        data.close();
        return list;
    }

    //Clear the cow SNF table
    public void clearSNFTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + SNF_table);
    }

    // Create the cow snf table with filepath pointing to the file on the phone
    public boolean createSNFTable(String filePath){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        FileInputStream inputStream;
        List<List<String>> lines= new ArrayList<>();
        List<String> fatList = new ArrayList<>();

        for(double i = 3.0; i <= 6.0; i = i + 0.1){
            Double truncatedValue = BigDecimal.valueOf(i).setScale(1, RoundingMode.HALF_UP).doubleValue();
            String fat = String.valueOf(truncatedValue);
            fatList.add(fat);
        }

        File file = new File(filePath);

        try {
            inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try{
                String csvLine;
                while((csvLine = reader.readLine()) != null){
                    String[] values = csvLine.split(",");
                    lines.add(Arrays.asList(values));
                }
                Log.d(TAG, "Lines: " + lines.toString());
                inputStream.close();

                for(int i = 0; i<lines.size(); i++){
                    Log.d(TAG, "Value: " + i);
                    contentValues.put(fat_column, fatList.get(i));
                    contentValues.put(SNFcolumns[0], String.valueOf(lines.get(i).get(0)));
                    contentValues.put(SNFcolumns[1], String.valueOf(lines.get(i).get(1)));
                    contentValues.put(SNFcolumns[2], String.valueOf(lines.get(i).get(2)));
                    contentValues.put(SNFcolumns[3], String.valueOf(lines.get(i).get(3)));
                    contentValues.put(SNFcolumns[4], String.valueOf(lines.get(i).get(4)));
                    contentValues.put(SNFcolumns[5], String.valueOf(lines.get(i).get(5)));
                    contentValues.put(SNFcolumns[6], String.valueOf(lines.get(i).get(6)));
                    contentValues.put(SNFcolumns[7], String.valueOf(lines.get(i).get(7)));
                    contentValues.put(SNFcolumns[8], String.valueOf(lines.get(i).get(8)));
                    contentValues.put(SNFcolumns[9], String.valueOf(lines.get(i).get(9)));
                    contentValues.put(SNFcolumns[10], String.valueOf(lines.get(i).get(10)));
                    contentValues.put(SNFcolumns[11], String.valueOf(lines.get(i).get(11)));
                    contentValues.put(SNFcolumns[12], String.valueOf(lines.get(i).get(12)));
                    contentValues.put(SNFcolumns[13], String.valueOf(lines.get(i).get(13)));
//                    contentValues.put(SNFcolumns[14], String.valueOf(lines.get(i).get(14)));
//                    contentValues.put(SNFcolumns[15], String.valueOf(lines.get(i).get(15)));
//                    contentValues.put(SNFcolumns[16], String.valueOf(lines.get(i).get(16)));
//                    contentValues.put(SNFcolumns[17], String.valueOf(lines.get(i).get(17)));
//                    contentValues.put(SNFcolumns[18], String.valueOf(lines.get(i).get(18)));
//                    contentValues.put(SNFcolumns[19], String.valueOf(lines.get(i).get(19)));


                    /*
                     * Important Note to self
                     * lines.get(i).get() represents number of columns in excel file
                     * lines.size() represnets number of rows in excel file
                     *
                     * */


                    db.insert(SNF_table, null, contentValues);
                    contentValues.clear();
                }
            }
            catch(Exception e){

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean createBuffaloSNFTable(String filePath){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        FileInputStream inputStream;
        List<List<String>> lines= new ArrayList<>();
        List<String> fatList = new ArrayList<>();

        for(double i = 3.0; i <= 10.1; i = i + 0.1){
            Double truncatedValue = BigDecimal.valueOf(i).setScale(1, RoundingMode.HALF_UP).doubleValue();
            String fat = String.valueOf(truncatedValue);
            fatList.add(fat);
        }

        File file = new File(filePath);

        try {
            inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try{
                String csvLine;
                while((csvLine = reader.readLine()) != null){
                    String[] values = csvLine.split(",");
                    lines.add(Arrays.asList(values));
                }
                Log.d(TAG, "Lines: " + lines.toString());
                inputStream.close();

                for(int i = 0; i<lines.size(); i++){
                    Log.d(TAG, "Value: " + i);
                    contentValues.put(fat_column, fatList.get(i));
                    contentValues.put(SNFcolumns[0], String.valueOf(lines.get(i).get(0)));
                    contentValues.put(SNFcolumns[1], String.valueOf(lines.get(i).get(1)));
                    contentValues.put(SNFcolumns[2], String.valueOf(lines.get(i).get(2)));
                    contentValues.put(SNFcolumns[3], String.valueOf(lines.get(i).get(3)));
                    contentValues.put(SNFcolumns[4], String.valueOf(lines.get(i).get(4)));
                    contentValues.put(SNFcolumns[5], String.valueOf(lines.get(i).get(5)));
                    contentValues.put(SNFcolumns[6], String.valueOf(lines.get(i).get(6)));
                    contentValues.put(SNFcolumns[7], String.valueOf(lines.get(i).get(7)));
                    contentValues.put(SNFcolumns[8], String.valueOf(lines.get(i).get(8)));
                    contentValues.put(SNFcolumns[9], String.valueOf(lines.get(i).get(9)));
                    contentValues.put(SNFcolumns[10], String.valueOf(lines.get(i).get(10)));
                    contentValues.put(SNFcolumns[11], String.valueOf(lines.get(i).get(11)));
                    contentValues.put(SNFcolumns[12], String.valueOf(lines.get(i).get(12)));
                    contentValues.put(SNFcolumns[13], String.valueOf(lines.get(i).get(13)));
//                    contentValues.put(SNFcolumns[14], String.valueOf(lines.get(i).get(14)));
//                    contentValues.put(SNFcolumns[15], String.valueOf(lines.get(i).get(15)));
//                    contentValues.put(SNFcolumns[16], String.valueOf(lines.get(i).get(16)));
//                    contentValues.put(SNFcolumns[17], String.valueOf(lines.get(i).get(17)));
//                    contentValues.put(SNFcolumns[18], String.valueOf(lines.get(i).get(18)));
//                    contentValues.put(SNFcolumns[19], String.valueOf(lines.get(i).get(19)));

                    /*
                     * Important Note to self
                     * lines.get(i).get() represents number of columns in excel file
                     * lines.size() represnets number of rows in excel file
                     *
                     * */


                    db.insert(Buffalo_SNF, null, contentValues);
                    contentValues.clear();
                }
            }
            catch(Exception e){

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return true;
    }

    public void clearBuffaloSNFTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Buffalo_SNF);
    }

    public Cursor getBuffaloSNFTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + Buffalo_SNF;
        Cursor data = db.rawQuery(query, null);
        data.close();
        return data;
    }


    public Cursor getSNFTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + SNF_table;
        Cursor data = db.rawQuery(query, null);
        data.close();
        return data;
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
        data.close();
        return data;
    }

    // Delete a buyer from database
    public void deleteBuyer(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + buyers_table + " WHERE " + BTCOL1 + " = '" + id + "'";
        db.execSQL(query);
    }

    // Returns the id of a buyer given name and phone number
    public int getBuyerId(String name, String phone_number){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + BTCOL1 + " FROM " + buyers_table + " WHERE " + BTCOL2
                + " ='" + name + "' AND " + BTCOL3 + " ='" + phone_number + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                int id = data.getInt(0);
                return id;
            }
        }
        data.close();
        return 0;
    }


    public String getRate(String fat, String snf) {
        String rate = "";
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            String query = "SELECT " + snf + " FROM " + SNF_table + " WHERE " + fat_column + " ='" + fat + "'";
            Cursor data = db.rawQuery(query, null);

            if(data.getCount() != 0){
                while(data.moveToNext()){
                    rate = data.getString(0);
                }
            }
        }
        catch (Exception e){
            rate = "Not Found";
        }
        return rate;
    }

    // Returns the name of a buyer with id passed
    public String getBuyerName(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String name = "";
        String query = "SELECT " + BTCOL2 + " FROM " + buyers_table + " WHERE " + BTCOL1 + " ='" + id + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                name = data.getString(0);
            }
        }
        else{
            Toast.makeText(context, "Empty buyer list", Toast.LENGTH_SHORT).show();
            return "";
        }
        data.close();
        return name;
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
        data.close();
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
        data.close();
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
        data.close();
        return 0;
    }

    // Sets the rate value
    public boolean setRate(double rate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Rate", String.valueOf(rate));
        long result = db.insert(rate_table, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    // Updates the rate value
    public void updateRate(double rate){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + rate_table + " SET " + COL1 +
                " = '" + String.valueOf(rate) + "'";
        db.execSQL(query);
    }

    // Returns the rate value
    public double getRate(){
        SQLiteDatabase db = this.getWritableDatabase();
        double rate = 0;
        String query = "SELECT " + COL1 + " FROM " + rate_table;
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() == 0){
            Toast.makeText(context, "No rate price", Toast.LENGTH_SHORT).show();
            return 0;
        }
        else{
            while(data.moveToNext()){
                String returned = data.getString(0);
                rate = Double.valueOf(returned);
            }
        }
        data.close();
        return rate;
    }
}
