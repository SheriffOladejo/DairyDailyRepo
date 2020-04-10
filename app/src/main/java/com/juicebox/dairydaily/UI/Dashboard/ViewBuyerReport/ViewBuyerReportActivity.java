package com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;

import static com.juicebox.dairydaily.Others.UtilityMethods.hideKeyboard;

public class ViewBuyerReportActivity extends AppCompatActivity {

    private CardView invoice, buyer_register, receive_cash, view_report_by_date, set_milk_rate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_buyer_report);

        getSupportActionBar().setTitle("View Buyer Report");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        hideKeyboard(this);

        invoice = findViewById(R.id.invoice);
        buyer_register = findViewById(R.id.buyer_register);
        receive_cash = findViewById(R.id.receive_cash);
        view_report_by_date = findViewById(R.id.view_report_by_date);
        set_milk_rate = findViewById(R.id.set_milk_rate);

        receive_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewBuyerReportActivity.this, ReceiveCashList.class));
            }
        });
        set_milk_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewBuyerReportActivity.this, SetMilkRateActivity.class));
            }
        });
        view_report_by_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewBuyerReportActivity.this, ViewReportByDateActivity.class));
            }
        });
        buyer_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewBuyerReportActivity.this, BuyerRegisterActivity.class));
            }
        });
        invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewBuyerReportActivity.this, InvoiceActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ViewBuyerReportActivity.this, DashboardActivity.class));
        finish();
    }
}
