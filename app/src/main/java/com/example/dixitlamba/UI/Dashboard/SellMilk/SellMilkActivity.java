package com.example.dixitlamba.UI.Dashboard.SellMilk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.dixitlamba.R;
import com.example.dixitlamba.UI.LoginActivity;

public class SellMilkActivity extends AppCompatActivity {

    LinearLayout rateByFatView;
    RadioGroup radioGroup;
    RadioButton fixed_price_button;
    RadioButton rateByFat_button;
    boolean fixed_price = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_milk);

        getSupportActionBar().setTitle("Sell Milk");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        fixed_price_button = findViewById(R.id.fixed_price);
        radioGroup = findViewById(R.id.radio_group);
        rateByFat_button = findViewById(R.id.rate_by_fat);
        fixed_price_button.setChecked(true);
        // Initially set the rateByFatView to be invisible
        rateByFatView = findViewById(R.id.rate_by_fat_view);
        rateByFatView.setVisibility(View.GONE);

        rateByFat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateByFatView.setVisibility(View.VISIBLE);
                fixed_price = false;
            }
        });

        fixed_price_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateByFatView.setVisibility(View.GONE);
                fixed_price = true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SellMilkActivity.this, LoginActivity.class));
    }
}
