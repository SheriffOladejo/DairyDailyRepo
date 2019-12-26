package com.example.dixitlamba.UI.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;

import com.example.dixitlamba.R;
import com.example.dixitlamba.UI.Dashboard.BuyMilk.BuyMilkActivity;
import com.example.dixitlamba.UI.Dashboard.SellMilk.SellMilkActivity;
import com.example.dixitlamba.UI.Dashboard.ViewReport.ViewReportActivity;

public class DashboardActivity extends AppCompatActivity {

    CardView view_report;
    CardView buy_milk, sell_milk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_dashboard);

        getSupportActionBar().setTitle("Dashboard");

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        view_report =  findViewById(R.id.view_report);
        buy_milk = findViewById(R.id.buy_milk_image);
        sell_milk = findViewById(R.id.sell_milk_image);

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
