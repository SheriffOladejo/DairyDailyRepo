package com.example.dairydaily.UI.Dashboard.SellMilk;

import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.dairydaily.Others.SelectPrinterDialog2;
import com.example.dairydaily.Others.SpinnerItem;
import com.example.dairydaily.R;
import com.example.dairydaily.UI.Dashboard.DashboardActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import static com.example.dairydaily.Others.UtilityMethods.useSnackBar;

public class SellMilkActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    // Bluetooth variables
    BluetoothAdapter bluetoothAdapter;
    static BluetoothSocket bluetoothSocket;
    static BluetoothDevice bluetoothDevice;
    static OutputStream outputStream;
    static InputStream inputStream;
    static Thread thread;

    private Button proceed;
    static ScrollView scrollview;

    String date;

    public static ArrayList<SpinnerItem> deviceList;
    static Set<BluetoothDevice> pairedDevice;

    static byte[] readBuffer;
    static int readBufferPosition;
    static volatile boolean stopWorker;

    private static final String TAG = "SellMilkActivity";

    // Date picker dialog pops up on calendar image click
    DatePickerDialog datePickerDialog;
    TextView dateView;
    Switch online_switch, print_switch;

    private String am_pm;

    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    // The layout that hides the rate selection
    LinearLayout rateByFatView;
    boolean fixed_price = false;

    RadioGroup radioGroup;
    RadioButton fixed_price_button;
    RadioButton rateByFat_button;
    private Switch morning_switch;
    private Switch evening_switch;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_milk);

        getSupportActionBar().setTitle("Sell Milk");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        online_switch = findViewById(R.id.online_switch);
        print_switch = findViewById(R.id.print_switch);
        morning_switch = findViewById(R.id.morning_switch);
        evening_switch = findViewById(R.id.evening_switch);
        scrollview = findViewById(R.id.scrollview);
        proceed = findViewById(R.id.proceed);
        fixed_price_button = findViewById(R.id.fixed_price);
        radioGroup = findViewById(R.id.radio_group);
        rateByFat_button = findViewById(R.id.rate_by_fat);
        rateByFatView = findViewById(R.id.rate_by_fat_view);
        ImageView calendar = findViewById(R.id.calendar_image);
        dateView = findViewById(R.id.date_textview);

        // Check here if time is AM or PM and update switches accordingly
        date = SimpleDateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        dateView.setText(date);

        Calendar dateTime = Calendar.getInstance();
        if(dateTime.get(Calendar.AM_PM) == Calendar.AM){
            am_pm = "AM";
        }
        else if(dateTime.get(Calendar.AM_PM) == Calendar.PM){
            am_pm = "PM";
        }
        if(am_pm.equals("AM")){
            morning_switch.setChecked(true);
        }
        if(am_pm.equals("PM")){
            evening_switch.setChecked(true);
        }

        fixed_price_button.setChecked(true);

        // Initially set the rateByFatView to be gone
        rateByFatView.setVisibility(View.GONE);

        // Check if user is connected to internet and update online switch accordingly
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean is3g = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        // check if mobile data or wifi
        if(!is3g || !isWifi){
            online_switch.setChecked(false);
        }
        else{
            online_switch.setChecked(true);
        }

        // Check for date picker dialog permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            datePickerDialog = new DatePickerDialog(this, SellMilkActivity.this, 2019, 11, 24);
        }

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        // Set an onClickListener for printer switch so that dialog pops up
        print_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    findBluetoothDevice();
                    SelectPrinterDialog2 dialog = new SelectPrinterDialog2(SellMilkActivity.this);
                    dialog.show();
                }
            }
        });


        rateByFat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateByFatView.setVisibility(View.VISIBLE);
                fixed_price = false;
            }
        });

        fixed_price_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateByFatView.setVisibility(View.GONE);
                fixed_price = true;
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!morning_switch.isChecked() && !evening_switch.isChecked()){
                    useSnackBar("Select Shift", scrollview);
                }
                else{
                    Intent intent = new Intent(SellMilkActivity.this, MilkSaleEntryActivity.class);
                    intent.putExtra("Shift", morning_switch.isChecked() ? "AM" : "PM");
                    intent.putExtra("Date", date);
                    startActivity(intent);
                }
            }
        });
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

    void findBluetoothDevice(){
        try{
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter == null){
            }
            if(!bluetoothAdapter.isEnabled()){
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
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

    // Connect to printer, could be any other bluetooth device too
    public static void connect(String deviceName){
        for(BluetoothDevice device: pairedDevice){
            if(device.getName().equals(deviceName)){
                bluetoothDevice = device;
                useSnackBar("Connected " + deviceName, scrollview);
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
            useSnackBar("openConnection: " +e.getMessage(), scrollview);
        }
    }

    // Method that listens for data sent from other device
    static void beginToListenForData(){
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
                            Log.d(TAG, "run: " + e.getMessage());
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
    public static void printData(String message) throws IOException {
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

    // Receive date parameters here(month, day and year)
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String mMonth = "";
        // switch statement to convert number to month String
        switch (month){
            case 0:
                mMonth = "Jan";
                break;
            case 1:
                mMonth = "Feb";
                break;
            case 2:
                mMonth = "Mar";
                break;
            case 3:
                mMonth = "Apr";
                break;
            case 4:
                mMonth = "May";
                break;
            case 5:
                mMonth = "Jun";
                break;
            case 6:
                mMonth = "Jul";
                break;
            case 7:
                mMonth= "August";
                break;
            case 8:
                mMonth = "Sep";
                break;
            case 9:
                mMonth = "Oct";
                break;
            case 10:
                mMonth = "Nov";
                break;
            case 11:
                mMonth = "Dec";
                break;
        }
        dateView.setText(mMonth + " " + dayOfMonth + ", " + year);
        date = mMonth + " " + dayOfMonth + ", " +year;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SellMilkActivity.this, DashboardActivity.class));
    }
}
