package com.juicebox.dairydaily.UI.Dashboard.SellMilk;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.juicebox.dairydaily.Others.BackupHandler;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.Logout;
import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.Others.WarningDialog;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.BuyMilk.BuyMilkActivity;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.DeleteHistory;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.MilkHistoryActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ProfileActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.UpgradeToPremium;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ViewAllEntryActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.paperdb.Paper;

import static com.juicebox.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.juicebox.dairydaily.Others.UtilityMethods.useSnackBar;

public class SellMilkActivity extends AppCompatActivity {

    private Button proceed;
    static ScrollView scrollview;

    public static boolean online = false;

    private EditText rate;
    private Button update;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    String date;

    private static final String TAG = "SellMilkActivity";

    // Date picker dialog pops up on calendar image click
    TextView dateView;
    Switch online_switch;

    private String am_pm;

    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    // The layout that hides the rate selection
    LinearLayout rateByFatView;
    boolean fixed_price = false;

    RadioGroup radioGroup;
    RadioButton fixed_price_button;
    RadioButton rateByFat_button;
    private Switch morning_switch;
    private Switch evening_switch;
    LinearLayout date_layout;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_milk);

        getSupportActionBar().setTitle("Sell Milk");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        final DbHelper dbHelper = new DbHelper(SellMilkActivity.this);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        date_layout = findViewById(R.id.date_layout);

        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initDashboard();

        online_switch = findViewById(R.id.online_switch);
        //print_switch = findViewById(R.id.print_switch);
        morning_switch = findViewById(R.id.morning_switch);
        evening_switch = findViewById(R.id.evening_switch);
        scrollview = findViewById(R.id.scrollview);
        proceed = findViewById(R.id.proceed);
        fixed_price_button = findViewById(R.id.fixed_price);
        radioGroup = findViewById(R.id.radio_group);
        rateByFat_button = findViewById(R.id.rate_by_fat);
        rateByFatView = findViewById(R.id.rate_by_fat_view);
        ImageView calendar = findViewById(R.id.calendar_image);
        dateView = findViewById(R.id.date_textview);
        rate = findViewById(R.id.rate);
        update = findViewById(R.id.update);

        date_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarDialog();
            }
        });

        double rateDouble = dbHelper.getRate();
        rate.setText(String.valueOf(rateDouble));
        checkInternetConnect();

        online_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(online_switch.isChecked()){
                    online = true;
                    Log.d(TAG, "online milk sell: " + online);
                }
                else if(!online_switch.isChecked()){
                    online = false;
                    Log.d(TAG, "online milk sell: " + online);
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rateString = rate.getText().toString();
                double rateDouble = Double.valueOf(rateString);
                dbHelper.updateRate(rateDouble);
                useSnackBar("Rate updated", scrollview);
            }
        });


        Date dateIntermediate = new Date();
        try{
            DateFormat df = new DateFormat();
            date = df.format("yyyy-MM-dd", dateIntermediate).toString();
        }
        catch(Exception e){
            date = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
        }
        dateView.setText(date);

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

        hideKeyboard(this);

        fixed_price_button.setChecked(true);

        // Initially set the rateByFatView to be gone
        rateByFatView.setVisibility(View.GONE);

        morning_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(evening_switch.isChecked()){
                    evening_switch.setChecked(false);
                    am_pm = "Morning";
                }
            }
        });

        evening_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(morning_switch.isChecked()){
                    morning_switch.setChecked(false);
                    am_pm = "Evening";
                }

            }
        });
//        calendar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                datePickerDialog.show();
//            }
//        });

        rateByFat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateByFatView.setVisibility(View.VISIBLE);
                fixed_price = false;
            }
        });

        fixed_price_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateByFatView.setVisibility(View.GONE);
                fixed_price = true;
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!morning_switch.isChecked() && !evening_switch.isChecked()){
                    useSnackBar("Select Shift", scrollview);
                }
                else{
                    Intent intent = new Intent(SellMilkActivity.this, MilkSaleEntryActivity.class);
                    intent.putExtra("Shift", morning_switch.isChecked() ? "Morning" : "Evening");
                    intent.putExtra("Date", date);
                    startActivity(intent);
                }
            }
        });
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

    private void showCalendarDialog() {
        Dialog dialog = new Dialog(SellMilkActivity.this);
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

    void initDashboard(){
        findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellMilkActivity.this, ProfileActivity.class));
            }
        });
        findViewById(R.id.dashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellMilkActivity.this, DashboardActivity.class));
                finish();
            }
        });
        findViewById(R.id.milk_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellMilkActivity.this, MilkHistoryActivity.class));
            }
        });
        findViewById(R.id.view_all_entry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellMilkActivity.this, ViewAllEntryActivity.class));
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Logout(SellMilkActivity.this);
            }
        });
        findViewById(R.id.recover_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WarningDialog(SellMilkActivity.this).show();
            }
        });
        findViewById(R.id.backup_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(SellMilkActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                // Send user's phone number for verification
                Date dateIntermediate = new Date();
                String date = new SimpleDateFormat("dd/MM/YYYY").format(dateIntermediate);
                Paper.book().write(Prevalent.last_update, date);
                new BackupHandler(SellMilkActivity.this);
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
                startActivity(new Intent(SellMilkActivity.this, DeleteHistory.class));
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
                startActivity(new Intent(SellMilkActivity.this, UpgradeToPremium.class));
            }
        });
        findViewById(R.id.legal_policies).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
}
