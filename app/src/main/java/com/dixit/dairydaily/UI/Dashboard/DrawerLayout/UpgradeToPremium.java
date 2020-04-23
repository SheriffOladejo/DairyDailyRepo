package com.dixit.dairydaily.UI.Dashboard.DrawerLayout;

import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.Others.Prevalent;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.CheckSum;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.paperdb.Paper;

public class UpgradeToPremium extends AppCompatActivity {

    TextView expiry_date, price, price1, price2;
    DbHelper helper;

    boolean isInternetConnected = false;

    public void checkInternetConnect(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if(manager != null){
            boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
            if(is3g)
                isInternetConnected = true;
            boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
            if(isWifi)
                isInternetConnected = true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upgrade_premium);
        expiry_date = findViewById(R.id.expiry_date);

        checkInternetConnect();

        Paper.init(this);

        String orderId = String.valueOf(System.currentTimeMillis());
        String customerId = String.valueOf(System.currentTimeMillis());
        price = findViewById(R.id.price);
        price1 = findViewById(R.id.price1);
        price2 = findViewById(R.id.price2);

        price.setText(""+Prevalent.starter);
        price1.setText(""+Prevalent.spark);
        price2.setText(""+Prevalent.enterprise);

        helper = new DbHelper(this);

        String date = "";

        Cursor data = helper.getExpiryDate();
        if(data.getCount() != 0){
            while(data.moveToNext()){
                date = data.getString(data.getColumnIndex("Date"));
            }
        }
        String format;
        try{
            DateFormat df = new DateFormat();
            format = df.format("yyyy-MM-dd", new Date(Long.valueOf(date))).toString();
        }
        catch(Exception e){
            format = new SimpleDateFormat("YYYY-MM-dd").format(new Date(Long.valueOf(date)));
        }

        expiry_date.setText("Expiry Date: " + format);

        findViewById(R.id.priceButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DashboardActivity.can_pay){
                    if(!isInternetConnected)
                        Toast.makeText(UpgradeToPremium.this, "Connect to the internet", Toast.LENGTH_SHORT).show();
                    else{
                        Intent intent = new Intent(UpgradeToPremium.this, CheckSum.class);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("customerId", customerId);
                        intent.putExtra("price", ""+Prevalent.starter);
                        startActivity(intent);
                    }
                }

            }
        });
        findViewById(R.id.priceButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DashboardActivity.can_pay){
                    if(!isInternetConnected)
                        Toast.makeText(UpgradeToPremium.this, "Connect to the internet", Toast.LENGTH_SHORT).show();
                    else{
                        Intent intent = new Intent(UpgradeToPremium.this, CheckSum.class);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("customerId", customerId);
                        intent.putExtra("price", ""+Prevalent.spark);
                        startActivity(intent);
                    }
                }

            }
        });
        findViewById(R.id.priceButton3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DashboardActivity.can_pay){
                    if(!isInternetConnected)
                        Toast.makeText(UpgradeToPremium.this, "Connect to the internet", Toast.LENGTH_SHORT).show();
                    else{
                        Intent intent = new Intent(UpgradeToPremium.this, CheckSum.class);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("customerId", customerId);
                        intent.putExtra("price", ""+Prevalent.enterprise);
                        startActivity(intent);
                    }
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.close, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.close){
            if(DashboardActivity.showReminder){

            }
            finish();
            return true;
        }
        else{
            return false;
        }
    }
}
