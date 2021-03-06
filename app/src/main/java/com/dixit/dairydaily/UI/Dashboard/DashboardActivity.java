package com.dixit.dairydaily.UI.Dashboard;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.InitDrawerBoard;
import com.dixit.dairydaily.UI.Login.LoginActivity;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.dixit.dairydaily.Models.MessagesModel;
import com.dixit.dairydaily.MyAdapters.MessagesAdapter;
import com.dixit.dairydaily.Others.BackupHandler;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.Others.Logout;
import com.dixit.dairydaily.Others.Prevalent;
import com.dixit.dairydaily.Others.SelectPrinterDialog;
import com.dixit.dairydaily.Others.SpinnerItem;
import com.dixit.dairydaily.Others.WarningDialog;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.RateChart.RateChartOptions;
import com.dixit.dairydaily.Others.BluetoothConnectionService;
import com.dixit.dairydaily.UI.Dashboard.AddProducts.AddProductActivity;
import com.dixit.dairydaily.UI.Dashboard.BuyMilk.BuyMilkActivity;
import com.dixit.dairydaily.UI.Dashboard.Customers.CustomersActivity;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.DeleteHistory;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.MilkHistoryActivity;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.ProfileActivity;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.UpgradeToPremium;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.ViewAllEntryActivity;
import com.dixit.dairydaily.UI.Dashboard.ProductSale.ProductSaleActivity;
import com.dixit.dairydaily.UI.Dashboard.SellMilk.SellMilkActivity;
import com.dixit.dairydaily.UI.Dashboard.ViewBuyerReport.ViewBuyerReportActivity;
import com.dixit.dairydaily.UI.Dashboard.ViewSellerReport.ViewReportActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import io.paperdb.Paper;

import static android.content.Intent.ACTION_VIEW;
import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.Others.UtilityMethods.useSnackBar;

public class DashboardActivity extends InitDrawerBoard {

    private static final int MY_REQUEST_CODE = 1000;
    // Bluetooth variables
    public static BluetoothAdapter bluetoothAdapter;
    public static BluetoothDevice bluetoothDevice;
    public static BluetoothConnectionService bluetoothConnectionService;
    public static ArrayList<SpinnerItem> deviceList;
    public static Set<BluetoothDevice> pairedDevice;
    public static SelectPrinterDialog dialog;
    AsyncTask<Void, Void, String> task1, task2, task3, task4;
    String imageString1, imageString2, imageString3, imageString4, adsDate;
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static boolean updated = false;

    private ImageView imageView1, imageView2, imageView3, imageView4;
    ViewFlipper viewFlipper;

    DatabaseReference ref;
    String message, time, status;
    public static boolean isReminderShown = false;
    public static boolean showReminder = false;

    ImageView printer, whatsapp, help;
    RelativeLayout notif;

    Toolbar toolbar;

    ProgressDialog progressDialog;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    ArrayList<MessagesModel> messages;

    long downloadId;

    public static boolean isExpired = false;

    int numberOfMessages = 0;
    TextView notif_count;


    public String show = "";
    CardView view_report, buyer_report, add_product, rate_chart;
    CardView buy_milk, sell_milk, customers, product_sale;
    LinearLayout linearLayout;
    private static final String TAG = "DashboardActivity";

    public static String expiryDate;
    private boolean suspended;
    String date;

    DbHelper helper;

