package com.juicebox.dairydaily.UI.Dashboard.ViewReport;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.juicebox.dairydaily.R;

public class DuplicateSlipActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    ImageView select_date_image_view;
    TextView select_date_text_view;

    RadioButton morning_radio, evening_radio;
    String date;

    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_slip);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Duplicate Slip");

        // Check for date picker dialog permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            datePickerDialog = new DatePickerDialog(this, this, 2019, 11, 24);
        }

        select_date_image_view = findViewById(R.id.select_date_image);
        select_date_text_view = findViewById(R.id.select_date_text);
        morning_radio = findViewById(R.id.morning_radio);
        evening_radio = findViewById(R.id.evening_radio);

        morning_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(evening_radio.isChecked())
                    evening_radio.setChecked(false);
            }
        });
        evening_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(morning_radio.isChecked())
                    morning_radio.setChecked(false);
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

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DuplicateSlipActivity.this, ViewReportActivity.class));
        finish();
    }

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
        select_date_text_view.setText(mMonth + " " + dayOfMonth + ", " + year);
        date = mMonth + " " + dayOfMonth + ", " +year;
    }

}