package com.juicebox.dairydaily.UI.Dashboard.BuyMilk;

import android.Manifest;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.juicebox.dairydaily.UI.BluetoothConnectionService;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.Others.SelectPrinterDialog;
import com.juicebox.dairydaily.Others.SpinnerItem;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.Dashboard.SellMilk.MilkSaleEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.SellMilk.SellMilkActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport.BuyerRegisterActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import io.paperdb.Paper;

import static com.juicebox.dairydaily.Others.UtilityMethods.useSnackBar;

//Activity for buying milk

public class BuyMilkActivity extends AppCompatActivity {

    // Bluetooth variables
    static BluetoothAdapter bluetoothAdapter;
    static BluetoothSocket bluetoothSocket;
    public static BluetoothDevice bluetoothDevice;
    public static BluetoothConnectionService bluetoothConnectionService;
    private final int REQUEST_READ_PHONE_STATE = 1;
    static OutputStream outputStream;
    static InputStream inputStream;
    static Thread thread;

    private Button proceed;

    public static ArrayList<SpinnerItem> deviceList;
    static Set<BluetoothDevice> pairedDevice;

    static byte[] readBuffer;
    static int readBufferPosition;
    static volatile boolean stopWorker;

    static ScrollView scrollview;

    private static final String TAG = "BuyMilkActivity";

    // Date picker dialog pops up on calendar image click
    DatePickerDialog datePickerDialog;
    TextView dateView;
    Switch online_switch, print_switch, morning_switch, evening_switch;

