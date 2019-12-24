package com.example.dixitlamba.UI.Dashboard.ViewReport;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dixitlamba.R;

public class CustomerReportActivity  extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    ImageView start_date_image;
    ImageView end_date_image;
    TextView start_date_text_view, end_date_text_view;

    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_report);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Customer Report");

        start_date_image = findViewById(R.id.start_date_image);
        end_date_image = findViewById(R.id.end_date_image_view);
        start_date_text_view = findViewById(R.id.start_date_text_view);
        end_date_text_view = findViewById(R.id.end_date_text_view);

        // Check for date picker dialog permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            datePickerDialog = new DatePickerDialog(this, this, 2019, 11, 24);
        }

        start_date_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        start_date_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        end_date_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        end_date_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CustomerReportActivity.this, ViewReportActivity.class));
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
        start_date_text_view.setText(mMonth + " " + dayOfMonth + ", " + year);
        Toast.makeText(CustomerReportActivity.this, year + " " + month + " " + dayOfMonth, Toast.LENGTH_LONG).show();
    }
}
