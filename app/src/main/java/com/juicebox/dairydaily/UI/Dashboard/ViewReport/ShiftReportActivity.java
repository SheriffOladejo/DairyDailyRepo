package com.juicebox.dairydaily.UI.Dashboard.ViewReport;

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
import android.widget.RadioButton;
import android.widget.TextView;

import com.juicebox.dairydaily.Models.ShiftReportModel;
import com.juicebox.dairydaily.MyAdapters.ShiftReportAdapter;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;

public class ShiftReportActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    double totalWeight;
    double totalAmount;
    double averageFat;
    double averageSnf;

    public static String phone_number = "";
    public static int count = 0;

    ArrayList<ShiftReportModel> list;

    ImageView select_date_image_view;
    TextView select_date_text_view;
    TextView amountTotal, weightTotal;
    RadioButton morning_radio, evening_radio;
    ConstraintLayout scrollview;
    String date;
    String shift;
    boolean printed = false;
    Button go, print;
    private static final String TAG = "ShiftReportActivity";

    RecyclerView recyclerView;

    DbHelper dbHelper = new DbHelper(this);

    DatePickerDialog datePickerDialog;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_report);

        //bluetoothConnectionService = new BluetoothConnectionService(this, this);

        getSupportActionBar().setTitle("Shift Report");
        getSupportActionBar().setHomeButtonEnabled(true);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        // Check for date picker dialog permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            datePickerDialog = new DatePickerDialog(this);
        }

        select_date_image_view = findViewById(R.id.select_date_image);
        scrollview = findViewById(R.id.constraintlayout);
        select_date_text_view = findViewById(R.id.select_date_text);
        morning_radio = findViewById(R.id.morning_radio);
        evening_radio = findViewById(R.id.evening_radio);
        weightTotal = findViewById(R.id.weightTotal);
        amountTotal = findViewById(R.id.amountTotal);
        recyclerView = findViewById(R.id.recyclerview);
        print = findViewById(R.id.print);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        go = findViewById(R.id.go);

        Date dateIntermediate = new Date();
        final String dateText = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
        select_date_text_view.setText(dateText);

        findViewById(R.id.send_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!phone_number.equals("") && count == 1){
                    String toSend = "TOTAL AMOUNT: " + truncate(totalAmount)+"Rs";
                    toSend += "\nTOTAL WEIGHT: " + truncate(totalWeight)+"Ltr";
                    toSend += "\nAVERAGE SNF: " + truncate(averageSnf);
                    toSend += "\nAVERAGE FAT: " + truncate(averageFat)+"%";
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
                    toast(ShiftReportActivity.this, "Can only send message to one user");
                }
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(list!=null){

                    Date dateIntermediate = new Date();
                    String line = "--------------------------------";

                    String date = new SimpleDateFormat("dd/MM/YYYY").format(dateIntermediate);
                    String toPrint = "\nDate | " + date + "\n" + "Shift | " + shift + "\n";
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
                                toast(ShiftReportActivity.this, "Unable to print");
                            }
                        } else {
                            toast(ShiftReportActivity.this, "Printer is not connected");
                        }
                    }
                    else{
                        toast(ShiftReportActivity.this, "Bluetooth is off");
                    }
                }
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("ShiftReport", "ShiftReport: " + shift);
                date = select_date_text_view.getText().toString();
                list = dbHelper.getShiftReport(date, shift);

                for(ShiftReportModel model : list){
                    totalWeight += Double.valueOf(model.getWeight());
                    totalAmount += Double.valueOf(model.getAmount());
                    averageSnf += Double.valueOf(model.getSnf());
                    averageFat += Double.valueOf(model.getFat());
                }
                averageFat /= list.size();
                averageSnf/=list.size();
                weightTotal.setText(String.valueOf(truncate(totalWeight)) + "Ltr");
                amountTotal.setText(String.valueOf(truncate(totalAmount)) + "Rs");

                ShiftReportAdapter adapter = new ShiftReportAdapter(ShiftReportActivity.this, list);
                recyclerView.setAdapter(adapter);
            }
        });

        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

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
            }
        });

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

        select_date_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ShiftReportActivity.this, ViewReportActivity.class));
        finish();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}
