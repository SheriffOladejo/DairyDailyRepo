package com.dixit.dairydaily.UI.Dashboard.Customers;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dixit.dairydaily.Models.CustomerModels;
import com.dixit.dairydaily.Models.PaymentRegisterModel;
import com.dixit.dairydaily.MyAdapters.CustomerAdapter;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;

import java.util.ArrayList;
import java.util.List;

import static com.dixit.dairydaily.Others.UtilityMethods.hideKeyboard;

public class CustomersActivity extends AppCompatActivity {

    LinearLayout seller_layout, buyer_layout;
    EditText search;
    Button buyers, sellers;
    DbHelper dbHelper;
    public List<CustomerModels> buyer_list = new ArrayList<>();
    public List<CustomerModels> seller_list = new ArrayList<>();
    public RecyclerView buyers_recyclerview, sellers_recyclerview;
    private static final String TAG = "CustomersActivity";

    public static CustomerAdapter buyer_adapter;
    public static CustomerAdapter seller_adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customers);

        getSupportActionBar().setTitle("Customers");
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        dbHelper = new DbHelper(this);

        seller_layout = findViewById(R.id.seller_layout);
        buyer_layout = findViewById(R.id.buyer_layout);
        buyers = findViewById(R.id.buyers_button);
        sellers = findViewById(R.id.sellers_button);
        buyers_recyclerview = findViewById(R.id.buyers_recyclerview);
        sellers_recyclerview = findViewById(R.id.sellers_recyclerview);
        search = findViewById(R.id.search);

        buyers_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        sellers_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        buyers_recyclerview.requestFocus();
        sellers_recyclerview.requestFocus();

        // Initially hide the seller layout
        buyer_layout.setVisibility(View.GONE);

        populateBuyerView();
        populateSellerView();

        // Get value passed from milkBuyEntryActivity
        boolean from_milk_entry = getIntent().getBooleanExtra("from_milk_entry", false);

        // initially make buyers button colorAccent and sellers to gray
        sellers.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        buyers.setBackgroundColor(getResources().getColor(R.color.lightgray));

        sellers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    sellers.setBackground(getDrawable(R.drawable.rectangle_border));
                    buyers.setBackground(getDrawable(R.drawable.rectangle_border));
                }
                // Hide the buyers view
                buyer_layout.setVisibility(View.GONE);
                seller_layout.setVisibility(View.VISIBLE);
                sellers.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                buyers.setBackgroundColor(getResources().getColor(R.color.lightgray));
            }
        });

        buyers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    sellers.setBackground(getDrawable(R.drawable.rectangle_border));
                    buyers.setBackground(getDrawable(R.drawable.rectangle_border));
                }
                // Hide the buyers view
                seller_layout.setVisibility(View.GONE);
                buyer_layout.setVisibility(View.VISIBLE);
                buyers.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                sellers.setBackgroundColor(getResources().getColor(R.color.lightgray));
            }
        });

        hideKeyboard(this);
    }

    private void populateSellerView() {
        Cursor sellers = dbHelper.getAllSellers();

        if(sellers.getCount() == 0){

        }
        else{
            while(sellers.moveToNext()){
                int id = sellers.getInt(0);
                String name = sellers.getString(1);
                String phone_number = sellers.getString(2);
                String address = sellers.getString(3);
                String status = sellers.getString(4);

                CustomerModels model = new CustomerModels(phone_number, name, address, id, status);
                seller_list.add(model);
                Log.d(TAG, "populateSellerView: " + status);
            }

            for(int i=0; i<seller_list.size();i++){
                for(int j=0; j<seller_list.size()-1; j++){
                    int value1 = seller_list.get(j).getId();
                    int value2 = seller_list.get(j+1).getId();
                    if(value1 > value2){
                        CustomerModels temp;
                        temp = seller_list.get(j);
                        seller_list.set(j, seller_list.get(j+1));
                        seller_list.set(j+1, temp);
                    }
                }
            }

            seller_adapter = new CustomerAdapter(seller_list, this);
            sellers_recyclerview.setAdapter(seller_adapter);

            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    seller_adapter.getFilter().filter(s.toString().toLowerCase());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    private void populateBuyerView() {
        Cursor buyers = dbHelper.getAllBuyers();

        if(buyers.getCount() == 0){

        }
        else{
            while(buyers.moveToNext()){
                int id = buyers.getInt(0);
                String name = buyers.getString(1);
                String phone_number = buyers.getString(2);
                String address = buyers.getString(3);
                String status = buyers.getString(4);

                CustomerModels model = new CustomerModels(phone_number, name, address, id, status);
                buyer_list.add(model);
                Log.d(TAG, "populateBuyerView: status " + status +" id: " + id);
            }

            for(int i=0; i<buyer_list.size();i++){
                for(int j=0; j<buyer_list.size()-1; j++){
                    int value1 = buyer_list.get(j).getId();
                    int value2 = buyer_list.get(j+1).getId();
                    if(value1 > value2){
                        CustomerModels temp;
                        temp = buyer_list.get(j);
                        buyer_list.set(j, buyer_list.get(j+1));
                        buyer_list.set(j+1, temp);
                    }
                }
            }

            buyer_adapter = new CustomerAdapter(buyer_list, this);
            buyers_recyclerview.setAdapter(buyer_adapter);
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    buyer_adapter.getFilter().filter(s.toString().toLowerCase());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            Log.d(TAG, "populateBuyerView: after adapter: buyer");
        }
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
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
