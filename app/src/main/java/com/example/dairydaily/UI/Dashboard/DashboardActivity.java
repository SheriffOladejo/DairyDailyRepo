package com.example.dairydaily.UI.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.example.dairydaily.R;
import com.example.dairydaily.UI.Dashboard.BuyMilk.BuyMilkActivity;
import com.example.dairydaily.UI.Dashboard.ViewBuyerReport.ViewBuyerReportActivity;
import com.example.dairydaily.UI.Dashboard.Customers.CustomersActivity;
import com.example.dairydaily.UI.Dashboard.SellMilk.SellMilkActivity;
import com.example.dairydaily.UI.Dashboard.ViewReport.ViewReportActivity;

public class DashboardActivity extends AppCompatActivity {

    CardView view_report, buyer_report;
    CardView buy_milk, sell_milk, customers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getSupportActionBar().setTitle("Dashboard");

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        view_report =  findViewById(R.id.view_report);
        buy_milk = findViewById(R.id.buy_milk_image);
        sell_milk = findViewById(R.id.sell_milk_image);
        customers = findViewById(R.id.customers);
        buyer_report = findViewById(R.id.buyer_report);

        customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, CustomersActivity.class));
            }
        });
        buyer_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ViewBuyerReportActivity.class));
            }
        });
        buy_milk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, BuyMilkActivity.class));
            }
        });
        sell_milk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, SellMilkActivity.class));
            }
        });
        view_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ViewReportActivity.class));
            }
        });
    }
}
