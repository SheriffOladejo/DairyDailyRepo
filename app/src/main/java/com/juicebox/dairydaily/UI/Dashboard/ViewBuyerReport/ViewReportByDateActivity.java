package com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.juicebox.dairydaily.R;

public class ViewReportByDateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    ImageView start_date_image;
    ImageView end_date_image;
    TextView start_date_text_view, end_date_text_view;
    RadioButton start_morning_radio, start_evening_radio, end_morning_radio, end_evening_radio;
    String start_date, end_date;

    DatePickerDialog startDatePickerDialog;
    DatePickerDialog endDatePickerDialog;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_report_by_date);

        getSupportActionBar().setTitle("Report By Date");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        start_date_image = findViewById(R.id.start_date_image);
        end_date_image = findViewById(R.id.end_date_image_view);
        start_date_text_view = findViewById(R.id.start_date_text_view);
        end_date_text_view = findViewById(R.id.end_date_text_view);
        start_morning_radio = findViewById(R.id.start_morning_radio);
        start_evening_radio= findViewById(R.id.start_evening_radio);
        end_morning_radio = findViewById(R.id.end_morning_radio);
        end_evening_radio = findViewById(R.id.end_evening_radio);

        // Check for date picker dialog permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            startDatePickerDialog = new DatePickerDialog(this, this, 2019, 11, 24);
            endDatePickerDialog = new DatePickerDialog(this, this, 2019, 11, 24);
        }

        start_date_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog.show();
            }
        });
        start_date_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog.show();
            }
        });
        end_date_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDatePickerDialog.show();
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
                String mMonth = "";
                // switch statement to convert number to month String
                switch (month){
                    case 0:
                        mMonth = "Jan";
                        break;
                    case 1:
                        mMonth = "Feb";
                        break;
                    case 2:
                        mMonth = "Mar";
                        break;
                    case 3:
                        mMonth = "Apr";
                        break;
                    case 4:
                        mMonth = "May";
                        break;
                    case 5:
                        mMonth = "Jun";
                        break;
                    case 6:
                        mMonth = "Jul";
                        break;
                    case 7:
                        mMonth= "August";
                        break;
                    case 8:
                        mMonth = "Sep";
                        break;
                    case 9:
                        mMonth = "Oct";
                        break;
                    case 10:
                        mMonth = "Nov";
                        break;
                    case 11:
                        mMonth = "Dec";
                        break;
                }
                start_date_text_view.setText(mMonth + " " + dayOfMonth + ", " + year);
                start_date = mMonth + " " + dayOfMonth + ", " +year;
            }
        });

        endDatePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String mMonth = "";
                // switch statement to convert number to month String
                switch (month){
                    case 0:
                        mMonth = "Jan";
                        break;
                    case 1:
                        mMonth = "Feb";
                        break;
                    case 2:
                        mMonth = "Mar";
                        break;
                    case 3:
                        mMonth = "Apr";
                        break;
                    case 4:
                        mMonth = "May";
                        break;
                    case 5:
                        mMonth = "Jun";
                        break;
                    case 6:
                        mMonth = "Jul";
                        break;
                    case 7:
                        mMonth= "August";
                        break;
                    case 8:
                        mMonth = "Sep";
                        break;
                    case 9:
                        mMonth = "Oct";
                        break;
                    case 10:
                        mMonth = "Nov";
                        break;
                    case 11:
                        mMonth = "Dec";
                        break;
                }
                end_date_text_view.setText(mMonth + " " + dayOfMonth + ", " + year);
                end_date = mMonth + " " + dayOfMonth + ", " +year;
            }
        });
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
