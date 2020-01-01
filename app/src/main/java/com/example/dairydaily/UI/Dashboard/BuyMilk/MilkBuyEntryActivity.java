package com.example.dairydaily.UI.Dashboard.BuyMilk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dairydaily.Others.DbHelper;
import com.example.dairydaily.R;
import com.example.dairydaily.UI.UsersListActivity;

import java.io.IOException;

import static com.example.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.example.dairydaily.UI.Dashboard.BuyMilk.BuyMilkActivity.printData;

public class MilkBuyEntryActivity extends AppCompatActivity {

    String shift, date;

    private EditText id, weight, snf;
    private TextView seller_display, rate_display, amount_display;

    private int rate;

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

        shift = getIntent().getStringExtra("Shift");
        date = getIntent().getStringExtra("Date");

        id = findViewById(R.id.id);
        weight = findViewById(R.id.weight);
        snf = findViewById(R.id.snf);
        seller_display = findViewById(R.id.seller_display);
        rate_display = findViewById(R.id.rate_display);
        amount_display = findViewById(R.id.amount_display);
        Button save = findViewById(R.id.save);

        String name_from_user_list = getIntent().getStringExtra("name");
        int id_from_user_list = getIntent().getIntExtra("id", 0);
        boolean received_a_name = getIntent().getBooleanExtra("passed", false);

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
                intent.putExtra("Shift", shift);
                intent.putExtra("Date", date);
                startActivity(intent);
            }
        });

        dbHelper = new DbHelper(this);

        rate = dbHelper.getRate();
        String.valueOf(rate);
        rate_display.setText("Rs/Ltr: " + rate);

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
            Toast.makeText(MilkBuyEntryActivity.this, shift, Toast.LENGTH_SHORT).show();
            evening.setVisible(false);
            day.setVisible(true);
        }
        else if(shift.equals("PM")){
            Toast.makeText(MilkBuyEntryActivity.this, shift, Toast.LENGTH_SHORT).show();
            evening.setVisible(true);
            day.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }
}
