package com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.juicebox.dairydaily.Models.BuyerRegisterModel;
import com.juicebox.dairydaily.MyAdapters.BuyerRegisterAdapter;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.SelectPrinterDialog;
import com.juicebox.dairydaily.Others.SpinnerItem;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.BluetoothConnectionService;
import com.juicebox.dairydaily.UI.Dashboard.BuyMilk.BuyMilkActivity;
import com.juicebox.dairydaily.UI.Dashboard.BuyMilk.MilkBuyEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.Dashboard.SellMilk.MilkSaleEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.SellMilk.SellMilkActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewReport.CustomerReportActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewReport.PaymentRegisterActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewReport.ShiftReportActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static com.juicebox.dairydaily.Others.UtilityMethods.getEndDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.getFirstname;
import static com.juicebox.dairydaily.Others.UtilityMethods.getStartDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;
import static com.juicebox.dairydaily.Others.UtilityMethods.useSnackBar;

public class BuyerRegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    ImageView start_date_image;
    ImageView end_date_image;
    TextView start_date_text_view, end_date_text_view;
    RecyclerView recyclerView;

    public static String phone_number;
    public static int count = 0;
    public static String totalWeight;
    public static String totalAmount;

    static ConstraintLayout scrollview;

    Button print;

    private static final String TAG = "BuyerRegisterActivity";

    ArrayList<BuyerRegisterModel> list;
    DbHelper dbHelper = new DbHelper(this);

    String startDate, endDate;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_register);

        getSupportActionBar().setTitle("Buyer Register");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        hideKeyboard(this);

        // Initialize widgets
        start_date_image = findViewById(R.id.start_date_image_view);
        scrollview = findViewById(R.id.constraintlayout);
        end_date_image = findViewById(R.id.end_date_image_view);
        start_date_text_view = findViewById(R.id.start_date_text_view);
        end_date_text_view = findViewById(R.id.end_date_text_view);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        print = findViewById(R.id.print);

        findViewById(R.id.send_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!phone_number.equals("") && count == 1){
                    String toSend = "TOTAL AMOUNT: " + truncate(Double.valueOf(totalAmount))+"Rs";
                    toSend += "\nTOTAL WEIGHT: " + truncate(Double.valueOf(totalWeight))+"Ltr";
                    toSend += "\nThank you for using DairyDaily!";

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.putExtra("address", phone_number);
                    i.putExtra("sms_body", toSend);
                    i.setType("vnd.android-dir/mms-sms");
                    startActivity(i);
                    phone_number = "";
                    count = 0;
                }
                else{
                    toast(BuyerRegisterActivity.this, "Can send to one numbe at once");
                }
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(list!=null){
                    String line = "--------------------------------";
                    Date dateIntermediate = new Date();
                    String date = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
                    String toPrint ="\n\n"+date+"\n"+ startDate + " To " + endDate + "\nID| "  + " NAME   |" + "WEIGHT|" + "AMOUNT|\n" +line + "\n";

                    double totalAmount = 0,totalWeight = 0;
                    for(BuyerRegisterModel object : list){
                        int id = object.getId();
                        String name = getFirstname(object.getName());
                        String amount = object.getAmount();
                        String weight = object.getWeight();
                        totalAmount += Double.valueOf(amount);
                        totalWeight += Double.valueOf(weight);
                        toPrint += id + " | "+name + " | " + weight + "|" + amount + "|\n";
                    }
                    toPrint += line + "\n";
                    toPrint += "TOTAL AMOUNT: "+ totalAmount + "Rs\n";
                    toPrint += "TOTAL WEIGHT: " + totalWeight + "Ltr\n";
                    toPrint += line + "\n";
                    toPrint += "       DAIRY DAILY APP";

                    Log.d(TAG, "toPrint: "+toPrint);
                    byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());
                    if(DashboardActivity.bluetoothAdapter != null) {
                        if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                            try {
                                DashboardActivity.bluetoothConnectionService.write(mybyte);
                                Log.d(TAG, "onOptionsSelected: Printing with DashboardActivity BT");
                            } catch (Exception e) {
                                Log.d(TAG, "onOptionsSelected: Unable to print");
                                toast(BuyerRegisterActivity.this, "Unable to print");
                            }
                        } else {
                            toast(BuyerRegisterActivity.this, "Printer is not connected");
                        }
                    }
                    else{
                        toast(BuyerRegisterActivity.this, "Bluetooth is off");
                    }
                }
            }
        });


        final DatePickerDialog startDatePickerDialog = new DatePickerDialog(this), endDatePickerDialog = new DatePickerDialog(this);
        startDate = getStartDate();
        endDate = getEndDate();
        Log.d(TAG, "StartDate, endDate: " + startDate + " " + endDate);

        start_date_text_view.setText(startDate);
        end_date_text_view.setText(endDate);

        start_date_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog.show();
            }
        });
        end_date_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDatePickerDialog.show();
            }
        });

        startDatePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
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
            }
        });

        endDatePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
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
            }
        });
        list = dbHelper.getBuyerRegister(startDate, endDate);
        BuyerRegisterAdapter adapter = new BuyerRegisterAdapter(BuyerRegisterActivity.this, list);
        recyclerView.setAdapter(adapter);

        // Hook up the "Go" button
        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startDate = start_date_text_view.getText().toString();
                String endDate = end_date_text_view.getText().toString();
                list = dbHelper.getBuyerRegister(startDate, endDate);
                BuyerRegisterAdapter adapter = new BuyerRegisterAdapter(BuyerRegisterActivity.this, list);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(BuyerRegisterActivity.this, ViewBuyerReportActivity.class));
        finish();
    }
}
