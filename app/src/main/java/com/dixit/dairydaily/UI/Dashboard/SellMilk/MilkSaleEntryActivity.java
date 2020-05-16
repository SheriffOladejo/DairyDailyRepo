package com.dixit.dairydaily.UI.Dashboard.SellMilk;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.InitDrawerBoard;
import com.google.android.material.navigation.NavigationView;
import com.dixit.dairydaily.Models.DailySalesObject;
import com.dixit.dairydaily.MyAdapters.MilkSaleAdapter;
import com.dixit.dairydaily.Others.BackupHandler;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;
import com.dixit.dairydaily.UI.Dashboard.UsersListActivity;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

import static com.dixit.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.Others.UtilityMethods.truncate;
import static com.dixit.dairydaily.UI.Dashboard.SellMilk.SellMilkActivity.online;

public class MilkSaleEntryActivity extends InitDrawerBoard {

    private static final String TAG = "MilkSaleEntryActivity";

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    MilkSaleAdapter adapter;

    public static double weightTotal = 0;
    public static double amountTotal = 0;
    public static double averageFat = 0;

    public static int unique_id;
    public static int user_id;

    ArrayList<DailySalesObject> list;
    static ConstraintLayout scrollview;

    public static String shift, date;

    public static EditText id, weight;
    public static TextView all_buyers, amount_display, rate_view;
    public static TextView totalAmount;
    public static TextView totalWeight;
    public static TextView fatAverage;

    MenuItem day, evening;
    public static RecyclerView recyclerView;

    public static DbHelper dbHelper;

