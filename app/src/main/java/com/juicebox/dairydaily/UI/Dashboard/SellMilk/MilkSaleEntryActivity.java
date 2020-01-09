package com.juicebox.dairydaily.UI.Dashboard.SellMilk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.juicebox.dairydaily.Models.DailySalesObject;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.BuyMilk.MilkBuyEntryActivity;
import com.juicebox.dairydaily.UI.UsersListActivity;

import java.util.ArrayList;

public class MilkSaleEntryActivity extends AppCompatActivity {

    String shift, date;

    EditText id, weight;
    TextView all_buyers, amount_display, rate_view;
    MenuItem day, evening;
    RecyclerView recyclerView;

    DbHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk_sale_entry);

        getSupportActionBar().setTitle("Sell Milk");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        id = findViewById(R.id.id);
        weight = findViewById(R.id.weight);
        all_buyers = findViewById(R.id.buyers);
        rate_view = findViewById(R.id.rate_view);
        amount_display = findViewById(R.id.amount_display);
        recyclerView = findViewById(R.id.recyclerview);

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEntry();
            }
        });

        dbHelper = new DbHelper(this);

        ArrayList<DailySalesObject> list = new ArrayList<>();
        list = dbHelper.getDailySalesData(date);


        String name_from_user_list = getIntent().getStringExtra("name");
        int id_from_user_list = getIntent().getIntExtra("id", 0);
        boolean received_a_name = getIntent().getBooleanExtra("passed", false);
        shift = getIntent().getStringExtra("Shift");
        date = getIntent().getStringExtra("Date");

        final double rate = dbHelper.getRate();
        rate_view.setText(String.valueOf(rate));

        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()){
                    double amount = rate * (Double.valueOf(s.toString()));
                    amount_display.setText(String.valueOf(amount));
                }
                else{
                    amount_display.setText("Amount");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(received_a_name){
            all_buyers.setText(name_from_user_list);
            id.setText(String.valueOf(id_from_user_list));
        }

        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")){
                    int id = Integer.valueOf(s.toString());
                    String name = dbHelper.getBuyerName(id);
                    all_buyers.setText(name);
                }
                else{
                    all_buyers.setText("All Buyers");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        all_buyers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MilkSaleEntryActivity.this, UsersListActivity.class);
                intent.putExtra("From", "Sell");
                intent.putExtra("Shift", shift);
                intent.putExtra("Date", date);
                startActivity(intent);
            }
        });
    }

    private void saveEntry(){
        String name = all_buyers.getText().toString();
        int Id = Integer.valueOf(id.getText().toString());
        double Weight = Double.valueOf(weight.getText().toString());
        double Amount = Double.valueOf(amount_display.getText().toString());
        double rate = Double.valueOf(rate_view.getText().toString());

        if(!String.valueOf(id).equals("") && !name.isEmpty()){
            if(!String.valueOf(weight).equals("")){
                if(!dbHelper.addSalesEntry(Id,name, Weight, Amount, rate,shift, date, Amount, 0.00))
                    Toast.makeText(MilkSaleEntryActivity.this, "Unable to add entry", Toast.LENGTH_SHORT).show();
            }
        }
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
        startActivity(new Intent(MilkSaleEntryActivity.this, SellMilkActivity.class));
        finish();
    }
}
