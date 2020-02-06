package com.juicebox.dairydaily.UI.Dashboard.ProductSale;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.juicebox.dairydaily.Models.AddProductModel;
import com.juicebox.dairydaily.Models.ProductSaleModel;
import com.juicebox.dairydaily.MyAdapters.ProductSaleAdapter;
import com.juicebox.dairydaily.MyAdapters.ProductsAdapter;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.SpinnerItem;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.Customers.CustomersActivity;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.UsersListActivity;

import java.util.ArrayList;

import static com.juicebox.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.juicebox.dairydaily.Others.UtilityMethods.toast;

public class ProductSaleActivity extends AppCompatActivity {

    TextView all_buyers;
    Spinner all_products;
    AddProductModel spinnerItem;
    TextView amount;
    EditText id, units, rate;
    Button save;
    String product;

    RecyclerView recyclerView;

    DbHelper dbHelper = new DbHelper(this);

    ArrayList<AddProductModel> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_sale);

        hideKeyboard(this);

        getSupportActionBar().setTitle("Product Sale");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        recyclerView = findViewById(R.id.recyclerview);
        all_buyers = findViewById(R.id.all_buyers);
        all_products = findViewById(R.id.all_products);
        amount = findViewById(R.id.amount);
        id = findViewById(R.id.id);
        rate = findViewById(R.id.rate);
        units = findViewById(R.id.units);
        save = findViewById(R.id.save);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        ProductSaleAdapter productSaleAdapter = new ProductSaleAdapter(this, dbHelper.getProductSale());
        recyclerView.setAdapter(productSaleAdapter);

        String name = getIntent().getStringExtra("name");
        int idInt = getIntent().getIntExtra("id", -1);
        if(idInt == -1){
            id.setText("");
        }
        else{
            id.setText(String.valueOf(idInt));
            all_buyers.setText(name);
        }

        all_buyers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductSaleActivity.this, UsersListActivity.class).putExtra("From", "ProductSaleActivity"));
                finish();
            }
        });
        rate.setText("");

        ArrayList<AddProductModel> list = dbHelper.getProducts();
        ProductsAdapter adapter = new ProductsAdapter(this, list);
        all_products.setAdapter(adapter);

        all_products.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerItem = (AddProductModel) parent.getItemAtPosition(position);
                String rateString = spinnerItem.getRate();
                if(!spinnerItem.getProduct_name().equals("Select Product")){
                    rate.setText(rateString);
                    product = spinnerItem.getProduct_name();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        units.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()){
                    double rateValue = Double.valueOf(rate.getText().toString());
                    double unitsValue = Double.valueOf(units.getText().toString());
                    amount.setText(String.valueOf(rateValue * unitsValue));
                }
                else{
                    amount.setText("Amount");
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idInt = Integer.valueOf(id.getText().toString());
                String name = all_buyers.getText().toString();
                String unitsValue = units.getText().toString();
                String amountValue = amount.getText().toString();

                if(!dbHelper.addProductSale(idInt, name, product, unitsValue, amountValue))
                    toast(ProductSaleActivity.this, "Unable to add data");
                else {
                    startActivity(new Intent(ProductSaleActivity.this, ProductSaleActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProductSaleActivity.this, DashboardActivity.class));
        finish();
    }
}
