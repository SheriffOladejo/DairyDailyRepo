package com.juicebox.dairydaily.CowChart;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.juicebox.dairydaily.MyAdapters.PagerAdapter;
import com.juicebox.dairydaily.Others.BackupHandler;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.Logout;
import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.Others.WarningDialog;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.RateChart.RateChartOptions;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.DeleteHistory;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.MilkHistoryActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ProfileActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.UpgradeToPremium;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ViewAllEntryActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.paperdb.Paper;

import static com.juicebox.dairydaily.Others.UtilityMethods.toast;


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

    private DbHelper dbHelper;
    private static final String TAG = "CowSNF";
    Button load_from_memory, cancel, back;
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_chart);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        dbHelper = new DbHelper(this);

        viewPager = findViewById(R.id.view_pager);
        load_from_memory = findViewById(R.id.load_from_memory);
        rate_values = findViewById(R.id.rate_values);
        navigate_phone = findViewById(R.id.navigate_phone);
        directory_view = findViewById(R.id.directory_view);
        cancel = findViewById(R.id.cancel);
        back = findViewById(R.id.back);
        directory_view.setVisibility(View.GONE);
        //navigate_phone.setVisibility(View.GONE);

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
                rate_values.setVisibility(View.GONE);
                directory_view.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle("Select File");
                count = 0;
                pathHistory = new ArrayList<>();
                pathHistory.add(count, System.getenv("EXTERNAL_STORAGE"));
                checkInternalStorage();
            }
        });

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
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.Permission.READ_EXTERNAL_STORAGE");
            permissionCheck += this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if(permissionCheck != 0){
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
            }
            else{
                Log.d(TAG, "checkPermission: No need to check permission, SDK version  LOLLIPOP");
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CowSNF.this, RateChartOptions.class));
        finish();
    }
}
