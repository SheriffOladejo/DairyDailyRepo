package com.juicebox.dairydaily.RateChart;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.juicebox.dairydaily.CowChart.CowSNF;
import com.juicebox.dairydaily.Others.BackupHandler;
import com.juicebox.dairydaily.Others.Logout;
import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.Others.WarningDialog;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.DeleteHistory;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.MilkHistoryActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ProfileActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.UpgradeToPremium;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ViewAllEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewReport.ShiftReportActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.paperdb.Paper;

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
