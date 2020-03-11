package com.juicebox.dairydaily.UI.Dashboard;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.juicebox.dairydaily.Others.BackupHandler;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.Logout;
import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.Others.SelectPrinterDialog;
import com.juicebox.dairydaily.Others.SpinnerItem;
import com.juicebox.dairydaily.Others.WarningDialog;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.RateChart.RateChartOptions;
import com.juicebox.dairydaily.UI.BluetoothConnectionService;
import com.juicebox.dairydaily.UI.Dashboard.AddProducts.AddProductActivity;
import com.juicebox.dairydaily.UI.Dashboard.BuyMilk.BuyMilkActivity;
import com.juicebox.dairydaily.UI.Dashboard.Customers.CustomersActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.DeleteHistory;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.MilkHistoryActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ProfileActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.UpgradeToPremium;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ViewAllEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.ProductSale.ProductSaleActivity;
import com.juicebox.dairydaily.UI.Dashboard.SellMilk.SellMilkActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport.ViewBuyerReportActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewReport.ViewReportActivity;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import io.paperdb.Paper;

import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.useSnackBar;

public class DashboardActivity extends AppCompatActivity {

    // Bluetooth variables
    public static BluetoothAdapter bluetoothAdapter;
    public static BluetoothDevice bluetoothDevice;
    public static BluetoothConnectionService bluetoothConnectionService;
    public static ArrayList<SpinnerItem> deviceList;
    public static Set<BluetoothDevice> pairedDevice;
    public static SelectPrinterDialog dialog;
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    boolean isExpired = false;

    CardView view_report, buyer_report, add_product, rate_chart;
    CardView buy_milk, sell_milk, customers, product_sale;
    LinearLayout linearLayout;
    private static final String TAG = "DashboardActivity";

    public static String expiryDate, hasPaid, hasExpired;
    String date;

    DbHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getSupportActionBar().setTitle("Dashboard");
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        helper = new DbHelper(this);
//
//        Calendar c = Calendar.getInstance();
//        c.setTime(new Date());
//        c.add(Calendar.DATE, 31);
//        helper.setExpiryDate(String.valueOf(c.getTime().getTime()));

        Date dateIntermediate = new Date();
        date = new SimpleDateFormat("dd-MM-YYYY").format(dateIntermediate);
        Log.d(TAG, "Date: "+ date);

        Paper.init(this);

