package com.juicebox.dairydaily.Others;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import io.paperdb.Paper;

import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;

public class DataRetrievalHandler {

    Context context;
    DbHelper dbHelper;

    public DataRetrievalHandler(Context context){
        this.context = context;
        dbHelper = new DbHelper(context);
        retrieveReceiveCash();
    }

    private void retrieveReceiveCash(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    try {
                        String retrievedJsonString = dataSnapshot.child("Receive Cash Data").getValue().toString();
                        Log.d("BackupHandler", "retrieveReceiveCashData: " + retrievedJsonString);
                        dbHelper.clearReceiveCash();
                        JSONObject jsonObject = new JSONObject(retrievedJsonString);

                        for(int i = 0; i<jsonObject.names().length(); i++){
                            JSONObject obj = jsonObject.getJSONObject(jsonObject.names().getString(i));
                            int id = obj.getInt("ID");
                            String date = obj.getString("Date");
                            String title = obj.getString("Title");
                            String credit = obj.getString("Credit");
                            String debit = obj.getString("Debit");
                            dbHelper.addReceiveCash(id, date, credit, debit, title);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //toast(context, "Unable to retrieve receive cash data");
                    }

                }
                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        retrieveMilkSaleData();
    }

    public void retrieveMilkSaleData(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    try {
                        String retrievedJsonString = dataSnapshot.child("Milk Sale Data").getValue().toString();
                        Log.d("BackupHandler", "retrieveMilkSaleData: " + retrievedJsonString);
                        dbHelper.clearMilkSaleTable();
                        JSONObject jsonObject = new JSONObject(retrievedJsonString);
                        for(int i = 0; i<jsonObject.names().length(); i++){
                            JSONObject obj = jsonObject.getJSONObject(jsonObject.names().getString(i));
                            int id = obj.getInt("ID");
                            String name = obj.getString("Name");
                            String weight = truncate(Double.valueOf(obj.getString("Weight")));
                            String amount = truncate(Double.valueOf(obj.getString("Amount")));
                            String rate = truncate(Double.valueOf(obj.getString("Rate")));
                            String shift = obj.getString("Shift");
                            String date = obj.getString("Date");
                            Log.d("BackupHandler", "retrieveMilkSaleData: " + date);
                            String credit = truncate(Double.valueOf(obj.getString("Credit")));
                            String debit = truncate(Double.valueOf(obj.getString("Debit")));
                            dbHelper.addSalesEntry(id, name, weight, amount, rate, shift, date, debit, credit);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //toast(context, "Unable to retrieve milk sale data");
                    }

                }
                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        retrieveCustomers();
    }

