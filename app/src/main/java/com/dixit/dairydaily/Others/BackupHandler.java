package com.dixit.dairydaily.Others;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.dixit.dairydaily.Models.AddProductModel;
import com.dixit.dairydaily.Models.ProductSaleModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.paperdb.Paper;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.UI.Dashboard.DrawerLayout.InitDrawerBoard.bpd;

public class BackupHandler {

    Context context;
    DbHelper dbHelper;
    private static final String TAG = "BackupHandler";

    public BackupHandler(Context context){
        this.context = context;
        dbHelper = new DbHelper(context);
        Log.d(TAG, "context: " + context.toString());

        checkInternetConnect();
        if(isInternetConnected) {
            backupMilkBuyData();
            backupReceiveCash();
        }
        else{
            toast(context, "Please connect to the internet");
        }

    }

    boolean isInternetConnected = false;

    public void checkInternetConnect(){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        if(manager != null){
            boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
            if(is3g)
                isInternetConnected = true;
            boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
            if(isWifi)
                isInternetConnected = true;
        }
    }

    private void backupReceiveCash() {
        JSONObject jsonToUpload = new JSONObject();
        HashMap<String, Object> toUpload = new HashMap<>();

        Cursor data = dbHelper.getReceiveCash();
        int key = 0;
        if(data.getCount() != 0){
            while(data.moveToNext()){

                //Log.d("BackupHandler", "backupReceiveCashData: " + key);
                try{
                    JSONObject values = new JSONObject();
                    values.put("Date", data.getString(data.getColumnIndex("Date")));
                    values.put("Title", data.getString(data.getColumnIndex("Title")));
                    values.put("Credit", data.getString(data.getColumnIndex("Credit")));
                    values.put("Debit", data.getString(data.getColumnIndex("Debit")));
                    values.put("ID", data.getInt(data.getColumnIndex("ID")));
                    values.put("Shift", data.getString(data.getColumnIndex("Shift")));
                    values.put("Weight", data.getString(data.getColumnIndex("Weight")));
                    values.put("Date_In_Long", data.getString(data.getColumnIndex("Date_In_Long")));
                    jsonToUpload.put(String.valueOf(key), values);
                    key++;
                    //Log.d("BackupHandler", "backupReceiveCashData: onAdd" + jsonToUpload.toString());
                }
                catch (Exception e){
                    Log.d("BackupHandler", "backupReceiveCashData: Unable to write JSON Object: " + e.getMessage());
                }
            }
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
            toUpload.put("Receive Cash Data", jsonToUpload.toString());
            ref.updateChildren(toUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                      // backupMilkBuyData();
                    }
                    else{
                        //Toast.makeText(context, "Data upload failed.", Toast.LENGTH_SHORT).show();
                        //backupMilkBuyData();
                    }
                }
            });
        }
        else{
            //backupMilkBuyData();
        }
    }

    public void backupMilkBuyData() {
        JSONObject jsonToUpload = new JSONObject();
        HashMap<String, Object> toUpload = new HashMap<>();

        Cursor data = dbHelper.getBuyData();
        int key = 0;
        if(data.getCount() != 0){
            while(data.moveToNext()){

                //Log.d("BackupHandler", "backupMilkBuyData: " + key);
                try{
                    JSONObject values = new JSONObject();
                    values.put("ID", data.getInt(data.getColumnIndex("ID")));
                    values.put("Name", data.getString(data.getColumnIndex("SellerName")));
                    values.put("Weight", data.getString(data.getColumnIndex("Weight")));
                    values.put("Amount", data.getString(data.getColumnIndex("Amount")));
                    values.put("Fat", data.getString(data.getColumnIndex("Fat")));
                    values.put("SNF", data.getString(data.getColumnIndex("SNF")));
                    values.put("Shift", data.getString(data.getColumnIndex("Shift")));
                    values.put("Date", data.getString(data.getColumnIndex("Date")));
                    values.put("Rate", data.getString(data.getColumnIndex("Rate")));
                    values.put("Type", data.getString(data.getColumnIndex("Type")));
                    values.put("Date_In_Long", data.getString(data.getColumnIndex("Date_In_Long")));

                    jsonToUpload.put(String.valueOf(key), values);
                    key++;
                    //Log.d("BackupHandler", "backupMilkBuyData: onAdd" + jsonToUpload.toString());
                }
                catch (Exception e){
                    toast(context, "Unable to write JSON Object");
                }
            }
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
            toUpload.put("Milk Buy Data", jsonToUpload.toString());
            ref.updateChildren(toUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        backupCustomers();
                    }
                    else{
                        Toast.makeText(context, "Data upload failed.", Toast.LENGTH_SHORT).show();
                        backupCustomers();
                    }
                }
            });
        }
        else{
            backupCustomers();
        }
    }

    private void backupCustomers() {
        JSONObject buyerJsonToUpload = new JSONObject();
        JSONObject sellerJsonToUpload =new JSONObject();
        HashMap<String, Object> buyerToUpload = new HashMap<>();
        HashMap<String, Object>sellerToUpload = new HashMap<>();

        Cursor allBuyers = dbHelper.getAllBuyers();
        Cursor allSellers = dbHelper.getAllSellers();

        if(allSellers.getCount() != 0){
            int key = 0;
            while(allSellers.moveToNext()){

                //Log.d("BackupHandler", "backupSellerData: " + key);
                try{
                    JSONObject values = new JSONObject();
                    values.put("Name", allSellers.getString(allSellers.getColumnIndex("Name")));
                    values.put("PhoneNumber", allSellers.getString(allSellers.getColumnIndex("PhoneNumber")));
                    values.put("Address", allSellers.getString(allSellers.getColumnIndex("Address")));
                    values.put("Status", allSellers.getString(allSellers.getColumnIndex("Status")));
                    values.put("ID", allSellers.getInt(allSellers.getColumnIndex("ID")));
                    sellerJsonToUpload.put(String.valueOf(key), values);
                    key++;
                    //Log.d("BackupHandler", "backupSellerData: onAdd" + sellerJsonToUpload.toString());
                }
                catch (Exception e){
                    Log.d("BackupHandler", "backupSellerData: Unable to write JSON Object: " + e.getMessage());
                }
            }
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
            sellerToUpload.put("All Sellers", sellerJsonToUpload.toString());
            ref.updateChildren(sellerToUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {

                    }
                    else{
                        Toast.makeText(context, "Data upload failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            //toast(context, "Seller data is 0");
        }

        if(allBuyers.getCount() != 0){
            int key = 0;
            while(allBuyers.moveToNext()){

                //Log.d("BackupHandler", "backupBuyerData: " + key);
                try{
                    JSONObject values = new JSONObject();
                    values.put("Name", allBuyers.getString(allBuyers.getColumnIndex("Name")));
                    values.put("PhoneNumber", allBuyers.getString(allBuyers.getColumnIndex("PhoneNumber")));
                    values.put("Address", allBuyers.getString(allBuyers.getColumnIndex("Address")));
                    values.put("Status", allBuyers.getString(allBuyers.getColumnIndex("Status")));
                    values.put("ID", allBuyers.getInt(allBuyers.getColumnIndex("ID")));
                    buyerJsonToUpload.put(String.valueOf(key), values);
                    key++;
                    //Log.d("BackupHandler", "backupBuyerData: onAdd" + buyerJsonToUpload.toString());
                }
                catch (Exception e){
                    Log.d("BackupHandler", "backupBuyerData: Unable to write JSON Object: " + e.getMessage());
                }
            }
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
            buyerToUpload.put("All Buyers", buyerJsonToUpload.toString());
            ref.updateChildren(buyerToUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        backupMilksaleData();
                    }
                    else{
                        Toast.makeText(context, "Buyer data upload failed.", Toast.LENGTH_SHORT).show();
                        backupMilksaleData();
                    }
                }
            });
        }
        else{
            //toast(context, "Buyer data is 0");
            backupMilksaleData();
        }
    }

    public void backupMilksaleData() {
        JSONObject jsonToUpload = new JSONObject();
        HashMap<String, Object> toUpload = new HashMap<>();

        Cursor data = dbHelper.getSalesData();
        int key = 0;
        if(data.getCount() != 0){
            while(data.moveToNext()){

                //Log.d("BackupHandler", "backupMilkSaleData: " + key);
                try{
                    JSONObject values = new JSONObject();
                    values.put("ID", data.getInt(data.getColumnIndex("ID")));
                    values.put("Name", data.getString(data.getColumnIndex("BuyerName")));
                    values.put("Weight", data.getString(data.getColumnIndex("Weight")));
                    values.put("Amount", data.getString(data.getColumnIndex("Amount")));
                    values.put("Rate", data.getString(data.getColumnIndex("Rate")));
                    values.put("Shift", data.getString(data.getColumnIndex("Shift")));
                    String date = data.getString(data.getColumnIndex("Date"));
                    Log.d("BackupHandler", "backupMilkSaleData: " + date);
                    values.put("Date", date);
                    values.put("Credit", data.getString(data.getColumnIndex("Credit")));
                    values.put("Debit", data.getString(data.getColumnIndex("Debit")));
                    values.put("Date_In_Long", data.getString(data.getColumnIndex("Date_In_Long")));

                    jsonToUpload.put(String.valueOf(key), values);
                    key++;
                    //Log.d("BackupHandler", "backupMilkSaleData: onAdd" + jsonToUpload.toString());
                }
                catch (Exception e){
                    toast(context, "Unable to write JSON Object: MilkSale");
                }
            }
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
            toUpload.put("Milk Sale Data", jsonToUpload.toString());
            ref.updateChildren(toUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        backupMilkRate();
                    }
                    else{
                        Toast.makeText(context, "Milk rate data upload failed.", Toast.LENGTH_SHORT).show();
                        backupMilkRate();
                    }
                }
            });
        }
        else{
            backupMilkRate();
        }
    }

    private void backupMilkRate() {
        JSONObject jsonToUpload = new JSONObject();
        HashMap<String, Object> toUpload = new HashMap<>();

        double rate = dbHelper.getRate();
        if(rate != 0){
            try {
                jsonToUpload.put("Rate",rate);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
                toUpload.put("Rate Data", jsonToUpload.toString());
                ref.updateChildren(toUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            backupProducts();
                        }
                        else{
                            Toast.makeText(context, "Rate data upload failed.", Toast.LENGTH_SHORT).show();
                            backupProducts();
                        }
                    }
                });
                Log.d("BackupHandler", "backupMilkRateData: onAdd" + jsonToUpload.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            backupProducts();
        }
    }


    private void backupProducts() {
        JSONObject jsonToUpload = new JSONObject();
        HashMap<String, Object> toUpload = new HashMap<>();
        ArrayList<AddProductModel> list = dbHelper.getProducts();
        Log.d("BackupHandler", "backupProductsData: " + list.size());

        if(list.size() != 0){
            for(int i = 0; i<list.size(); i++){
                JSONObject obj = new JSONObject();
                if(!list.get(i).getProduct_name().equals("Select Product")){
                    try {
                        obj.put("Rate", list.get(i).getRate());
                        obj.put("Product Name", list.get(i).getProduct_name());
                        jsonToUpload.put(String.valueOf(i), obj);
                        Log.d("BackupHandler", "backupProductsData: onAdd" + jsonToUpload.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
            toUpload.put("Products Data", jsonToUpload.toString());
            ref.updateChildren(toUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        toast(context, "Data uploaded successfully");
                        try{
                            bpd.dismiss();
                        }
                        catch(Exception e){}
                        backupProductSale();
                    }
                    else{
                        Toast.makeText(context, "Product data upload failed.", Toast.LENGTH_SHORT).show();
                        backupProductSale();
                    }
                }
            });
        }
        else{
            backupProductSale();
        }
    }

    private void backupProductSale() {
        JSONObject jsonToUpload = new JSONObject();
        HashMap<String, Object> toUpload = new HashMap<>();
        ArrayList<ProductSaleModel> list = dbHelper.getProductSale();
        if(list.size() != 0){
            for(int i = 0; i<list.size(); i++){
                JSONObject obj = new JSONObject();
                try {
                    obj.put("ID",list.get(i).getId());
                    obj.put("Product Name", list.get(i).getProduct_name());
                    obj.put("Name", list.get(i).getName());
                    obj.put("Amount",list.get(i).getAmount());
                    obj.put("Units", list.get(i).getUnits());
                    obj.put("Date", list.get(i).getDate());
                    jsonToUpload.put(String.valueOf(i), obj);
                    Log.d("BackupHandler", "backupProductSaleData: onAdd" + jsonToUpload.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
            toUpload.put("Product Sale Data", jsonToUpload.toString());
            ref.updateChildren(toUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        //toast(context, "Data uploaded successfully");
                        backupExpiryDate();
                    }
                    else{
                        Toast.makeText(context, "Product sale data upload failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            try{

            }
            catch (Exception e){

            }
        }

    }

    private void backupExpiryDate() {
        JSONObject jsonToUpload = new JSONObject();
        HashMap<String, Object> toUpload = new HashMap<>();

        String date = "", hasExpired = "";
        Cursor data = dbHelper.getExpiryDate();
        if(data.getCount() != 0){
            while(data.moveToNext()){
                date = data.getString(data.getColumnIndex("Date"));
            }
        }

        if(!date.equals("")){
            try {
                jsonToUpload.put("Expiry Date",date);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
                toUpload.put("Subscription Details", jsonToUpload.toString());
                ref.updateChildren(toUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            //toast(context, "Data uploaded successfully");
                        }
                        else{
                            toast(context, "Unable to backup");
                        }
                    }
                });
                Log.d("BackupHandler", "backupMilkRateData: onAdd" + jsonToUpload.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            try{
            }
            catch (Exception e){

            }
        }
    }

}