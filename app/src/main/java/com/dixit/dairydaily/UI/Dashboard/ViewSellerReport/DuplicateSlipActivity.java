package com.dixit.dairydaily.UI.Dashboard.ViewSellerReport;

import android.Manifest;
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

import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.InitDrawerBoard;
import com.google.android.material.navigation.NavigationView;
import com.dixit.dairydaily.Models.ShiftReportModel;
import com.dixit.dairydaily.Others.BackupHandler;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.Others.Logout;
import com.dixit.dairydaily.Others.Prevalent;
import com.dixit.dairydaily.Others.WarningDialog;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.DeleteHistory;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.MilkHistoryActivity;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.ProfileActivity;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.UpgradeToPremium;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.ViewAllEntryActivity;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.paperdb.Paper;

import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.Others.UtilityMethods.truncate;

@RequiresApi(api = Build.VERSION_CODES.N)
public class DuplicateSlipActivity extends InitDrawerBoard {

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
        initDrawer();

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
