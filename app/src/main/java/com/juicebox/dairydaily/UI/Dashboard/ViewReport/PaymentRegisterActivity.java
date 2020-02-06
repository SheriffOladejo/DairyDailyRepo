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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.juicebox.dairydaily.Models.BuyerRegisterModel;
import com.juicebox.dairydaily.Models.DailyBuyObject;
import com.juicebox.dairydaily.Models.PaymentRegisterModel;
import com.juicebox.dairydaily.Models.ReportByDateModels;
import com.juicebox.dairydaily.MyAdapters.BuyerRegisterAdapter;
import com.juicebox.dairydaily.MyAdapters.PaymentRegisterAdapter;
import com.juicebox.dairydaily.MyAdapters.ShiftReportAdapter;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.Others.SelectPrinterDialog;
import com.juicebox.dairydaily.Others.SpinnerItem;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.BluetoothConnectionService;
import com.juicebox.dairydaily.UI.Dashboard.BuyMilk.BuyMilkActivity;
import com.juicebox.dairydaily.UI.Dashboard.BuyMilk.MilkBuyEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.Dashboard.SellMilk.MilkSaleEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.SellMilk.SellMilkActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport.BuyerRegisterActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport.ViewReportByDateActivity;

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

import static com.juicebox.dairydaily.Others.UtilityMethods.getDateRange;
import static com.juicebox.dairydaily.Others.UtilityMethods.getEndDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.getFirstname;
import static com.juicebox.dairydaily.Others.UtilityMethods.getMonth;
import static com.juicebox.dairydaily.Others.UtilityMethods.getStartDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;
import static com.juicebox.dairydaily.Others.UtilityMethods.useSnackBar;

