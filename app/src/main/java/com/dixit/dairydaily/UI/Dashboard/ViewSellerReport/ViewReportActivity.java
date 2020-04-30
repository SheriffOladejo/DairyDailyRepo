package com.dixit.dairydaily.UI.Dashboard.ViewSellerReport;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dixit.dairydaily.R;

public class ViewReportActivity extends AppCompatActivity {

    LinearLayout payment_register;
    LinearLayout customer_report;
    LinearLayout shift_report;
    LinearLayout duplicate_slip;
    LinearLayout add_bonus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_report_activity);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("View Report");


        payment_register = findViewById(R.id.payment_report);
        customer_report = findViewById(R.id.customer_report);
        shift_report = findViewById(R.id.shift_report);
        duplicate_slip = findViewById(R.id.duplicate_slip);
        add_bonus = findViewById(R.id.add_bonus);

        add_bonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewReportActivity.this, Experiment.class));
            }
        });
        shift_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewReportActivity.this, ShiftReportActivity.class));
            }
        });
        duplicate_slip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewReportActivity.this, DuplicateSlipActivity.class));
            }
        });
        payment_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewReportActivity.this, PaymentRegisterActivity.class));
            }
        });
        customer_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewReportActivity.this, CustomerReportActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(ViewReportActivity.this, DashboardActivity.class));
        finish();
    }
}
