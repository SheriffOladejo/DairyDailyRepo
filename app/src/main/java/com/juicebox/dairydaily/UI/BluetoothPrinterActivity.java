package com.juicebox.dairydaily.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.Others.SpinnerAdapter;
import com.juicebox.dairydaily.Others.SpinnerItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothPrinterActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;
    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    public static ArrayList<SpinnerItem> deviceList;

    // A set of all bluetooth devices ever paired to the device
    Set<BluetoothDevice> pairedDevice;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    TextView lblPrinterName;
    EditText text;
    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_printer);

        Button btnConnect = findViewById(R.id.btnConnect);
        Button btnDisconnect = findViewById(R.id.btnDisconnect);
        Button print = findViewById(R.id.btnPrint);
        Button display = findViewById(R.id.btnDisplay);
        text = findViewById(R.id.txtText);
        listView = findViewById(R.id.listView);
        lblPrinterName = findViewById(R.id.lblPrinterName);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SpinnerItem item = (SpinnerItem) parent.getItemAtPosition(position);
                String name = item.getDevice_name();
                connect(name);
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    findBluetoothDevice();
                    openBluetoothPrinter();
                }
                catch(Exception e){

                }
            }
        });
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bluetoothAdapter == null){
                    Toast.makeText(BluetoothPrinterActivity.this, "Turn on bluetooth", Toast.LENGTH_SHORT).show();
                }
                SpinnerAdapter adapter = new SpinnerAdapter(BluetoothPrinterActivity.this,deviceList);
                listView.setAdapter(adapter);
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    disconnectBT();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    printData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    void findBluetoothDevice(){
        try{
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter == null){
                lblPrinterName.setText("No Bluetooth adapter found");
            }
            if(!bluetoothAdapter.isEnabled()){
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            // Here is where we receive all bonded devices
            // We further want to retrieve all the names and add them to a spinner so user can select printer device manually
            pairedDevice = bluetoothAdapter.getBondedDevices();
            // Add all paired devices to a list
            deviceList = new ArrayList<>();
            if(pairedDevice.size() > 0){
                for(BluetoothDevice pairedDev : pairedDevice){
                    deviceList.add(new SpinnerItem(pairedDev.getName()));
                }
            }
        }
        catch(Exception e){
            Toast.makeText(BluetoothPrinterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Connect to printer, could be any other bluetooth device too
    void connect(String deviceName){
        for(BluetoothDevice device: pairedDevice){
            if(device.getName().equals(deviceName)){
                bluetoothDevice = device;
                lblPrinterName.setText("Bluetooth device attached: " + device.getName());
                break;
            }
        }
        try {
            openBluetoothPrinter();
        } catch (IOException e) {
            Toast.makeText(BluetoothPrinterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //open Bluetooth printer
    public void openBluetoothPrinter() throws IOException{
        try{
            UUID uuidString = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidString);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();

            beginToListenForData();
        }
        catch (Exception e){

        }
    }

    void beginToListenForData(){
        try{
            final Handler handler = new Handler();
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition=0;
            readBuffer = new byte[1024];

            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!Thread.currentThread().isInterrupted() && !stopWorker){
                        try{
                            int byteAvailable = inputStream.available();
                            if(byteAvailable>0){
                                byte[] packetByte = new byte[byteAvailable];
                                inputStream.read(packetByte);

                                for(int i=0; i<byteAvailable; i++){
                                    byte b = packetByte[i];
                                    if(b==delimiter){
                                        byte[] encodedByte = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer,0,
                                                encodedByte, 0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte, "US-ASCII");
                                        readBufferPosition =0;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                lblPrinterName.setText(data);
                                            }
                                        });
                                    }
                                    else{
                                        readBuffer[readBufferPosition++]=b;
                                    }
                                }
                            }
                        }
                        catch(Exception e){
                            stopWorker = true;
                        }
                    }
                }
            });
            thread.start();
        }
        catch(Exception e){

        }
    }

    // Printing text to bluetooth printer
    void printData() throws IOException{
        try{
            String msg = text.getText().toString();
            msg +="\n";
            outputStream.write(msg.getBytes());
            lblPrinterName.setText("Printing Text...");
        }
        catch(Exception e){

        }
    }

    // Disconnect printer
    void disconnectBT() throws IOException{
        try{
            stopWorker = true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
            lblPrinterName.setText("Printer Disconnected");
        }
        catch(Exception e){

        }
    }

}
