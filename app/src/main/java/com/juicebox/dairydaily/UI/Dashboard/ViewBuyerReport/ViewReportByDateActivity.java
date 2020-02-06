package com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.juicebox.dairydaily.Models.DailySalesObject;
import com.juicebox.dairydaily.Models.ReportByDateModels;
import com.juicebox.dairydaily.MyAdapters.BuyerRegisterAdapter;
import com.juicebox.dairydaily.MyAdapters.ReportByDateAdapter;
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
import com.juicebox.dairydaily.UI.UsersListActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static com.juicebox.dairydaily.Others.UtilityMethods.getDateRange;
import static com.juicebox.dairydaily.Others.UtilityMethods.getEndDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.getMonth;
import static com.juicebox.dairydaily.Others.UtilityMethods.getStartDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;
import static com.juicebox.dairydaily.Others.UtilityMethods.useSnackBar;

public class ViewReportByDateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "ViewReportByDateActivit";
    private final int REQUEST_READ_PHONE_STATE = 1;

    ImageView start_date_image;
    ImageView end_date_image;
    TextView start_date_text_view, end_date_text_view, amount, weight;
    RadioButton start_morning_radio, start_evening_radio, end_morning_radio, end_evening_radio;
    String start_date = "", end_date = "";
    Button go;
    double weightTotal;
    double amountTotal;
    double averageFat;
    EditText id;
    TextView buyers;
    RecyclerView recyclerView;
    ArrayList<ReportByDateModels> list;

    Button print, send_msg;

    static ConstraintLayout scrollview;

    DbHelper dbHelper;
    int idInt = 0;

    String startDate, endDate, startShift, endShift;

    DatePickerDialog startDatePickerDialog;
    DatePickerDialog endDatePickerDialog;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_report_by_date);

        dbHelper = new DbHelper(this);

        getSupportActionBar().setTitle("Report By Date");
        print = findViewById(R.id.print);
        send_msg = findViewById(R.id.send_msg);
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        hideKeyboard(this);

        start_date_image = findViewById(R.id.start_date_image_view);
        end_date_image = findViewById(R.id.end_date_image_view);
        scrollview = findViewById(R.id.constraintlayout);
        start_date_text_view = findViewById(R.id.start_date_text_view);
        end_date_text_view = findViewById(R.id.end_date_text_view);
        start_morning_radio = findViewById(R.id.start_morning_radio);
        start_evening_radio= findViewById(R.id.start_evening_radio);
        end_morning_radio = findViewById(R.id.end_morning_radio);
        end_evening_radio = findViewById(R.id.end_evening_radio);
        amount = findViewById(R.id.amountTotal);
        weight = findViewById(R.id.weightTotal);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        go = findViewById(R.id.go);
        id = findViewById(R.id.id);
        buyers = findViewById(R.id.buyers);

        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone_number = dbHelper.getBuyerPhone_Number(idInt);
                String toSend = "TOTAL AMOUNT: " + truncate(amountTotal)+"Rs";
                toSend += "\nTOTAL WEIGHT: " + truncate(weightTotal)+"Ltr";
                toSend += "\nAVERAGE FAT: " + truncate(averageFat)+"%";
                toSend += "\n   DAIRYDAILY APP";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.putExtra("address", phone_number);
                i.putExtra("sms_body", toSend);
                i.setType("vnd.android-dir/mms-sms");
                startActivity(i);
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

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(list!=null){

                    Date dateIntermediate = new Date();
                    String date = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
                    String line = "--------------------------------";
                    String toPrint = "\n\nID|" + idInt +"  " + buyers.getText().toString() + "\n";
                    toPrint += "DATE | " + date + "\n" + startDate + " To " + endDate + "\n Date |" + "Rate  |" + "WEIGHT|" + "AMOUNT\n" +line + "\n";

                    for(ReportByDateModels object : list){
                        String dateString = object.getDate();
                        String rate = truncate(Double.valueOf(object.getRate()));
                        String amount = truncate(Double.valueOf(object.getAmount()));
                        String shift = object.getShift();
                        shift = shift.equals("Morning") ? "M" : "E";
                        String weight =truncate(Double.valueOf(object.getWeight()));
                        amountTotal += Double.valueOf(object.getAmount());
                        weightTotal += Double.valueOf(object.getWeight());
                        averageFat +=Double.valueOf(object.getRate());
                        toPrint += dateString.substring(8,10) +" - " + shift + "|" +" " + rate + "|" + ""+ weight + " |" +""+ amount + "\n";
                    }
                    averageFat /= list.size();
                    toPrint += line + "\n";
                    toPrint += "TOTAL AMOUNT: " + truncate(amountTotal) + "Rs";
                    toPrint += "\nTOTAL WEIGHT: " + truncate(weightTotal) + "Ltr";
                    toPrint += "\n" + line;
                    toPrint += "\n    DAIRYDAILY APP\n\n\n";
                    Log.d(TAG, "toPrint: " + toPrint);

                    byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());
                    if(DashboardActivity.bluetoothAdapter != null) {
                        if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                            try {
                                DashboardActivity.bluetoothConnectionService.write(mybyte);
                                Log.d(TAG, "onOptionsSelected: Printing with DashboardActivity BT");
                            } catch (Exception e) {
                                Log.d(TAG, "onOptionsSelected: Unable to print");
                                toast(ViewReportByDateActivity.this, "Unable to print");
                            }
                        } else {
                            toast(ViewReportByDateActivity.this, "Printer is not connected");
                        }
                    }
                    else{
                        toast(ViewReportByDateActivity.this, "Bluetooth is off");
                    }
                }
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


        idInt = getIntent().getIntExtra("id", 0);
        String name = getIntent().getStringExtra("name");
        try{
            if(!name.equals("")){
                buyers.setText(name);
            }
        }
        catch(Exception e){}
        if(idInt != 0){
            id.setText(String.valueOf(idInt));
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
                            buyers.setText("Not Found");
                        }
                        else{
                            buyers.setText(name);
                        }
                    }
                    catch(Exception e){
                        buyers.setText("Not Found");
                    }
                }
                else{
                    buyers.setText("All Buyers");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        start_morning_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShift = "Morning";
                start_evening_radio.setChecked(false);
            }
        });

        end_morning_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endShift = "Evening";
                end_evening_radio.setChecked(false);
            }
        });

        start_evening_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShift="Evening";
                start_morning_radio.setChecked(false);
            }
        });

        end_evening_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endShift = "Evening";
                end_morning_radio.setChecked(false);
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountTotal = 0;
                weightTotal = 0;
                startDate = start_date_text_view.getText().toString();
                endDate = end_date_text_view.getText().toString();
                Log.d(TAG, "Start date: " + startDate);
                Log.d(TAG, "End date: " + endDate);
                if(start_morning_radio.isChecked()){
                    startShift = "Morning";
                    start_evening_radio.setChecked(false);
                }
                else if(start_evening_radio.isChecked()){
                    startShift = "Evening";
                    start_morning_radio.setChecked(false);
                }

                if(end_morning_radio.isChecked()){
                    end_evening_radio.setChecked(false);
                    endShift = "Morning";
                }
                else if(end_evening_radio.isChecked()){
                    endShift = "Evening";
                    end_morning_radio.setChecked(false);
                }
                try{
                    idInt = Integer.valueOf(id.getText().toString());
                }
                catch(Exception e){

                }

                try{
                    if(startDate.isEmpty() || endDate.isEmpty() || startShift.isEmpty() || endShift.isEmpty()){
                        Toast.makeText(ViewReportByDateActivity.this, "Operation failed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Operation failed");
                    }
                    else{
                        list = dbHelper.getReportByDate(idInt, startDate, startShift, endDate, endShift);
                        Log.d(TAG, "goClick:pp " + list.size());
                        ReportByDateAdapter adapter = new ReportByDateAdapter(ViewReportByDateActivity.this, list);

                        for(ReportByDateModels model : list){
                            weightTotal += Double.valueOf(model.getWeight());
                            amountTotal += Double.valueOf(model.getAmount());
                        }
                        Log.d(TAG, "goClick: " + weightTotal);
                        amount.setText(String.valueOf(truncate(amountTotal)) + "Rs");
                        weight.setText(String.valueOf(weightTotal) + "Ltr");

                        recyclerView.setAdapter(adapter);
                    }
                }
                catch(Exception e){

                }

            }
        });

        buyers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewReportByDateActivity.this, UsersListActivity.class);
                intent.putExtra("From", "ViewReport");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_READ_PHONE_STATE:
                if((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){

                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ViewReportByDateActivity.this, ViewBuyerReportActivity.class));
        finish();
    }
}
