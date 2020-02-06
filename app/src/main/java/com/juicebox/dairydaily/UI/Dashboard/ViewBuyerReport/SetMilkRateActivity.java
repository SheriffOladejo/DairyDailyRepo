package com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;

import static com.juicebox.dairydaily.Others.UtilityMethods.hideKeyboard;

public class SetMilkRateActivity extends AppCompatActivity{

    private Button save;
    private EditText rate;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_milk_rate);

        dbHelper = new DbHelper(this);
        final double rateValue = dbHelper.getRate();

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Set Milk Rate");
        hideKeyboard(this);

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
                    dbHelper.updateRate(Double.valueOf(rate.getText().toString()));
                    startActivity(new Intent(SetMilkRateActivity.this, DashboardActivity.class));
                    finish();
                }
                else{
                    dbHelper.setRate(Double.valueOf(rate.getText().toString()));
                    startActivity(new Intent(SetMilkRateActivity.this, DashboardActivity.class));
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SetMilkRateActivity.this, ViewBuyerReportActivity.class));
        finish();
    }

}
