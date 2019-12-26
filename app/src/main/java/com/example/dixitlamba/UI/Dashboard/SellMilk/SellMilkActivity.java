package com.example.dixitlamba.UI.Dashboard.SellMilk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;

import com.example.dixitlamba.R;
import com.example.dixitlamba.UI.Dashboard.BuyMilk.BuyMilkActivity;
import com.example.dixitlamba.UI.Dashboard.BuyMilk.MilkBuyEntryActivity;
import com.example.dixitlamba.UI.Dashboard.DashboardActivity;
import com.example.dixitlamba.UI.LoginActivity;

import java.util.Calendar;

import static com.example.dixitlamba.Others.UtilityMethods.useSnackBar;

public class SellMilkActivity extends AppCompatActivity {

    // The layout that hides the rate selection
    LinearLayout rateByFatView;
    boolean fixed_price = false;

    RadioGroup radioGroup;
    RadioButton fixed_price_button;
    RadioButton rateByFat_button;

    private String am_pm;
    private Switch morning_switch;
    private Switch evening_switch;

    private Button proceed;

    ScrollView scrollview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_milk);

        getSupportActionBar().setTitle("Sell Milk");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        morning_switch = findViewById(R.id.morning_switch);
        evening_switch = findViewById(R.id.evening_switch);
        scrollview = findViewById(R.id.scrollview);
        proceed = findViewById(R.id.proceed);

        // Check here if time is AM or PM and update switches accordingly
        Calendar dateTime = Calendar.getInstance();
        if(dateTime.get(Calendar.AM_PM) == Calendar.AM){
            am_pm = "AM";
        }
        else if(dateTime.get(Calendar.AM_PM) == Calendar.PM){
            am_pm = "PM";
        }
        if(am_pm.equals("AM")){
            morning_switch.setChecked(true);
        }
        if(am_pm.equals("PM")){
            evening_switch.setChecked(true);
        }

        fixed_price_button = findViewById(R.id.fixed_price);
        radioGroup = findViewById(R.id.radio_group);
        rateByFat_button = findViewById(R.id.rate_by_fat);
        fixed_price_button.setChecked(true);
        rateByFatView = findViewById(R.id.rate_by_fat_view);

        // Initially set the rateByFatView to be gone
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

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!morning_switch.isChecked() && !evening_switch.isChecked()){
                    useSnackBar("Select Shift", scrollview);
                }
                else{
                    Intent intent = new Intent(SellMilkActivity.this, MilkBuyEntryActivity.class);
                    intent.putExtra("Shift", morning_switch.isChecked() ? "AM" : "PM");
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SellMilkActivity.this, DashboardActivity.class));
    }
}
