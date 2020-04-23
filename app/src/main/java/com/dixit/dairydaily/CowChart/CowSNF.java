package com.dixit.dairydaily.CowChart;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.dixit.dairydaily.Models.RateChartModel;
import com.dixit.dairydaily.MyAdapters.PagerAdapter;
import com.dixit.dairydaily.MyAdapters.RateChartAdapter;
import com.dixit.dairydaily.Others.BackupHandler;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.Others.Logout;
import com.dixit.dairydaily.Others.Prevalent;
import com.dixit.dairydaily.Others.UtilityMethods;
import com.dixit.dairydaily.Others.WarningDialog;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.RateChart.RateChartOptions;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.DeleteHistory;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.MilkHistoryActivity;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.ProfileActivity;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.UpgradeToPremium;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.ViewAllEntryActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;

import static com.dixit.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.dixit.dairydaily.Others.UtilityMethods.toast;


public class CowSNF extends AppCompatActivity implements
        CowTab1.OnFragmentInteractionListener,
        CowTab2.OnFragmentInteractionListener,
        CowTab3.OnFragmentInteractionListener,
        CowTab4.OnFragmentInteractionListener,
        CowTab5.OnFragmentInteractionListener,
        CowTab6.OnFragmentInteractionListener,
        CowTab7.OnFragmentInteractionListener,
        CowTab8.OnFragmentInteractionListener,
        CowTab9.OnFragmentInteractionListener,
        CowTab10.OnFragmentInteractionListener,
        CowTab11.OnFragmentInteractionListener,
        CowTab12.OnFragmentInteractionListener,
        CowTab13.OnFragmentInteractionListener,
        CowTab14.OnFragmentInteractionListener,
        CowTab15.OnFragmentInteractionListener,
        CowTab16.OnFragmentInteractionListener,
        CowTab17.OnFragmentInteractionListener,
        CowTab18.OnFragmentInteractionListener,
        CowTab19.OnFragmentInteractionListener,
        CowTab20.OnFragmentInteractionListener{

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    Uri fileUri;

    private static long downloadId;

    ProgressDialog progressDialog;

    private DbHelper dbHelper;
    private static final String TAG = "CowSNF";
    Button load_from_memory, cancel, back, update_from_server;
    ConstraintLayout rate_values, directory_view;
    ListView navigate_phone;

    private String[] filePathStrings;
    private String[] fileNameStrings;
    private File[] listFile;
    File file;

    ViewPager viewPager;

    String nameOfSnf;

    ArrayList<String> pathHistory;
    String lastDirectory;
    int count = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){

                }
                else{
                    toast(CowSNF.this, "Permission Denied");
                }
                break;
            case 2:
                if((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){

                }
                else{
                    toast(CowSNF.this, "External storage write permission Denied");
                }
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_chart);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        dbHelper = new DbHelper(this);

        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        viewPager = findViewById(R.id.view_pager);
        load_from_memory = findViewById(R.id.load_from_memory);
        rate_values = findViewById(R.id.rate_values);
        navigate_phone = findViewById(R.id.navigate_phone);
        directory_view = findViewById(R.id.directory_view);
        cancel = findViewById(R.id.cancel);
        back = findViewById(R.id.back);
        directory_view.setVisibility(View.GONE);
        update_from_server = findViewById(R.id.update_from_server);
        //navigate_phone.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Rate Chart");
        progressDialog.setCancelable(true);

        if(!UtilityMethods.checkInternetConnect(CowSNF.this))
            toast(this, "Connect to the internet");

        //checkRateFileStatus();

        nameOfSnf = getIntent().getStringExtra("Name");
        if(nameOfSnf.equals("Cow"))
            getSupportActionBar().setTitle("Cow Rate Chart");
        else if(nameOfSnf.equals("Buffalo"))
            getSupportActionBar().setTitle("Buffalo Rate Chart");



        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.black));
        tabLayout.addTab(tabLayout.newTab().setText("7.6"));
        tabLayout.addTab(tabLayout.newTab().setText("7.7"));
        tabLayout.addTab(tabLayout.newTab().setText("7.8"));
        tabLayout.addTab(tabLayout.newTab().setText("7.9"));
        tabLayout.addTab(tabLayout.newTab().setText("8.0"));
        tabLayout.addTab(tabLayout.newTab().setText("8.1"));
        tabLayout.addTab(tabLayout.newTab().setText("8.2"));
        tabLayout.addTab(tabLayout.newTab().setText("8.3"));
        tabLayout.addTab(tabLayout.newTab().setText("8.4"));
        tabLayout.addTab(tabLayout.newTab().setText("8.5"));
        tabLayout.addTab(tabLayout.newTab().setText("8.6"));
        tabLayout.addTab(tabLayout.newTab().setText("8.7"));
        tabLayout.addTab(tabLayout.newTab().setText("8.8"));
        tabLayout.addTab(tabLayout.newTab().setText("8.9"));
        tabLayout.addTab(tabLayout.newTab().setText("9.0"));
        tabLayout.addTab(tabLayout.newTab().setText("9.1"));
        tabLayout.addTab(tabLayout.newTab().setText("9.2"));
        tabLayout.addTab(tabLayout.newTab().setText("9.3"));
        tabLayout.addTab(tabLayout.newTab().setText("9.4"));
        tabLayout.addTab(tabLayout.newTab().setText("9.5"));


        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        initDashboard();
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        hideKeyboard(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count == 0){
                    Log.d(TAG, "back button: You have reached the highest level directory");
                }
                else{
                    pathHistory.remove(count);
                    count--;
                    checkInternalStorage();
                    Log.d(TAG, "back button: " + pathHistory.get(count));
                }
            }
        });

        update_from_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFromServer("");
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate_values.setVisibility(View.VISIBLE);
                directory_view.setVisibility(View.GONE);
                getSupportActionBar().setTitle(nameOfSnf + " Rate Chart");
            }
        });

        navigate_phone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(nameOfSnf.equals("Buffalo")){
                    lastDirectory = pathHistory.get(count);
                    if(lastDirectory.equals(parent.getItemAtPosition(position))){
                        Log.d(TAG, "Internal Storage: Selected a file for upload");
                        // Execute method for reading the excel file
                        dbHelper.clearBuffaloSNFTable();
                        dbHelper.createBuffaloSNFTable(lastDirectory);
                        startActivity(new Intent(CowSNF.this, CowSNF.class).putExtra("Name", nameOfSnf));
                        finish();
                    }
                    else{
                        count++;
                        pathHistory.add(count, (String) parent.getItemAtPosition(position));
                        checkInternalStorage();
                        Log.d(TAG, "Internal Storage: " + pathHistory.get(count));
                    }
                }
                else if(nameOfSnf.equals("Cow")){
                    lastDirectory = pathHistory.get(count);
                    if(lastDirectory.equals(parent.getItemAtPosition(position))){
                        Log.d(TAG, "Internal Storage: Selected a file for upload");
                        // Execute method for reading the excel file
                        dbHelper.clearSNFTable();
                        dbHelper.createSNFTable(lastDirectory);
                        startActivity(new Intent(CowSNF.this, CowSNF.class).putExtra("Name", nameOfSnf));
                        finish();
                    }
                    else{
                        count++;
                        pathHistory.add(count, (String) parent.getItemAtPosition(position));
                        checkInternalStorage();
                        Log.d(TAG, "Internal Storage: " + pathHistory.get(count));
                    }
                }
            }
        });

        checkFilePermissions();

        load_from_memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                rate_values.setVisibility(View.GONE);
