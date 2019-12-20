package com.example.dixitlamba.Others;

// Dialog that pops up for user to select slip printer

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dixitlamba.R;

import static com.example.dixitlamba.UI.BuyMilkActivity.deviceList;

public class SelectPrinterDialog extends Dialog implements View.OnClickListener {

    private Context context;
    public Dialog d;
    private Button connect;
    private SpinnerAdapter adapter;
    String clickedDeviceName;
    SpinnerItem clickedItem;
    BluetoothConnectionService bluetoothConnectionService;

    public SelectPrinterDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.my_printer_dialog);
        connect = findViewById(R.id.connect);
        connect.setOnClickListener(this);

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

                dismiss();
                break;
            default:
                dismiss();
        }
    }
}
