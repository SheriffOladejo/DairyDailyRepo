package com.example.dixitlamba.UI.Dashboard.BuyMilk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.dixitlamba.R;

import java.io.IOException;

import static com.example.dixitlamba.UI.Dashboard.BuyMilk.BuyMilkActivity.printData;

public class MilkBuyEntryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.milk_buy_entry);

        getSupportActionBar().setTitle("Entry");

        Button save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    printData("Hello World");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
