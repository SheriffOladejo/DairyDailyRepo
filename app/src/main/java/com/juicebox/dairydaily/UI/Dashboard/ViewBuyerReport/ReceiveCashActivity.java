package com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.juicebox.dairydaily.Models.BuyerRegisterModel;
import com.juicebox.dairydaily.Models.DailyBuyObject;
import com.juicebox.dairydaily.Models.ReceiveCashModel;
import com.juicebox.dairydaily.Models.ReportByDateModels;
import com.juicebox.dairydaily.MyAdapters.BuyerRegisterAdapter;
import com.juicebox.dairydaily.MyAdapters.ReceiveCashAdapter;
import com.juicebox.dairydaily.MyAdapters.ReportByDateAdapter;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.ReceiveCashDialog;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.BuyMilk.MilkBuyEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.UsersListActivity;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static com.juicebox.dairydaily.Others.UtilityMethods.getDateRange;
import static com.juicebox.dairydaily.Others.UtilityMethods.getEndDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.getFirstname;
import static com.juicebox.dairydaily.Others.UtilityMethods.getMonth;
import static com.juicebox.dairydaily.Others.UtilityMethods.getStartDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;

public class ReceiveCashActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    ImageView start_date_image;
    ImageView end_date_image;
    TextView start_date_text_view, end_date_text_view;
    TextView names;

    private static final String TAG = "ReceiveCashActivity";

    ArrayList<ReceiveCashModel> list;
    double creditTotal = 0;
    double debitTotal = 0;
    double remain = 0;

    EditText id;

    Button go, receive_cash, send_msg;
    RecyclerView recyclerView;

    int idInt;

    String startDate, endDate;

    DatePickerDialog startDatePickerDialog;
    DatePickerDialog endDatePickerDialog;

    DbHelper dbHelper = new DbHelper(this);

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_cash);

        getSupportActionBar().setTitle("Receive Cash");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        start_date_image = findViewById(R.id.start_date_image);
        end_date_image = findViewById(R.id.end_date_image_view);
        start_date_text_view = findViewById(R.id.start_date_text_view);
        end_date_text_view = findViewById(R.id.end_date_text_view);
        names = findViewById(R.id.name);
        id = findViewById(R.id.id);
        go = findViewById(R.id.go);
        final TextView totalCredit = findViewById(R.id.totalCredit);
        final TextView totalDebit = findViewById(R.id.totalDebit);
        final TextView remaining = findViewById(R.id.remaining);
        receive_cash = findViewById(R.id.receive_cash);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        send_msg = findViewById(R.id.send_msg);


        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_number = dbHelper.getBuyerPhone_Number(idInt);
                String toSend = "TOTAL DEBIT: " + truncate(creditTotal)+"Rs";
                toSend += "\nTOTAL CREDIT: " + truncate(debitTotal)+"Rs";
                toSend += "\nOUTSTANDING: " + truncate(-remain)+"Rs";
                toSend += "\nThank you for using DairyDaily!";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.putExtra("address", phone_number);
                i.putExtra("sms_body", toSend);
                i.setType("vnd.android-dir/mms-sms");
                startActivity(i);
            }
        });

        receive_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    idInt = Integer.valueOf(id.getText().toString());
                }
                catch(Exception e){

                }
                new ReceiveCashDialog(ReceiveCashActivity.this, idInt).show();
            }
        });

        idInt = getIntent().getIntExtra("id", 0);
        String name = getIntent().getStringExtra("name");
        try{
            if(!name.equals("")){
                names.setText(name);
            }
        }
        catch(Exception e){}
        if(idInt != 0){
            id.setText(String.valueOf(idInt));
        }

        names.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReceiveCashActivity.this, UsersListActivity.class);
                intent.putExtra("From", "ReceiveCash");
                startActivity(intent);
            }
        });

        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    if(!s.toString().equals("")){
                        int id = Integer.valueOf(s.toString());
                        String name = dbHelper.getBuyerName(id);
                        if(name.equals("")){
                            names.setText("Not Found");
                        }
                        else{
                        names.setText(name);
                        }
                    }
                    else{
                        names.setText("All Buyers");
                    }
                }
                catch(Exception e){

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        go.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            idInt = Integer.valueOf(id.getText().toString());
                        }
                        catch(Exception e){

                        }

                        try{
                            if(startDate.isEmpty() || endDate.isEmpty()){
                                Toast.makeText(ReceiveCashActivity.this, "Operation failed", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                list = dbHelper.getReceiveCash(idInt, startDate, endDate);
                                Log.d("Sheriff", "receive cash: " + list.size());
                                ReceiveCashAdapter adapter = new ReceiveCashAdapter(ReceiveCashActivity.this, list);
                                for(ReceiveCashModel model : list){
                                    creditTotal += Double.valueOf(model.getCredit());
                                    debitTotal += Double.valueOf(model.getDebit());
                                }
                                remain = creditTotal - debitTotal;
                                totalCredit.setText(String.valueOf(truncate(creditTotal)) + "Rs");
                                totalDebit.setText(String.valueOf(truncate(debitTotal)) + "Rs");
                                remaining.setText(String.valueOf(truncate(-remain)) + "Rs");
                                recyclerView.setAdapter(adapter);
                            }
                        }
                        catch(Exception e){
                            Log.d("Sheriff", "receive cash: " + e.getMessage());
                        }

                    }
                }
        );

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.printer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.printer) {
            if(list!=null){
                //Collections.reverse(list);
                String line = "------------------------------";
                Date dateIntermediate = new Date();
                String date = new SimpleDateFormat("dd/MM/YYYY").format(dateIntermediate);
                String toPrint ="\nID| " +idInt + "   " + dbHelper.getBuyerName(idInt) + "\n"+date + "\n"+ startDate + " To " + endDate + "\n   Date   |"  + "Title|" + "Debit|" + "Credit|\n" +line + "\n";

                for(ReceiveCashModel object : list){
                    int idInt = object.getId();
                    String title = getFirstname(object.getTitle());
                    String credit = object.getCredit();
                    String debit = object.getDebit();
                    String datee = object.getDate();
                    toPrint += datee + "|"+title + " | " + truncate(Double.valueOf(credit)) + "|" + truncate(Double.valueOf(debit)) + "|\n";
                }
                toPrint += line + "\n";
                toPrint += "TOTAL CREDIT: "+ truncate(creditTotal) + "Rs\n";
                toPrint += "TOTAL DEBIT: " + truncate(debitTotal) + "Rs\n";
                toPrint += "AMOUNT REMAINING: " + truncate(-remain) + "Rs\n";
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
                            toast(ReceiveCashActivity.this, "Unable to print");
                        }
                    } else {
                        toast(ReceiveCashActivity.this, "Printer is not connected");
                    }
                }
                else{
                    toast(ReceiveCashActivity.this, "Bluetooth is off");
                }
            }
        }
        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ReceiveCashActivity.this, ViewBuyerReportActivity.class));
        finish();
    }
}
