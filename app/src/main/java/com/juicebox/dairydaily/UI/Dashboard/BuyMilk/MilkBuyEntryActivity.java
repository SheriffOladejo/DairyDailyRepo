package com.juicebox.dairydaily.UI.Dashboard.BuyMilk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.UsersListActivity;

import java.io.IOException;

import static com.juicebox.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.juicebox.dairydaily.UI.Dashboard.BuyMilk.BuyMilkActivity.printData;

public class MilkBuyEntryActivity extends AppCompatActivity {

    String shift, date;
    String fatString;
    String weightString;
    String snfValue;
    private String rate;

    private static final String TAG = "MilkBuyEntryActivity";

    private EditText id, weight, snf, fat;
    private TextView seller_display, rate_display, amount_display;

    private DbHelper dbHelper;

    MenuItem day, evening;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.milk_buy_entry);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        hideKeyboard(MilkBuyEntryActivity.this);

        getSupportActionBar().setTitle("Buy Milk Entry");
        getSupportActionBar().setHomeButtonEnabled(true);

        id = findViewById(R.id.id);
        weight = findViewById(R.id.weight);
        fat = findViewById(R.id.fat);
        snf = findViewById(R.id.snf);
        seller_display = findViewById(R.id.seller_display);
        rate_display = findViewById(R.id.rate_display);
        amount_display = findViewById(R.id.amount_display);
        Button save = findViewById(R.id.save);

        String name_from_user_list = getIntent().getStringExtra("name");
        int id_from_user_list = getIntent().getIntExtra("id", 0);
        boolean received_a_name = getIntent().getBooleanExtra("passed", false);
        shift = getIntent().getStringExtra("Shift");
        date = getIntent().getStringExtra("Date");

        if(received_a_name){
            seller_display.setText(name_from_user_list);
            id.setText(String.valueOf(id_from_user_list));
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    printData("Hello World");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        seller_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MilkBuyEntryActivity.this, UsersListActivity.class);
                intent.putExtra("From", "Buy");
                intent.putExtra("Shift", shift);
                intent.putExtra("Date", date);
                startActivity(intent);
            }
        });

        dbHelper = new DbHelper(this);

        snf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                fatString = fat.getText().toString();
                weightString = weight.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!fatString.isEmpty() && !weightString.isEmpty()){
                    snfValue = s.toString();
                    if(!snfValue.isEmpty()){
                        double intermediate = Double.valueOf(snfValue) * 10;
                        int convertedValue = (int) intermediate;
                        String concatValue = "SNF" + convertedValue;
                        Log.d(TAG, "concatValue: " + concatValue);
                        rate = dbHelper.getRate(fatString, concatValue);
                        if(!rate.equals("Not Found") && !rate.equals("")){
                            rate_display.setText(rate);
                            amount_display.setText(String.valueOf(Double.valueOf(weightString) * Double.valueOf(rate)));
                            rate = "";
                        }
                        else{
                            rate_display.setText("Not Found");
                            amount_display.setText("Amount");
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                snfValue = snf.getText().toString();
                weightString = weight.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!snfValue.isEmpty() && !weightString.isEmpty()){
                    fatString = s.toString();
                    if(!fatString.isEmpty()){
                        double intermediate = Double.valueOf(snfValue) * 10;
                        int convertedValue = (int) intermediate;
                        String concatValue = "SNF" + convertedValue;
                        rate = dbHelper.getRate(fatString, concatValue);
                        if(!rate.equals("Not Found") && !rate.equals("")){
                            rate_display.setText(rate);
                            Log.d(TAG, "Rate: " + rate);
                            amount_display.setText(String.valueOf(Double.valueOf(weightString) * Double.valueOf(rate)));
                            rate = "";
                        }
                        else{
                            rate_display.setText("Not Found");
                            amount_display.setText("Amount");
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")){
                    int id = Integer.valueOf(s.toString());
                    String name = dbHelper.getSellerName(id);
                    seller_display.setText(name);
                }
                else{
                    seller_display.setText("All Sellers");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.buy_milk_printer_shift, menu );
        day = menu.findItem(R.id.day);
        evening = menu.findItem(R.id.evening);
        MenuItem dateItem = menu.findItem(R.id.date);
        dateItem.setTitle(date);

        if(shift.equals("AM")){
            evening.setVisible(false);
            day.setVisible(true);
        }
        else if(shift.equals("PM")){
            evening.setVisible(true);
            day.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MilkBuyEntryActivity.this, BuyMilkActivity.class));
        finish();
    }
}
