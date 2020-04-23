package com.dixit.dairydaily.UI.Dashboard;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.dixit.dairydaily.Others.SpinnerItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static com.dixit.dairydaily.Others.UtilityMethods.checkBTPermissions;

public class BluetoothClass {
    private static final String TAG = "BluetoothClass";

    // Bluetooth variables
    static BluetoothAdapter bluetoothAdapter;
    static BluetoothSocket bluetoothSocket;
    static BluetoothDevice bluetoothDevice;
    static OutputStream outputStream;
    static InputStream inputStream;
    ArrayList<SpinnerItem> deviceList;
    static Set<BluetoothDevice> pairedDevice;
    static volatile boolean stopWorker;

    Activity activity;

    public BluetoothClass(ArrayList<SpinnerItem> deviceList, Activity activity){
        this.activity = activity;
        this.deviceList = deviceList;
    }

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND");

            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(new SpinnerItem(device.getName()));
                String name = device.getName();
                Log.d(TAG, "Bluetooth device found" + name + "" + device.getAddress());
            }
        }
    };

    public void findBluetoothDevice(){
        try{
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter == null){
            }
            if(!bluetoothAdapter.isEnabled()){
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBluetooth, 0);
            }
            // Here is where we receive all bonded devices
            // We further want to retrieve all the names and add them to a spinner so user can select printer device manually
            bluetoothAdapter.startDiscovery();
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

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startDiscovery(View view) {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices");

        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "Cancelling discovery");

            //check BT permissions in manifest
            checkBTPermissions(activity);

            bluetoothAdapter.startDiscovery();
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            activity.registerReceiver(mBroadcastReceiver3, intentFilter);
        }
        if(!bluetoothAdapter.isDiscovering()){
            checkBTPermissions(activity);

            bluetoothAdapter.startDiscovery();
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            activity.registerReceiver(mBroadcastReceiver3, intentFilter);
        }
    }

    // Connect to printer, could be any other bluetooth device too
    public static void connect(String deviceName){
        for(BluetoothDevice device: pairedDevice){
            if(device.getName().equals(deviceName)){
                bluetoothDevice = device;
                //useSnackBar("Connected " + deviceName, scrollview);
                openConnection();
            }
        }
    }

    // Attempt to create a connection for communication between connected devices
    public static void openConnection() {
        try{
            UUID uuidString = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidString);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();

            //beginToListenForData();
        }
        catch (Exception e){
            Log.e(TAG, "openConnection: " +e.getMessage());
            //useSnackBar("openConnection: " +e.getMessage(), scrollview);
        }
    }

    // Printing text to bluetooth printer
    public static void printData(String message) throws IOException {
        try{
            byte[] bytes = message.getBytes();
            outputStream.write(bytes);
            //outputStream.close();
        }
        catch(Exception e){
            //useSnackBar("printData: " + e.getMessage(), scrollview);
            Log.d(TAG, "printData: " + e.getMessage() + "\n " + e.getCause());
            outputStream.close();
        }
    }

    // Disconnect printer
    void disconnectBT() throws IOException{
        try{
            stopWorker = true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
        }
        catch(Exception e){

        }
    }
}