    @Override
    protected void onStart() {
        super.onStart();

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Paper.init(this);

        //getSupportActionBar().setTitle("Dashboard");
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        initDrawer();

        if(Paper.book().read(Prevalent.has_account).equals("false")){
            toast(DashboardActivity.this, "Please Login");
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        }

        helper = new DbHelper(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Rate Chart...");
        progressDialog.setCancelable(false);

        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference().child("Users").child("" + Paper.book().read(Prevalent.phone_number)).child("Active");
        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String active = dataSnapshot.getValue().toString();
                    if(active.equals("false")){
                        suspended = true;
                        toast(DashboardActivity.this, "Your account has been suspended, contact the admin.");
                        Paper.book().write(Prevalent.remember_me, "false");
                        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(suspended) {

        }

        //checkFilePermissions();
        if(ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number)).child("Messages");
        messages = new ArrayList<>();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        try{
                            message = snapshot.child("message").getValue().toString();
                            time = snapshot.child("time").getValue().toString();
                            status = snapshot.child("status").getValue().toString();
                            if(status.equals("unread")){
                                numberOfMessages++;
                            MessagesModel model = new MessagesModel(message, time, status);
                            messages.add(model);}
                        }
                        catch(Exception e){}
                    }
                    notif_count.setVisibility(View.VISIBLE);
                    notif_count.setText(""+numberOfMessages);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String expiry = dataSnapshot.child("Expiry Date").getValue().toString();
                    try{
                    helper.setExpiryDate(expiry);
                    }
                    catch (Exception e){}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        checkRateFileStatus();

        Date dateIntermediate = new Date();
        try{
            DateFormat df = new DateFormat();
            date = df.format("yyyy-MM-dd", dateIntermediate).toString();
        }
        catch(Exception e){
            date = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
        }
        Log.d(TAG, "Date: "+ date);

        Cursor data = helper.getExpiryDate();
        if(data.getCount() != 0){
            while(data.moveToNext()){
                expiryDate = data.getString(data.getColumnIndex("Date"));
            }
        }else{
            expiryDate = Paper.book().read(Prevalent.expiry_date);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Log.d(TAG, "expiryDate: " + expiryDate);

        if(Long.valueOf(expiryDate)<cal.getTime().getTime()){
            isExpired = true;
        }
        else if(Long.valueOf(expiryDate)>cal.getTime().getTime()){
            isExpired = false;
        }

        if(Long.valueOf(expiryDate) - cal.getTime().getTime() < 864000000 && Long.valueOf(expiryDate) - cal.getTime().getTime()>0){
            showReminder = true;
        }

        view_report = findViewById(R.id.view_report);
        buy_milk = findViewById(R.id.buy_milk_image);
        sell_milk = findViewById(R.id.sell_milk_image);
        customers = findViewById(R.id.customers);
        buyer_report = findViewById(R.id.buyer_report);
        add_product = findViewById(R.id.add_product);
        product_sale = findViewById(R.id.product_sale);
        rate_chart = findViewById(R.id.rate_chart);
        linearLayout = findViewById(R.id.ll);
        notif_count = findViewById(R.id.notif_count);
        notif_count.setVisibility(View.GONE);
        printer = findViewById(R.id.printer);
        whatsapp = findViewById(R.id.whatsapp);
        notif = findViewById(R.id.notif);
        help = findViewById(R.id.help);
        toolbar = findViewById(R.id.toolbar);
        imageView1 = findViewById(R.id.image1);
        imageView2 = findViewById(R.id.image2);
        imageView3 = findViewById(R.id.image3);
        imageView4 = findViewById(R.id.image4);

        viewFlipper = findViewById(R.id.viewflipper);
        viewFlipper.setVisibility(View.GONE);

        String adsD = Paper.book().read(Prevalent.adsDate);

        DatabaseReference dateRef= FirebaseDatabase.getInstance().getReference().child("Ads");
        dateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adsDate = dataSnapshot.child("Date").getValue().toString();
                if(adsD != null){
                    if(!adsD.equals(adsDate))
                        getAds();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "imageString"+ adsD);

        if(adsD != null){
            imageString1 = Paper.book().read(Prevalent.image1);
            imageString2 = Paper.book().read(Prevalent.image2);
            imageString3 = Paper.book().read(Prevalent.image3);
            imageString4 = Paper.book().read(Prevalent.image4);
            Log.d(TAG, "imageString"+ "called");

            byte[] decodedString1 = Base64.decode(imageString1, Base64.DEFAULT);
            byte[] decodedString2 = Base64.decode(imageString2, Base64.DEFAULT);
            byte[] decodedString3 = Base64.decode(imageString3, Base64.DEFAULT);
            byte[] decodedString4 = Base64.decode(imageString4, Base64.DEFAULT);

            Bitmap decodedByte1 = BitmapFactory.decodeByteArray(decodedString1, 0, decodedString1.length);
            Bitmap decodedByte2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
            Bitmap decodedByte3 = BitmapFactory.decodeByteArray(decodedString3, 0, decodedString3.length);
            Bitmap decodedByte4 = BitmapFactory.decodeByteArray(decodedString4, 0, decodedString4.length);

            imageView1.setImageBitmap(decodedByte1);
            imageView2.setImageBitmap(decodedByte2);
            imageView3.setImageBitmap(decodedByte3);
            imageView4.setImageBitmap(decodedByte4);
            viewFlipper.setVisibility(View.VISIBLE);
        }
        else{
            getAds();
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Pricing");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String starter = dataSnapshot.child("Starter Plan").getValue().toString();
                    String spark = dataSnapshot.child("Spark Plan").getValue().toString();
                    String enterprise = dataSnapshot.child("Enterprise Plan").getValue().toString();
                    try{
                        double starter_double = Double.valueOf(starter);
                        double spark_double = Double.valueOf(spark);
                        double enterprise_double = Double.valueOf(enterprise);
                        Paper.book().write(Prevalent.starter, starter_double+"");
                        Paper.book().write(Prevalent.spark, spark_double+"");
                        Paper.book().write(Prevalent.enterprise, enterprise_double+"");
                    }
                    catch(Exception e){
                        Toast.makeText(DashboardActivity.this, "Error retrieving pricing plans. Contact the admin.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference5 = FirebaseDatabase.getInstance().getReference().child("Show Ads");
        reference5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    show = dataSnapshot.getValue().toString();
                    if(show.equals("true")){
                        viewFlipper.setVisibility(View.VISIBLE);
                    }
                    else if(show.equals("false")){
                        viewFlipper.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(DashboardActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.messges_dialog);
                RecyclerView recyclerView = dialog.findViewById(R.id.recyclerview);
                TextView ok = dialog.findViewById(R.id.ok);
                TextView delete = dialog.findViewById(R.id.delete);
                recyclerView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this));
                Collections.reverse(messages);
                MessagesAdapter adapter = new MessagesAdapter(DashboardActivity.this, messages);
                recyclerView.setAdapter(adapter);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    toast(DashboardActivity.this, "Notifs deleted");
                                }
                            }
                        });
                    }
                });
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        //bluetoothConnectionService.write("m68fn".getBytes(Charset.defaultCharset()));
                    }
                });
                dialog.show();
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = "https://www.youtube.com/channel/UCVCQxEmcWR_aIPqgcl86rXA";
                startActivity(new Intent(ACTION_VIEW, Uri.parse(link)));
            }
        });

        printer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findBluetoothDevice();
                dialog = new SelectPrinterDialog(DashboardActivity.this, deviceList, DashboardActivity.this);
                dialog.show();
            }
        });

        if(show.equals("true")){
            viewFlipper.setVisibility(View.VISIBLE);
        }
        else if(show.equals("false")){
            viewFlipper.setVisibility(View.GONE);
        }

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent sendintent = new Intent("android.intent.action.MAIN");
                    String phoneNumber = "918449852828";
                    sendintent.setAction(Intent.ACTION_SEND);
                    sendintent.putExtra(Intent.EXTRA_TEXT, "Hello");
                    sendintent.setType("text/plain");
                    sendintent.setPackage("com.whatsapp");
                    sendintent.putExtra("jid", phoneNumber+"@s.whatsapp.net");
                    startActivity(sendintent);
                }
                catch(Exception e){
                    toast(DashboardActivity.this, "Unable to open WhatsApp.");
                }
            }
        });

        getUpdate();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


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
        //initDashboard();
