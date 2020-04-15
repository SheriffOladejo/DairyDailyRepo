package com.juicebox.dairydaily.UI.Dashboard.BuyMilk;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.juicebox.dairydaily.Models.DailyBuyObject;
import com.juicebox.dairydaily.MyAdapters.MilkBuyAdapter;
import com.juicebox.dairydaily.Others.BackupHandler;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.Logout;
import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.Others.WarningDialog;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.BluetoothConnectionService;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.DeleteHistory;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.MilkHistoryActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ProfileActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.UpgradeToPremium;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ViewAllEntryActivity;
import com.juicebox.dairydaily.UI.UsersListActivity;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import io.paperdb.Paper;

import static com.juicebox.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncateDouble;
import static com.juicebox.dairydaily.Others.UtilityMethods.useSnackBar;

public class MilkBuyEntryActivity extends AppCompatActivity {

    private final int REQUEST_READ_PHONE_STATE = 1;
    public static BluetoothConnectionService bluetoothConnectionService;

    boolean wantToUpdate = false;
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
    String type;
    public static RecyclerView recyclerView;

    boolean printed = false;

    public static double weightTotal = 0;
    public static double amountTotal = 0;
    public static double averageFat = 0;

    public static TextView totalAmount, totalWeight, fatAverage;

    String passedDate;

    private RadioButton cow_button;
    private RadioButton buffalo_button;
    ArrayList<DailyBuyObject> list;

    private EditText id, weight, snf, fat, rate_display;
    private TextView seller_display, amount_display;

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
        initDashboard();

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
        passedDate = getIntent().getStringExtra("PassedDate");

        int unique_id = getIntent().getIntExtra("Unique_Id", -1);
        int user_id = getIntent().getIntExtra("User_Id", -1);
        String name = getIntent().getStringExtra("Name");
        String passedweight = getIntent().getStringExtra("Weight");
        String passedFat = getIntent().getStringExtra("Fat");

        String passedSnf= getIntent().getStringExtra("SNF");
        String passedrate = getIntent().getStringExtra("Rate");
        String passedamount = getIntent().getStringExtra("Amount");