        Cursor data = helper.getExpiryDate();
        if(data.getCount() != 0){
            while(data.moveToNext()){
                expiryDate = data.getString(data.getColumnIndex("Date"));
                //toast(DashboardActivity.this, expiryDate);
                Log.d(TAG, "Expiry date from cursor: " + expiryDate + "  " + hasExpired);
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        if(Long.valueOf(expiryDate)<cal.getTime().getTime()){
            isExpired = true;
        }
        else if(Long.valueOf(expiryDate)>cal.getTime().getTime()){
            isExpired = false;
        }

//        if(date.equals(expiryDate))
//            helper.setExpiryDate(expiryDate);
//
//        Date todayDate = null, myExpiryDate = null;
//
//        try {
//            todayDate = new SimpleDateFormat("dd-MM-YYYY").parse(date);
//            myExpiryDate = new SimpleDateFormat("dd-MM-YYYY").parse(expiryDate);
//            Log.d(TAG, "Today Date: " + todayDate + "   MyExpiryDate: " + myExpiryDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        if(myExpiryDate != null && todayDate != null){
//            if(myExpiryDate.compareTo(todayDate) >0 ){
//                helper.setExpiryDate(expiryDate);
//                isExpired = false;
//                Log.d(TAG, "Expirty date greater: " + expiryDate + "  " + isExpired);
//                Log.d(TAG, "Expirty date is after now");
//            }
//            else if(myExpiryDate.compareTo(todayDate)<0){
//                helper.setExpiryDate(expiryDate);
//                isExpired = true;
//                Log.d(TAG, "Expirty date less: " + expiryDate + "  " + isExpired);
//                Log.d(TAG, "Expirty date is before now");
//            }
//        }
//        Log.d(TAG, "Expiry date" + myExpiryDate + "   " + todayDate + "    " + expiryDate);

        view_report = findViewById(R.id.view_report);
        buy_milk = findViewById(R.id.buy_milk_image);
        sell_milk = findViewById(R.id.sell_milk_image);
        customers = findViewById(R.id.customers);
        buyer_report = findViewById(R.id.buyer_report);
        add_product = findViewById(R.id.add_product);
        product_sale = findViewById(R.id.product_sale);
        rate_chart = findViewById(R.id.rate_chart);
        linearLayout = findViewById(R.id.ll);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.enable();
        pairedDevice = bluetoothAdapter.getBondedDevices();

        Animation slide = AnimationUtils.loadAnimation(DashboardActivity.this, R.anim.image_slide_left);
        //Animation slideLL = AnimationUtils.loadAnimation(this, R.anim.image_slide_up);
        //linearLayout.startAnimation(slideLL);
        view_report.startAnimation(slide);
        buy_milk.startAnimation(slide);
        sell_milk.startAnimation(slide);
        customers.startAnimation(slide);
        buyer_report.startAnimation(slide);
        add_product.startAnimation(slide);
        product_sale.startAnimation(slide);
        rate_chart.startAnimation(slide);

        view_report.animate().translationX(2f);
        initDashboard();

        rate_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, RateChartOptions.class));
            }
        });

        if(ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 1);
        }

        product_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(DashboardActivity.this, ProductSaleActivity.class));
                if(!isExpired){
                    startActivity(new Intent(DashboardActivity.this, ProductSaleActivity.class));
                }
                else{
                    startActivity(new Intent(DashboardActivity.this, UpgradeToPremium.class));
                }
            }
        });
        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(DashboardActivity.this, AddProductActivity.class));
                if(!isExpired){
                    startActivity(new Intent(DashboardActivity.this, AddProductActivity.class));
                }
                else{
                    startActivity(new Intent(DashboardActivity.this, UpgradeToPremium.class));
                }
            }
        });
        customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, CustomersActivity.class));
            }
        });
        buyer_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ViewBuyerReportActivity.class));
            }
        });
        buy_milk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isExpired){
                    startActivity(new Intent(DashboardActivity.this, BuyMilkActivity.class));
                }
                else{
                    startActivity(new Intent(DashboardActivity.this, UpgradeToPremium.class));
                }
            }
        });
        sell_milk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(DashboardActivity.this, SellMilkActivity.class));
                if(!isExpired){
                    startActivity(new Intent(DashboardActivity.this, SellMilkActivity.class));
                }
                else{
                    startActivity(new Intent(DashboardActivity.this, UpgradeToPremium.class));
                }
            }
        });
        view_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ViewReportActivity.class));
            }
        });

        //Broadcasts when bond state changes(i.e pairing)
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//        registerReceiver(mBroadcastReceiver4, intentFilter);
    }

    void initDashboard(){
        findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
            }
        });
        findViewById(R.id.legal_policies).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(DashboardActivity.this, UpgradeToPremium.class));
            }
        });
        findViewById(R.id.view_all_entry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ViewAllEntryActivity.class));
            }
        });
        findViewById(R.id.milk_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, MilkHistoryActivity.class));
            }
        });
        findViewById(R.id.upgrade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, UpgradeToPremium.class));
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Logout(DashboardActivity.this);
            }
        });
        findViewById(R.id.recover_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WarningDialog(DashboardActivity.this).show();
            }
        });
        findViewById(R.id.backup_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(DashboardActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                // Send user's phone number for verification
                Date dateIntermediate = new Date();
                String date = new SimpleDateFormat("dd/MM/YYYY").format(dateIntermediate);
                Paper.book().write(Prevalent.last_update, date);
                new BackupHandler(DashboardActivity.this);
            }
        });

        LinearLayout backup, recover, update_rate_charts, erase_milk_history;
        ImageView arrow = findViewById(R.id.arrow);
        final boolean[] arrowClicked = {false};
        backup = findViewById(R.id.backup_data);
        erase_milk_history = findViewById(R.id.erase_milk_history);
        update_rate_charts = findViewById(R.id.update_rate_charts);
        recover = findViewById(R.id.recover_data);
        update_rate_charts.setVisibility(View.GONE);
        erase_milk_history.setVisibility(View.GONE);
        backup.setVisibility(View.GONE);
        recover.setVisibility(View.GONE);
        findViewById(R.id.erase_milk_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, DeleteHistory.class));
            }
        });
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrowClicked[0]){
                    backup.setVisibility(View.GONE);
                    recover.setVisibility(View.GONE);
                    erase_milk_history.setVisibility(View.GONE);
                    update_rate_charts.setVisibility(View.GONE);
                    arrowClicked[0] = false;
                    arrow.setImageResource(R.drawable.ic_drop_down);
                }
                else{
                    arrow.setImageResource(R.drawable.drop_down);
                    backup.setVisibility(View.VISIBLE);
                    erase_milk_history.setVisibility(View.VISIBLE);
                    update_rate_charts.setVisibility(View.VISIBLE);
                    recover.setVisibility(View.VISIBLE);
                    arrowClicked[0] = true;
                }
            }
        });
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrowClicked[0]){
                    backup.setVisibility(View.GONE);
                    recover.setVisibility(View.GONE);
                    erase_milk_history.setVisibility(View.GONE);
                    update_rate_charts.setVisibility(View.GONE);
                    arrowClicked[0] = false;
                    arrow.setImageResource(R.drawable.ic_drop_down);
                }
                else{
                    arrow.setImageResource(R.drawable.drop_down);
                    backup.setVisibility(View.VISIBLE);
                    erase_milk_history.setVisibility(View.VISIBLE);
                    update_rate_charts.setVisibility(View.VISIBLE);
                    recover.setVisibility(View.VISIBLE);
                    arrowClicked[0] = true;
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

    //Create a broadcast receiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);
                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        //Toast.makeText(DashboardActivity.this, "Bluetooth OFF", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onReceive: STATE TURNING OFF");
                        //bluetoothAdapter.disable();
                        bluetoothAdapter = null;
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: STATE ON");
                        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        //Toast.makeText(DashboardActivity.this, "Bluetooth ON", Toast.LENGTH_LONG).show();
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
            case 1:
                if((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){

                }
                else{
                    toast(DashboardActivity.this, "Permission Denied");
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

    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases
                //case1: bonded already
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED"+mDevice.getBondState());
                    bluetoothDevice = mDevice;
                    Toast.makeText(DashboardActivity.this, "Devices connected", Toast.LENGTH_SHORT).show();
                }
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDING){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING"+mDevice.getBondState());
                }
                if(mDevice.getBondState() == BluetoothDevice.BOND_NONE){
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE"+mDevice.getBondState());
                }
                else{
                    bluetoothDevice = null;
                }
            }
        }
    };

    public void findBluetoothDevice(){
        try{
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter == null){
                useSnackBar("No bluetooth device.", linearLayout);
            }
            if(!bluetoothAdapter.isEnabled()){
                bluetoothAdapter.enable();
            }

            if(bluetoothAdapter.isDiscovering()){
                bluetoothAdapter.cancelDiscovery();
                bluetoothAdapter.startDiscovery();
            }
            if(!bluetoothAdapter.isDiscovering()){
                bluetoothAdapter.startDiscovery();
            }

            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, intentFilter);
            IntentFilter BTIntent = new IntentFilter(bluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
            IntentFilter intentFilters = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            registerReceiver(mBroadcastReceiver2, intentFilters);

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
        Log.d(TAG, "connect: " + deviceName);
        for(BluetoothDevice device: pairedDevice){
            if(device.getName().equals(deviceName)){
                Log.d(TAG, "connect: Connecting to" + deviceName);
                bluetoothDevice = device;
                bluetoothDevice.createBond();
                Log.d(TAG, "connect: Device name" + bluetoothDevice.getName());
                try{
                    Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
                    ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(bluetoothAdapter, null);
                    bluetoothConnectionService.startClient(bluetoothDevice, MY_UUID_INSECURE);
                }
                catch (Exception e){
                    Log.d(TAG, "connect: " + e.getMessage());
                }
            }
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.sign_up_text, menu );
        inflater.inflate(R.menu.printer, menu);
        inflater.inflate(R.menu.dashboard_menu, menu);
        MenuItem dateItem = menu.findItem(R.id.sign_up);
        dateItem.setTitle(date);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.printer:
                findBluetoothDevice();
                dialog = new SelectPrinterDialog(DashboardActivity.this, deviceList, this);
                dialog.show();
                break;
            case R.id.help:
                break;
            case R.id.message:
                break;
            case R.id.whatsapp:
                break;
        }
        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
