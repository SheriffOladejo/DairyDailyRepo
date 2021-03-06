package com.dixit.dairydaily.Others;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.dixit.dairydaily.Models.BuyerRegisterModel;
import com.dixit.dairydaily.Models.CustomerReportModel;
import com.dixit.dairydaily.Models.DailyBuyObject;
import com.dixit.dairydaily.Models.DailySalesObject;
import com.dixit.dairydaily.Models.MilkHistoryObject;
import com.dixit.dairydaily.Models.PaymentRegisterModel;
import com.dixit.dairydaily.Models.AddProductModel;
import com.dixit.dairydaily.Models.ProductSaleModel;
import com.dixit.dairydaily.Models.ReceiveCashListModel;
import com.dixit.dairydaily.Models.ReceiveCashModel;
import com.dixit.dairydaily.Models.ReportByDateModels;
import com.dixit.dairydaily.Models.ShiftReportModel;
import com.dixit.dairydaily.Models.ViewAllEntryModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.Others.UtilityMethods.truncate;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DairyDaily";

    private static final String TAG = "DbHelper";

    // Receive cash table columns
    private static final String receive_cash_table = "Receive_Cash";
    private static final String Rec_COL6 = "Unique_ID";
    private static final String Rec_COL5 = "ID";
    private static final String Rec_COL1 = "Date";
    private static final String Rec_COL2 = "Credit";
    private static final String Rec_COL3 = "Debit";
    private static final String Rec_COL4 = "Title";
    private static final String Rec_COL7 = "Shift";
    private static final String Rec_COL8 = "Weight";
    private static final String Rec_COL9 = "Date_In_Long";

    // Expiry date
    private static final String expiry_date_table = "Expiry_Date";
    private static final String EXCol1 = "Date";
    private static final String EXCol2 = "HasExpired";

    // Product Sale table columns
    private static final String product_sale_table = "Product_Sale";
    private static final String Pro_COL1 = "ID";
    private static final String Pro_COL2 = "Name";
    private static final String Pro_COL3 = "Units";
    private static final String Pro_COL4 = "ProductName";
    private static final String Pro_COL5 = "Amount";
    private static final String Pro_COL6 = "Date";

    // Buyer table columns
    private static final String buyers_table = "Buyer_Table";
    private static final String BTCOL1 = "ID";
    private static final String BTCOL2 = "Name";
    private static final String BTCOL3 = "PhoneNumber";
    private static final String BTCOL4 = "Address";
    private static final String BTCOL5 = "Status";

    // Seller table columns
    private static final String sellers_table = "Seller_Table";
    private static final String STCOL1 = "ID";
    private static final String STCOL2 = "Name";
    private static final String STCOL3 = "PhoneNumber";
    private static final String STCOL4 = "Address";
    private static final String STCOL5 = "Status";

    // Milk rate table
    private static final String rate_table = "Rate_Table";
    private static final String COL1 = "Rate";

    // Milk Sale columns
    private static final String milk_sale_table = "MilkSale";
    private static final String Unique_ID_Sale = "Unique_ID";
    private static final String Sale_COL1 = "ID";
    private static final String Sale_COL2 = "BuyerName";
    private static final String Sale_COL3 = "Weight";
    private static final String Sale_COL4 = "Amount";
    private static final String Sale_COL5 = "Rate";
    private static final String Sale_COL6 = "Shift";
    private static final String Sale_COL7 = "Date";
    private static final String Sale_COL8 = "Debit";
    private static final String Sale_COL9 = "Credit";
    private static final String Sale_COL10 = "Date_In_Long";

    // Product Table
    private static final String product_table = "ProductTable";
    private static final String Product_COL1 = "Product_Name";
    private static final String Product_COL2 = "Price_Per_Unit";

    // Milk buy columns
    private static final String milk_buy_table = "MilkBuy";
    private static final String Unique_ID_Buy = "Unique_ID";
    private static final String Buy_COL1 = "ID";
    private static final String Buy_COL2 = "SellerName";
    private static final String Buy_COL3 = "Weight";
    private static final String Buy_COL4 = "Fat";
    private static final String Buy_COL5 = "SNF";
    private static final String Buy_COL6 = "Amount";
    private static final String Buy_COL7 = "Date";
    private static final String Buy_COL8 = "Shift";
    private static final String Buy_COL9 = "Rate";
    private static final String Buy_COL10 = "Type";
    private static final String Buy_COL11 = "Date_In_Long";

    // SNF tables
    private static final String SNF_table = "SNF_Table";
    private static final String Buffalo_SNF = "Buffalo_SNF";
    private static final String[] SNFcolumns = {"SNF76", "SNF77", "SNF78", "SNF79", "SNF80", "SNF81", "SNF82", "SNF83",
                                             "SNF84", "SNF85", "SNF86", "SNF87", "SNF88", "SNF89", "SNF90", "SNF91",
                                                "SNF92", "SNF93", "SNF94", "SNF95"};
    private static final String fat_column = "FatColumn";
    Context context;


    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        //context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProductSaleTable = "CREATE TABLE " + product_sale_table + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, Units TEXT, ProductName TEXT, Amount TEXT, Date Text)";
        String createBuyerTable = "CREATE TABLE " + buyers_table + " (ID INTEGER, " +
                "Name TEXT, PhoneNumber TEXT, Address TEXT, Status TEXT)";
        String createExpiryTable = "CREATE TABLE " + expiry_date_table + " (Date TEXT, HasExpired TEXT)";
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
        String createSalesTable = "CREATE TABLE " + milk_sale_table + " (Unique_ID INTEGER PRIMARY KEY AUTOINCREMENT, ID INTEGER, " +
                "BuyerName TEXT, Weight String, Amount String, Rate String, Shift TEXT, Date TEXT, Credit String, Debit String, Recog_Date TEXT, Date_In_Long TEXT)";
        String createBuyTable = "CREATE TABLE " + milk_buy_table + " (Unique_ID INTEGER PRIMARY KEY AUTOINCREMENT, ID INTEGER, " +
                "SellerName TEXT,  Weight TEXT, Fat TEXT, SNF TEXT, Amount TEXT, Date TEXT, Shift TEXT, Rate TEXT, Type TEXT, Date_In_Long TEXT)";
        String createReceiveCashTable = "CREATE TABLE " + receive_cash_table + " (Unique_ID INTEGER PRIMARY KEY AUTOINCREMENT, ID INTEGER, Date TEXT, Credit TEXT, Debit TEXT, Title TEXT, Shift TEXT, Weight TEXT,Date_In_Long TEXT)";
        String createProductTable = "CREATE TABLE " + product_table + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, Product_Name TEXT, Price_Per_Unit TEXT)";

        db.execSQL(createProductSaleTable);
        db.execSQL(createExpiryTable);
        db.execSQL(createProductTable);
        db.execSQL(createBuyTable);
        db.execSQL(createSNFTable);
        db.execSQL(createRateTable);
        db.execSQL(createReceiveCashTable);
        db.execSQL(createBuyerTable);
        db.execSQL(createSellerTable);
        db.execSQL(createBuffaloSNFTable);
        db.execSQL(createSalesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + buyers_table);
        db.execSQL("DROP TABLE IF EXISTS " + sellers_table);
        db.execSQL("DROP TABLE IF EXISTS " + rate_table);
        db.execSQL("DROP TABLE IF EXISTS " + expiry_date_table);
        db.execSQL("DROP TABLE IF EXISTS " + SNF_table);
        db.execSQL("DROP TABLE IF EXISTS " + Buffalo_SNF);
        db.execSQL("DROP TABLE IF EXISTS " + milk_sale_table);
        db.execSQL("DROP TABLE IF EXISTS " + milk_buy_table);
        db.execSQL("DROP TABLE IF EXISTS " + receive_cash_table);
        db.execSQL("DROP TABLE IF EXISTS " + product_table);
        db.execSQL("DROP TABLE IF EXISTS " + product_sale_table);
    }

    // Set expiry date
    public void setExpiryDate(String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String query = "DELETE FROM " + expiry_date_table;
        db.execSQL(query);
        contentValues.put(EXCol1, date);
        db.insert(expiry_date_table, null, contentValues);
    }

    public Cursor getExpiryDate(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + expiry_date_table;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    // Add buy data to buy table
    public boolean addBuyEntry(int id, String name, String weight, String amount, String rate, String shift, String date, String fat, String snf, String type, String date_in_long){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Buy_COL1, id);
        contentValues.put(Buy_COL2, name);
        contentValues.put(Buy_COL3, String.valueOf(weight));
        contentValues.put(Buy_COL4, String.valueOf(fat));
        contentValues.put(Buy_COL5, String.valueOf(snf));
        contentValues.put(Buy_COL6, String.valueOf(amount));
        contentValues.put(Buy_COL8, shift);
        contentValues.put(Buy_COL9, String.valueOf(rate));
        contentValues.put(Buy_COL7, date);
        contentValues.put(Buy_COL10, type);
        contentValues.put(Buy_COL11, date_in_long);
        return db.insert(milk_buy_table, null, contentValues) != -1;
    }

    // Get buy data
    public Cursor getBuyData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + milk_buy_table;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void clearMilkBuyTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " +milk_buy_table);
    }

    // Add new product
    public boolean addProduct(String productName, String rate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Product_COL1, productName);
        contentValues.put(Product_COL2, rate);
        long result = db.insert(product_table, null, contentValues);
        if (result == -1) {
            return false;
        }
        else
            return true;
    }

    // Get all products
    public ArrayList<AddProductModel> getProducts(){
        ArrayList<AddProductModel> list = new ArrayList<>();
        list.add(new AddProductModel("","Select Product", "0.00"));
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + product_table;
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                String product_name = data.getString(data.getColumnIndex(Product_COL1));
                String rate = data.getString(data.getColumnIndex(Product_COL2));
                String id = data.getInt(data.getColumnIndex("ID"))+"";
                AddProductModel model = new AddProductModel(id,product_name, rate);
                list.add(model);
            }
        }
        else{
            Log.d(TAG, "getProducts: no products to display");
        }
        return list;
    }

    public void updateProducts(String id, String name, String rate){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "update " + product_table + " set " + Product_COL1 + " ='" + name + "', " + Product_COL2 + " ='" + rate + "' where ID ='" + id + "'";
        try {
            db.execSQL(query);
        }
        catch (Exception e){
            toast(context, "Unable to update");
        }
    }

    public void deleteProduct(String id){
        String query = "delete from " + product_table + " where ID='" + id +"'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    public void updateProductSale(String id, String name, String product_name, String units, String amount){
        String query = "update " + product_sale_table + " set " + Pro_COL2 + "='" + name + "', " +
                Pro_COL4 + "='" + product_name + "', " + Pro_COL3 + "='" + units + "', " + Pro_COL5 + "='" + amount + "' where " +
                Pro_COL1 + "='" + id +"'";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(query);
        }
        catch (Exception e){
            toast(context, "Unable to update");
        }
    }

    public void deleteProductSale(String id){
        String query = "delete from " +product_sale_table + " where " +Pro_COL1 + "='" + id +"'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    // Add new product sale
    public boolean addProductSale(int id, String name, String product_name, String units, String amount, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Pro_COL2, name);
        contentValues.put(Pro_COL3, units);
        contentValues.put(Pro_COL4, product_name);
        contentValues.put(Pro_COL5, amount);
        contentValues.put(Pro_COL6, date);

        long result = db.insert(product_sale_table, null, contentValues);
        return result != -1;
    }

    // Returns all  products buyers have bought
    public ArrayList<ProductSaleModel> getProductSale(){
        ArrayList<ProductSaleModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + product_sale_table;
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                int id = data.getInt(data.getColumnIndex(Pro_COL1));
                String name = data.getString(data.getColumnIndex(Pro_COL2));
                String product_name = data.getString(data.getColumnIndex(Pro_COL4));
                String units = data.getString(data.getColumnIndex(Pro_COL3));
                String amount = data.getString(data.getColumnIndex(Pro_COL5));
                String date = data.getString(data.getColumnIndex(Pro_COL6));

                ProductSaleModel model = new ProductSaleModel(id, name, product_name, units, amount, date);
                list.add(model);
            }
        }
        return list;
    }

    // Get milk history
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<MilkHistoryObject> getMilkHistory(String startDate, String endDate){
        ArrayList<MilkHistoryObject> list = new ArrayList<>();
        double weightTotal = 0;
        double amountTotal = 0;
        double averageFat = 0;
        ArrayList<DailyBuyObject> dailyDataMorning;
        ArrayList<DailyBuyObject> dailyDataEvening;

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<LocalDate> totalDates = new ArrayList<>();
        while(!start.isAfter(end)){
            totalDates.add(start);
            start = start.plusDays(1);
        }
        for(LocalDate date : totalDates){
            dailyDataMorning = getDailyBuyData(String.valueOf(date), "Morning");
            dailyDataEvening = getDailyBuyData(String.valueOf(date), "Evening");
            weightTotal = 0;
            amountTotal = 0;
            averageFat = 0;
            for(DailyBuyObject model : dailyDataMorning){
                weightTotal += Double.valueOf(model.getWeight());
                amountTotal += Double.valueOf(model.getAmount());
                averageFat += Double.valueOf(model.getFat());
            }
            Log.d(TAG, "getMilkHistory: Total Weight" + weightTotal + " " + String.valueOf(date));
            double fat = 0;
            try{
                fat = averageFat/dailyDataMorning.size();
            }
            catch(Exception e){

            }
            MilkHistoryObject object = new MilkHistoryObject("Morning", String.valueOf(date), String.valueOf(truncate(amountTotal)), String.valueOf(truncate(weightTotal)), String.valueOf(truncate(fat)));
            weightTotal = 0;
            amountTotal = 0;
            averageFat = 0;
            list.add(object);
            for(DailyBuyObject model : dailyDataEvening){
                weightTotal += Double.valueOf(model.getWeight());
                amountTotal += Double.valueOf(model.getAmount());
                averageFat += Double.valueOf(model.getFat());
            }
            Log.d(TAG, "getMilkHistory: Total Weight" + weightTotal);
            double fat2 = 0;
            try{
                fat2 = averageFat/dailyDataEvening.size();
            }
            catch(Exception e){

            }
            MilkHistoryObject object2 = new MilkHistoryObject("Evening", String.valueOf(date), String.valueOf(truncate(amountTotal)), String.valueOf(truncate(weightTotal)), String.valueOf(truncate(fat2)));
            list.add(object2);
            Log.d(TAG, "getMilkHistory: " + list.size());
        }
        return list;
    }

    public void clearReceiveCash(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + receive_cash_table);
    }

    // Add receive cash entry
    public boolean addReceiveCash(int id, String date, String credit, String debit, String title, String shift, String weight, String date_in_long){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Rec_COL5, id);
        contentValues.put(Rec_COL1, date);
        contentValues.put(Rec_COL2, credit);
        contentValues.put(Rec_COL3, debit);
        contentValues.put(Rec_COL4, title);
        contentValues.put(Rec_COL7, shift);
        contentValues.put(Rec_COL8, weight);
        contentValues.put(Rec_COL9, date_in_long);
        //Log.d(TAG, "addReceiveCash: " + title);

        long result = db.insert(receive_cash_table, null, contentValues);
        if(result == -1){
            //Log.d(TAG, "addReceiveCash: cannot add");
            return false;
        }
        else{
            return true;
        }
    }

    public void updateReceiveCash(int unique_id, int id, String date, String title, String debit, String credit, String shift, String weight, String date_in_long){
        SQLiteDatabase db = this.getWritableDatabase();
        String query2 = "DELETE FROM " + receive_cash_table + " WHERE " + Rec_COL6 + " ='" + unique_id + "'";
        db.execSQL(query2);
        addReceiveCash(id, date, credit, debit, title, shift, weight, date_in_long);
        String query = "UPDATE " + receive_cash_table + " SET " + Rec_COL1 + " ='" + date + "' AND " + Rec_COL2 + " ='" + credit + "' AND "
                + Rec_COL3 + " ='" + debit + "' AND " + Rec_COL5 + " ='" + id + "' AND " + Rec_COL4 + " ='" + title + "' WHERE " + Rec_COL6 + " ='" + unique_id + "'";
        Log.d(TAG, "Query: " + query);

    }

    public void deleteReceiveCash(int id, String desc, String date){
        String query;
        if(desc.equals("user_id")){
            query = "DELETE FROM " + receive_cash_table + " WHERE " + Rec_COL5 + " ='" + id + "'";
        }
        else if(desc.equals("delete_from_milk_sale")){
            query = "DELETE FROM " + receive_cash_table + " WHERE " + Rec_COL5+ " ='" + id + "' AND " + Rec_COL1 + " ='" + date + "'";
        }
        else{
            query = "DELETE FROM " + receive_cash_table + " WHERE " + Rec_COL6 + " ='" + id + "'";
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    public void deleteReceiveCash(String startDate, String endDate){
        String query = "delete from " + receive_cash_table + " where " + Rec_COL1 +" between '" + startDate + "' and '" + endDate + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    public Cursor getReceiveCash(){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + receive_cash_table;

        Cursor data = db.rawQuery(query, null);
        return data;
    }

    // Method that gets Buyer Register entries by date
    public ArrayList<BuyerRegisterModel> getInvoice(String startDate, String endDate){
        ArrayList<BuyerRegisterModel> list = new ArrayList<>();
        ArrayList<BuyerRegisterModel> users = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + milk_sale_table;// + " WHERE " + Sale_COL7 + " BETWEEN '" + startDate + "' AND '" + endDate + "'";
        String query2 = "SELECT ID, Name FROM " + buyers_table;

        Cursor data = db.rawQuery(query, null);
        Cursor data2 = db.rawQuery(query2, null);

        if(data.getCount() != 0){
            while(data.moveToNext()){
                int id = data.getInt(data.getColumnIndex(Sale_COL1));
                String name = data.getString(data.getColumnIndex(Sale_COL2));
                String date = data.getString(data.getColumnIndex(Sale_COL7));
                Log.d("BuyerRegister", "getBuyerRegister: " + date + date.length());
                String weight = data.getString(data.getColumnIndex(Sale_COL3));
                String amount = data.getString(data.getColumnIndex(Sale_COL4));
                BuyerRegisterModel model = new BuyerRegisterModel(name, weight, amount, id);
                Log.d(TAG, "getBuyerRegister: " + amount);
                list.add(model);
            }
        }
        else{
            Log.d("BuyerRegister", "getBuyerRegister: Data count is 0 " + startDate + " " + endDate);
        }

        if(data2.getCount() != 0){
            while(data2.moveToNext()){
                int id = data2.getInt(data2.getColumnIndex(BTCOL1));
                String name = data2.getString(data2.getColumnIndex(BTCOL2));
                BuyerRegisterModel model2 = new BuyerRegisterModel(name, String.valueOf(0), String.valueOf(0), id);
                users.add(model2);
                Log.d(TAG, "gets called");
            }
        }
        else{

        }

        for(BuyerRegisterModel model : list){
            for(BuyerRegisterModel user : users){
                if(model.getName().equals(user.getName())){
                    double amount = Double.valueOf(user.getAmount());
                    double weight = Double.valueOf(user.getWeight());
                    double increasedWeight = Double.valueOf(model.getWeight()) + weight;
                    double increasedAmount = Double.valueOf(model.getAmount()) + amount;
                    user.setAmount(String.valueOf(truncate(increasedAmount)));
                    user.setWeight(String.valueOf(truncate(increasedWeight)));
                }
            }
        }

        for(BuyerRegisterModel model : users){
            int id = model.getId();
            double credit =0;
            double debit=0;
            String query3 = "SELECT * FROM " + receive_cash_table + " WHERE ID ='" + id + "'";// + Rec_COL1 + " BETWEEN '" + startDate + "' AND '" + endDate + "'";
            Cursor data3 = db.rawQuery(query3, null);
            if(data3.getCount() != 0){
                while(data3.moveToNext()){
                    Log.d(TAG, "Credit: " + data3.getColumnIndex(Rec_COL2));
                    credit += Double.valueOf(data3.getString(data3.getColumnIndex(Rec_COL2)));
                    debit += Double.valueOf(data3.getString(data3.getColumnIndex(Rec_COL3)));
                }
                Log.d(TAG, "credit -debit: " + credit + "  " + debit + "  " + users.get(2).getAmount());
                model.setAmount(String.valueOf(Double.valueOf((debit-credit))));
                model.setWeight(String.valueOf(Double.valueOf((debit-credit)/getRate())));
            }
            else{
                Log.d(TAG, "data3 is empty");
            }
        }
        return users;
    }

    public ArrayList<ReceiveCashListModel> getReceiveCashList(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<ReceiveCashListModel> list = new ArrayList<>();
        String query2 = "SELECT * FROM " + buyers_table;
        Cursor data1 = db.rawQuery(query2, null);
        if(data1.getCount()!=0){
            while(data1.moveToNext()){
                String name = data1.getString(data1.getColumnIndex(BTCOL2));
                int id = data1.getInt(data1.getColumnIndex(BTCOL1));
                ReceiveCashListModel model = new ReceiveCashListModel(""+id, name, "", "");
                list.add(model);
            }
        }
        Log.d(TAG, "Hello");

        if(list.size() != 0){
            for(int i=0; i<list.size(); i++){
                String query = "SELECT * FROM " + receive_cash_table + " WHERE ID ='" + list.get(i).getId() + "'";
                Cursor data = db.rawQuery(query, null);
                if(data.getCount()!=0){
                    double debit = 0;
                    double credit = 0;
                    double weight = 0;
                    double remaining_amount=0;
                    while(data.moveToNext()){
                        debit += Double.valueOf(data.getString(data.getColumnIndex(Rec_COL3)));
                        credit += Double.valueOf(data.getString(data.getColumnIndex(Rec_COL2)));
                        weight+= Double.valueOf(data.getString(data.getColumnIndex(Rec_COL8)));
                    }
                    try{
                        remaining_amount = debit-credit;
                        list.get(i).setDue(""+remaining_amount);
                        list.get(i).setWeight(""+weight);
                    }
                    catch(Exception e){
                        list.get(i).setDue("0");
                        list.get(i).setWeight("0");
                    }
                }
            }
        }
        for(int i=0; i<list.size();i++){
            for(int j=0; j<list.size()-1; j++){
                long value1 = Long.valueOf(list.get(j).getId());
                long value2 = Long.valueOf(list.get(j+1).getId());
                if(value1 > value2){
                    ReceiveCashListModel temp;
                    temp = list.get(j);
                    list.set(j, list.get(j+1));
                    list.set(j+1, temp);
                }
            }
        }
        // Log.d(TAG, "List size: " + list.size());
        return list;
    }

    // Get buyer receive cash details
    public ArrayList<ReceiveCashModel> getReceiveCash(int id, String startDate, String endDate){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<ReceiveCashModel> list = new ArrayList<>();
        String query2;
        if(startDate.equals("") && endDate.equals("")){
            query2 = "SELECT * FROM " + receive_cash_table + " WHERE ID ='" + id + "'";
        }
        else{
            query2 = "SELECT * FROM " + receive_cash_table + " WHERE ID ='" + id + "' AND " + Rec_COL1 + " BETWEEN '" + startDate + "' AND '" + endDate + "'";
        }

        Cursor data2 = db.rawQuery(query2, null);

        if(data2.getCount() != 0){
            while(data2.moveToNext()){
                String title = data2.getString(data2.getColumnIndex(Rec_COL4));
                String date = data2.getString(data2.getColumnIndex("Date"));
                String credit = data2.getString(data2.getColumnIndex(Rec_COL2));
                String debit = data2.getString(data2.getColumnIndex(Rec_COL3));
                int unique_id = data2.getInt(data2.getColumnIndex(Rec_COL6));
                String shift = data2.getString(data2.getColumnIndex(Rec_COL7));
                String date_in_long = data2.getString(data2.getColumnIndex(Rec_COL9));
                ReceiveCashModel model = new ReceiveCashModel(date, title, credit, debit, id, unique_id, shift, date_in_long);
                list.add(model);
            }
        }
        else{

        }
        try {
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < list.size() - 1; j++) {
                    long value1 = Long.valueOf(list.get(j).getDate_in_long());
                    long value2 = Long.valueOf(list.get(j + 1).getDate_in_long());
                    if (value1 > value2) {
                        ReceiveCashModel temp;
                        temp = list.get(j);
                        list.set(j, list.get(j + 1));
                        list.set(j + 1, temp);
                    }
                }
            }
        }
        catch (Exception e){}
        for(int i =0; i<list.size(); i++){
            Log.d(TAG, "Date: " + list.get(i).getDate() + " Date in long: " + list.get(i).getDate_in_long());
        }
        return list;
    }

    //Update milk sale entry
    public void updateMilkSale(int unique_Id, int id, String name, String weight, String rate, String amount){
        String query = "UPDATE " + milk_sale_table + " SET " + Sale_COL1 + " ='" + id + "', " + Sale_COL2 + " ='" + name + "', "+
                Sale_COL3 + " ='" + weight + "', " + Sale_COL4 + " ='" + amount + "', " + Sale_COL5 + " ='" + rate + "' WHERE " +
                Unique_ID_Sale + " ='" + unique_Id + "'";
        Log.d(TAG, "updateMilkSale: " + weight);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    //Update milk buy entry
    public void updateMilkBuy(int unique_Id, int id, String name, String weight, String rate, String amount, String fat, String snf, String type){
        String query = "UPDATE " + milk_buy_table + " SET " + Buy_COL1 + " ='" + id + "', " + Buy_COL2 + " ='" + name + "', "+
                Buy_COL3 + " ='" + weight + "', " + Buy_COL4 + " ='" + fat + "', " + Buy_COL5 + " ='" + snf + "', " + Buy_COL6 + " ='" + amount + "', " +
                Buy_COL9 + " ='" + rate + "' WHERE " + Unique_ID_Buy + " ='" + unique_Id + "'";
        Log.d(TAG, "updateMilkBuy: " + weight);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    //Delete milk sale entry
    public void deleteMilkSaleEntry(int unique_ID, boolean toClear) {
        String query;
        if(toClear){
            query = "DELETE FROM " + milk_sale_table + " WHERE " + Sale_COL1 + " ='" + unique_ID + "'";
        }
        else{
            query = "DELETE FROM " + milk_sale_table + " WHERE " + Unique_ID_Sale + " ='" + unique_ID + "'";
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    //Delete milk buy entry
    public void deleteMilkBuyEntry(int unique_ID, boolean toClear) {
        String query;
        if(toClear){
            query = "DELETE FROM " + milk_buy_table + " WHERE " + Buy_COL1 + " ='" + unique_ID + "'";
        }
        else{
            query = "DELETE FROM " + milk_buy_table + " WHERE " + Unique_ID_Buy + " ='" + unique_ID + "'";
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    // Get milk sale range
    public ArrayList<DailySalesObject> getMilkSaleRange(String startDate, String endDate){
        ArrayList<DailySalesObject> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + milk_sale_table + " WHERE " + Sale_COL7 + " BETWEEN '" + startDate +"' AND '" +endDate + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.getCount()!=0){
            while(data.moveToNext()){
                int id = data.getInt(1);
                int unique_id = data.getInt(0);
                String name = data.getString(2);
                String weight = data.getString(3);
                String amount = data.getString(4);
                String rate = data.getString(5);
                String shift = data.getString(6);
                String date = data.getString(7);
                String debit = data.getString(8);
                String credit = data.getString(9);
                String date_in_long = data.getString(data.getColumnIndex(Sale_COL10));
                Log.d(TAG, "getSalesData: " + weight);
                DailySalesObject object = new DailySalesObject(id, name, Double.valueOf(weight), Double.valueOf(amount),
                        Double.valueOf(rate), shift, date, Double.valueOf(debit), Double.valueOf(credit), unique_id, date_in_long);
                list.add(object);
            }
        }
        return list;
    }

    // Get milk buy range
    public ArrayList<DailyBuyObject> getMilkBuyRange(String startDate, String endDate){
        ArrayList<DailyBuyObject> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + milk_buy_table + " WHERE " + Buy_COL7 + " BETWEEN '" + startDate +"' AND '" +endDate + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.getCount()!=0){
            while(data.moveToNext()){
                int id = data.getInt(1);
                int unique_id = data.getInt(0);
                String name = data.getString(2);
                String rate = data.getString(9);
                String weight = data.getString(3);
                String fat = data.getString(4);
                String snf = data.getString(5);
                String amount = data.getString(6);
                String date = data.getString(7);
                String type = data.getString(10);
                String shift = data.getString(8);
                String date_in_long = data.getString(data.getColumnIndex(Buy_COL11));
                Log.d(TAG, "getSalesData: " + date);
                DailyBuyObject object = new DailyBuyObject(id, name, Double.valueOf(weight), Double.valueOf(rate),
                        Double.valueOf(amount), shift, Double.valueOf(fat), Double.valueOf(snf), date, type, unique_id, date_in_long);
                list.add(object);
            }
        }
        return list;
    }

    // Get buyer report by date and shift
    public ArrayList<ReportByDateModels> getReportByDate(int id, String beginDate, String beginShift, String endDate, String endShift){
        ArrayList<ReportByDateModels> list = new ArrayList<>();
        ArrayList<ReportByDateModels> toRemoveFromStart = new ArrayList<>();
        ArrayList<ReportByDateModels> toRemoveFromEnd = new ArrayList<>();
        ArrayList<ReportByDateModels> startDay = new ArrayList<>();
        ArrayList<ReportByDateModels> endDay = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + milk_sale_table + " WHERE " + Sale_COL1 + " ='" + id + "' AND "  + Sale_COL7 + " BETWEEN '" + beginDate + "' AND '" + endDate + "'";
        Cursor data = db.rawQuery(query, null);

        if(data.getCount() != 0){
            Log.d(TAG, "data count is not 0" + " " + data.getCount());
            while(data.moveToNext()){

                String date = data.getString(data.getColumnIndex(Sale_COL7));
                String rate = data.getString(data.getColumnIndex(Sale_COL5));
                String weight = data.getString(data.getColumnIndex(Sale_COL3));
                String amount = data.getString(data.getColumnIndex(Sale_COL4));
                String shift = data.getString(data.getColumnIndex(Sale_COL6));
                String credit = data.getString(data.getColumnIndex(Sale_COL9));
                String debit = data.getString(data.getColumnIndex(Sale_COL8));
                String date_in_long = data.getString(data.getColumnIndex(Sale_COL10));
                Log.d(TAG, "called");
                ReportByDateModels model = new ReportByDateModels(date, rate, weight, amount, shift, credit, debit,date_in_long);
                list.add(model);
            }
            // Sort list further by shift
            Log.d(TAG, "list count is not 0 " + list.size());
            for(ReportByDateModels model : list){
                if(!endDate.equals(beginDate)){
                    if(model.getDate().equals(beginDate))
                        startDay.add(model);
                    if(model.getDate().equals(endDate))
                        endDay.add(model);
                }
                else{
                    if(model.getDate().equals(beginDate))
                        startDay.add(model);
                }
            }
            for(ReportByDateModels model : startDay){
                if(beginShift.equals("Morning")){
                    if(!model.getShift().equals(beginShift))
                        toRemoveFromStart.add(model);
                }
            }
            for(ReportByDateModels model : endDay){
                if(endShift.equals("Evening")){
                    if(!model.getShift().equals(endDate))
                        toRemoveFromEnd.add(model);
                }
            }
            list.removeAll(toRemoveFromEnd);
            list.removeAll(toRemoveFromStart);

        }
        else{
            Log.d(TAG, "getReportByDate: Data count is 0");
        }
        for(int i=0; i<list.size();i++){
            for(int j=0; j<list.size()-1; j++){
                long value1 = Long.valueOf(list.get(j).getDate_in_long());
                long value2 = Long.valueOf(list.get(j+1).getDate_in_long());
                if(value1 > value2){
                    Log.d(TAG, "switching " + value1 + " for " + value2);
                    ReportByDateModels temp;
                    temp = list.get(j);
                    list.set(j, list.get(j+1));
                    list.set(j+1, temp);
                }
            }
        }

        for(int i=0; i<list.size(); i++){
            Log.d(TAG, "Date " + list.get(i).getDate() + " Date in long " + list.get(i).getDate_in_long());
        }
        Log.d(TAG, "list size: " + list.size());
        return list;
    }

    // Method that gets Buyer Register entries by date
    public ArrayList<BuyerRegisterModel> getBuyerRegister(String startDate, String endDate){
        ArrayList<BuyerRegisterModel> list = new ArrayList<>();
        ArrayList<BuyerRegisterModel> users = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query;
        if(startDate.equals("") && endDate.equals("")){
            query = "select * from " + milk_sale_table;
        }
        else {
            query = "SELECT * FROM " + milk_sale_table + " WHERE " + Sale_COL7 + " BETWEEN '" + startDate + "' AND '" + endDate + "'";
        }
        String query2 = "SELECT ID, Name FROM " + buyers_table;

        Cursor data = db.rawQuery(query, null);
        Cursor data2 = db.rawQuery(query2, null);

        if(data.getCount() != 0){
            while(data.moveToNext()){
                int id = data.getInt(data.getColumnIndex(Sale_COL1));
                String name = data.getString(data.getColumnIndex(Sale_COL2));
                String date = data.getString(data.getColumnIndex(Sale_COL7));
                Log.d("BuyerRegister", "getBuyerRegister: " + date + date.length());
                String weight = data.getString(data.getColumnIndex(Sale_COL3));
                String amount = data.getString(data.getColumnIndex(Sale_COL4));
                BuyerRegisterModel model = new BuyerRegisterModel(name, weight, amount, id);
                Log.d(TAG, "getBuyerRegister: " + amount);
                list.add(model);
            }
        }
        else{
            Log.d("BuyerRegister", "getBuyerRegister: Data count is 0 " + startDate + " " + endDate);
        }

        if(data2.getCount() != 0){
            while(data2.moveToNext()){
                int id = data2.getInt(data2.getColumnIndex(BTCOL1));
                String name = data2.getString(data2.getColumnIndex(BTCOL2));
                BuyerRegisterModel model2 = new BuyerRegisterModel(name, String.valueOf(0), String.valueOf(0), id);
                users.add(model2);
            }
        }
        else{

        }

        for(BuyerRegisterModel model : list){
            for(BuyerRegisterModel user : users){
                if(model.getName().equals(user.getName())){
                    double amount = Double.valueOf(user.getAmount());
                    double weight = Double.valueOf(user.getWeight());
                    double increasedWeight = Double.valueOf(model.getWeight()) + weight;
                    double increasedAmount = Double.valueOf(model.getAmount()) + amount;
                    user.setAmount(String.valueOf(truncate(increasedAmount)));
                    user.setWeight(String.valueOf(truncate(increasedWeight)));
                }
            }
        }
        for(int i=0; i<users.size();i++){
            for(int j=0; j<users.size()-1; j++){
                long value1 = Integer.valueOf(users.get(j).getId());
                long value2 = Integer.valueOf(users.get(j+1).getId());
                if(value1 > value2){
                    BuyerRegisterModel temp;
                    temp = users.get(j);
                    users.set(j, users.get(j+1));
                    users.set(j+1, temp);
//                    int temp = 0;
//                    temp = array[j];
//                    array[j] = array[j+1];
//                    array[j+1] = temp;
                }
            }
        }
        return users;
    }

    public ArrayList<PaymentRegisterModel> getPaymentRegister(String beginDate, String beginShift, String endDate, String endShift){
        ArrayList<PaymentRegisterModel> list = new ArrayList<>();
        ArrayList<PaymentRegisterModel> toRemoveFromStart = new ArrayList<>();
        ArrayList<PaymentRegisterModel> toRemoveFromEnd = new ArrayList<>();
        ArrayList<PaymentRegisterModel> startDay = new ArrayList<>();
        ArrayList<PaymentRegisterModel> endDay = new ArrayList<>();
        ArrayList<PaymentRegisterModel> users = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String query;
        if(beginDate.equals("")&& endDate.equals("")){
            query = "select * from " + milk_buy_table;
        }
        else{
            query = "SELECT * FROM " + milk_buy_table + " WHERE " + Buy_COL7 + " BETWEEN '" + beginDate + "' AND '" + endDate + "'";
        }
        String query2 = "SELECT * FROM " + sellers_table;
        Cursor data = db.rawQuery(query, null);
        Cursor data2 = db.rawQuery(query2, null);

        if(data2.getCount() !=0){
            while(data2.moveToNext()){
                int id = data2.getInt(data2.getColumnIndex(STCOL1));
                String name = data2.getString(data2.getColumnIndex(STCOL2));
                PaymentRegisterModel model = new PaymentRegisterModel(id, "0","0","0","0","0","0",name,"0");
                users.add(model);
            }
        }

        if(data.getCount() != 0){
            while(data.moveToNext()){
                int id = data.getInt(data.getColumnIndex(Buy_COL1));
                String snf = data.getString(data.getColumnIndex(Buy_COL5));
                String weight = data.getString(data.getColumnIndex(Buy_COL3));
                String amount = data.getString(data.getColumnIndex(Buy_COL6));
                String fat = data.getString(data.getColumnIndex(Buy_COL4));
                String shift = data.getString(data.getColumnIndex(Buy_COL8));
                String date = data.getString(data.getColumnIndex(Buy_COL7));
                String name = data.getString(data.getColumnIndex(Buy_COL2));
                String date_in_long = data.getString(data.getColumnIndex(Buy_COL11));
                PaymentRegisterModel model = new PaymentRegisterModel(id, fat, snf, weight, amount, date, shift, name,date_in_long);
                list.add(model);
                // list object after sorting by date
            }
            // Sort list further by shift
            for(PaymentRegisterModel model : list){
                if(!endDate.equals(beginDate)){
                    if(model.getDate().equals(beginDate))
                        startDay.add(model);
                    if(model.getDate().equals(endDate))
                        endDay.add(model);
                }
                else{
                    if(model.getDate().equals(beginDate))
                        startDay.add(model);
                }
            }
            for(PaymentRegisterModel model : startDay){
                if(beginShift.equals("Evening")){
                    if(!model.getShift().equals(beginShift))
                        toRemoveFromStart.add(model);
                }
            }
            for(PaymentRegisterModel model : endDay){
                if(endShift.equals("Morning")){
                    if(!model.getShift().equals(endDate))
                        toRemoveFromEnd.add(model);
                }
            }
            list.removeAll(toRemoveFromEnd);
            list.removeAll(toRemoveFromStart);

        }

        for(PaymentRegisterModel model : list){
            for(PaymentRegisterModel user : users){
                if(model.getName().equals(user.getName())){
                    double amount = Double.valueOf(user.getAmount());
                    double weight = Double.valueOf(user.getWeight());
                    double increasedWeight = Double.valueOf(model.getWeight()) + weight;
                    double increasedAmount = Double.valueOf(model.getAmount()) + amount;
                    user.setAmount(String.valueOf(truncate(increasedAmount)));
                    user.setWeight(String.valueOf(truncate(increasedWeight)));
                }
            }
        }

        for(int i=0; i<users.size();i++){
            for(int j=0; j<users.size()-1; j++){
                int value1 = users.get(j).getId();
                int value2 = users.get(j+1).getId();
                if(value1 > value2){
                    PaymentRegisterModel temp;
                    temp = users.get(j);
                    users.set(j, users.get(j+1));
                    users.set(j+1, temp);
                }
            }
        }

        return users;
    }


    // Get Shift report
    public ArrayList<ShiftReportModel> getShiftReport(String date, String shift){
        ArrayList<ShiftReportModel> list = new ArrayList<>();
        ArrayList<ShiftReportModel> toRemove = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + milk_buy_table + " WHERE " + Buy_COL7 + " ='" + date + "' AND " + Buy_COL8 + " ='" + shift + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                int id = data.getInt(data.getColumnIndex(Buy_COL1));
                String fat = data.getString(data.getColumnIndex(Buy_COL4));
                String snf = data.getString(data.getColumnIndex(Buy_COL5));
                String weight = data.getString(data.getColumnIndex(Buy_COL3));
                String amount = data.getString(data.getColumnIndex(Buy_COL6));
                String name = data.getString(data.getColumnIndex(Buy_COL2));
                String rate = data.getString(data.getColumnIndex(Buy_COL9));

                ShiftReportModel model = new ShiftReportModel(id, weight, amount, fat, snf, name, rate);
                list.add(model);
            }
        }
        else{
//            toast(context, "Shift Report is empty");
        }
        return  list;
    }

    // Get Shift report
    public ArrayList<ShiftReportModel> getDuplicateSlip(int id, String date, String shift){
        ArrayList<ShiftReportModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + milk_buy_table + " WHERE " + Buy_COL7 + " ='" + date + "' AND " + Buy_COL8 + " ='" + shift + "'AND " + Buy_COL1 + " ='" + id +"'";
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                String fat = data.getString(data.getColumnIndex(Buy_COL4));
                String snf = data.getString(data.getColumnIndex(Buy_COL5));
                String weight = data.getString(data.getColumnIndex(Buy_COL3));
                String amount = data.getString(data.getColumnIndex(Buy_COL6));
                String name = data.getString(data.getColumnIndex(Buy_COL2));
                String rate = data.getString(data.getColumnIndex(Buy_COL9));

                ShiftReportModel model = new ShiftReportModel(id, weight, amount, fat, snf, name, rate);
                list.add(model);
            }
        }
        else{
//            toast(context, "Shift Report is empty");
        }
        return  list;
    }

    // Get buy data for a day
    public ArrayList<DailyBuyObject> getDailyBuyData(String dateToGet, String shiftToGet){
        ArrayList<DailyBuyObject> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + milk_buy_table + " WHERE " + Buy_COL8 + " ='" + shiftToGet + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                //Log.d(TAG, "getBuyData: date not equal : " + data.getString(7)/* + dateToGet.trim()*/);
                if(data.getString(data.getColumnIndex(Buy_COL7)).equals(dateToGet)){
                    int unique_id = data.getInt(0);
                    int id = data.getInt(1);
                    String name = data.getString(2);
                    String rate = data.getString(9);
                    String weight = data.getString(3);
                    String fat = data.getString(4);
                    String snf = data.getString(5);
                    String amount = data.getString(6);
                    String date = data.getString(7);
                    String type = data.getString(10);
                    String shift = data.getString(8);
                    String date_in_long = data.getString(data.getColumnIndex(Buy_COL11));
                    Log.d(TAG, "getSalesData: " + date);
                    DailyBuyObject object = new DailyBuyObject(id, name, Double.valueOf(weight), Double.valueOf(rate),
                            Double.valueOf(amount), shift, Double.valueOf(fat), Double.valueOf(snf), date, type, unique_id, date_in_long);
                    list.add(object);
                }
                else{
                    Log.d(TAG, "getBuyData: date not equal : " + data.getString(7) + dateToGet.trim());
                }
            }
        }
        else{

        }
        return list;
    }

    public ArrayList<CustomerReportModel> getCustomerReport(int id, String startDate, String endDate){
        ArrayList<CustomerReportModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + milk_buy_table+ " WHERE " + Buy_COL1 + " ='" + id + "' AND " +
                Buy_COL7 + " BETWEEN '" + startDate + "' AND '" + endDate + "'";
        //Log.d(TAG, "getCustomerReport: Data count is 0" + startDate + endDate);
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                String date = data.getString(data.getColumnIndex(Buy_COL7));
                String fat = data.getString(data.getColumnIndex(Buy_COL4));
                String shift = data.getString(data.getColumnIndex(Buy_COL8));
                String snf = data.getString(data.getColumnIndex(Buy_COL5));
                String weight = data.getString(data.getColumnIndex(Buy_COL3));
                String amount = data.getString(data.getColumnIndex(Buy_COL6));
                String date_in_long = data.getString(data.getColumnIndex(Buy_COL11));
                Log.d(TAG, "getCustomerReport: Date " + date_in_long);
                CustomerReportModel model = new CustomerReportModel(date, fat, snf, weight, amount, shift, date_in_long);
                list.add(model);
            }
        }
        else{
            Log.d(TAG, "getCustomerReport: Data count is 0");
        }
        for(int i=0; i<list.size();i++){
            for(int j=0; j<list.size()-1; j++){
                long value1 = Long.valueOf(list.get(j).getDate_in_long());
                long value2 = Long.valueOf(list.get(j+1).getDate_in_long());
                if(value1 > value2){
                    CustomerReportModel temp;
                    temp = list.get(j);
                    list.set(j, list.get(j+1));
                    list.set(j+1, temp);
                }
            }
        }
        for(int i=0; i<list.size(); i++){
            Log.d(TAG, "Date: " + list.get(i).getDate() + "  Date_In_Long: " + list.get(i).getDate_in_long());
        }
        return list;
    }

    // Add sales data to sales table
    public boolean addSalesEntry(int id, String name, String weight, String amount, String rate, String shift, String date
                                    ,String debit, String credit, String date_in_long){
        SQLiteDatabase db = this.getWritableDatabase();
        long dateInLong;
        long result;
        ContentValues contentValues = new ContentValues();
        contentValues.put(Sale_COL1, id);
        contentValues.put(Sale_COL2, name);
        contentValues.put(Sale_COL3, String.valueOf(weight));
        contentValues.put(Sale_COL4, String.valueOf(amount));
        contentValues.put(Sale_COL5, String.valueOf(rate));
        contentValues.put(Sale_COL6, shift);
        contentValues.put(Sale_COL7, date);
        contentValues.put(Sale_COL8, String.valueOf(debit));
        contentValues.put(Sale_COL9, String.valueOf(credit));
        contentValues.put(Sale_COL10, date_in_long);
        result = db.insert(milk_sale_table, null, contentValues);
        return result != -1;
    }

    // Get sales data
    public Cursor getSalesData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + milk_sale_table;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    // Get entries by date range
    public ArrayList<ViewAllEntryModel> getEntries(int id, String startDate, String endDate){
        ArrayList<ViewAllEntryModel> list = new ArrayList<>();
        String query = "SELECT * FROM " + milk_buy_table + " WHERE ID ='" + id +"' AND " + Buy_COL7 +" BETWEEN '" + startDate + "' AND '" + endDate +"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor entries = db.rawQuery(query, null);

        if(entries.getCount() != 0){
            while(entries.moveToNext()){
                String date = entries.getString(entries.getColumnIndex(Buy_COL7));
                String shift = entries.getString(entries.getColumnIndex(Buy_COL8));
                String fat = entries.getString(entries.getColumnIndex(Buy_COL4));
                String snf = entries.getString(entries.getColumnIndex(Buy_COL5));
                String weight = entries.getString(entries.getColumnIndex(Buy_COL3));
                String rate = entries.getString(entries.getColumnIndex(Buy_COL9));
                String amount = entries.getString(entries.getColumnIndex(Buy_COL6));
                String date_in_long = entries.getString(entries.getColumnIndex(Buy_COL11));
                Log.d(TAG, "getEntries: " + startDate + " " + endDate);
                Log.d(TAG, "getEntries: " + date);
                ViewAllEntryModel model = new ViewAllEntryModel(date,shift,fat,snf,weight,rate,amount,"-", date_in_long);
                list.add(model);
            }
        }
        else{
            toast(context, "Entry data is empty");
        }
        for(int i=0; i<list.size();i++){
            for(int j=0; j<list.size()-1; j++){
                long value1 = Long.valueOf(list.get(j).getDate_in_long());
                long value2 = Long.valueOf(list.get(j+1).getDate_in_long());
                if(value1 > value2){
                    ViewAllEntryModel temp;
                    temp = list.get(j);
                    list.set(j, list.get(j+1));
                    list.set(j+1, temp);
//                    int temp = 0;
//                    temp = array[j];
//                    array[j] = array[j+1];
//                    array[j+1] = temp;
                }
            }
        }
        return list;
    }

    // Get Sales data for a day
    public ArrayList<DailySalesObject> getDailySalesData(String dateToGet, String shiftToGet){
        ArrayList<DailySalesObject> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + milk_sale_table + " WHERE " + Sale_COL7 + " ='" + dateToGet + "' AND " + Sale_COL6 + " ='" + shiftToGet + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                Log.d(TAG, "dates: " + data.getString(data.getColumnIndex(Sale_COL7)));
                if(data.getString(data.getColumnIndex(Sale_COL7)).equals(String.valueOf(dateToGet))){
                    int id = data.getInt(1);
                    int unique_id = data.getInt(0);
                    String name = data.getString(2);
                    String weight = data.getString(3);
                    String amount = data.getString(4);
                    String rate = data.getString(5);
                    String shift = data.getString(6);
                    String debit = data.getString(8);
                    String credit = data.getString(9);
                    String date = data.getString(7);
                    String date_in_long = data.getString(data.getColumnIndex(Sale_COL10));
                    DailySalesObject object = new DailySalesObject(id, name, Double.valueOf(weight), Double.valueOf(amount),
                            Double.valueOf(rate), shift, date, Double.valueOf(debit), Double.valueOf(credit), unique_id, date_in_long);
                    list.add(object);
                }
                else{

                }
            }
        }
        else{
            Log.d(TAG, "dates: ");
        }
        return list;
    }

    //Clear the cow SNF table
    public void clearSNFTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + SNF_table);
    }

    public void clearMilkSaleTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " +milk_sale_table);
    }

    // Create the cow snf table with filepath pointing to the file on the phone
    public boolean createSNFTable(String filePath){

        if(filePath.equals("")){
            try {
                InputStream is = context.getAssets().open("Book1.csv");
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues contentValues = new ContentValues();

                List<List<String>> lines = new ArrayList<>();
                List<String> fatList = new ArrayList<>();

                for (double i = 3.0; i <= 4.5; i = i + 0.1) {
                    Double truncatedValue = BigDecimal.valueOf(i).setScale(1, RoundingMode.HALF_UP).doubleValue();
                    String fat = String.valueOf(truncatedValue);
                    fatList.add(fat);
                }

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    try {
                        String csvLine;
                        while ((csvLine = reader.readLine()) != null) {
                            String[] values = csvLine.split(",");
                            lines.add(Arrays.asList(values));
                        }

                        for (int i = 0; i < lines.size(); i++) {
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
                            contentValues.put(SNFcolumns[14], String.valueOf(lines.get(i).get(14)));
                            contentValues.put(SNFcolumns[15], String.valueOf(lines.get(i).get(15)));
                            contentValues.put(SNFcolumns[16], String.valueOf(lines.get(i).get(16)));
                            contentValues.put(SNFcolumns[17], String.valueOf(lines.get(i).get(17)));
                            contentValues.put(SNFcolumns[18], String.valueOf(lines.get(i).get(18)));
                            contentValues.put(SNFcolumns[19], String.valueOf(lines.get(i).get(19)));


                            /*
                             * Important Note to self
                             * lines.get(i).get() represents number of columns in excel file
                             * lines.size() represnets number of rows in excel file
                             *
                             * */


                            db.insert(SNF_table, null, contentValues);
                            contentValues.clear();
                        }
                    } catch (Exception e) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        else{
            File file;
            FileInputStream fileInputStream;

            file = new File(filePath);
            Log.d(TAG, "createSNFTable: " + file.getPath() + " " + file.exists());
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            List<List<String>> lines = new ArrayList<>();
            List<String> fatList = new ArrayList<>();

            for (double i = 3.0; i <= 11.1; i = i + 0.1) {
                Double truncatedValue = BigDecimal.valueOf(i).setScale(1, RoundingMode.HALF_UP).doubleValue();
                String fat = String.valueOf(truncatedValue);
                fatList.add(fat);
            }

            try {
                fileInputStream = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
                try {
                    String csvLine;
                    while ((csvLine = reader.readLine()) != null) {
                        String[] values = csvLine.split(",");
                        lines.add(Arrays.asList(values));
                    }
                    Log.d(TAG, "Lines: " + lines.toString());
                    Log.d(TAG, "Size: " + lines.size());
                    fileInputStream.close();
                    Log.d(TAG, "Size: " + lines.size());

                    for (int i = 0; i < lines.size(); i++) {
                        try{
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
                            contentValues.put(SNFcolumns[14], String.valueOf(lines.get(i).get(14)));
                            contentValues.put(SNFcolumns[15], String.valueOf(lines.get(i).get(15)));
                            contentValues.put(SNFcolumns[16], String.valueOf(lines.get(i).get(16)));
                            contentValues.put(SNFcolumns[17], String.valueOf(lines.get(i).get(17)));
                            contentValues.put(SNFcolumns[18], String.valueOf(lines.get(i).get(18)));
                            contentValues.put(SNFcolumns[19], String.valueOf(lines.get(i).get(19)));


                            /*
                             * Important Note to self
                             * lines.get(i).get() represents number of columns in excel file
                             * lines.size() represnets number of rows in excel file
                             *
                             * */


                            db.insert(SNF_table, null, contentValues);
                            contentValues.clear();
                        }
                        catch(Exception e){
                            Log.d(TAG, "Exception: " + e.getMessage());
                            db.insert(SNF_table, null, contentValues);
                            contentValues.clear();
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Exception: " + e.getMessage());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public void updatetRateChart(String rate, String fat, String snf){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "dbHelper Fat: " + rate + " " + fat + " " + snf);
        String query = "UPDATE " + SNF_table + " SET " + snf + " ='" + rate + "' WHERE " + fat_column + " ='" + fat + "'";
        try {
            db.execSQL(query);
        }
        catch(Exception e){}
    }

    public boolean createBuffaloSNFTable(String filePath){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        FileInputStream inputStream;
        List<List<String>> lines= new ArrayList<>();
        List<String> fatList = new ArrayList<>();

        for(double i = 5.0; i <= 10.1; i = i + 0.1){
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
        return data;
    }


    public Cursor getSNFTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + SNF_table;
        Cursor data = db.rawQuery(query, null);
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
        int id = 0;
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                int Id = data.getInt(0);
                id = Id;
            }
        }
        return id;
    }

    public int getBuyerId(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + BTCOL1 + " FROM " + buyers_table + " WHERE " + BTCOL2
                + " ='" + name + "'";
        int id = 0;
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                int Id = data.getInt(0);
                id = Id;
            }
        }
        return id;
    }

    // Returns the id of a buyer given name and phone number
    public String getBuyerPhone_Number(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + BTCOL3 + " FROM " + buyers_table + " WHERE " + BTCOL1
                + " ='" + id + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                String phone_number = data.getString(data.getColumnIndex(BTCOL3));
                return phone_number;
            }
        }
        return "";
    }

    // Returns the id of a buyer given name and phone number
    public String getSellerPhone_Number(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + STCOL3 + " FROM " +sellers_table + " WHERE " + STCOL1
                + " ='" + id + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                String phone_number = data.getString(data.getColumnIndex(STCOL3));
                return phone_number;
            }
        }
        return "";
    }

    public String getRate(String fat, String snf) {
        String rate = "";
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            String query = "SELECT " + snf + " FROM " + SNF_table + " WHERE " + fat_column + " ='" + fat + "'";
            Cursor data = db.rawQuery(query, null);
            Log.d(TAG, "fat and snf: " + fat + " " + snf);

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

    public String getBuffaloRate(String fat, String snf) {
        String rate = "";
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            String query = "SELECT " + snf + " FROM " + Buffalo_SNF + " WHERE " + fat_column + " ='" + fat + "'";
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
            return "";
        }
        return name;
    }

    // Returns the name of a buyer with id passed
    public String getBuyerPhone(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String name = "";
        String query = "SELECT " + BTCOL3 + " FROM " + buyers_table + " WHERE " + BTCOL1 + " ='" + id + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                name = data.getString(0);
            }
        }
        else{
            return "";
        }
        return name;
    }

    // Returns the name of a buyer with id passed
    public String getSellerPhone(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String name = "";
        String query = "SELECT " + STCOL3 + " FROM " + sellers_table + " WHERE " + STCOL1 + " ='" + id + "'";
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                name = data.getString(0);
            }
        }
        else{
            return "";
        }
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
    public void updateBuyer(int old_id, int new_id, String name, String phone_number, String address, String status, String oldname){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + buyers_table + " SET " + BTCOL1 + " = '" + new_id + "'" +
                ", " + BTCOL2 + " = '" + name + "', " + BTCOL3 + " = '" + phone_number + "', " +
                BTCOL4 + " = '" + address + "', " + BTCOL5 + " = '" + status + "' WHERE " + BTCOL1 + " = '" + old_id +"'";
        String query2 = "UPDATE " + milk_sale_table + " SET " + Sale_COL2 + " ='" + name + "' WHERE " + Sale_COL2 + " ='" + oldname + "'";
        db.execSQL(query2);
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
    public void updateSeller(int old_id, int new_id, String name, String phone_number, String address, String status, String oldName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + sellers_table + " SET " + STCOL1 + " = '" + new_id + "'" +
                ", " + STCOL2 + " = '" + name + "', " + STCOL3 + " = '" + phone_number + "', " +
                STCOL4 + " = '" + address + "', " + STCOL5 + " = '" + status + "' WHERE " + STCOL1 + " = '" + old_id +"'";
        String query2 = "UPDATE " + milk_buy_table + " SET " + Buy_COL2 + " ='" + name + "' WHERE " + Buy_COL2 + " ='" + oldName + "'";
        db.execSQL(query);
        db.execSQL(query2);
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
    public boolean setRate(double rate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Rate", String.valueOf(rate));
        Log.d(TAG, "setRate: " + rate);
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

            return 0;
        }
        else{
            while(data.moveToNext()){
                String returned = data.getString(0);
                rate = Double.valueOf(returned);
            }
        }
        return rate;
    }

    public void clearSellerTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + sellers_table);
    }

    public void clearBuyerTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + buyers_table);
    }

    public void clearProductSale() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + product_sale_table);
    }

    public void clearProducts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + product_table);
    }

    public void clearRate() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + rate_table);
    }

    public void destroyDb(){
        context.deleteDatabase(DATABASE_NAME);
    }

    public void deleteHistory(String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + milk_sale_table + " WHERE " + Buy_COL7 + " BETWEEN '" + startDate + "' AND '" + endDate+ "'";
        String query1 = "DELETE FROM " + milk_buy_table + " WHERE " + Sale_COL7 + " BETWEEN '" + startDate + "' AND '" + endDate+ "'";
        db.execSQL(query);
        db.execSQL(query1);
    }
}