    private void retrieveCustomers(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    try {
                        String retrievedJsonString = dataSnapshot.child("All Sellers").getValue().toString();
                        Log.d("BackupHandler", "retrieveSellerData: " + retrievedJsonString);
                        dbHelper.clearSellerTable();
                        JSONObject jsonObject = new JSONObject(retrievedJsonString);
                        Log.d("DataRetrievalHandler", "retrieveCustomers: " + retrievedJsonString);
                        for(int i = 0; i<jsonObject.names().length(); i++){
                            JSONObject obj = jsonObject.getJSONObject(jsonObject.names().getString(i));
                            int id = obj.getInt("ID");
                            String name = obj.getString("Name");
                            String phoneNumber = obj.getString("PhoneNumber");
                            String status = obj.getString("Status");
                            String address = obj.getString("Address");
                            dbHelper.addSeller(id, name, phoneNumber, address, status);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        //toast(context, "Unable to retrieve seller data");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    try {
                        String retrievedJsonString = dataSnapshot.child("All Buyers").getValue().toString();
                        Log.d("BackupHandler", "retrieveBuyerData: " + retrievedJsonString);
                        dbHelper.clearBuyerTable();
                        JSONObject jsonObject = new JSONObject(retrievedJsonString);
                        for(int i = 0; i<jsonObject.names().length(); i++){
                            JSONObject obj = jsonObject.getJSONObject(jsonObject.names().getString(i));
                            int id = obj.getInt("ID");
                            String name = obj.getString("Name");
                            String phoneNumber = obj.getString("PhoneNumber");
                            String status = obj.getString("Status");
                            String address = obj.getString("Address");
                            dbHelper.addBuyer(id, name, phoneNumber, address, status);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //toast(context, "Unable to retrieve buyer data");
                    }

                }
                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        retrieveMilkBuyData();
    }

    public void retrieveMilkBuyData(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    try {
                        String retrievedJsonString = dataSnapshot.child("Milk Buy Data").getValue().toString();
                        Log.d("BackupHandler", "retrieveMilkBuyData: " + retrievedJsonString);
                        dbHelper.clearMilkBuyTable();
                        JSONObject jsonObject = new JSONObject(retrievedJsonString);
                        for(int i = 0; i<jsonObject.names().length(); i++){
                            JSONObject obj = jsonObject.getJSONObject(jsonObject.names().getString(i));
                            int id = obj.getInt("ID");
                            String name = obj.getString("Name");
                            String weight = truncate(Double.valueOf(obj.getString("Weight")));
                            String amount = truncate(Double.valueOf(obj.getString("Amount")));
                            String rate = truncate(Double.valueOf(obj.getString("Rate")));
                            String shift = obj.getString("Shift");
                            String date = obj.getString("Date");
                            String type = obj.getString("Type");
                            Log.d("BackupHandler", "retrieveMilkBuyData: " + date);
                            String fat = obj.getString("Fat");
                            String snf = obj.getString("SNF");
                            dbHelper.addBuyEntry(id, name, weight, amount, rate, shift, date, fat, snf, type);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        retrieveProductSale();
    }

    private void retrieveProductSale(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    try {
                        String retrievedJsonString = dataSnapshot.child("Product Sale Data").getValue().toString();
                        dbHelper.clearProductSale();
                        JSONObject jsonObject = new JSONObject(retrievedJsonString);
                        for(int i = 0; i<jsonObject.names().length(); i++){
                            JSONObject obj = jsonObject.getJSONObject(jsonObject.names().getString(i));
                            int id = obj.getInt("ID");
                            String name = obj.getString("Name");
                            String product_name = obj.getString("Product Name");
                            String units = obj.getString("Units");
                            String amount = obj.getString("Amount");
                            dbHelper.addProductSale(id, name, product_name, units, amount);
                            Log.d("BackupHandler", "retrieveProductSaleData: " + retrievedJsonString);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                        //toast(context, "Unable to retrieve product sale data");
                    }

                }
                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        retrieveProducts();
    }

    private void retrieveProducts(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    try {
                        String retrievedJsonString = dataSnapshot.child("Products Data").getValue().toString();
                        dbHelper.clearProducts();
                        JSONObject jsonObject = new JSONObject(retrievedJsonString);
                        for(int i = 0; i<jsonObject.names().length(); i++){
                            JSONObject obj = jsonObject.getJSONObject(jsonObject.names().getString(i));
                            String rate = obj.getString("Rate");
                            String name = obj.getString("Product Name");
                            dbHelper.addProduct(name, rate);
                        }
                        Log.d("BackupHandler", "retrieveProductData: " + retrievedJsonString);
                    } catch (Exception e) {
                        e.printStackTrace();

                        //toast(context, "Unable to retrieve milk rate data");
                    }

                }
                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        retrieveMilkRate();
    }

    private void retrieveMilkRate(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    try {
                        String retrievedJsonString = dataSnapshot.child("Rate Data").getValue().toString();
                        Log.d("RetrievalHandler", "retrieveMilkRateData: " + retrievedJsonString);
                        //dbHelper.clearRate();
                        JSONObject obj = new JSONObject(retrievedJsonString);
                        dbHelper.setRate(obj.getDouble("Rate"));
                        retrieveExpiryDate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //toast(context, "Data recovered successfully");
                }
                else{
                    toast(context, "Data recovered successfully");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void retrieveExpiryDate(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    try {
                        String retrievedJsonString = dataSnapshot.child("Subscription Details").getValue().toString();
                        Log.d("RetrievalHandler", "retrieveMilkRateData: " + retrievedJsonString);
                        //dbHelper.clearRate();
                        JSONObject obj = new JSONObject(retrievedJsonString);
                        dbHelper.setExpiryDate(obj.getString("Expiry Date"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //toast(context, "Data recovered successfully");
                }
                else{
                    toast(context, "Data recovered successfully");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
