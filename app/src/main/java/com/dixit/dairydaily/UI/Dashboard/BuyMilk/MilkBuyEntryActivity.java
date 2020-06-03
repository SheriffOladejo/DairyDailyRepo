package com.dixit.dairydaily.UI.Dashboard.BuyMilk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.InitDrawerBoard;
import com.dixit.dairydaily.UI.Dashboard.SellMilk.SellMilkActivity;
import com.google.android.material.navigation.NavigationView;
import com.dixit.dairydaily.Models.DailyBuyObject;
import com.dixit.dairydaily.MyAdapters.MilkBuyAdapter;
import com.dixit.dairydaily.Others.BackupHandler;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.Others.BluetoothConnectionService;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;
import com.dixit.dairydaily.UI.Dashboard.UsersListActivity;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

import static com.dixit.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.Others.UtilityMethods.truncate;
import static com.dixit.dairydaily.Others.UtilityMethods.truncateDouble;
import static com.dixit.dairydaily.Others.UtilityMethods.useSnackBar;

public class MilkBuyEntryActivity extends InitDrawerBoard {

    private final int REQUEST_READ_PHONE_STATE = 1;
    public static BluetoothConnectionService bluetoothConnectionService;

    public static boolean wantToUpdate = false;
    double weightAmount;
    MilkBuyAdapter adapter;

    private static final String TAG = "MilkBuyEntryActivity";

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    String shift, date;
    String fatString;
    String weightString;
    String snfValue;
    private String rate;
    private static ConstraintLayout linearLayout;
    public static String type;
    public static RecyclerView recyclerView;

    private String date_in_long;
    public static int unique_id = -1;

    public static double weightTotal = 0;
    public static double amountTotal = 0;
    public static double averageFat = 0;

    public static TextView totalAmount, totalWeight, fatAverage;

    String passedDate;

    public static RadioButton cow_button;
    private RadioButton buffalo_button;
    ArrayList<DailyBuyObject> list;

    public static EditText id, weight, snf, fat, rate_display;
    public static TextView seller_display, amount_display;

    public static DbHelper dbHelper;

