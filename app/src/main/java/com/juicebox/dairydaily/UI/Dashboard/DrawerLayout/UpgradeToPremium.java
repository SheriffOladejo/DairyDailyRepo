package com.juicebox.dairydaily.UI.Dashboard.DrawerLayout;

import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.CheckSum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.paperdb.Paper;

public class UpgradeToPremium extends AppCompatActivity {

    TextView expiry_date;
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

        helper = new DbHelper(this);

        String date = "";

        Cursor data = helper.getExpiryDate();
        if(data.getCount() != 0){
            while(data.moveToNext()){
                date = data.getString(data.getColumnIndex("Date"));
            }
        }
        String format = "";
        try {
            format = new SimpleDateFormat("dd-MM-YYYY").format(new Date(Long.valueOf(date)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        expiry_date.setText("Expiry Date: " + format);

        findViewById(R.id.priceButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isInternetConnected)
                    Toast.makeText(UpgradeToPremium.this, "Connect to the internet", Toast.LENGTH_SHORT).show();
                else{
                    Intent intent = new Intent(UpgradeToPremium.this, CheckSum.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("customerId", customerId);
                    intent.putExtra("price", "149.99");
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.priceButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isInternetConnected)
                    Toast.makeText(UpgradeToPremium.this, "Connect to the internet", Toast.LENGTH_SHORT).show();
                else{
                    Intent intent = new Intent(UpgradeToPremium.this, CheckSum.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("customerId", customerId);
                    intent.putExtra("price", "249.99");
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.priceButton3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isInternetConnected)
                    Toast.makeText(UpgradeToPremium.this, "Connect to the internet", Toast.LENGTH_SHORT).show();
                else{
                    Intent intent = new Intent(UpgradeToPremium.this, CheckSum.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("customerId", customerId);
                    intent.putExtra("price", "449.99");
                    startActivity(intent);
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
            finish();
            return true;
        }
        else{
            return false;
        }
    }
}