//                directory_view.setVisibility(View.VISIBLE);
//                getSupportActionBar().setTitle("Select File");
//                count = 0;
//                pathHistory = new ArrayList<>();
//                pathHistory.add(count, System.getenv("EXTERNAL_STORAGE"));
//                checkInternalStorage();
                selectFileToUpload();

            }
        });

    }

    private void selectFileToUpload() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("*/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private void uploadSelectedFile() {
        String filename = "Rate File";
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number)).child(filename);
        ref.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    updateFromServer("");
                }
                else{
                    Toast.makeText(CowSNF.this, "Something went wrong while uploading the file." + task.getException(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            if (fileUri != null) {
                if (fileUri.getPath().endsWith(".csv")) {
                    progressDialog.show();
                    uploadSelectedFile();
                } else {
                    Toast.makeText(CowSNF.this, "Please select a CSV file.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(CowSNF.this, "Please select a file.", Toast.LENGTH_LONG).show();
            }
            //selectedFile.setText(fileUri.getPath());
        } else {
            Toast.makeText(CowSNF.this, "Please select a CSV file.", Toast.LENGTH_LONG).show();
        }
    }

    private void updateFromServer(String l) {
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
                        Toast.makeText(CowSNF.this, "Downloading file...", Toast.LENGTH_SHORT).show();
                        DownloadFile(CowSNF.this, "Rate File", ".csv", "/dairyDaily", url);
                    }
                    else{
                        Toast.makeText(CowSNF.this, "Something went wrong when downloading the file.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CowSNF.this, "Downloading file...", Toast.LENGTH_SHORT).show();
                        DownloadFile(CowSNF.this, "Rate File", ".csv", "/dairyDaily", url);
                    }
                    else{
                        Toast.makeText(CowSNF.this, "Something went wrong when downloading the file.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void checkRateFileStatus() {
        ProgressDialog dialog = new ProgressDialog(CowSNF.this);
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
                                dialog.dismiss();
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
        //dialog.dismiss();
    }

    private void DownloadFile(Context context, String fileName, String fileExtension, String destinationDirectoy, String url) {
        File file = new File(Environment.getExternalStorageDirectory() + "/Download/", "Rate File.csv");
        file.delete();
        if(ContextCompat.checkSelfPermission(CowSNF.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(CowSNF.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(CowSNF.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
                DbHelper helper = new DbHelper(CowSNF.this);
                helper.clearSNFTable();
                helper.createSNFTable(Environment.getExternalStorageDirectory() + "/Download/Rate File.csv");
                List<RateChartModel> list = new ArrayList<>();
                DbHelper dbHelper = new DbHelper(CowSNF.this);

                String nameOfSnf = getIntent().getStringExtra("Name");
                if(nameOfSnf.equals("Buffalo")){

                    Cursor data = dbHelper.getBuffaloSNFTable();

                    if(data.getCount() != 0){
                        while(data.moveToNext()){
                            list.add(new RateChartModel(data.getString(0), data.getString(1)));
                            //Log.d(TAG, "CowTab1: " + data.getString(0) + " " + data.getString(1));
                        }
                    }else{
                    }
                    RateChartAdapter adapter = new RateChartAdapter(CowSNF.this, list);
                    CowTab1.recyclerView.setAdapter(adapter);
                }
                else if(nameOfSnf.equals("Cow")) {
                    Cursor data = dbHelper.getSNFTable();

                    if (data.getCount() != 0) {
                        while (data.moveToNext()) {
                            list.add(new RateChartModel(data.getString(0), data.getString(1)));
                            //Log.d(TAG, "CowTab1: " + data.getString(0) + " " + data.getString(1));
                        }
                    } else {
                    }
                    RateChartAdapter adapter = new RateChartAdapter(CowSNF.this, list);
                    CowTab1.recyclerView.setAdapter(adapter);
                }
                File file = new File(Environment.getExternalStorageDirectory() + "/Download/", "Rate File.csv");
                boolean deleted = file.delete();
                progressDialog.dismiss();
                Toast.makeText(CowSNF.this, "Chart Updated", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    void initDashboard(){
        findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CowSNF.this, ProfileActivity.class));
            }
        });
        findViewById(R.id.dashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CowSNF.this, DashboardActivity.class));
                finish();
            }
        });
        findViewById(R.id.view_all_entry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CowSNF.this, ViewAllEntryActivity.class));
            }
        });
        findViewById(R.id.milk_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CowSNF.this, MilkHistoryActivity.class));
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Logout(CowSNF.this);
            }
        });
        findViewById(R.id.recover_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WarningDialog(CowSNF.this).show();
            }
        });
        findViewById(R.id.backup_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(CowSNF.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                // Send user's phone number for verification
                Date dateIntermediate = new Date();
                String date = new SimpleDateFormat("dd/MM/YYYY").format(dateIntermediate);
                Paper.book().write(Prevalent.last_update, date);
                new BackupHandler(CowSNF.this);
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
                startActivity(new Intent(CowSNF.this, DeleteHistory.class));
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
        findViewById(R.id.upgrade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CowSNF.this, UpgradeToPremium.class));
            }
        });
        findViewById(R.id.legal_policies).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private void checkInternalStorage() {
        Log.d(TAG, "checkInternalStorage: Started.");
        try{
            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                Log.d(TAG, "checkInternalStorage: No SD Card found");
            }
            else{
                // locate the image folder in your sd card
                file = new File(pathHistory.get(count));
            }
            listFile = file.listFiles();
            // Create a String array for filePathStrings
            filePathStrings = new String[listFile.length];

            // Create a String array for fileNameStrings
            fileNameStrings = new String[listFile.length];

            for(int i=0; i<listFile.length; i++){
                // Get the path of the image file
                filePathStrings[i] = listFile[i].getAbsolutePath();
                // Get the name of the image file
                fileNameStrings[i] = listFile[i].getName();
            }

            for(int i=0; i < listFile.length; i++){
                Log.d("Files", "Filename: " + listFile[i].getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filePathStrings);
            navigate_phone.setAdapter(adapter);
        }
        catch(NullPointerException e){
            Log.e(TAG, "checkInternalStorage: NullPointerException " + e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkFilePermissions() {
        if(ContextCompat.checkSelfPermission(CowSNF.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(CowSNF.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(CowSNF.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
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
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        if(!RateChartAdapter.isUploaded)
            toast(CowSNF.this, "Unable to upload chart");
        startActivity(new Intent(CowSNF.this, RateChartOptions.class));
        finish();
    }
}
