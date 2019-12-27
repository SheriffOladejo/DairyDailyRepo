package com.example.dairydaily.UI.Dashboard.SellMilk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dairydaily.R;
import com.example.dairydaily.UI.Dashboard.BuyMilk.MilkBuyEntryActivity;

public class MilkSaleEntryActivity extends AppCompatActivity {

    String shift, date;

    MenuItem day, evening;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk_sale_entry);

        getSupportActionBar().setTitle("Sell Milk");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

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
            Toast.makeText(MilkSaleEntryActivity.this, shift, Toast.LENGTH_SHORT).show();
            evening.setVisible(false);
            day.setVisible(true);
        }
        else if(shift.equals("PM")){
            Toast.makeText(MilkSaleEntryActivity.this, shift, Toast.LENGTH_SHORT).show();
            evening.setVisible(true);
            day.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }
}
