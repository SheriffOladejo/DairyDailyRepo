package com.juicebox.dairydaily.UI;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.juicebox.dairydaily.Models.CustomerModels;
import com.juicebox.dairydaily.MyAdapters.UsersListAdapter;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.BuyMilk.BuyMilkActivity;
import com.juicebox.dairydaily.UI.Dashboard.BuyMilk.MilkBuyEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.ProductSale.ProductSaleActivity;
import com.juicebox.dairydaily.UI.Dashboard.SellMilk.MilkSaleEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport.ReceiveCashActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport.ViewReportByDateActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewReport.CustomerReportActivity;

import java.util.ArrayList;
import java.util.List;

import static com.juicebox.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.juicebox.dairydaily.Others.UtilityMethods.toast;

public class UsersListActivity extends AppCompatActivity {
    LinearLayout seller_layout;
    EditText search;
    DbHelper dbHelper;
    private List<CustomerModels> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private static final String TAG = "UsersListActivity";

    private String date, from, shift;

    UsersListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Users list");

        dbHelper = new DbHelper(this);

        seller_layout = findViewById(R.id.seller_layout);
        recyclerView = findViewById(R.id.sellers_recyclerview);
        search = findViewById(R.id.search);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        from = getIntent().getStringExtra("From");
        date = getIntent().getStringExtra("Date");
        shift = getIntent().getStringExtra("Shift");

        if(from.equals("MilkBuyEntryActivity"))
            populateSellerView();
        else if(from.equals("MilkSaleEntryActivity"))
            populateBuyerView();
        else if(from.equals("ViewReport"))
            populateBuyerView();
        else if(from.equals("CustomerReportActivity"))
            populateSellerView();
        else if(from.equals("ReceiveCash"))
            populateBuyerView();
        else if(from.equals("ProductSaleActivity"))
            populateBuyerView();
        else if(from.equals("ViewAllEntryActivity"))
            populateSellerView();

        hideKeyboard(UsersListActivity.this);
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
                list.add(model);
                Log.d(TAG, "populateSellerView: " + status);
            }
            adapter = new UsersListAdapter(list, this, from, shift, date);
            recyclerView.setAdapter(adapter);
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s.toString().toLowerCase());
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
                list.add(model);
                Log.d(TAG, "populateSellerView: " + status);
            }
            adapter = new UsersListAdapter(list, this, from, shift, date);
            recyclerView.setAdapter(adapter);
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s.toString().toLowerCase());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if(from.equals("MilkBuyEntryActivity")){
//            Intent intent = new Intent(UsersListActivity.this, MilkBuyEntryActivity.class);
//            intent.putExtra("Shift", shift);
//            intent.putExtra("Date", date);
//            startActivity(intent);
            finish();
        }
        else if(from.equals("MilkSaleEntryActivity")){
//            Intent intent = new Intent(UsersListActivity.this, MilkSaleEntryActivity.class);
//            intent.putExtra("Shift", shift);
//            intent.putExtra("Date", date);
//            startActivity(intent);
            finish();
        }
        else if(from.equals("ViewReport")){
//            Intent intent = new Intent(UsersListActivity.this, ViewReportByDateActivity.class);
//            intent.putExtra("Shift", shift);
//            intent.putExtra("Date", date);
//            startActivity(intent);
            finish();
        }
        else if(from.equals("CustomerReportActivity")){
//            Intent intent = new Intent(UsersListActivity.this, CustomerReportActivity.class);
//            intent.putExtra("Shift", shift);
//            intent.putExtra("Date", date);
//            startActivity(intent);
            finish();
        }
        else if(from.equals("ReceiveCash")){
//            Intent intent = new Intent(UsersListActivity.this, ReceiveCashActivity.class);
//            intent.putExtra("Shift", shift);
//            intent.putExtra("Date", date);
//            startActivity(intent);
            finish();
        }
        else if(from.equals("ProductSaleActivity")){
//            Intent intent = new Intent(UsersListActivity.this, ProductSaleActivity.class);
//            intent.putExtra("Shift", shift);
//            intent.putExtra("Date", date);
//            startActivity(intent);
            finish();
        }
    }
}

