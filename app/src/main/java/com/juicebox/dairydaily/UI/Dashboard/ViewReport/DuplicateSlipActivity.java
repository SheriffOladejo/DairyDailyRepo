package com.juicebox.dairydaily.UI.Dashboard.ViewReport;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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

@RequiresApi(api = Build.VERSION_CODES.N)
public class DuplicateSlipActivity extends AppCompatActivity {

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

    DatePickerDialog datePickerDialog;

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
        datePickerDialog = new DatePickerDialog(this);
        idEditText = findViewById(R.id.id);

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
                datePickerDialog.show();
            }
        });
        select_date_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        Date newDate = new Date();
        date = new SimpleDateFormat("YYYY-MM-dd").format(newDate);
        select_date_text_view.setText(date);

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

                        String date = new SimpleDateFormat("dd/MM/YYYY").format(dateIntermediate);
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DuplicateSlipActivity.this, ViewReportActivity.class));
        finish();
    }

}
