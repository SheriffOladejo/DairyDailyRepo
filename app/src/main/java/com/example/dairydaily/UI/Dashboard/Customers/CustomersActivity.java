package com.example.dairydaily.UI.Dashboard.Customers;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.dairydaily.R;
import com.example.dairydaily.UI.Dashboard.DashboardActivity;

import static com.example.dairydaily.Others.UtilityMethods.hideKeyboard;

public class CustomersActivity extends AppCompatActivity {

    LinearLayout seller_layout, buyer_layout;
    Button buyers, sellers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customers);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Customers");

        seller_layout = findViewById(R.id.seller_layout);
        buyer_layout = findViewById(R.id.buyer_layout);
        buyers = findViewById(R.id.buyers_button);
        sellers = findViewById(R.id.sellers_button);

        // initially make buyers button colorAccent and sellers to gray
        sellers.setBackgroundColor(getResources().getColor(R.color.lightgray));
        buyers.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        sellers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    sellers.setBackground(getDrawable(R.drawable.rectangle_border));
                    buyers.setBackground(getDrawable(R.drawable.rectangle_border));
                }
                // Hide the buyers view
                buyer_layout.setVisibility(View.GONE);
                // Show the sellers layout
                seller_layout.setVisibility(View.VISIBLE);
                sellers.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                buyers.setBackgroundColor(getResources().getColor(R.color.lightgray));
            }
        });

        buyers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    sellers.setBackground(getDrawable(R.drawable.rectangle_border));
                    buyers.setBackground(getDrawable(R.drawable.rectangle_border));
                }
                // Hide the buyers view
                seller_layout.setVisibility(View.GONE);
                // Show the sellers layout
                buyer_layout.setVisibility(View.VISIBLE);
                buyers.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                sellers.setBackgroundColor(getResources().getColor(R.color.lightgray));
            }
        });

        // Initially hide the seller layout
        seller_layout.setVisibility(View.GONE);
        hideKeyboard(CustomersActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add_customer){
            startActivity(new Intent(CustomersActivity.this, AddCustomers.class));
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CustomersActivity.this, DashboardActivity.class));
    }
}
