package com.example.dairydaily.UI.Dashboard.ViewBuyerReport;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dairydaily.Others.DbHelper;
import com.example.dairydaily.R;
import com.example.dairydaily.UI.Dashboard.DashboardActivity;

public class SetMilkRateActivity extends AppCompatActivity {

    private Button save;
    private EditText rate;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_milk_rate);

        dbHelper = new DbHelper(this);
        final int rateValue = dbHelper.getRate();

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Set Milk Rate");

        rate = findViewById(R.id.milk_rate);
        save = findViewById(R.id.save);

        rate.setText(String.valueOf(rateValue));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rateString = rate.getText().toString();
                if(rateString.isEmpty())
                    rate.setError("Required");
                else if(rateValue != 0){
                    dbHelper.updateRate(Integer.valueOf(rate.getText().toString()));
                    startActivity(new Intent(SetMilkRateActivity.this, DashboardActivity.class));
                    finish();
                }
                else{
                    dbHelper.setRate(Integer.valueOf(rate.getText().toString()));
                    startActivity(new Intent(SetMilkRateActivity.this, DashboardActivity.class));
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SetMilkRateActivity.this, DashboardActivity.class));
    }
}