        if(unique_id != -1 && user_id != -1){
            id.setText(String.valueOf(user_id));
            weight.setText(passedweight);
            fat.setText(passedFat);
            amount_display.setText(passedamount);
            snf.setText(passedSnf);
            seller_display.setText(name);
            wantToUpdate = true;
        }

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(wantToUpdate){
                    dbHelper.updateMilkBuy(unique_id, user_id, seller_display.getText().toString(),
                            weight.getText().toString(), rate_display.getText().toString(),
                            amount_display.getText().toString(), fat.getText().toString(), snf.getText().toString(), type);
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
                    amount_display.setText(""+amount);
                }
                catch(Exception e){

                }
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
                    if(!dbHelper.addBuyEntry(idInt, nameString, weightDouble, amountDouble, rateDouble, shift, date, fatString, snfDouble, type)){
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
                        Log.d(TAG, "toPrint: " + toPrint);

                        recyclerView.setAdapter(adapter);

                        //new BackupHandler(MilkBuyEntryActivity.this);
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

    void initDashboard(){
        findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MilkBuyEntryActivity.this, ProfileActivity.class));
            }
        });
        findViewById(R.id.dashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MilkBuyEntryActivity.this, DashboardActivity.class));
                finish();
            }
        });
        findViewById(R.id.view_all_entry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MilkBuyEntryActivity.this, ViewAllEntryActivity.class));
            }
        });
        findViewById(R.id.milk_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MilkBuyEntryActivity.this, MilkHistoryActivity.class));
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Logout(MilkBuyEntryActivity.this);
            }
        });
        findViewById(R.id.recover_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WarningDialog(MilkBuyEntryActivity.this).show();
            }
        });
        findViewById(R.id.backup_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MilkBuyEntryActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                // Send user's phone number for verification
                Date dateIntermediate = new Date();
                String date = new SimpleDateFormat("dd/MM/YYYY").format(dateIntermediate);
                Paper.book().write(Prevalent.last_update, date);
                new BackupHandler(MilkBuyEntryActivity.this);
            }
        });

        LinearLayout backup, recover, update_rate_charts, erase_milk_history;
        ImageView arrow = findViewById(R.id.arrow);
        final boolean[] arrowClicked = {false};
        backup = findViewById(R.id.backup_data);
        erase_milk_history = findViewById(R.id.erase_milk_history);
        update_rate_charts = findViewById(R.id.update_rate_charts);
        recover = findViewById(R.id.recover_data);
        update_rate_charts.setVisibility(View.GONE);
        erase_milk_history.setVisibility(View.GONE);
        backup.setVisibility(View.GONE);
        recover.setVisibility(View.GONE);
        findViewById(R.id.erase_milk_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MilkBuyEntryActivity.this, DeleteHistory.class));
            }
        });
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrowClicked[0]){
                    backup.setVisibility(View.GONE);
                    recover.setVisibility(View.GONE);
                    erase_milk_history.setVisibility(View.GONE);
                    update_rate_charts.setVisibility(View.GONE);
                    arrowClicked[0] = false;
                    arrow.setImageResource(R.drawable.ic_drop_down);
                }
                else{
                    arrow.setImageResource(R.drawable.drop_down);
                    backup.setVisibility(View.VISIBLE);
                    erase_milk_history.setVisibility(View.VISIBLE);
                    update_rate_charts.setVisibility(View.VISIBLE);
                    recover.setVisibility(View.VISIBLE);
                    arrowClicked[0] = true;
                }
            }
        });
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrowClicked[0]){
                    backup.setVisibility(View.GONE);
                    recover.setVisibility(View.GONE);
                    erase_milk_history.setVisibility(View.GONE);
                    update_rate_charts.setVisibility(View.GONE);
                    arrowClicked[0] = false;
                    arrow.setImageResource(R.drawable.ic_drop_down);
                }
                else{
                    arrow.setImageResource(R.drawable.drop_down);
                    backup.setVisibility(View.VISIBLE);
                    erase_milk_history.setVisibility(View.VISIBLE);
                    update_rate_charts.setVisibility(View.VISIBLE);
                    recover.setVisibility(View.VISIBLE);
                    arrowClicked[0] = true;
                }
            }
        });
        findViewById(R.id.upgrade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MilkBuyEntryActivity.this, UpgradeToPremium.class));
            }
        });
        findViewById(R.id.legal_policies).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
        String line = "--------------------------------";
        list = dbHelper.getDailyBuyData(date, shift);
        String toPrint = "\nDATE  |" + date + "\nSHIFT |" + shift + "\n"+line +  "\nID | " + "FAT/SNF |" + "RATE |" + "WEIGHT|" + "AMOUNT\n";
        if (id == R.id.printer) {
            Collections.reverse(list);
            for (DailyBuyObject object : list) {
                int sellerId = object.getId();
                String amount = StringUtils.rightPad(StringUtils.truncate(truncate(object.getAmount()), 6), 6, " ");
                String fat = truncate(object.getFat());
                String snf = truncate(object.getSnf());
                String rate = StringUtils.rightPad(truncate(object.getRate()), 5, " ");
                String weight = StringUtils.rightPad(truncate(object.getWeight()), 6, " ");
                Log.d(TAG, "amount:" +amount);
                toPrint += StringUtils.rightPad(""+sellerId, 3, "") + "|" + fat + "-" + snf + "|" + rate + "|"  + weight + "|" + amount + "\\n";
            }
            toPrint += line + "\n";
            toPrint += "TOTAL AMOUNT: " + truncate(amountTotal) + "Rs";
            toPrint += "\nTOTAL WEIGHT: " + weightTotal + "Ltr\n";
            toPrint += line + "\n";
            toPrint += "      DAIRYDAILY APP";
            Log.d(TAG, "toPrint: " + toPrint);
            byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());

            if (DashboardActivity.bluetoothAdapter != null) {
                if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                    try {
                        DashboardActivity.bluetoothConnectionService.write(mybyte);
                        list.clear();
                        adapter = new MilkBuyAdapter(MilkBuyEntryActivity.this, list);
                        recyclerView.setAdapter(adapter);
                        Log.d(TAG, "onOptionsSelected: Printing with DashboardActivity BT");
                    } catch (Exception e) {
                        Log.d(TAG, "onOptionsSelected: Unable to print");
                        toast(MilkBuyEntryActivity.this, "Unable to print");
                    }
                } else {
                    toast(MilkBuyEntryActivity.this, "Printer is not connected");
                }
            } else {
                toast(MilkBuyEntryActivity.this, "Bluetooth is off");
            }
        }
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
