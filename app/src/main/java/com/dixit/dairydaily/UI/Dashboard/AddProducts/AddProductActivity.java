package com.dixit.dairydaily.UI.Dashboard.AddProducts;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.InitDrawerBoard;
import com.google.android.material.navigation.NavigationView;
import com.dixit.dairydaily.MyAdapters.AddProductAdapter;
import com.dixit.dairydaily.Others.BackupHandler;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.R;

import java.util.ArrayList;

import static com.dixit.dairydaily.Others.UtilityMethods.toast;

public class AddProductActivity extends InitDrawerBoard {

    public static EditText product_name, rate;
    public static String id;
    public static boolean want_to_update = false;
    Button save;
    public static RecyclerView recyclerView;
    public static AddProductAdapter adapter;

    DbHelper dbHelper = new DbHelper(this);
    public static ArrayList list;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        getSupportActionBar().setTitle("Add Product");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        product_name = findViewById(R.id.product_name);
        rate = findViewById(R.id.rate);
        save = findViewById(R.id.save);
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initDrawer();

        list = dbHelper.getProducts();
        adapter = new AddProductAdapter(this, list);
        recyclerView.setAdapter(adapter);

        recyclerView.requestFocus();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = product_name.getText().toString();
                String Rate = rate.getText().toString();

                if(productName.isEmpty() || Rate.isEmpty()){
                    toast(AddProductActivity.this, "Field should be filled");
                }
                if(want_to_update){
                    dbHelper.updateProducts(id, productName, Rate);
                    want_to_update = false;
                    new BackupHandler(AddProductActivity.this);
                    list = dbHelper.getProducts();
                    adapter = new AddProductAdapter(AddProductActivity.this, list);
                    recyclerView.setAdapter(adapter);
                    product_name.setText("");
                    rate.setText("");
                }
                else{
                    if(!dbHelper.addProduct(productName, Rate))
                        toast(AddProductActivity.this, "Unable to add product");
                    else{
                        new BackupHandler(AddProductActivity.this);
                        list = dbHelper.getProducts();
                        adapter = new AddProductAdapter(AddProductActivity.this, list);
                        recyclerView.setAdapter(adapter);
                        product_name.setText("");
                        rate.setText("");
                    }
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
        finish();
    }
}
