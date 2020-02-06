package com.juicebox.dairydaily.UI.Dashboard.SellMilk;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.juicebox.dairydaily.Models.DailyBuyObject;
import com.juicebox.dairydaily.Models.DailySalesObject;
import com.juicebox.dairydaily.Models.ShiftReportModel;
import com.juicebox.dairydaily.MyAdapters.MilkBuyAdapter;
import com.juicebox.dairydaily.MyAdapters.MilkSaleAdapter;
import com.juicebox.dairydaily.Others.BackupHandler;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.SelectPrinterDialog;
import com.juicebox.dairydaily.Others.SelectPrinterDialog2;
import com.juicebox.dairydaily.Others.SpinnerItem;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.BluetoothConnectionService;
import com.juicebox.dairydaily.UI.Dashboard.BuyMilk.BuyMilkActivity;
import com.juicebox.dairydaily.UI.Dashboard.BuyMilk.MilkBuyEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.DeleteHistory;
import com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport.BuyerRegisterActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport.ViewReportByDateActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewReport.CustomerReportActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewReport.PaymentRegisterActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewReport.ShiftReportActivity;
import com.juicebox.dairydaily.UI.UsersListActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static com.juicebox.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncateDouble;
import static com.juicebox.dairydaily.Others.UtilityMethods.useSnackBar;
import static com.juicebox.dairydaily.UI.Dashboard.SellMilk.SellMilkActivity.printData;

public class MilkSaleEntryActivity extends AppCompatActivity {

    private static final String TAG = "MilkSaleEntryActivity";

    double weightTotal = 0;
    double amountTotal = 0;
    double averageFat = 0;

    ArrayList<DailySalesObject> list;
    static ConstraintLayout scrollview;

    public static String shift, date;

    EditText id, weight;
    TextView all_buyers, amount_display, rate_view;
    TextView totalAmount;
    TextView totalWeight;
    TextView fatAverage;

    MenuItem day, evening;
    public static RecyclerView recyclerView;

    public static DbHelper dbHelper;

    boolean wantToUpdate = false;

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

        totalAmount = findViewById(R.id.amountTotal);
        totalWeight = findViewById(R.id.weightTotal);
        fatAverage = findViewById(R.id.fatAverage);

        dbHelper = new DbHelper(this);

        String name_from_user_list = getIntent().getStringExtra("name");
        int id_from_user_list = getIntent().getIntExtra("id", 0);

        if(id_from_user_list != 0){
            id.setText(String.valueOf(id_from_user_list));
        }

        boolean received_a_name = getIntent().getBooleanExtra("passed", false);
        shift = getIntent().getStringExtra("Shift");
        date = getIntent().getStringExtra("Date");

        int unique_id = getIntent().getIntExtra("Unique_Id", -1);
        int user_id = getIntent().getIntExtra("User_Id", -1);
        String name = getIntent().getStringExtra("Name");
        String passedweight = getIntent().getStringExtra("Weight");
        String passedrate = getIntent().getStringExtra("Rate");
        String passedamount = getIntent().getStringExtra("Amount");

        if(unique_id != -1 && user_id != -1){
            id.setText(String.valueOf(user_id));
            weight.setText(passedweight);
            rate_view.setText(passedrate);
            amount_display.setText(passedamount);
            all_buyers.setText(name);
            wantToUpdate = true;
        }

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

        for(DailySalesObject model : list){
            weightTotal += Double.valueOf(model.getWeight());
            amountTotal += Double.valueOf(model.getAmount());
            averageFat += Double.valueOf(model.getRate());
        }
        averageFat = averageFat / list.size();
        totalAmount.setText(String.valueOf(truncate(amountTotal)) + "Rs");
        totalWeight.setText(String.valueOf(truncate(weightTotal)) + "Ltr");
        fatAverage.setText(String.valueOf(truncate(averageFat)) + "%");
        MilkSaleAdapter adapter = new MilkSaleAdapter(list, this);
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
                    if(dbHelper.addReceiveCash(Id, date, truncate(0.00), Amount, "Sale")){
                        String toPrint ="\n\n\n"+ date + "\n" + "ID " + Id + " " + name + "\n" +
                                "SHIFT   | " + shift  +
                                "\nWEIGHT  | " + Weight + "Ltr\nRATE    | " + rate + "Rs/Ltr\n" + "TOTAL RS| " + Amount + "Rs\n      DAIRYDAILY APP\n\n\n";
                        ArrayList<DailySalesObject> list = dbHelper.getDailySalesData(date, shift);
                        MilkSaleAdapter adapter = new MilkSaleAdapter(list, this);
                        double weightTotal = 0;
                        double amountTotal = 0;
                        double averageFat = 0;
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

                        if(DashboardActivity.bluetoothAdapter != null) {
                            if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                                try {
                                    DashboardActivity.bluetoothConnectionService.write(mybyte);
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
            }
        }
        else{
            toast(this, "Fields should be filled");
        }
        id.setText("");
        all_buyers.setText("All Buyers");
        weight.setText("");
        amount_display.setText("Amount");
        new BackupHandler(MilkSaleEntryActivity.this);
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
        int id = item.getItemId();
        String line = "--------------------------------";
        String toPrint = "\n\n\nDATE  : " + date + "\nSHIFT : " + shift + "\nRate  :" + " " + rate_view.getText().toString() +"Rs/Ltr\n"+line + "\nID| " + " FAT/SNF  |" + "WEIGHT| " + "AMOUNT\n";
        if(id == R.id.printer){
            Collections.reverse(list);
            for(DailySalesObject object : list){
                int sellerId = object.getId();
                double amount = object.getAmount();
                double fat = object.getRate();
                double weight = object.getWeight();
                double snf = 0.00;
                toPrint += sellerId + "| " +truncate(fat) + "-" + truncate(snf) +"| "  + truncate(weight) + "| " + truncate(amount) + "\n";
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
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MilkSaleEntryActivity.this, SellMilkActivity.class));
        finish();
    }
}