    private String date;
    private String am_pm;

    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_milk_activity);

        dateView = findViewById(R.id.date_textview);
        online_switch = findViewById(R.id.online_switch);
        //print_switch = findViewById(R.id.print_switch);
        morning_switch = findViewById(R.id.morning_switch);
        evening_switch = findViewById(R.id.evening_switch);
        ImageView calendarImage = findViewById(R.id.calendar_image);
        scrollview = findViewById(R.id.scrollview);
        proceed = findViewById(R.id.proceed);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        morning_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(evening_switch.isChecked())
                    evening_switch.setChecked(false);
            }
        });

        evening_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(morning_switch.isChecked())
                    morning_switch.setChecked(false);
            }
        });

        Paper.init(this);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Buy Milk");

        // Check for date picker dialog permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            datePickerDialog = new DatePickerDialog(this);
        }

        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                if(String.valueOf(month).length() == 1){
                    if(String.valueOf(dayOfMonth).length() == 1){
                        date = year + "-0" + (month+1) + "-" + "0"+dayOfMonth;
                        dateView.setText(date);
                    }
                    else{
                        date = year + "-0" + (month+1) + "-" + dayOfMonth;
                        dateView.setText(date);
                    }
                }
                else{
                    date = year + "-" + (month+1) + "-" + dayOfMonth;
                    dateView.setText(date);
                }
            }
        });


        Date dateIntermediate = new Date();
        date = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
        dateView.setText(date);

        // Check time whether its morning or evening and check shift switches accordingly
        am_pm = "";
        Calendar dateTime = Calendar.getInstance();
        if(dateTime.get(Calendar.AM_PM) == Calendar.AM){
            am_pm = "Morning";
        }
        else if(dateTime.get(Calendar.AM_PM) == Calendar.PM){
            am_pm = "Evening";
        }
        if(am_pm.equals("Morning")){
            morning_switch.setChecked(true);
        }
        if(am_pm.equals("Evening")){
            evening_switch.setChecked(true);
        }

        checkInternetConnect();

        calendarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!morning_switch.isChecked() && !evening_switch.isChecked()){
                    useSnackBar("Select Shift", scrollview);
                }
                else{
                    Intent intent = new Intent(BuyMilkActivity.this, MilkBuyEntryActivity.class);
                    intent.putExtra("Shift", morning_switch.isChecked() ? "Morning" : "Evening");
                    intent.putExtra("Date", date);
                    startActivity(intent);
                }
            }
        });

        //Broadcasts when bond state changes(i.e pairing)
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, intentFilter);

        int permissionChceck = ContextCompat.checkSelfPermission(BuyMilkActivity.this, Manifest.permission.READ_PHONE_STATE);
        if(permissionChceck != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "permission denied so ");
            ActivityCompat.requestPermissions(BuyMilkActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        }
        else{
            Log.d(TAG, "permission granted so ");
            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        }
    }

    public void checkInternetConnect(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if(manager != null){
            boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
            if(is3g)
                online_switch.setChecked(true);
            boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
            if(isWifi)
                online_switch.setChecked(true);
        }
    }

    //Create a broadcast receiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);
                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(BuyMilkActivity.this, "Bluetooth OFF", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onReceive: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: STATE ON");
                        Toast.makeText(BuyMilkActivity.this, "Bluetooth ON", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "onReceive: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_READ_PHONE_STATE:
                if((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){

                }
                break;
            default:
                break;
        }
    }

    /*
     * Broadcast receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire
     * */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, bluetoothAdapter.ERROR);

                switch(mode){
                    //Device is in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability enabled");
                        break;
                    //Device is not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability enabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability disabled: Not able to receive connections");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }
            }
        }
    };

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

    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases
                //case1: bonded already
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED");
                    bluetoothDevice = mDevice;
                    Toast.makeText(BuyMilkActivity.this, "Devices connected", Toast.LENGTH_SHORT).show();
                }
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDING){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING");
                }
                if(mDevice.getBondState() == BluetoothDevice.BOND_NONE){
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE");
                }
            }
        }
    };

    public void findBluetoothDevice(){
        try{
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter == null){
                useSnackBar("No bluetooth device.", scrollview);
            }
            if(!bluetoothAdapter.isEnabled()){
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }
            // Here is where we receive all bonded devices
            // We further want to retrieve all the names and add them to a spinner so user can select printer device manually

            if(bluetoothAdapter.isDiscovering()){
                bluetoothAdapter.cancelDiscovery();
                bluetoothAdapter.startDiscovery();
                IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadcastReceiver3, intentFilter);

            }
            if(!bluetoothAdapter.isDiscovering()){
                bluetoothAdapter.startDiscovery();
                IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadcastReceiver3, intentFilter);
            }

            pairedDevice = bluetoothAdapter.getBondedDevices();
            // Add all paired devices to a list
            deviceList = new ArrayList<>();
            if(pairedDevice.size() > 0){
                for(BluetoothDevice pairedDev : pairedDevice){
                    deviceList.add(new SpinnerItem(pairedDev.getName()));
                }
            }
            Intent discoverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            //startActivity(discoverIntent);

            IntentFilter intentFilter = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            registerReceiver(mBroadcastReceiver2, intentFilter);
        }
        catch(Exception e){

        }
    }

    // Connect to printer, could be any other bluetooth device too
    public static void connect(String deviceName){
        Log.d(TAG, "connect: " + deviceName);
        for(BluetoothDevice device: pairedDevice){
            if(device.getName().equals(deviceName)){
                bluetoothDevice = device;
                try{
                    Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
                    ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(bluetoothAdapter, null);
                    if (uuids != null){
                        String uuid = uuids[1].getUuid().toString();
                        if(!uuid.equals("")){
                            bluetoothConnectionService.startClient(device, UUID.fromString(uuid));
                            Log.d(TAG, "connect: Connection started");
                            useSnackBar("Connected to " + deviceName + " sucessfully", scrollview);
                        }
                    }
                }
                catch (Exception e){
                    Log.d(TAG, "connect: " + e.getMessage());
                }
            }
        }
    }

    // Attempt to create a connection for communication between connected devices
    public static void openConnection(String passedUuid, BluetoothDevice device) {
        try{
            Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
            ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(bluetoothAdapter, null);
            if (uuids != null){
                String uuid = uuids[1].getUuid().toString();
                if(!uuid.equals("")){
                    UUID uuidString = UUID.fromString(uuid);
                    bluetoothSocket = device.createRfcommSocketToServiceRecord(uuidString);
                    bluetoothSocket.connect();
                    outputStream = bluetoothSocket.getOutputStream();
                    inputStream = bluetoothSocket.getInputStream();
                }
                else{
                    UUID uuidString = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                    bluetoothSocket = device.createRfcommSocketToServiceRecord(uuidString);
                    bluetoothSocket.connect();
                    outputStream = bluetoothSocket.getOutputStream();
                    inputStream = bluetoothSocket.getInputStream();
                }
            }
            else{
                Log.d(TAG, "openConnection: UUIDs not found, be sure to enable bletooth");
            }
        }
        catch(Exception e){
            Log.d(TAG, "openConnection: " + e.getMessage());
        }
    }

    // Printing text to bluetooth printer
    public static void printData(String message) throws IOException{
        try{
            byte[] bytes = message.getBytes();
            outputStream.write(bytes);
            outputStream.close();
        }
        catch(Exception e){
            useSnackBar("printData: " + e.getMessage(), scrollview);
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(BuyMilkActivity.this, DashboardActivity.class));
        finish();
    }
}
