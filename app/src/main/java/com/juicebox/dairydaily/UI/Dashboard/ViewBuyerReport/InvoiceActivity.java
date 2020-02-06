package com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import com.juicebox.dairydaily.Models.BuyerRegisterModel;
import com.juicebox.dairydaily.MyAdapters.BuyerRegisterAdapter;
import com.juicebox.dairydaily.MyAdapters.InvoiceAdapter;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static com.juicebox.dairydaily.Others.UtilityMethods.getEndDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.getFirstname;
import static com.juicebox.dairydaily.Others.UtilityMethods.getStartDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.toast;

public class InvoiceActivity extends AppCompatActivity {

    ImageView start_date_image;
    ImageView end_date_image;
    TextView start_date_text_view, end_date_text_view;
    ConstraintLayout scrollview;
    private static final String TAG = "InvoiceActivity";
    RecyclerView recyclerView;
    Button print;
    ArrayList<BuyerRegisterModel> list;
    DbHelper dbHelper = new DbHelper(this);

    String startDate, endDate;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        getSupportActionBar().setTitle("Invoice");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        // Initialize widgets
        start_date_image = findViewById(R.id.start_date_image_view);
        scrollview = findViewById(R.id.constraintlayout);
        end_date_image = findViewById(R.id.end_date_image_view);
        start_date_text_view = findViewById(R.id.start_date_text_view);
        end_date_text_view = findViewById(R.id.end_date_text_view);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        print = findViewById(R.id.print);

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(list!=null){
                    //Collections.reverse(list);
                    String line = "------------------------------";
                    Date dateIntermediate = new Date();
                    String date = new SimpleDateFormat("dd/MM/YYYY").format(dateIntermediate);
                    String toPrint ="\n"+date + "\n"+ startDate + " To " + endDate + "\nID| "  + " NAME   |" + "WEIGHT|" + "AMOUNT|\n" +line + "\n";

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
                                toast(InvoiceActivity.this, "Unable to print");
                            }
                        } else {
                            toast(InvoiceActivity.this, "Printer is not connected");
                        }
                    }
                    else{
                        toast(InvoiceActivity.this, "Bluetooth is off");
                    }
                }
            }
        });


        final DatePickerDialog startDatePickerDialog = new DatePickerDialog(this), endDatePickerDialog = new DatePickerDialog(this);
        startDate = getStartDate();
        endDate = getEndDate();

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
                        endDate = year+ "-0" + (month+1) + "-" + "0"+dayOfMonth;
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
        list = dbHelper.getInvoice(startDate, endDate);
        InvoiceAdapter adapter = new InvoiceAdapter(InvoiceActivity.this, list);
        recyclerView.setAdapter(adapter);

        // Hook up the "Go" button
        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startDate = start_date_text_view.getText().toString();
                String endDate = end_date_text_view.getText().toString();
                list = dbHelper.getInvoice(startDate, endDate);
                InvoiceAdapter adapter = new InvoiceAdapter(InvoiceActivity.this, list);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(InvoiceActivity.this, ViewBuyerReportActivity.class));
        finish();
    }
}
