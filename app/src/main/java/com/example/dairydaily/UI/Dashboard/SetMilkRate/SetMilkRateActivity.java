package com.example.dairydaily.UI.Dashboard.SetMilkRate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.dairydaily.R;
import com.example.dairydaily.UI.Dashboard.DashboardActivity;

public class SetMilkRateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_milk_rate);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Set Milk Rate");

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SetMilkRateActivity.this, DashboardActivity.class));
    }
}
