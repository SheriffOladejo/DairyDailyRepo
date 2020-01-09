package com.juicebox.dairydaily.UI.Dashboard.AddProducts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;

public class AddProductActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        getSupportActionBar().setTitle("Add Product");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddProductActivity.this, DashboardActivity.class));
        finish();
    }
}
