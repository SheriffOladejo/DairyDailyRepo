package com.juicebox.dairydaily.UI.Dashboard.AddProducts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.juicebox.dairydaily.MyAdapters.AddProductAdapter;
import com.juicebox.dairydaily.Others.BackupHandler;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;

import java.util.ArrayList;

import static com.juicebox.dairydaily.Others.UtilityMethods.toast;

public class AddProductActivity extends AppCompatActivity {

    EditText product_name, rate;
    Button save;
    RecyclerView recyclerView;

    DbHelper dbHelper = new DbHelper(this);

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

        ArrayList list = dbHelper.getProducts();
        AddProductAdapter adapter = new AddProductAdapter(this, list);
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
                else{
                    if(!dbHelper.addProduct(productName, Rate))
                        toast(AddProductActivity.this, "Unable to add product");
                    else{
                        new BackupHandler(AddProductActivity.this);
                        startActivity(new Intent(AddProductActivity.this, AddProductActivity.class));
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddProductActivity.this, DashboardActivity.class));
        finish();
    }
}
