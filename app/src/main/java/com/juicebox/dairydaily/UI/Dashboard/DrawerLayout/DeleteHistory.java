package com.juicebox.dairydaily.UI.Dashboard.DrawerLayout;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.juicebox.dairydaily.Models.DailyBuyObject;
import com.juicebox.dairydaily.Models.DailySalesObject;
import com.juicebox.dairydaily.MyAdapters.MilkBuyAdapter;
import com.juicebox.dairydaily.MyAdapters.MilkSaleAdapter;
import com.juicebox.dairydaily.Others.BackupHandler;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.Logout;
import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.Others.WarningDialog;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport.BuyerRegisterActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.paperdb.Paper;

import static com.juicebox.dairydaily.Others.UtilityMethods.getEndDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.getStartDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;

public class DeleteHistory extends AppCompatActivity {

    ArrayList<DailyBuyObject> buyObjects;
    ArrayList<DailySalesObject> salesObjects;

    TextView start_date_text_view, end_date_text_view;
    String startDate, endDate;
    ConstraintLayout scrollview;
    private DbHelper dbHelper;
    RecyclerView sales, buys;
    RelativeLayout cal1, cal2;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_milk_history);

        dbHelper = new DbHelper(this);
        getSupportActionBar().setTitle("Delete Milk History");

        // Initialize widgets
        initDashboard();
        scrollview = findViewById(R.id.constraintlayout);
        navigationView = findViewById(R.id.nav_view);
        start_date_text_view = findViewById(R.id.start_date_text_view);
        end_date_text_view = findViewById(R.id.end_date_text_view);
        sales = findViewById(R.id.recyclerview);
        buys = findViewById(R.id.recyclerview2);
        sales.setLayoutManager(new LinearLayoutManager(this));
        buys.setLayoutManager(new LinearLayoutManager(this));

        drawerLayout = findViewById(R.id.drawerlayout);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startDate = getStartDate();
        endDate = getEndDate();

        start_date_text_view.setText(startDate);
        end_date_text_view.setText(endDate);
        cal1 = findViewById(R.id.cal1);
        cal2 =findViewById(R.id.cal2);

        cal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarDialog1();
            }
        });
        cal2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarDialog2();
            }
        });

        // Hook up the "Go" button
        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startDate = start_date_text_view.getText().toString();
                String endDate = end_date_text_view.getText().toString();
                buyObjects = dbHelper.getMilkBuyRange(startDate, endDate);
                salesObjects = dbHelper.getMilkSaleRange(startDate, endDate);
                TextView totalAmount, totalWeight, fatAverage;
                totalAmount = findViewById(R.id.amountTotal);
                totalWeight = findViewById(R.id.weightTotal);
                fatAverage = findViewById(R.id.fatAverage);
                double weightTotal = 0, amountTotal = 0, averageFat = 0;

                for(DailySalesObject model : salesObjects){
                    weightTotal += Double.valueOf(model.getWeight());
                    amountTotal += Double.valueOf(model.getAmount());
                    averageFat += Double.valueOf(model.getRate());
                }
                totalAmount.setText(String.valueOf(truncate(amountTotal)) + "Rs");
                totalWeight.setText(String.valueOf(truncate(weightTotal)) + "Ltr");
                fatAverage.setText(String.valueOf(truncate(averageFat/salesObjects.size())) + "%");

                TextView totalAmountBuy, totalWeightBuy, fatAverageBuy;
                double weightTotalBuy = 0, amountTotalBuy = 0, averageFatBuy = 0;
                totalAmountBuy = findViewById(R.id.amountTotalBuy);
                totalWeightBuy = findViewById(R.id.weightTotalBuy);
                fatAverageBuy = findViewById(R.id.fatAverageBuy);

                for(DailyBuyObject model : buyObjects){
                    weightTotalBuy += Double.valueOf(model.getWeight());
                    amountTotalBuy += Double.valueOf(model.getAmount());
                    averageFatBuy += Double.valueOf(model.getFat());
                }
                totalAmountBuy.setText(String.valueOf(truncate(amountTotalBuy)) + "Rs");
                totalWeightBuy.setText(String.valueOf(truncate(weightTotalBuy)) + "Ltr");
                fatAverageBuy.setText(String.valueOf(truncate(averageFatBuy/buyObjects.size())) + "%");
                MilkSaleAdapter adapter = new MilkSaleAdapter( salesObjects, DeleteHistory.this);
                MilkBuyAdapter adapter1 = new MilkBuyAdapter(DeleteHistory.this, buyObjects);
                sales.setAdapter(adapter);
                buys.setAdapter(adapter1);
            }
        });
    }

    private void showCalendarDialog1() {
        Dialog dialog = new Dialog(DeleteHistory.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_view_dialog);
        CalendarView cal = dialog.findViewById(R.id.calendar_view);
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                if(String.valueOf(month).length() == 1){
                    if(String.valueOf(dayOfMonth).length() == 1){
                        startDate = year + "-0" + (month+1) + "-" + "0"+dayOfMonth;
                        start_date_text_view.setText(startDate);
                    }
                    else{
                        startDate = year + "-0" + (month+1) + "-" + dayOfMonth;
                        start_date_text_view.setText(startDate);
                    }
                }
                else{
                    startDate = year + "-" + (month+1) + "-" + dayOfMonth;
                    start_date_text_view.setText(startDate);

                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showCalendarDialog2() {
        Dialog dialog = new Dialog(DeleteHistory.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_view_dialog);
        CalendarView cal = dialog.findViewById(R.id.calendar_view);
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                if(String.valueOf(month).length() == 1){
                    if(String.valueOf(dayOfMonth).length() == 1){
                        endDate = year + "-0" + (month+1) + "-" + "0"+dayOfMonth;
                        end_date_text_view.setText(endDate);
                    }
                    else{
                        endDate = year + "-0" + (month+1) + "-" + dayOfMonth;
                        end_date_text_view.setText(endDate);
                    }
                }
                else{
                    endDate = year + "-" + (month+1) + "-" + dayOfMonth;
                    end_date_text_view.setText(endDate);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void initDashboard(){
        findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeleteHistory.this, ProfileActivity.class));
            }
        });
        findViewById(R.id.view_all_entry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeleteHistory.this, ViewAllEntryActivity.class));
            }
        });
        findViewById(R.id.milk_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeleteHistory.this, MilkHistoryActivity.class));
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Logout(DeleteHistory.this);
            }
        });
        findViewById(R.id.recover_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WarningDialog(DeleteHistory.this).show();
            }
        });
        findViewById(R.id.backup_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(DeleteHistory.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                // Send user's phone number for verification
                Date dateIntermediate = new Date();
                String date;
                try{
                    DateFormat df = new DateFormat();
                    date = df.format("yyyy-MM-dd", dateIntermediate).toString();
                }
                catch(Exception e){
                    date = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
                }

                Paper.book().write(Prevalent.last_update, date);
                new BackupHandler(DeleteHistory.this);
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
        findViewById(R.id.dashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeleteHistory.this, DashboardActivity.class));
                finish();
            }
        });
        findViewById(R.id.erase_milk_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                startActivity(new Intent(DeleteHistory.this, UpgradeToPremium.class));
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
        getMenuInflater().inflate(R.menu.delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;
        if(item.getItemId() == R.id.delete){
            dbHelper.deleteHistory(startDate, endDate);
            dbHelper.deleteReceiveCash(startDate, endDate);
            new BackupHandler(DeleteHistory.this);

            buyObjects = dbHelper.getMilkBuyRange(startDate, endDate);
            salesObjects = dbHelper.getMilkSaleRange(startDate, endDate);
            MilkSaleAdapter adapter = new MilkSaleAdapter( salesObjects, DeleteHistory.this);
            MilkBuyAdapter adapter1 = new MilkBuyAdapter(DeleteHistory.this, buyObjects);
            sales.setAdapter(adapter);
            buys.setAdapter(adapter1);
            return true;
        }
        else{
            return false;
        }
    }
}
