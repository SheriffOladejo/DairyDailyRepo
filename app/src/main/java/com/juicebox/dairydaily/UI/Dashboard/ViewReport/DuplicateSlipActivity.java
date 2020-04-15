package com.juicebox.dairydaily.UI.Dashboard.ViewReport;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.juicebox.dairydaily.Models.ShiftReportModel;
import com.juicebox.dairydaily.Others.BackupHandler;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.Logout;
import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.Others.WarningDialog;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.DeleteHistory;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.MilkHistoryActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ProfileActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.UpgradeToPremium;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ViewAllEntryActivity;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.paperdb.Paper;

import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;

@RequiresApi(api = Build.VERSION_CODES.N)
public class DuplicateSlipActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    ImageView select_date_image_view;
    TextView select_date_text_view;
    String shift;
    private static final String TAG = "DuplicateSlipActivity";
    private ArrayList<ShiftReportModel> list;
    double totalWeight, totalAmount, averageSnf, averageFat;
    DbHelper dbHelper = new DbHelper(this);

    int id;
    EditText idEditText;

    RadioButton morning_radio, evening_radio;
    String date;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_slip);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Duplicate Slip");

        select_date_image_view = findViewById(R.id.select_date_image);
        select_date_text_view = findViewById(R.id.select_date_text);
        morning_radio = findViewById(R.id.morning_radio);
        evening_radio = findViewById(R.id.evening_radio);
        idEditText = findViewById(R.id.id);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initDashboard();

        morning_radio.setChecked(true);
        shift = "Morning";

        morning_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(evening_radio.isChecked()){
                    evening_radio.setChecked(false);
                    shift = "Morning";
                }
            }
        });
        evening_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(morning_radio.isChecked()){
                    morning_radio.setChecked(false);
                    shift = "Evening";
                }
            }
        });

        select_date_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarDialog1();
            }
        });
        select_date_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarDialog1();
            }
        });

        Date newDate = new Date();
        try{
            DateFormat df = new DateFormat();
            date = df.format("yyyy-MM-dd", newDate).toString();
        }
        catch(Exception e){
            date = new SimpleDateFormat("YYYY-MM-dd").format(newDate);
        }
        select_date_text_view.setText(date);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        findViewById(R.id.print).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    id = Integer.valueOf(idEditText.getText().toString());
                    date = select_date_text_view.getText().toString();
                    list = dbHelper.getDuplicateSlip(id, date, shift);

                    for(ShiftReportModel model : list){
                        totalWeight += Double.valueOf(model.getWeight());
                        totalAmount += Double.valueOf(model.getAmount());
                        averageSnf += Double.valueOf(model.getSnf());
                        averageFat += Double.valueOf(model.getFat());
                    }
                    averageFat /= list.size();
                    averageSnf/=list.size();

                    if(list!=null){

                        Date dateIntermediate = new Date();
                        String line = "--------------------------------";

                        String date;
                        try{
                            DateFormat df = new DateFormat();
                            date = df.format("yyyy-MM-dd", dateIntermediate).toString();
                        }
                        catch(Exception e){
                            date = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
                        }
                        String toPrint ="Name| " + dbHelper.getBuyerName(id) + "\nDate | " + date + "\n" + "Shift | " + shift + "\n";
                        toPrint += "ID |Weight| FAT | Rate |Amount\n" + line + "\n";

                        for(ShiftReportModel object : list){
                            int id = object.getId();
                            String amount = truncate(Double.valueOf(object.getAmount()));
                            String fat = truncate(Double.valueOf(object.getFat()));
                            String rate = truncate(Double.valueOf(object.getRate()));
                            String weight = truncate(Double.valueOf(object.getWeight()));
                            toPrint += id + "  |" + weight + " |" + fat + " | " + rate + "|" + amount + " | \n";
                        }
                        toPrint += "TOTAL WEIGHT | " + totalWeight+"Ltr" + "\n";
                        toPrint += "TOTAL AMOUNT | " + truncate(totalAmount)+"Rs" + "\n";
                        Log.d(TAG, "toPrint: " + toPrint);

                        byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());
                        if(DashboardActivity.bluetoothAdapter != null) {
                            if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                                try {
                                    DashboardActivity.bluetoothConnectionService.write(mybyte);
                                    Log.d(TAG, "onOptionsSelected: Printing with DashboardActivity BT");
                                } catch (Exception e) {
                                    Log.d(TAG, "onOptionsSelected: Unable to print");
                                    toast(DuplicateSlipActivity.this, "Unable to print");
                                }
                            } else {
                                toast(DuplicateSlipActivity.this, "Printer is not connected");
                            }
                        }
                        else{
                            toast(DuplicateSlipActivity.this, "Bluetooth is off");
                        }
                    }
                }
                catch(Exception e){}
            }
        });
    }

    private void showCalendarDialog1() {
        Dialog dialog = new Dialog(DuplicateSlipActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_view_dialog);
        CalendarView cal = dialog.findViewById(R.id.calendar_view);
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                if(String.valueOf(month).length() == 1){
                    if(String.valueOf(dayOfMonth).length() == 1){
                        date = year + "-0" + (month+1) + "-" + "0"+dayOfMonth;
                        select_date_text_view.setText(date);
                    }
                    else{
                        date = year + "-0" + (month+1) + "-" + dayOfMonth;
                        select_date_text_view.setText(date);
                    }
                }
                else{
                    date = year + "-" + (month+1) + "-" + dayOfMonth;
                    select_date_text_view.setText(date);
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
                startActivity(new Intent(DuplicateSlipActivity.this, ProfileActivity.class));
            }
        });
        findViewById(R.id.dashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DuplicateSlipActivity.this, DashboardActivity.class));
                finish();
            }
        });
        findViewById(R.id.view_all_entry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DuplicateSlipActivity.this, ViewAllEntryActivity.class));
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Logout(DuplicateSlipActivity.this);
            }
        });
        findViewById(R.id.recover_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WarningDialog(DuplicateSlipActivity.this).show();
            }
        });
        findViewById(R.id.milk_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DuplicateSlipActivity.this, MilkHistoryActivity.class));
            }
        });
        findViewById(R.id.backup_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(DuplicateSlipActivity.this,
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
                new BackupHandler(DuplicateSlipActivity.this);
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
                startActivity(new Intent(DuplicateSlipActivity.this, DeleteHistory.class));
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
                startActivity(new Intent(DuplicateSlipActivity.this, UpgradeToPremium.class));
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
        //startActivity(new Intent(DuplicateSlipActivity.this, ViewReportActivity.class));
        finish();
    }

}
