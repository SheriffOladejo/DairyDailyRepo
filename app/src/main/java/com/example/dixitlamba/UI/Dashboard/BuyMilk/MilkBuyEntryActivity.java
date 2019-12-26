package com.example.dixitlamba.UI.Dashboard.BuyMilk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dixitlamba.R;

import java.io.IOException;

import static com.example.dixitlamba.Others.UtilityMethods.hideKeyboard;
import static com.example.dixitlamba.UI.Dashboard.BuyMilk.BuyMilkActivity.printData;

public class MilkBuyEntryActivity extends AppCompatActivity {

    String shift, date;

    MenuItem day, evening;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.milk_buy_entry);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        hideKeyboard(MilkBuyEntryActivity.this);

        getSupportActionBar().setTitle("Buy Milk Entry");
        getSupportActionBar().setHomeButtonEnabled(true);

        Button save = findViewById(R.id.save);
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

        shift = getIntent().getStringExtra("Shift");
        date = getIntent().getStringExtra("Date");
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