//        new InitDrawerBoard(this, this);
//        super.onCreate(savedInstanceState);
//        startActivity(new Intent(this, InitDrawerBoard.class));

        rate_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, RateChartOptions.class));
            }
        });

        product_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(DashboardActivity.this, ProductSaleActivity.class));
                if(!isExpired){
                    if(showReminder){
                        if(!isReminderShown){
                            isReminderShown = true;
                            startActivity(new Intent(DashboardActivity.this, UpgradeToPremium.class));
                        }
                        else{
                            startActivity(new Intent(DashboardActivity.this, ProductSaleActivity.class));
                        }
                    }
                    else
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
                    if(showReminder){
                        if(!isReminderShown){
                            isReminderShown = true;
                            startActivity(new Intent(DashboardActivity.this, UpgradeToPremium.class));
                        }
                        else{
                            startActivity(new Intent(DashboardActivity.this, AddProductActivity.class));
                        }
                    }
                    else
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
                    if(showReminder){
                        if(!isReminderShown){
                            isReminderShown = true;
                            startActivity(new Intent(DashboardActivity.this, UpgradeToPremium.class));
                        }
                        else{
                            startActivity(new Intent(DashboardActivity.this, BuyMilkActivity.class));
                        }
                    }
                    else
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
                    if(showReminder){
                        if(!isReminderShown){
                            isReminderShown = true;
                            startActivity(new Intent(DashboardActivity.this, UpgradeToPremium.class));
                        }
                        else{
                            startActivity(new Intent(DashboardActivity.this, SellMilkActivity.class));
                        }
                    }
                    else
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
                if(showReminder){
                    if(!isReminderShown){
                        isReminderShown = true;
                        startActivity(new Intent(DashboardActivity.this, UpgradeToPremium.class));
                    }
                    else{
                        startActivity(new Intent(DashboardActivity.this, ViewReportActivity.class));
                    }
                }
                else
                    startActivity(new Intent(DashboardActivity.this, ViewReportActivity.class));
            }
        });

        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void getAds(){
        DatabaseReference adsRef = FirebaseDatabase.getInstance().getReference().child("Ads");
        adsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    imageString1 = dataSnapshot.child("Image 1").getValue().toString();
                    imageString2 = dataSnapshot.child("Image 2").getValue().toString();
                    imageString3 = dataSnapshot.child("Image 3").getValue().toString();
                    imageString4 = dataSnapshot.child("Image 4").getValue().toString();
                    adsDate = dataSnapshot.child("Date").getValue().toString();

                    Paper.book().write(Prevalent.image1, imageString1);
                    Paper.book().write(Prevalent.image2, imageString2);
                    Paper.book().write(Prevalent.image3, imageString3);
                    Paper.book().write(Prevalent.image4, imageString4);
                    Paper.book().write(Prevalent.adsDate, adsDate);

                    byte[] decodedString1 = Base64.decode(imageString1, Base64.DEFAULT);
                    byte[] decodedString2 = Base64.decode(imageString2, Base64.DEFAULT);
                    byte[] decodedString3 = Base64.decode(imageString3, Base64.DEFAULT);
                    byte[] decodedString4 = Base64.decode(imageString4, Base64.DEFAULT);

                    Bitmap decodedByte1 = BitmapFactory.decodeByteArray(decodedString1, 0, decodedString1.length);
                    Bitmap decodedByte2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
                    Bitmap decodedByte3 = BitmapFactory.decodeByteArray(decodedString3, 0, decodedString3.length);
                    Bitmap decodedByte4 = BitmapFactory.decodeByteArray(decodedString4, 0, decodedString4.length);

                    imageView1.setImageBitmap(decodedByte1);
                    imageView2.setImageBitmap(decodedByte2);
                    imageView3.setImageBitmap(decodedByte3);
                    imageView4.setImageBitmap(decodedByte4);
                    //viewFlipper.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static String versionName;

    private void getUpdate() {

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Updates");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String new_version_code = dataSnapshot.child("Latest_Version_Code").getValue().toString();
                        String priority = dataSnapshot.child("Priority").getValue().toString();
                        versionName = dataSnapshot.child("Version Name").getValue().toString();
                        Paper.book().write(Prevalent.version_name, versionName);
                        if(Integer.valueOf(new_version_code) > pInfo.versionCode){
                            AlertDialog dialog = new AlertDialog.Builder(DashboardActivity.this).
                                    setTitle("Update").
                                    setMessage("This version is obsolete, please update to a newer version.").
                                    setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id="+pInfo.packageName)));
                                        }
                                    }).create();
                            if(priority.equals("low"))
                                dialog.setCancelable(true);
                            else
                                dialog.setCancelable(false);
                            try {
                                dialog.show();
                            }
                            catch (Exception e){}
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                toast(DashboardActivity.this, "Update failed. Retrying");
            }
            else{
                getUpdate();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    private void  updateFromServer(String l) {
        progressDialog.show();
        if(l.equals("")){
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number)).child("Rate File");
            ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number)).child("Personal Rate File Status");
                        ref.setValue("old");
                        String url = task.getResult().toString();
                        Toast.makeText(DashboardActivity.this, "Downloading file...", Toast.LENGTH_SHORT).show();
                        DownloadFile(DashboardActivity.this, "Rate File", ".csv", "/dairyDaily", url);
                    }
                    else{
                        Toast.makeText(DashboardActivity.this, "Something went wrong when downloading the file.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
        else{
            String filename = "Rate File";
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("Rate Chart").child(filename);
            ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number)).child("General Rate File Status");
                        ref.setValue("old");
                        String url = task.getResult().toString();
                        Toast.makeText(DashboardActivity.this, "Downloading file...", Toast.LENGTH_SHORT).show();
                        DownloadFile(DashboardActivity.this, "Rate File", ".csv", "/dairyDaily", url);
                    }
                    else{
                        Toast.makeText(DashboardActivity.this, "Something went wrong when downloading the file.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void checkRateFileStatus() {
        ProgressDialog dialog = new ProgressDialog(DashboardActivity.this);
        dialog.setTitle("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number)).child("Personal Rate File Status");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dialog.dismiss();
                    String value = dataSnapshot.getValue().toString();
                    if(value.equals("new")){
                        updateFromServer("");
                    }
                    else{
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number)).child("General Rate File Status");
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    dialog.dismiss();
                                    String value = dataSnapshot.getValue().toString();
                                    if(value.equals("new")){
                                        updateFromServer("l");
                                    }
                                }
                                else{
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else{
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number)).child("General Rate File Status");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                               // dialog.dismiss();
                                String value = dataSnapshot.getValue().toString();
                                //Toast.makeText(CowSNF.this, value, Toast.LENGTH_LONG).show();
                                if(value.equals("new")){
                                    updateFromServer("l");
                                }
                            }
                            else{
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dialog.dismiss();
    }

    private void DownloadFile(Context context, String fileName, String fileExtension, String destinationDirectoy, String url) {
        File file = new File(Environment.getExternalStorageDirectory() + "/Download/", "Rate File.csv");
        file.delete();
        if(ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName+fileExtension);
        request.setTitle("Rate File");
        downloadId = downloadManager.enqueue(request);
        //Toast.makeText(DashboardActivity.this,"Directory for file: " + downloadManager.getUriForDownloadedFile(downloadId), Toast.LENGTH_LONG).show();
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(downloadId == id){
                DbHelper helper = new DbHelper(DashboardActivity.this);
                helper.clearSNFTable();
                helper.createSNFTable(Environment.getExternalStorageDirectory() + "/Download/Rate File.csv");
                File file = new File(Environment.getExternalStorageDirectory() + "/Download/", "Rate File.csv");
                file.delete();
                progressDialog.dismiss();
                Toast.makeText(DashboardActivity.this, "Chart Updated", Toast.LENGTH_SHORT).show();
            }
        }
    };

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void checkFilePermissions() {
//        if(ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//                || ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//        ){
//            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//        }
//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
//            int permissionCheck = this.checkSelfPermission("Manifest.Permission.READ_EXTERNAL_STORAGE");
//            permissionCheck += this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
//            if(permissionCheck != 0){
//                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
//                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
//            }
//            else{
//                Log.d(TAG, "checkPermission: No need to check permission, SDK version  LOLLIPOP");
//            }
//        }
//    }

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
                    //toast(DashboardActivity.this, "Permission granted");
                }
                else{
                    //toast(DashboardActivity.this, "Permission Denied");
                }
                break;
            case 2:
                if((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){

                }
                else{
                    //toast(DashboardActivity.this, "External storage write permission Denied");
                }
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
//        inflater.inflate(R.menu.printer, menu);
//        inflater.inflate(R.menu.dashboard_action_bar_items, menu);
//        admin_messages = menu.findItem(R.id.message);
//        admin_messages.setTitle("Messages From Admin("+numberOfMessages+")");
        return true;
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
                Dialog dialog = new Dialog(DashboardActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.messges_dialog);
                RecyclerView recyclerView = dialog.findViewById(R.id.recyclerview);
                TextView ok = dialog.findViewById(R.id.ok);
                TextView delete = dialog.findViewById(R.id.delete);
                recyclerView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this));
                Collections.reverse(messages);
                MessagesAdapter adapter = new MessagesAdapter(DashboardActivity.this, messages);
                recyclerView.setAdapter(adapter);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    toast(DashboardActivity.this, "Notifs deleted");
                                }
                            }
                        });
                    }
                });
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.whatsapp:
                PackageManager packageManager = getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);
                try{
                    String url = "https://api.whatsapp.com/send?phone=918449852828&text="+ URLEncoder.encode("Hi, I need help", "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    if(i.resolveActivity(packageManager) != null){
                        startActivity(i);
                    }
                }
                catch (Exception e){
                    Toast.makeText(DashboardActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
//                Intent sendintent = new Intent("android.intent.action.MAIN");
//                String formattedPhoneNumber = "918449852828";
//                sendintent.setAction(Intent.ACTION_SEND);
//                sendintent.putExtra(Intent.EXTRA_TEXT, "Hello");
//                sendintent.setType("text/plain");
//                sendintent.setPackage("com.whatsapp");
//                sendintent.putExtra("jid", formattedPhoneNumber+"@s.whatsapp.net");
//                startActivity(sendintent);
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