public class PaymentRegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private final int REQUEST_READ_PHONE_STATE = 1;

    ImageView start_date_image;
    ImageView end_date_image;
    TextView start_date_text_view, end_date_text_view;
    private static BluetoothConnectionService bluetoothConnectionService;
    RadioButton start_morning_radio, start_evening_radio, end_morning_radio, end_evening_radio;
    String startDate, endDate;
    String startShift = "", endShift="";
    ArrayList<PaymentRegisterModel> list;
    public static ArrayList<PaymentRegisterModel> selectedUsers = new ArrayList<>();

    double weightTotal = 0;
    double amountTotal = 0;

    private static final String TAG = "PaymentRegisterActivity";

    // Bluetooth variables
    public static BluetoothAdapter bluetoothAdapter;
    public static BluetoothSocket bluetoothSocket;
    public static BluetoothDevice bluetoothDevice;
    static OutputStream outputStream;
    static InputStream inputStream;
    static Thread thread;
    public static Thread printThread;
    public static ArrayList<SpinnerItem> deviceList;
    static Set<BluetoothDevice> pairedDevice;
    static volatile boolean stopWorker;

    static ConstraintLayout scrollview;

    Button print;
    boolean printed = false;

    DbHelper dbHelper = new DbHelper(this);

    Button go;

    DatePickerDialog startDatePickerDialog;
    DatePickerDialog endDatePickerDialog;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_register);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        Paper.init(this);

        getSupportActionBar().setTitle("Payment Register");
        getSupportActionBar().setHomeButtonEnabled(true);

        start_date_image = findViewById(R.id.start_date_image_view);
        scrollview = findViewById(R.id.constraintlayout);
        print = findViewById(R.id.print);
        end_date_image = findViewById(R.id.end_date_image_view);
        start_date_text_view = findViewById(R.id.start_date_text_view);
        end_date_text_view = findViewById(R.id.end_date_text_view);
        start_morning_radio = findViewById(R.id.start_morning_radio);
        start_evening_radio= findViewById(R.id.start_evening_radio);
        end_morning_radio = findViewById(R.id.end_morning_radio);
        end_evening_radio = findViewById(R.id.end_evening_radio);
        go = findViewById(R.id.go);
        final TextView amount = findViewById(R.id.totalAmount);
        final TextView weight = findViewById(R.id.totalWeight);
        final RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.send_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_number = Paper.book().read(Prevalent.phone_number);
                String toSend = "TOTAL AMOUNT: " + truncate(amountTotal)+"Rs";
                toSend += "\nTOTAL WEIGHT: " + truncate(weightTotal)+"Ltr";
                toSend += "\nThank you for using DairyDaily!";

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

                    Date dateIntermediate = new Date();
                    String date = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
                    String toPrint = "\nDATE | " + date + "\n";
                    toPrint += startDate + " To " + endDate + "\nID| " + "  NAME  |" + "WEIGHT|" + "AMOUNT\n";

                    weightTotal = 0;
                    amountTotal = 0;
                    for(PaymentRegisterModel object : list){
                        int id = object.getId();
                        String name = getFirstname(object.getName());
                        String amount = object.getAmount();
                        String weight = object.getWeight();
                        weightTotal += Double.valueOf(object.getWeight());
                        amountTotal += Double.valueOf(object.getAmount());
                        toPrint += id + "|" +name + " | " + weight + "| " + amount + "| \n";
                    }
                    toPrint += "TOTAL AMOUNT: " + truncate(amountTotal) + "Rs";
                    toPrint += "\nTOTAL WEIGHT: " + weightTotal + "Ltr\n\n   DAIRYDAILY APP";
                    Log.d(TAG, "toPrint: " + toPrint);

                    byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());
                    if(DashboardActivity.bluetoothAdapter != null) {
                        if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                            try {
                                DashboardActivity.bluetoothConnectionService.write(mybyte);
                                Log.d(TAG, "onOptionsSelected: Printing with DashboardActivity BT");
                            } catch (Exception e) {
                                Log.d(TAG, "onOptionsSelected: Unable to print");
                                toast(PaymentRegisterActivity.this, "Unable to print");
                            }
                        } else {
                            toast(PaymentRegisterActivity.this, "Printer is not connected");
                        }
                    }
                    else{
                        toast(PaymentRegisterActivity.this, "Bluetooth is off");
                    }
                }
            }
        });


        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("TAG", "start_shift: " + startShift);
                if(startShift.isEmpty() || endShift.isEmpty()){
                    toast(PaymentRegisterActivity.this, "Select Shifts");
                }
                else{
                    list = dbHelper.getPaymentRegister(startDate, startShift, endDate, endShift);
                    PaymentRegisterAdapter adapter = new PaymentRegisterAdapter(PaymentRegisterActivity.this, list);
                    for(PaymentRegisterModel model : list){
                        weightTotal += Double.valueOf(model.getWeight());
                        amountTotal += Double.valueOf(model.getAmount());
                    }
                    amount.setText(String.valueOf(truncate(amountTotal)) + "Rs");
                    weight.setText(String.valueOf(truncate(weightTotal)) + "Ltr");
                    recyclerView.setAdapter(adapter);
                }
            }
        });

        start_morning_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShift = "Morning";
                start_evening_radio.setChecked(false);
            }
        });
        start_evening_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShift = "Evening";
                start_morning_radio.setChecked(false);
            }
        });
        end_evening_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endShift = "Evening";
                end_morning_radio.setChecked(false);
            }
        });
        end_morning_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endShift = "Morning";
                end_evening_radio.setChecked(false);
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
                        Toast.makeText(PaymentRegisterActivity.this, "Bluetooth OFF", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onReceive: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: STATE ON");
                        Toast.makeText(PaymentRegisterActivity.this, "Bluetooth ON", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(PaymentRegisterActivity.this, "Devices connected", Toast.LENGTH_SHORT).show();
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
            //registerReceiver(mBroadcastReceiver2, intentFilter);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.printer, menu );
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Date dateIntermediate = new Date();
        String date = new SimpleDateFormat("dd/MM/YYYY").format(dateIntermediate);

        if(selectedUsers.size() >0 ){
            String toPrint = "DATE | " + date + "\n";
            toPrint += startDate + " To " + endDate + "\nID| " + "NAME |" + "TOTAL WEIGHT |" + "TOTAL AMOUNT\n";
            if(id == R.id.printer){
                double weightTotal = 0;
                double amountTotal = 0;
                for(PaymentRegisterModel object : selectedUsers){
                    int sellerId = object.getId();
                    String amount = object.getAmount();
                    String fat = object.getFat();
                    String snf = object.getSnf();
                    String weight = object.getWeight();
                    weightTotal += Double.valueOf(object.getWeight());
                    amountTotal += Double.valueOf(object.getAmount());
                    toPrint += sellerId + " | "+fat + "-" + snf + "| " + weight + "| " + amount + "| \n";
                }
                toPrint += "TOTAL AMOUNT: " + amountTotal + "Rs";
                toPrint += "\nTOTAL WEIGHT: " + weightTotal + "Ltr\n\n\n";
                byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());
                if(DashboardActivity.bluetoothAdapter != null) {
                    if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                        try {
                            DashboardActivity.bluetoothConnectionService.write(mybyte);
                            Log.d(TAG, "onOptionsSelected: Printing with DashboardActivity BT");
                        } catch (Exception e) {
                            Log.d(TAG, "onOptionsSelected: Unable to print");
                            toast(PaymentRegisterActivity.this, "Unable to print");
                        }
                    } else {
                        toast(PaymentRegisterActivity.this, "Printer is not connected");
                    }
                }
                else{
                    toast(PaymentRegisterActivity.this, "Bluetooth is off");
                }

                return true;
            }
            return false;
        }
        else{
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PaymentRegisterActivity.this, ViewReportActivity.class));
        finish();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}
