package com.juicebox.dairydaily.UI.Dashboard.DrawerLayout;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.juicebox.dairydaily.Models.CustomerReportModel;
import com.juicebox.dairydaily.Models.ViewAllEntryModel;
import com.juicebox.dairydaily.MyAdapters.CustomerReportAdapter;
import com.juicebox.dairydaily.MyAdapters.ViewAllEntryAdapter;
import com.juicebox.dairydaily.Others.BackupHandler;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.Logout;
import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.Others.WarningDialog;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewReport.CustomerReportActivity;
import com.juicebox.dairydaily.UI.LoginActivity;
import com.juicebox.dairydaily.UI.UsersListActivity;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import io.paperdb.Paper;

import static com.juicebox.dairydaily.Others.UtilityMethods.getEndDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.getStartDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;
import static com.juicebox.dairydaily.Others.UtilityMethods.useSnackBar;

public class ViewAllEntryActivity extends AppCompatActivity {

    ImageView start_date_image;
    ImageView end_date_image;
    TextView start_date_text_view, end_date_text_view;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private static final String TAG = "ViewAllEntryActivity";

    ArrayList<ViewAllEntryModel> list;

    TextView weightTotal;
    TextView amountTotal;

    static ConstraintLayout scrollview;

    String name;
    int passedId;

    DbHelper dbHelper = new DbHelper(this);

    EditText id;
    TextView all_sellers;
    Button go;
    int idInt;

    Button print;

    String startDate = "";
    String endDate = "";

    RecyclerView recyclerView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_all_entry);

        getSupportActionBar().setTitle("View Entry");

        weightTotal = findViewById(R.id.weightTotal);
        scrollview = findViewById(R.id.constraintlayout);
        navigationView = findViewById(R.id.nav_view);
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

        drawerLayout = findViewById(R.id.drawerlayout);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(list!=null){
                    String line = "--------------------------------";
                    String toPrint = "\nID | " + idInt + " \nNAME | " + all_sellers.getText().toString() + "\n";
                    toPrint += startDate + " TO " + endDate + "\n" + line +
                            "\n DATE |"  + "WEIGHT| " +" FAT/SNF |" + "AMOUNT"+"\n" + line + "\n";

                    for(ViewAllEntryModel object : list){
                        String session = object.getSession();
                        session = session.equals("Morning") ? "M" : "E";
                        String rate = truncate(Double.valueOf(object.getRate()));
                        String bonus = object.getBonus();
                        String date = object.getDate();
                        date = date.substring(8,10);
                        String fat = truncate(Double.valueOf(object.getFat()));
                        String amount = truncate(Double.valueOf(object.getAmount()));
                        String snf = truncate(Double.valueOf(object.getSnf()));
                        String weight = truncate(Double.valueOf(object.getWeight()));
                        toPrint += date + " - " + session + "| " + weight + "|" + fat+"-"+snf + "|" + amount +"\n";
                    }
                    toPrint += line + "\n";
                    toPrint += "TOTAL AMOUNT: " + amountTotal.getText().toString();
                    toPrint += "\nTOTAL WEIGHT: " + weightTotal.getText().toString();
                    toPrint += "\n" + line;
                    toPrint += "\n\n   DAIRYDAILY APP";
                    Log.d(TAG, "toPrint: " + toPrint);

                    byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());
                    if(DashboardActivity.bluetoothAdapter != null) {
                        if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                            try {
                                DashboardActivity.bluetoothConnectionService.write(mybyte);
                                Log.d(TAG, "onOptionsSelected: Printing with DashboardActivity BT");
                            } catch (Exception e) {
                                Log.d(TAG, "onOptionsSelected: Unable to print");
                                toast(ViewAllEntryActivity.this, "Unable to print");
                            }
                        } else {
                            toast(ViewAllEntryActivity.this, "Printer is not connected");
                        }
                    }
                    else{
                        toast(ViewAllEntryActivity.this, "Bluetooth is off");
                    }
                }
            }
        });


        final DatePickerDialog startDatePickerDialog = new DatePickerDialog(this), endDatePickerDialog = new DatePickerDialog(this);

        startDate = getStartDate();
        endDate = getEndDate();

        start_date_text_view.setText(startDate);
        end_date_text_view.setText(endDate);
        initDashboard();

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
                        endDate = year + "-0" + (month+1) + "-" + "0"+dayOfMonth;
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
                        list = dbHelper.getEntries(idInt, startDate, endDate);
                        ViewAllEntryAdapter adapter = new ViewAllEntryAdapter( list, ViewAllEntryActivity.this);
                        recyclerView.setAdapter(adapter);
                        for(ViewAllEntryModel model : list){
                            amountTotalDouble += Double.valueOf(model.getAmount());
                            weightTotalDouble += Double.valueOf(model.getWeight());
                        }
                        amountTotal.setText(String.valueOf(truncate(amountTotalDouble))+"Rs");
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
                startActivity(new Intent(ViewAllEntryActivity.this, UsersListActivity.class).putExtra("From", "ViewAllEntryActivity"));
                finish();
            }
        });

    }

    void initDashboard(){
        DbHelper helper = new DbHelper(ViewAllEntryActivity.this);
        findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewAllEntryActivity.this, ProfileActivity.class));
            }
        });
        findViewById(R.id.dashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewAllEntryActivity.this, DashboardActivity.class));
            }
        });
        findViewById(R.id.milk_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewAllEntryActivity.this, MilkHistoryActivity.class));
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Logout(ViewAllEntryActivity.this);
            }
        });
        findViewById(R.id.recover_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WarningDialog(ViewAllEntryActivity.this).show();
            }
        });
        findViewById(R.id.backup_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(ViewAllEntryActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                // Send user's phone number for verification
                Date dateIntermediate = new Date();
                String date = new SimpleDateFormat("dd/MM/YYYY").format(dateIntermediate);
                Paper.book().write(Prevalent.last_update, date);
                new BackupHandler(ViewAllEntryActivity.this);
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
        findViewById(R.id.erase_milk_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewAllEntryActivity.this, DeleteHistory.class));
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ViewAllEntryActivity.this, DashboardActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.close, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;
        if(item.getItemId() == R.id.close)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
