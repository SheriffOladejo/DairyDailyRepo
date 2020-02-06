package com.juicebox.dairydaily.Others;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.juicebox.dairydaily.MyAdapters.SpinnerAdapter;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.BluetoothConnectionService;
import com.juicebox.dairydaily.UI.Dashboard.SellMilk.SellMilkActivity;


import java.util.ArrayList;

import io.paperdb.Paper;

public class SelectPrinterDialog2  extends Dialog implements View.OnClickListener {
    private Context context;
    private Button connect;
    private ImageView close;
    private SpinnerAdapter adapter;
    ArrayList<SpinnerItem> deviceList;
    String clickedDeviceName;
    SpinnerItem clickedItem;
    BluetoothConnectionService bluetoothConnectionService;

    public SelectPrinterDialog2(Context context, ArrayList<SpinnerItem> deviceList) {
        super(context);
        this.context = context;
        this.deviceList = deviceList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.my_printer_dialog);
        connect = findViewById(R.id.connect);
        close = findViewById(R.id.close);
        close.setOnClickListener(this);
        connect.setOnClickListener(this);

        Paper.init(context);

        // Initialize spinner object
        Spinner spinner = findViewById(R.id.spinner);

        final TextView textView = findViewById(R.id.device_text);

        adapter = new SpinnerAdapter(context, deviceList);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clickedItem =(SpinnerItem) parent.getItemAtPosition(position);
                clickedDeviceName = clickedItem.getDevice_name();
                textView.setText(clickedDeviceName);
                Paper.book().write(Prevalent.selected_device, clickedDeviceName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.connect:
                // Connect Device here
                SellMilkActivity.connect(clickedDeviceName);
                dismiss();
                break;
            case R.id.close:
                dismiss();
                break;
            default:
                dismiss();
        }
    }
}
