package com.example.dixitlamba.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.dixitlamba.R;

public class MilkBuyEntryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.milk_buy_entry);

        getSupportActionBar().setTitle("Entry");
    }
}
