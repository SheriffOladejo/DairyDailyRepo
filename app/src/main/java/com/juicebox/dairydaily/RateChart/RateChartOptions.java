package com.juicebox.dairydaily.RateChart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.juicebox.dairydaily.CowChart.CowSNF;
import com.juicebox.dairydaily.R;

public class RateChartOptions extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snf);

        getSupportActionBar().setTitle("Rate Chart");
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        findViewById(R.id.cow_snf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RateChartOptions.this, CowSNF.class);
                intent.putExtra("Name", "Cow");
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.buffalo_snf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RateChartOptions.this, CowSNF.class);
                intent.putExtra("Name", "Buffalo");
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(RateChartOptions.this, DashboardActivity.class));
        finish();
    }
}
