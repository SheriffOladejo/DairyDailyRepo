package com.juicebox.dairydaily.UI.Dashboard.ViewReport;

import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.juicebox.dairydaily.Models.CustomerReportModel;
import com.juicebox.dairydaily.MyAdapters.CustomerReportAdapter;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.SpinnerItem;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.BluetoothConnectionService;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.UsersListActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static com.juicebox.dairydaily.Others.UtilityMethods.getEndDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.getStartDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;
import static com.juicebox.dairydaily.Others.UtilityMethods.useSnackBar;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CustomerReportActivity  extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private static final String TAG = "CustomerReportActivity";
    private final int REQUEST_READ_PHONE_STATE = 1;

    // Bluetooth variables
    public static BluetoothAdapter bluetoothAdapter;
    public static BluetoothSocket bluetoothSocket;
    public static BluetoothDevice bluetoothDevice;
    public static BluetoothConnectionService bluetoothConnectionService;
    static OutputStream outputStream;
    static InputStream inputStream;
    static Thread thread;
    public static Thread printThread;
    public static ArrayList<SpinnerItem> deviceList;
    static Set<BluetoothDevice> pairedDevice;
    static volatile boolean stopWorker;

    static ConstraintLayout scrollview;
    ArrayList<CustomerReportModel> list;

    ImageView start_date_image;
    ImageView end_date_image;
    TextView start_date_text_view, end_date_text_view;

    Button print;

    TextView weightTotal;
    TextView amountTotal;

    double totalWeight = 0, totalAmount = 0, averageFat = 0, averageSnf = 0;

    String name;
    int passedId;

    DbHelper dbHelper = new DbHelper(this);

    EditText id;
    TextView all_sellers;
    Button go;
    int idInt;

    String startDate = "";
    String endDate = "";

    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_report);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Customer Report");

        weightTotal = findViewById(R.id.weightTotal);
        scrollview = findViewById(R.id.constraintlayout);
        amountTotal = findViewById(R.id.amountTotal);
        id = findViewById(R.id.id);
        all_sellers = findViewById(R.id.sellers);
        go = findViewById(R.id.go);
        print = findViewById(R.id.print);
        start_date_image = findViewById(R.id.start_date_image_view);
        end_date_image = findViewById(R.id.end_date_image_view);
        start_date_text_view = findViewById(R.id.start_date_text_view);
        end_date_text_view = findViewById(R.id.end_date_text_view);
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        findViewById(R.id.send_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_number = dbHelper.getSellerPhone_Number(idInt);
                String toSend = "TOTAL AMOUNT: " + truncate(totalAmount)+"Rs";
                toSend += "\nTOTAL WEIGHT: " + truncate(totalWeight)+"Ltr";
                toSend += "\nAVERAGE SNF: " + truncate(averageSnf);
                toSend += "\nAVERAGE FAT: " + truncate(averageFat)+"%";
                toSend += "\n   DAIRYDAILY APP";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.putExtra("address", phone_number);
                i.putExtra("sms_body", toSend);
                i.setType("vnd.android-dir/mms-sms");
                startActivity(i);
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(list!=null){
                    Collections.reverse(list);
                    String line = "--------------------------------";
                    Date dateIntermediate = new Date();
                    String datee = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
                    String toPrint = "\n\n"+datee+"\nID | " + idInt + " \nNAME | " + all_sellers.getText().toString() + "\n";
                    toPrint += startDate + " TO " + endDate + "\n"+line +  "\n DATE | " + "FAT/SNF |" +"WEIGHT|" + "AMOUNT\n";
                    toPrint += line + "\n";


                    for(CustomerReportModel object : list){
                        String date = object.getDate();
                        String shift = object.getShift();
                        shift = shift.equals("Morning") ? "M" : "E";
                        String snf = truncate(Double.valueOf(object.getSnf()));
                        String fat = truncate(Double.valueOf(object.getFat()));
                        String amount = object.getAmount();
                        String weight = object.getWeight();
                        totalAmount+=Double.valueOf(amount);
                        totalWeight+=Double.valueOf(weight);
                        averageFat +=Double.valueOf(fat);
                        averageSnf += Double.valueOf(snf);
                        toPrint += date.substring(8,10) +" - " +shift + "|" +fat + "-" + snf + "|" + weight + " |" + amount + "| \n";
                    }
                    toPrint += line + "\n";
                    toPrint += "TOTAL AMOUNT: " + truncate(totalAmount) +"Rs\n";
                    toPrint += "TOTAL WEIGHT: " + truncate(totalWeight) + "Ltr\n";
                    averageFat /= list.size();
                    averageSnf /= list.size();
                    toPrint += "AVERAGE FAT: " + truncate(averageFat) + "%\n";
                    toPrint += line + "\n";
                    toPrint += "\n    DAIRYDAILY APP\n\n\n";

                    Log.d(TAG, "toPrint: " + toPrint);
                    byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());
                    if(DashboardActivity.bluetoothAdapter != null) {
                        if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                            try {
                                DashboardActivity.bluetoothConnectionService.write(mybyte);
                                Log.d(TAG, "onOptionsSelected: Printing with DashboardActivity BT");
                            } catch (Exception e) {
                                Log.d(TAG, "onOptionsSelected: Unable to print");
                                toast(CustomerReportActivity.this, "Unable to print");
                            }
                        } else {
                            toast(CustomerReportActivity.this, "Printer is not connected");
                        }
                    }
                    else{
                        toast(CustomerReportActivity.this, "Bluetooth is off");
                    }
                }
            }
        });


        final DatePickerDialog startDatePickerDialog = new DatePickerDialog(this), endDatePickerDialog = new DatePickerDialog(this);

        startDate = getStartDate();
        endDate = getEndDate();

        start_date_text_view.setText(startDate);
        end_date_text_view.setText(endDate);

        start_date_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog.show();
            }
        });
        end_date_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDatePickerDialog.show();
            }
        });

        startDatePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if(String.valueOf(month).length() == 1){
                    if(String.valueOf(dayOfMonth).length() == 1){
                        startDate = year + "-0" + (month+1) + "-" + "0"+dayOfMonth;
                        start_date_text_view.setText(startDate);
                    }
                    else{
                        startDate = year + "-0" + (month+1) + "-" + dayOfMonth;
                        start_date_text_view.setText(startDate);
                    }
                }
                else{
                    startDate = year + "-" + (month+1) + "-" + dayOfMonth;
                    start_date_text_view.setText(startDate);
                }
            }
        });

        endDatePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if(String.valueOf(month).length() == 1){
                    if(String.valueOf(dayOfMonth).length() == 1){
                        endDate = year+ "-0" + (month+1) + "-" + "0"+dayOfMonth;
                        end_date_text_view.setText(endDate);
                    }
                    else{
                        endDate = year + "-0" + (month+1) + "-" + dayOfMonth;
                        end_date_text_view.setText(endDate);
                    }
                }
                else{
                    endDate = year + "-" + (month+1) + "-" + dayOfMonth;
                    end_date_text_view.setText(endDate);
                }
            }
        });


        name = getIntent().getStringExtra("name");
        passedId = getIntent().getIntExtra("id", -1);
        if(passedId != -1){
            id.setText(String.valueOf(passedId));
        }
        try{
            if(!name.equals("")){
                all_sellers.setText(name);
            }
        }
        catch(Exception e){}

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double amountTotalDouble = 0;
                double weightTotalDouble = 0;
                Log.d(TAG, "Start Date: " + startDate);
                Log.d(TAG, "End Date: " + endDate);
                try{
                    idInt = Integer.valueOf(id.getText().toString());
                    if(startDate.isEmpty() || endDate.isEmpty() || String.valueOf(idInt).isEmpty()){

                    }
                    else{
                        list = dbHelper.getCustomerReport(idInt, startDate, endDate);
                        CustomerReportAdapter adapter = new CustomerReportAdapter(CustomerReportActivity.this, list);
                        recyclerView.setAdapter(adapter);
                        for(CustomerReportModel model : list){
                            amountTotalDouble += Double.valueOf(model.getAmount());
                            weightTotalDouble += Double.valueOf(model.getWeight());
                        }
                        amountTotal.setText(truncate(amountTotalDouble)+"Rs");
                        weightTotal.setText(String.valueOf(truncate(weightTotalDouble))+"Ltr");
                    }
                }
                catch(Exception e){

                }
            }
        });

        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")){
                    try{
                        int id = Integer.valueOf(s.toString());
                        String name = dbHelper.getSellerName(id);
                        if(name.equals("")){
                            all_sellers.setText("Not Found");
                        }
                        else{
                            all_sellers.setText(name);
                        }
                    }
                    catch(Exception e){
                        all_sellers.setText("Not Found");
                    }
                }
                else{
                    all_sellers.setText("All Sellers");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        all_sellers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerReportActivity.this, UsersListActivity.class).putExtra("From", "CustomerReportActivity"));
                finish();
            }
        });

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
                        Toast.makeText(CustomerReportActivity.this, "Bluetooth OFF", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onReceive: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: STATE ON");
                        Toast.makeText(CustomerReportActivity.this, "Bluetooth ON", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(CustomerReportActivity.this, "Devices connected", Toast.LENGTH_SHORT).show();
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
            startActivity(discoverIntent);

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
            useSnackBar(e.getMessage(), scrollview);
        }
    }

    // Printing text to bluetooth printer
    public static void printData(final String message) throws IOException {
        printThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = message.getBytes();
                try {
                    outputStream.write(bytes);
//                    outputStream.close();
//                    printThread.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        printThread.start();
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
        startActivity(new Intent(CustomerReportActivity.this, ViewReportActivity.class));
        finish();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}