    MenuItem day, evening;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.milk_buy_entry);

        dbHelper = new DbHelper(this);
        //initPrinting();

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        hideKeyboard(MilkBuyEntryActivity.this);

        getSupportActionBar().setTitle("Buy Milk Entry");
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initDrawer();

        id = findViewById(R.id.id);
        linearLayout = findViewById(R.id.constraintlayout);
        weight = findViewById(R.id.weight);
        fat = findViewById(R.id.fat);
        snf = findViewById(R.id.snf);
        seller_display = findViewById(R.id.seller_display);
        rate_display = findViewById(R.id.rate_display);
        amount_display = findViewById(R.id.amount_display);
        recyclerView = findViewById(R.id.recyclerview);
        buffalo_button = findViewById(R.id.buffalo_button);
        cow_button = findViewById(R.id.cow_button);

        cow_button.setChecked(true);
        cow_button.toggle();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        totalAmount = findViewById(R.id.amountTotal);
        totalWeight = findViewById(R.id.weightTotal);
        fatAverage = findViewById(R.id.fatAverage);

        String name_from_user_list = getIntent().getStringExtra("name");
        int id_from_user_list = getIntent().getIntExtra("id", 0);
        boolean received_a_name = getIntent().getBooleanExtra("passed", false);
        shift = getIntent().getStringExtra("Shift");
        date = getIntent().getStringExtra("Date");
        date_in_long = getIntent().getStringExtra("Date_In_Long").equals("0") ? System.currentTimeMillis()+"":getIntent().getStringExtra("Date_In_Long");
        Log.d(TAG, "date_in_long: " + date_in_long);
        passedDate = getIntent().getStringExtra("PassedDate");

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(wantToUpdate){

                    fatString = fat.getText().toString();
                    snfValue = snf.getText().toString();
                    weightString = weight.getText().toString();
                    String user_id = id.getText().toString();
                    if(user_id.equals("") || seller_display.getText().toString().equals("Not Found"))
                        toast(MilkBuyEntryActivity.this, "Error");
                    else{
                        if(fatString.equals("") || snfValue.equals("") || weightString.equals("")){
                            toast(MilkBuyEntryActivity.this, "Error");
                        }
                        else{
                            try{
                                dbHelper.updateMilkBuy(unique_id, Integer.valueOf(id.getText().toString()), seller_display.getText().toString(),
                                        weightString, rate_display.getText().toString(),
                                        amount_display.getText().toString(), fatString, snfValue, type);
                            }
                            catch (Exception e){
                                toast(MilkBuyEntryActivity.this, "Unable to update. Check values");
                            }
                        }


                        id.setText("");
                        seller_display.setText("All Sellers");
                        weight.setText("");
                        fat.setText("");
                        snf.setText("");
                        rate_display.setText("Rs/Ltr");
                        amount_display.setText("Amount");
                        list = dbHelper.getDailyBuyData(date, shift);
                        Log.d(TAG, "list size: " + list.size());
                        MilkBuyAdapter adapter = new MilkBuyAdapter(MilkBuyEntryActivity.this, list);
                        recyclerView.setAdapter(adapter);
                        new BackupHandler(MilkBuyEntryActivity.this);
                        wantToUpdate = false;
                        list = dbHelper.getDailyBuyData(date, shift);


                        weightTotal = 0;
                        amountTotal = 0;
                        averageFat = 0;

                        for(DailyBuyObject model : list){
                            weightTotal += Double.valueOf(model.getWeight());
                            amountTotal += Double.valueOf(model.getAmount());
                            averageFat += Double.valueOf(model.getFat());
                        }
                        totalAmount.setText(String.valueOf(truncate(amountTotal)) + "Rs");
                        totalWeight.setText(String.valueOf(truncate(weightTotal)) + "Ltr");
                        fatAverage.setText(String.valueOf(truncate(averageFat/list.size())) + "%");

                    }

                }
                else{
                    saveEntry();
                }
            }
        });

        list = dbHelper.getDailyBuyData(date, shift);
        Log.d(TAG, "list size: " + list.size());
        adapter = new MilkBuyAdapter(this, list);

        weightTotal = 0;
        amountTotal = 0;
        averageFat = 0;

        for(DailyBuyObject model : list){
            weightTotal += Double.valueOf(model.getWeight());
            amountTotal += Double.valueOf(model.getAmount());
            averageFat += Double.valueOf(model.getFat());
        }
        totalAmount.setText(String.valueOf(truncate(amountTotal)) + "Rs");
        totalWeight.setText(String.valueOf(truncate(weightTotal)) + "Ltr");
        fatAverage.setText(String.valueOf(truncate(averageFat/list.size())) + "%");


        int permissionChceck = ContextCompat.checkSelfPermission(MilkBuyEntryActivity.this, Manifest.permission.READ_PHONE_STATE);
        if(permissionChceck != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "permission denied so ");
            ActivityCompat.requestPermissions(MilkBuyEntryActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        }
        else{
            Log.d(TAG, "permission granted so ");
            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        }

        recyclerView.setAdapter(adapter);

        if(received_a_name){
            seller_display.setText(name_from_user_list);
            id.setText(String.valueOf(id_from_user_list));
        }

        seller_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MilkBuyEntryActivity.this, UsersListActivity.class);
                intent.putExtra("From", "MilkBuyEntryActivity");
                intent.putExtra("Shift", shift);
                intent.putExtra("Date", date);
                startActivity(intent);
            }
        });

        rate_display.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                try{
                    weightAmount = Double.valueOf(weight.getText().toString());
                }
                catch(Exception e){

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    double amount = weightAmount * Double.valueOf(rate_display.getText().toString());
                    amount_display.setText(""+truncate(amount));
                }
                catch(Exception e){

                }
            }
        });

        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                fatString = fat.getText().toString();
                snfValue = snf.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!snfValue.isEmpty()){
                    try{
                        double intermediate = truncateDouble(Double.valueOf(snfValue) * 10);
                        int convertedValue = (int) intermediate;
                        String concatValue = "SNF" + convertedValue;
                        Log.d(TAG, "concatvalue:"+concatValue);
                        if(cow_button.isChecked()) {
                            type = "COW";
                            rate = dbHelper.getRate(fatString, concatValue);
                        }
                        else {
                            type = "BUFFALO";
                            rate = dbHelper.getBuffaloRate(fatString, concatValue);
                        }
                        if(!rate.equals("Not Found") && !rate.equals("")){
                            rate_display.setText(rate);
                            amount_display.setText(String.valueOf(truncate(Double.valueOf(weightString) * Double.valueOf(rate))));
                        }
                        else{
                            rate_display.setText("Not Found");
                            amount_display.setText("Amount");
                        }
                    }
                    catch(Exception e){
                        //toast(MilkBuyEntryActivity.this,"Error: " + e.getMessage());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        snf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                fatString = fat.getText().toString();
                weightString = weight.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!fatString.isEmpty() && !weightString.isEmpty()){
                    snfValue = s.toString();
                    if(!snfValue.isEmpty()){
                        try{
                            double intermediate = truncateDouble(Double.valueOf(snfValue) * 10);
                            int convertedValue = (int) intermediate;
                            String concatValue = "SNF" + convertedValue;
                            Log.d(TAG, "concatValue: " + concatValue);
                            if(cow_button.isChecked()) {
                                type = "COW";
                                rate = dbHelper.getRate(fatString, concatValue);
                            }
                            else {
                                type = "BUFFALO";
                                rate = dbHelper.getBuffaloRate(fatString, concatValue);
                            }
                            if(!rate.equals("Not Found") && !rate.equals("")){
                                rate_display.setText(rate);
                                amount_display.setText(String.valueOf(truncate(Double.valueOf(weightString) * Double.valueOf(rate))));
                                rate = "";
                            }
                            else{
                                rate_display.setText("Not Found");
                                amount_display.setText("Amount");
                            }
                        }
                        catch(Exception e){

                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                snfValue = snf.getText().toString();
                weightString = weight.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!snfValue.isEmpty() && !weightString.isEmpty()){
                    fatString = s.toString();
                    if(!fatString.isEmpty()){
                        try{
                            double intermediate = truncateDouble(Double.valueOf(snfValue) * 10);
                            int convertedValue = (int) intermediate;
                            String concatValue = "SNF" + convertedValue;
                            if(cow_button.isChecked())
                                rate = dbHelper.getRate(fatString, concatValue);
                            else
                                rate = dbHelper.getBuffaloRate(fatString, concatValue);
                            if(!rate.equals("Not Found") && !rate.equals("")){
                                rate_display.setText(rate);
                                Log.d(TAG, "Rate: " + rate);
                                amount_display.setText(String.valueOf(truncate(Double.valueOf(weightString) * Double.valueOf(rate))));
                                rate = "";
                            }
                            else{
                                rate_display.setText("Not Found");
                                amount_display.setText("Amount");
                            }
                        }
                        catch(Exception e){

                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                snfValue = snf.getText().toString();
                fatString = fat.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    if(!snfValue.isEmpty() && !weightString.isEmpty()){
                        weightString = s.toString();
                        if(!weightString.isEmpty()){
                            try{
                                double intermediate = Double.valueOf(snfValue) * 10;
                                String concatValue = "SNF" + intermediate;
                                if(cow_button.isChecked())
                                    rate = dbHelper.getRate(fatString, concatValue);
                                else
                                    rate = dbHelper.getBuffaloRate(fatString, concatValue);
                                if(!rate.equals("Not Found") && !rate.equals("")){
                                    rate_display.setText(rate);
                                    Log.d(TAG, "Rate: " + rate);
                                    amount_display.setText(String.valueOf(truncate(Double.valueOf(weightString) * Double.valueOf(rate))));
                                    rate = "";
                                }
                                else{
                                    rate_display.setText("Not Found");
                                    amount_display.setText("Amount");
                                }
                            }
                            catch(Exception e){

                            }
                        }
                    }
                }
                catch(Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")){
                    try{
                        int id = Integer.valueOf(s.toString());
                        String name = dbHelper.getSellerName(id);
                        if(name.equals("")){
                            seller_display.setText("Not Found");
                        }
                        else{
                            seller_display.setText(name);
                        }
                    }
                    catch(Exception e){
                        seller_display.setText("Not Found");
                    }
                }
                else{
                    seller_display.setText("All Sellers");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveEntry(){
        int idInt = -1;
        try {

            idInt = Integer.valueOf(id.getText().toString());
            String nameString = seller_display.getText().toString();
            String amountDouble = truncate(Double.valueOf(amount_display.getText().toString()));
            String rateDouble = truncate(Double.valueOf(rate_display.getText().toString()));
            String weightDouble = truncate(Double.valueOf(weight.getText().toString()));
            String fatDouble = truncate(Double.valueOf(fat.getText().toString()));
            String snfDouble = truncate(Double.valueOf(snf.getText().toString()));

            if(idInt > 0 && !nameString.equals("Not Found")){
                if(String.valueOf(idInt).equals("") || String.valueOf(amountDouble).equals("") || String.valueOf(rateDouble).equals("")
                        || String.valueOf(weightDouble).equals("") || String.valueOf(fatDouble).equals("") || String.valueOf(snfDouble).equals("")){
                    useSnackBar("Operation Failed", linearLayout);
                }
                else{
                    if(!dbHelper.addBuyEntry(idInt, nameString, weightDouble, amountDouble, rateDouble, shift, date, fatString, snfDouble, type, date_in_long)){
                        useSnackBar("Unable to add entry.", linearLayout);
                        Log.d(TAG, "Unable to add entry");
                    }
                    else{
                        String toPrint = date + "\n" + "ID " + idInt + "    " + nameString + "\n" +
                                "SHIFT  :   " + shift + "\n" + "TYPE   :   " + type + "\nFAT %  :   " + fatString + "\nSNF/CLR:   " + snfValue +
                                "\nWEIGHT :   " + weightString + " Ltr\nRATE   :   " + rateDouble + "Rs/Ltr" + "\nAmount :   " + amountDouble + "Rs";
                        ArrayList<DailyBuyObject> list = dbHelper.getDailyBuyData(date, shift);
                        Log.d(TAG, "list size: " + list.size());
                        MilkBuyAdapter adapter = new MilkBuyAdapter(this, list);
                        weightTotal = 0;
                        amountTotal = 0;
                        averageFat = 0;
                        for(DailyBuyObject model : list){
                            weightTotal += Double.valueOf(model.getWeight());
                            amountTotal += Double.valueOf(model.getAmount());
                            averageFat += Double.valueOf(model.getFat());
                        }
                        toPrint += "\nDAIRYDAILY APP\n\n\n";
                        totalAmount.setText(String.valueOf(truncate(amountTotal)) + "Rs");
                        totalWeight.setText(String.valueOf(truncate(weightTotal)) + "Ltr");
                        fatAverage.setText(String.valueOf(truncate(averageFat/list.size())) + "%");
                        byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());

                        recyclerView.setAdapter(adapter);

                        //new BackupHandler(MilkBuyEntryActivity.this);
                        if(BuyMilkActivity.usePrinter){
                            Log.d(TAG, "toPrint: " + toPrint);
                            if(DashboardActivity.bluetoothAdapter != null) {
                                if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                                    DashboardActivity.bluetoothConnectionService.write(mybyte);
                                } else {
                                    toast(MilkBuyEntryActivity.this, "Printer is not connected");
                                }
                            }
                            else{
                                toast(MilkBuyEntryActivity.this, "Bluetooth is off");
                            }
                        }

                        if(BuyMilkActivity.sms_enable){

                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.putExtra("address", dbHelper.getSellerPhone(idInt));
                            i.putExtra("sms_body", toPrint);
                            i.setType("vnd.android-dir/mms-sms");
                            startActivity(i);
                        }

                        id.setText("");
                        amount_display.setText("Amount");
                        rate_display.setText("Rs/Ltr");
                        weight.setText("");
                        fat.setText("");
                        snf.setText("");
                        seller_display.setText("All Sellers");
                        if(BuyMilkActivity.online){
                            new BackupHandler(MilkBuyEntryActivity.this);
                        }
                    }
                }
            }
        }
        catch(Exception e){

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
        dateItem.setTitle(date);

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
        int id = item.getItemId();
        if(toggle.onOptionsItemSelected(item))
            return true;
        String line = "----------------------------";
        if(BuyMilkActivity.usePrinter){
            list = dbHelper.getDailyBuyData(date, shift);
            String toPrint = "\nDATE  |" + date + "\nSHIFT |" + shift + "\n"+line +  "\nID |" + " FAT/SNF | " + "WEIGHT|" + "AMOUNT\n";
            if (id == R.id.printer) {
                Collections.reverse(list);
                for (DailyBuyObject object : list) {
                    int sellerId = object.getId();
                    String amount = StringUtils.rightPad(StringUtils.truncate(truncate(object.getAmount()), 5), 6, " ");
                    String fat = truncate(object.getFat());
                    String snf = truncate(object.getSnf());
                    String weight = StringUtils.rightPad(truncate(object.getWeight()), 6, " ");
                    Log.d(TAG, "amount:" +amount);
                    toPrint += StringUtils.rightPad(""+sellerId, 3, "") + "|" + fat + "-" + snf + "| "  + weight + "|" + amount + "\n";
                }
                toPrint += line + "\n";
                toPrint += "TOTAL AMOUNT: " + truncate(amountTotal) + "Rs";
                toPrint += "\nTOTAL WEIGHT: " + weightTotal + "Ltr\n";
                toPrint += line + "\n";
                toPrint += "      DAIRYDAILY APP\n\n\n";
                Log.d(TAG, "toPrint: " + toPrint);
                byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());

                if (DashboardActivity.bluetoothAdapter != null) {
                    if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                        try {
                            DashboardActivity.bluetoothConnectionService.write(mybyte);
                        } catch (Exception e) {
                            toast(MilkBuyEntryActivity.this, "Unable to print");
                        }
                    } else {
                        toast(MilkBuyEntryActivity.this, "Printer is not connected");
                    }
                } else {
                    toast(MilkBuyEntryActivity.this, "Bluetooth is off");
                }
            }
        }
        else
            toast(MilkBuyEntryActivity.this, "Enable printer use");
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
