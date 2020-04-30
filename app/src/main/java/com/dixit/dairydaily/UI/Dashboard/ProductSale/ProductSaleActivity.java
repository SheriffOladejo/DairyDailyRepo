package com.dixit.dairydaily.UI.Dashboard.ProductSale;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.InitDrawerBoard;
import com.google.android.material.navigation.NavigationView;
import com.dixit.dairydaily.Models.AddProductModel;
import com.dixit.dairydaily.MyAdapters.ProductSaleAdapter;
import com.dixit.dairydaily.MyAdapters.ProductsAdapter;
import com.dixit.dairydaily.Others.BackupHandler;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.Others.Logout;
import com.dixit.dairydaily.Others.Prevalent;
import com.dixit.dairydaily.Others.WarningDialog;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.DeleteHistory;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.MilkHistoryActivity;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.ProfileActivity;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.UpgradeToPremium;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.ViewAllEntryActivity;
import com.dixit.dairydaily.UI.UsersListActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.paperdb.Paper;

import static com.dixit.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.dixit.dairydaily.Others.UtilityMethods.toast;

public class ProductSaleActivity extends AppCompatActivity {

    TextView all_buyers;
    Spinner all_products;
    AddProductModel spinnerItem;
    TextView amount;
    EditText id, units, rate;
    Button save;
    String product;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

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

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                try{
                    if(!s.toString().isEmpty()){
                        double rateValue = Double.valueOf(rate.getText().toString());
                        double unitsValue = Double.valueOf(units.getText().toString());
                        amount.setText(String.valueOf(rateValue * unitsValue));
                    }
                    else{
                        amount.setText("Amount");
                    }
                }
                catch (Exception e){}
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
                try {
                    if (!s.toString().equals("")) {
                        int id = Integer.valueOf(s.toString());
                        String name = dbHelper.getBuyerName(id);
                        all_buyers.setText(name);
                    } else {
                        all_buyers.setText("All Buyers");
                    }
                }
                catch (Exception e){}
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProductSaleActivity.this, DashboardActivity.class));
        finish();
    }
}
