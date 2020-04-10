package com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.juicebox.dairydaily.Models.ReceiveCashListModel;
import com.juicebox.dairydaily.MyAdapters.ReceiveCashAdapter;
import com.juicebox.dairydaily.MyAdapters.ReceiveCashListAdapter;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.R;

import java.util.ArrayList;

public class ReceiveCashList extends AppCompatActivity {
    public static RecyclerView recyclerView;
    public static ReceiveCashListAdapter adapter;
    public static ArrayList<ReceiveCashListModel> list;
    DbHelper helper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_cash_list);
        Log.d("ReceiveCashList", "Hello");
        //getSupportActionBar().setTitle("Receive Cash");
        helper = new DbHelper(this);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = helper.getReceiveCashList();
        adapter = new ReceiveCashListAdapter(this, list);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReceiveCashActivity.remain = 0;
        ReceiveCashActivity.creditTotal = 0;
        ReceiveCashActivity.debitTotal = 0;
    }
}
