package com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.juicebox.dairydaily.R;

public class InvoiceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        getSupportActionBar().setTitle("Invoice");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(InvoiceActivity.this, ViewBuyerReportActivity.class));
        finish();
    }
}
