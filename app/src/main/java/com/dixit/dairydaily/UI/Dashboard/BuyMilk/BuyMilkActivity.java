package com.dixit.dairydaily.UI.Dashboard.BuyMilk;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;

import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.InitDrawerBoard;
import com.google.android.material.navigation.NavigationView;
import com.dixit.dairydaily.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.paperdb.Paper;

import static com.dixit.dairydaily.Others.UtilityMethods.useSnackBar;

//Activity for buying milk

public class BuyMilkActivity extends InitDrawerBoard implements DatePickerDialog.OnDateSetListener {

    private Button proceed;

    static ScrollView scrollview;
    public static boolean online = false;

    private static final String TAG = "BuyMilkActivity";

    // Date picker dialog pops up on calendar image click
    DatePickerDialog datePickerDialog;
    TextView dateView;
    Switch online_switch, print_switch, morning_switch, evening_switch;

    private String date;
    private String am_pm;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    LinearLayout date_layout;

    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_milk_activity);

        dateView = findViewById(R.id.date_textview);
        initDrawer();
        online_switch = findViewById(R.id.online_switch);
        //print_switch = findViewById(R.id.print_switch);
        morning_switch = findViewById(R.id.morning_switch);
        evening_switch = findViewById(R.id.evening_switch);
        scrollview = findViewById(R.id.scrollview);
        proceed = findViewById(R.id.proceed);
        date_layout = findViewById(R.id.date_layout);

        date_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //datePickerDialog.show();
                showCalendarDialog();
            }
        });
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        morning_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(evening_switch.isChecked())
                    evening_switch.setChecked(false);
            }
        });

        evening_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(morning_switch.isChecked())
                    morning_switch.setChecked(false);
            }
        });

        online_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(online_switch.isChecked()){
                    online = true;
                }
                else if(!online_switch.isChecked()){
                    online = false;
                                    }
            }
        });

        Paper.init(this);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Buy Milk");

        Date dateIntermediate = new Date();
        try{
            DateFormat df = new DateFormat();
            date = df.format("yyyy-MM-dd", dateIntermediate).toString();
        }
        catch(Exception e){
            date = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
        }
        dateView.setText(date);

        // Check time whether its morning or evening and check shift switches accordingly
        am_pm = "";
        Calendar dateTime = Calendar.getInstance();
        if(dateTime.get(Calendar.AM_PM) == Calendar.AM){
            am_pm = "Morning";
        }
        else if(dateTime.get(Calendar.AM_PM) == Calendar.PM){
            am_pm = "Evening";
        }
        if(am_pm.equals("Morning")){
            morning_switch.setChecked(true);
        }
        if(am_pm.equals("Evening")){
            evening_switch.setChecked(true);
        }

        checkInternetConnect();

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!morning_switch.isChecked() && !evening_switch.isChecked()){
                    useSnackBar("Select Shift", scrollview);
                }
                else{
                    Intent intent = new Intent(BuyMilkActivity.this, MilkBuyEntryActivity.class);
                    intent.putExtra("Shift", morning_switch.isChecked() ? "Morning" : "Evening");
                    intent.putExtra("Date", date);
                    startActivity(intent);
                }
            }
        });

    }

    private void showCalendarDialog() {
        Dialog dialog = new Dialog(BuyMilkActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_view_dialog);
        CalendarView cal = dialog.findViewById(R.id.calendar_view);
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                if(String.valueOf(month).length() == 1){
                    if(String.valueOf(dayOfMonth).length() == 1){
                        date = year + "-0" + (month+1) + "-" + "0"+dayOfMonth;
                        dateView.setText(date);
                    }
                    else{
                        date = year + "-0" + (month+1) + "-" + dayOfMonth;
                        dateView.setText(date);
                    }
                }
                else{
                    date = year + "-" + (month+1) + "-" + dayOfMonth;
                    dateView.setText(date);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void checkInternetConnect(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if(manager != null){
            boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
            if(is3g){
                online_switch.setChecked(true);
                online = true;
            }
            boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
            if(isWifi){
                online_switch.setChecked(true);
                online = true;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if(String.valueOf(month).length() == 1){
            if(String.valueOf(dayOfMonth).length() == 1){
                date = year + "-0" + (month+1) + "-" + "0"+dayOfMonth;
                dateView.setText(date);
            }
            else{
                date = year + "-0" + (month+1) + "-" + dayOfMonth;
                dateView.setText(date);
            }
        }
        else{
            date = year + "-" + (month+1) + "-" + dayOfMonth;
            dateView.setText(date);
        }
    }
}
