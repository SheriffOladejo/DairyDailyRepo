package com.example.dairydaily.UI;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dairydaily.Models.CustomerModels;
import com.example.dairydaily.MyAdapters.UsersListAdapter;
import com.example.dairydaily.Others.DbHelper;
import com.example.dairydaily.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.dairydaily.Others.UtilityMethods.hideKeyboard;

public class UsersListActivity extends AppCompatActivity {
    LinearLayout seller_layout;
    EditText search;
    DbHelper dbHelper;
    private List<CustomerModels> seller_list = new ArrayList<>();
    private RecyclerView sellers_recyclerview;
    private static final String TAG = "UsersListActivity";

    UsersListAdapter buyer_adapter;
    UsersListAdapter seller_adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Users list");

        dbHelper = new DbHelper(this);

        seller_layout = findViewById(R.id.seller_layout);
        sellers_recyclerview = findViewById(R.id.sellers_recyclerview);
        search = findViewById(R.id.search);

        sellers_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        populateSellerView();

        // Get value passed from milkBuyEntryActivity
        boolean from_milk_entry = getIntent().getBooleanExtra("from_milk_entry", false);

        hideKeyboard(UsersListActivity.this);
    }

    private void populateSellerView() {
        Cursor sellers = dbHelper.getAllSellers();

        if(sellers.getCount() == 0){
            Toast.makeText(UsersListActivity.this, "Seller database is empty", Toast.LENGTH_SHORT).show();
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
            seller_adapter = new UsersListAdapter(seller_list, this);
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

}

