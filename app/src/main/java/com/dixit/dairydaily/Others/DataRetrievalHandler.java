package com.dixit.dairydaily.Others;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import io.paperdb.Paper;

import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.Others.UtilityMethods.truncate;
import static com.dixit.dairydaily.UI.Dashboard.DrawerLayout.InitDrawerBoard.rpd;

public class DataRetrievalHandler {

    Context context;
    DbHelper dbHelper;
    DatabaseReference ref;
    ValueEventListener valueEventListener, valueEventListener1;

    public DataRetrievalHandler(Context context){
        this.context = context;
        dbHelper = new DbHelper(context);
        retrieveReceiveCash();
        retrieveMilkSaleData();

    }

    private void retrieveReceiveCash(){
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        valueEventListener = new ValueEventListener() {
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
                            String shift = obj.getString("Shift");
                            String weight = obj.getString("Weight");
                            String date_in_long = obj.getString("Date_In_Long");
                            Log.d(TAG, "weoght: " +weight);
                            dbHelper.addReceiveCash(id, date, credit, debit, title, shift, weight, date_in_long);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "Unable to retrieve receive cash data." + e.getMessage());
                    }

                }
                else{
                    Log.d(TAG, "retrieveReceiveCashData: datasnapshot doesn't exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
        //retrieveMilkSaleData();
    }

    private static final String TAG = "DataRetrievalHandler";

    public void retrieveMilkSaleData(){
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        valueEventListener = new ValueEventListener() {
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
                            String date_in_long = obj.getString("Date_In_Long");
                            String credit = truncate(Double.valueOf(obj.getString("Credit")));
                            String debit = truncate(Double.valueOf(obj.getString("Debit")));
                            dbHelper.addSalesEntry(id, name, weight, amount, rate, shift, date, debit, credit, date_in_long);
                            //dbHelper.addReceiveCash(id,date,credit,debit,"Sale",shift,weight, date_in_long);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "Unable to retrieve milk sale data." + e.getMessage());
                    }

                }
                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
        //ref.removeEventListener(valueEventListener);
        retrieveCustomers();
    }

    private void retrieveCustomers(){
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        valueEventListener = new ValueEventListener() {
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
                        Log.d(TAG, "Unable to retrieve seller data." + e.getMessage());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
        //ref.removeEventListener(valueEventListener);

        valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    try {
                        String retrievedJsonString = dataSnapshot.child("All Buyers").getValue().toString();
                        Log.d("DataRetrievalHandler", "This is also called");
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
                        Log.d(TAG, "Unable to retrieve buyer data." + e.getMessage());
                    }

                }
                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener1);
        //ref.removeEventListener(valueEventListener1);
        retrieveMilkBuyData();
    }

    public void retrieveMilkBuyData(){
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    try {
                        String retrievedJsonString = dataSnapshot.child("Milk Buy Data").getValue().toString();
                        Log.d("DataRetrievalHandler", "This is called");
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
                            String date_in_long = obj.getString("Date_In_Long");
                            dbHelper.addBuyEntry(id, name, weight, amount, rate, shift, date, fat, snf, type, date_in_long);

                        }
                        try{
                            rpd.dismiss();
                        }
                        catch(Exception e){

                        }

                    } catch (Exception e) {
                        Log.d(TAG, "Unable to retrieve milk buy data." + e.getMessage());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
        //ref.removeEventListener(valueEventListener);
        retrieveProductSale();
    }

    private void retrieveProductSale(){
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        valueEventListener = new ValueEventListener() {
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
                            String date = obj.getString("Date");
                            dbHelper.addProductSale(id, name, product_name, units, amount, date);
                            Log.d("BackupHandler", "retrieveProductSaleData: " + retrievedJsonString);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "Unable to retrieve product sale data." + e.getMessage());
                    }

                }
                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
        //ref.removeEventListener(valueEventListener);
        retrieveProducts();
    }

    private void retrieveProducts(){
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        valueEventListener = new ValueEventListener() {
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
                        Log.d(TAG, "Unable to retrieve receive products data." + e.getMessage());
                    }

                }
                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
        //ref.removeEventListener(valueEventListener);
        retrieveMilkRate();
    }

    private void retrieveMilkRate(){
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    try {
                        String retrievedJsonString = dataSnapshot.child("Rate Data").getValue().toString();
                        Log.d("RetrievalHandler", "retrieveMilkRateData: " + retrievedJsonString);
                        //dbHelper.clearRate();
                        JSONObject obj = new JSONObject(retrievedJsonString);
                        dbHelper.setRate(obj.getDouble("Rate"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "Unable to retrieve milk rate data." + e.getMessage());
                    }
                    //toast(context, "Data recovered successfully");
                }
                else{
                    //toast(context, "Data recovered successfully");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ref.addListenerForSingleValueEvent(valueEventListener);
        retrieveExpiryDate();
    }

    private void retrieveExpiryDate(){
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    try {
                        String retrievedJsonString = dataSnapshot.child("Expiry Date").getValue().toString();
                        Log.d("RetrievalHandler", "retrieveMilkRateData: " + retrievedJsonString);
                        //dbHelper.clearRate();
                        //JSONObject obj = new JSONObject(retrievedJsonString);
                        dbHelper.setExpiryDate(retrievedJsonString);
                        toast(context, "Data recovered successfully");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "Unable to retrieve expiry date data." + e.getMessage());
                    }
                }
                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //ref.addValueEventListener(valueEventListener);
    }

    public void removeValueEventListener(){
        ref.removeEventListener(valueEventListener);
        ref.removeEventListener(valueEventListener1);
    }
}