   public static boolean wantToUpdate = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk_sale_entry);

        getSupportActionBar().setTitle("Sell Milk");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        id = findViewById(R.id.id);
        weight = findViewById(R.id.weight);
        all_buyers = findViewById(R.id.buyers);
        rate_view = findViewById(R.id.rate_view);
        amount_display = findViewById(R.id.amount_display);
        scrollview = findViewById(R.id.constraintlayout);
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        initDrawer();

        totalAmount = findViewById(R.id.amountTotal);
        totalWeight = findViewById(R.id.weightTotal);
        fatAverage = findViewById(R.id.fatAverage);

        dbHelper = new DbHelper(this);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String name_from_user_list = getIntent().getStringExtra("name");
        int id_from_user_list = getIntent().getIntExtra("id", 0);

        if(id_from_user_list != 0){
            id.setText(String.valueOf(id_from_user_list));
        }

        boolean received_a_name = getIntent().getBooleanExtra("passed", false);
        shift = getIntent().getStringExtra("Shift");
        date = getIntent().getStringExtra("Date");


        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(wantToUpdate){
                    dbHelper.updateMilkSale(unique_id, user_id, all_buyers.getText().toString(), weight.getText().toString(), rate_view.getText().toString(), amount_display.getText().toString());
                    id.setText("");
                    all_buyers.setText("All Buyers");
                    weight.setText("");
                    amount_display.setText("Amount");
                    list = dbHelper.getDailySalesData(date, shift);
                    Log.d(TAG, "list size: " + list.size());
                    MilkSaleAdapter adapter = new MilkSaleAdapter( list, MilkSaleEntryActivity.this);
                    recyclerView.setAdapter(adapter);
                    new BackupHandler(MilkSaleEntryActivity.this);
                    wantToUpdate = false;
                }
                else{
                    saveEntry();
                }
            }
        });

        list = dbHelper.getDailySalesData(date, shift);

        weightTotal = 0;
        amountTotal=0;
        averageFat=0;

        for(DailySalesObject model : list){
            weightTotal += Double.valueOf(model.getWeight());
            amountTotal += Double.valueOf(model.getAmount());
            averageFat += Double.valueOf(model.getRate());
        }
        averageFat = averageFat / list.size();
        totalAmount.setText(String.valueOf(truncate(amountTotal)) + "Rs");
        totalWeight.setText(String.valueOf(truncate(weightTotal)) + "Ltr");
        fatAverage.setText(String.valueOf(truncate(averageFat)) + "%");
        adapter = new MilkSaleAdapter(list, this);
        recyclerView.setAdapter(adapter);

        final double rate = dbHelper.getRate();
        rate_view.setText(String.valueOf(rate));

        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    if(!s.toString().isEmpty()){
                        double amount = rate * (Double.valueOf(s.toString()));
                        amount_display.setText(String.valueOf(truncate(amount)));
                    }
                    else{
                        amount_display.setText("Amount");
                    }
                }
                catch(Exception e){

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(received_a_name){
            all_buyers.setText(name_from_user_list);
            id.setText(String.valueOf(id_from_user_list));
        }

        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")){
                    try{
                        int id = Integer.valueOf(s.toString());
                        String name = dbHelper.getBuyerName(id);
                        if(name.equals("")){
                            all_buyers.setText("Not Found");
                        }
                        else{
                            all_buyers.setText(name);
                        }
                    }
                    catch(Exception e){
                        all_buyers.setText("Not Found");
                    }
                }
                else{
                    all_buyers.setText("All Buyers");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        all_buyers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MilkSaleEntryActivity.this, UsersListActivity.class);
                intent.putExtra("From", "MilkSaleEntryActivity");
                intent.putExtra("Shift", shift);
                intent.putExtra("Date", date);
                startActivity(intent);
            }
        });

        hideKeyboard(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveEntry(){
        Log.d(TAG, "wantToSave: " + wantToUpdate);
        String name = all_buyers.getText().toString();
        int Id = -1;
        try{
            Id = Integer.valueOf(id.getText().toString());
        }
        catch (Exception e){}

        String Weight = "", Amount="", rate="";

        try{
            Weight = truncate(Double.valueOf(weight.getText().toString()));
            Amount = truncate(Double.valueOf(amount_display.getText().toString()));
            rate = truncate(Double.valueOf(rate_view.getText().toString()));
        }
        catch (Exception e){

        }

        if(Id != -1 && !name.equals("Not Found")){
            if(!String.valueOf(weight).equals("")){
                Log.d(TAG, "saveEntry: " + date);
                if(!dbHelper.addSalesEntry(Id,name, Weight, Amount, rate, shift, date, Amount, truncate(0.00)))
                    Toast.makeText(MilkSaleEntryActivity.this, "Unable to add entry", Toast.LENGTH_SHORT).show();
                else {
                    if(dbHelper.addReceiveCash(Id, date, truncate(0.00), Amount, "Sale", shift.substring(0,1))){
                        String toPrint ="\n\n\n"+ date + "\n" + "ID      : " + Id + " " + name + "\n" +
                                "SHIFT   :   " + shift  +
                                "\nWEIGHT  :   " + Weight + "Ltr\nRATE    :   " + rate + "Rs/Ltr\n" + "TOTAL RS:   " + Amount + "Rs\n      DAIRYDAILY APP\n\n\n";
                        ArrayList<DailySalesObject> list = dbHelper.getDailySalesData(date, shift);
                        MilkSaleAdapter adapter = new MilkSaleAdapter(list, this);
                        weightTotal = 0;
                        amountTotal = 0;
                        averageFat = 0;
                        for(DailySalesObject model : list){
                            weightTotal += Double.valueOf(model.getWeight());
                            amountTotal += Double.valueOf(model.getAmount());
                            averageFat += Double.valueOf(model.getRate());
                        }
                        totalAmount.setText(String.valueOf(truncate(amountTotal)) + "Rs");
                        totalWeight.setText(String.valueOf(truncate(weightTotal)) + "Ltr");
                        fatAverage.setText(String.valueOf(truncate(averageFat/list.size())) + "%");
                        Log.d(TAG, "toPrint: " + toPrint);
                        byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());

                        recyclerView.setAdapter(adapter);
                        //new BackupHandler(MilkSaleEntryActivity.this);

                        if(DashboardActivity.bluetoothAdapter != null) {
                            if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                                DashboardActivity.bluetoothConnectionService.write(mybyte);
                            } else {
                                toast(MilkSaleEntryActivity.this, "Printer is not connected");
                            }
                        }
                        else{
                            toast(MilkSaleEntryActivity.this, "Bluetooth is off");
                        }
                    }
                    else{
                        toast(MilkSaleEntryActivity.this, "Unable to add entry to receive cash table");
                    }
                }
            }
        }
        else{
            toast(this, "Fields should be filled");
        }
        id.setText("");
        all_buyers.setText("All Buyers");
        weight.setText("");
        amount_display.setText("Amount");
        Log.d(TAG, "online: " + online);
        if(online){
            new BackupHandler(MilkSaleEntryActivity.this);
        }
    }

    boolean isInternetConnected = false;

    public void checkInternetConnect(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if(manager != null){
            boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
            if(is3g)
                isInternetConnected = true;
            boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
            if(isWifi)
                isInternetConnected = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.buy_milk_printer_shift, menu );
        day = menu.findItem(R.id.day);
        evening = menu.findItem(R.id.evening);
        MenuItem dateItem = menu.findItem(R.id.date);
        dateItem.setTitle(date.substring(0,10));

        if(shift.equals("Morning")){
            evening.setVisible(false);
            day.setVisible(true);
        }
        else if(shift.equals("Evening")){
            evening.setVisible(true);
            day.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;

        int id = item.getItemId();
        list = dbHelper.getDailySalesData(date, shift);
        String line = "--------------------------------";
        String toPrint = "\n\n\nDATE  : " + date + "\nSHIFT : " + shift + "\nRate  :" + " " + rate_view.getText().toString() +"Rs/Ltr\n"+line + "\nID | " + " FAT/SNF |" + "WEIGHT| " + "AMOUNT\n";
        if(id == R.id.printer){
            if(list.isEmpty()){
                toast(MilkSaleEntryActivity.this, "Nothing to print");
            }
            else{
                Collections.reverse(list);
                for(DailySalesObject object : list){
                    int sellerId = object.getId();
                    double amount = object.getAmount();
                    double fat = object.getRate();
                    double weight = object.getWeight();
                    double snf = 0.00;
                    toPrint += StringUtils.rightPad(""+sellerId, 3, "") + "|" +truncate(fat) + "-" + truncate(snf) +"|"  + StringUtils.rightPad(""+truncate(weight), 6,"") + "|" + StringUtils.rightPad(truncate(amount), 6, "") + "\n";
                }
                toPrint += line + "\n";
                toPrint += "TOTAL AMOUNT: " + amountTotal + "Rs";
                toPrint += "\nTOTAL WEIGHT: " + weightTotal + "Ltr";
                toPrint += "\n" + line;
                toPrint += "\n      DAIRYDAILY APP\n\n\n";
                Log.d(TAG, "toPrint: " + toPrint);

                byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());

                if(DashboardActivity.bluetoothAdapter != null) {
                    if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                        try {
                            DashboardActivity.bluetoothConnectionService.write(mybyte);
                            list.clear();
                            adapter = new MilkSaleAdapter(list,MilkSaleEntryActivity.this);
                            recyclerView.setAdapter(adapter);
                            Log.d(TAG, "onOptionsSelected: Printing with DashboardActivity BT");
                        } catch (Exception e) {
                            Log.d(TAG, "onOptionsSelected: Unable to print");
                            toast(MilkSaleEntryActivity.this, "Unable to print");
                        }
                    } else {
                        toast(MilkSaleEntryActivity.this, "Printer is not connected");
                    }
                }
                else{
                    toast(MilkSaleEntryActivity.this, "Bluetooth is off");
                }
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
