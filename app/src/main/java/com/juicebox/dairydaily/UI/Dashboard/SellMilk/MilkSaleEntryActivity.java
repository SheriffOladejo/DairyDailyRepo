package com.juicebox.dairydaily.UI.Dashboard.SellMilk;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.juicebox.dairydaily.Models.DailySalesObject;
import com.juicebox.dairydaily.MyAdapters.MilkSaleAdapter;
import com.juicebox.dairydaily.Others.BackupHandler;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.Logout;
import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.Others.WarningDialog;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.BuyMilk.MilkBuyEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.DeleteHistory;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.MilkHistoryActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ProfileActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.UpgradeToPremium;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ViewAllEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewReport.PaymentRegisterActivity;
import com.juicebox.dairydaily.UI.UsersListActivity;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import io.paperdb.Paper;

import static com.juicebox.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;

public class MilkSaleEntryActivity extends AppCompatActivity {

    private static final String TAG = "MilkSaleEntryActivity";

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    MilkSaleAdapter adapter;

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

        initDashboard();

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
                    if(dbHelper.addReceiveCash(Id, date, truncate(0.00), Amount, "Sale")){
                        String toPrint ="\n\n\n"+ date + "\n" + "ID      : " + Id + " " + name + "\n" +
                                "SHIFT   :   " + shift  +
                                "\nWEIGHT  :   " + Weight + "Ltr\nRATE    :   " + rate + "Rs/Ltr\n" + "TOTAL RS:   " + Amount + "Rs\n      DAIRYDAILY APP\n\n\n";
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
                                    Dialog dialog = new Dialog(MilkSaleEntryActivity.this);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.dialog_want_to_print);
                                    Button yes = dialog.findViewById(R.id.yes);
                                    Button no = dialog.findViewById(R.id.no);

                                    yes.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DashboardActivity.bluetoothConnectionService.write(mybyte);
                                        }
                                    });
                                    no.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
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
        Log.d(TAG, "online: " + SellMilkActivity.online);
        if(SellMilkActivity.online){
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

    void initDashboard(){
        findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MilkSaleEntryActivity.this, ProfileActivity.class));
            }
        });
        findViewById(R.id.dashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MilkSaleEntryActivity.this, DashboardActivity.class));
                finish();
            }
        });
        findViewById(R.id.view_all_entry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MilkSaleEntryActivity.this, ViewAllEntryActivity.class));
            }
        });
        findViewById(R.id.milk_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MilkSaleEntryActivity.this, MilkHistoryActivity.class));
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Logout(MilkSaleEntryActivity.this);
            }
        });
        findViewById(R.id.recover_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WarningDialog(MilkSaleEntryActivity.this).show();
            }
        });
        findViewById(R.id.backup_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MilkSaleEntryActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                // Send user's phone number for verification
                Date dateIntermediate = new Date();
                String date = new SimpleDateFormat("dd/MM/YYYY").format(dateIntermediate);
                Paper.book().write(Prevalent.last_update, date);
                new BackupHandler(MilkSaleEntryActivity.this);
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
                startActivity(new Intent(MilkSaleEntryActivity.this, DeleteHistory.class));
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
                startActivity(new Intent(MilkSaleEntryActivity.this, UpgradeToPremium.class));
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
        String line = "--------------------------------";
        String toPrint = "\n\n\nDATE  : " + date + "\nSHIFT : " + shift + "\nRate  :" + " " + rate_view.getText().toString() +"Rs/Ltr\n"+line + "\nID| " + " FAT/SNF  |" + "WEIGHT| " + "AMOUNT\n";
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
