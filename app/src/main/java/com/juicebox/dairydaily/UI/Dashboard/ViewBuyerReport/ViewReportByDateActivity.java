package com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport;

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.juicebox.dairydaily.Models.DailySalesObject;
import com.juicebox.dairydaily.Models.ReportByDateModels;
import com.juicebox.dairydaily.MyAdapters.BuyerRegisterAdapter;
import com.juicebox.dairydaily.MyAdapters.ReportByDateAdapter;
import com.juicebox.dairydaily.Others.BackupHandler;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.Logout;
import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.Others.SelectPrinterDialog;
import com.juicebox.dairydaily.Others.SpinnerItem;
import com.juicebox.dairydaily.Others.WarningDialog;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.BluetoothConnectionService;
import com.juicebox.dairydaily.UI.Dashboard.BuyMilk.BuyMilkActivity;
import com.juicebox.dairydaily.UI.Dashboard.BuyMilk.MilkBuyEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.DeleteHistory;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.MilkHistoryActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ProfileActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.UpgradeToPremium;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ViewAllEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.SellMilk.MilkSaleEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.SellMilk.SellMilkActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewReport.CustomerReportActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewReport.PaymentRegisterActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewReport.ShiftReportActivity;
import com.juicebox.dairydaily.UI.UsersListActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.paperdb.Paper;

import static com.juicebox.dairydaily.Others.UtilityMethods.getDateRange;
import static com.juicebox.dairydaily.Others.UtilityMethods.getEndDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.getMonth;
import static com.juicebox.dairydaily.Others.UtilityMethods.getStartDate;
import static com.juicebox.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;
import static com.juicebox.dairydaily.Others.UtilityMethods.useSnackBar;

public class ViewReportByDateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "ViewReportByDateActivit";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private final int REQUEST_READ_PHONE_STATE = 1;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    ImageView start_date_image;
    ImageView end_date_image;
    TextView start_date_text_view, end_date_text_view, amount, weight;
    RadioButton start_morning_radio, start_evening_radio, end_morning_radio, end_evening_radio;
    String start_date = "", end_date = "";
    Button go;
    double weightTotal;
    double amountTotal;
    double averageFat;
    EditText id;
    TextView buyers;
    RecyclerView recyclerView;
    ArrayList<ReportByDateModels> list;

    Button print, send_msg;

    static ConstraintLayout scrollview;

    DbHelper dbHelper;
    int idInt = 0;

    String startDate, endDate, startShift, endShift;

    DatePickerDialog startDatePickerDialog;
    DatePickerDialog endDatePickerDialog;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_report_by_date);

        dbHelper = new DbHelper(this);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initDashboard();

        getSupportActionBar().setTitle("Report By Date");
        print = findViewById(R.id.print);
        send_msg = findViewById(R.id.send_msg);
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        hideKeyboard(this);

        start_date_image = findViewById(R.id.start_date_image_view);
        end_date_image = findViewById(R.id.end_date_image_view);
        scrollview = findViewById(R.id.constraintlayout);
        start_date_text_view = findViewById(R.id.start_date_text_view);
        end_date_text_view = findViewById(R.id.end_date_text_view);
        start_morning_radio = findViewById(R.id.start_morning_radio);
        start_evening_radio= findViewById(R.id.start_evening_radio);
        end_morning_radio = findViewById(R.id.end_morning_radio);
        end_evening_radio = findViewById(R.id.end_evening_radio);
        amount = findViewById(R.id.amountTotal);
        weight = findViewById(R.id.weightTotal);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        go = findViewById(R.id.go);
        id = findViewById(R.id.id);
        buyers = findViewById(R.id.buyers);

        findViewById(R.id.pdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPdfWrapper();
            }
        });

        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone_number = dbHelper.getBuyerPhone_Number(idInt);
                String toSend = "TOTAL AMOUNT: " + truncate(amountTotal)+"Rs";
                toSend += "\nTOTAL WEIGHT: " + truncate(weightTotal)+"Ltr";
                toSend += "\nAVERAGE FAT: " + truncate(averageFat)+"%";
                toSend += "\n   DAIRYDAILY APP";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.putExtra("address", phone_number);
                i.putExtra("sms_body", toSend);
                i.setType("vnd.android-dir/mms-sms");
                startActivity(i);
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

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(list!=null){

                    Date dateIntermediate = new Date();
                    String date = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
                    String line = "--------------------------------";
                    String toPrint = "\n\nID|" + idInt +"  " + buyers.getText().toString() + "\n";
                    toPrint += "DATE | " + date + "\n" + startDate + " To " + endDate + "\n Date |" + "Rate  |" + "WEIGHT|" + "AMOUNT\n" +line + "\n";

                    for(ReportByDateModels object : list){
                        String dateString = object.getDate();
                        String rate = truncate(Double.valueOf(object.getRate()));
                        String amount = truncate(Double.valueOf(object.getAmount()));
                        String shift = object.getShift();
                        shift = shift.equals("Morning") ? "M" : "E";
                        String weight =truncate(Double.valueOf(object.getWeight()));
                        amountTotal += Double.valueOf(object.getAmount());
                        weightTotal += Double.valueOf(object.getWeight());
                        averageFat +=Double.valueOf(object.getRate());
                        toPrint += dateString.substring(8,10) +" - " + shift + "|" +" " + rate + "|" + ""+ weight + " |" +""+ amount + "\n";
                    }
                    averageFat /= list.size();
                    toPrint += line + "\n";
                    toPrint += "TOTAL AMOUNT: " + truncate(amountTotal) + "Rs";
                    toPrint += "\nTOTAL WEIGHT: " + truncate(weightTotal) + "Ltr";
                    toPrint += "\n" + line;
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
                                toast(ViewReportByDateActivity.this, "Unable to print");
                            }
                        } else {
                            toast(ViewReportByDateActivity.this, "Printer is not connected");
                        }
                    }
                    else{
                        toast(ViewReportByDateActivity.this, "Bluetooth is off");
                    }
                }
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


        idInt = getIntent().getIntExtra("id", 0);
        String name = getIntent().getStringExtra("name");
        try{
            if(!name.equals("")){
                buyers.setText(name);
            }
        }
        catch(Exception e){}
        if(idInt != 0){
            id.setText(String.valueOf(idInt));
        }

        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")){
                    try{
                        int id = Integer.valueOf(s.toString());
                        String name = dbHelper.getBuyerName(id);
                        if(name.equals("")){
                            buyers.setText("Not Found");
                        }
                        else{
                            buyers.setText(name);
                        }
                    }
                    catch(Exception e){
                        buyers.setText("Not Found");
                    }
                }
                else{
                    buyers.setText("All Buyers");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        start_morning_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShift = "Morning";
                start_evening_radio.setChecked(false);
            }
        });

        end_morning_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endShift = "Evening";
                end_evening_radio.setChecked(false);
            }
        });

        start_evening_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShift="Evening";
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

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountTotal = 0;
                weightTotal = 0;
                startDate = start_date_text_view.getText().toString();
                endDate = end_date_text_view.getText().toString();
                Log.d(TAG, "Start date: " + startDate);
                Log.d(TAG, "End date: " + endDate);
                if(start_morning_radio.isChecked()){
                    startShift = "Morning";
                    start_evening_radio.setChecked(false);
                }
                else if(start_evening_radio.isChecked()){
                    startShift = "Evening";
                    start_morning_radio.setChecked(false);
                }

                if(end_morning_radio.isChecked()){
                    end_evening_radio.setChecked(false);
                    endShift = "Morning";
                }
                else if(end_evening_radio.isChecked()){
                    endShift = "Evening";
                    end_morning_radio.setChecked(false);
                }
                try{
                    idInt = Integer.valueOf(id.getText().toString());
                }
                catch(Exception e){

                }

                try{
                    if(startDate.isEmpty() || endDate.isEmpty() || startShift.isEmpty() || endShift.isEmpty()){
                        Toast.makeText(ViewReportByDateActivity.this, "Operation failed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Operation failed");
                    }
                    else{
                        list = dbHelper.getReportByDate(idInt, startDate, startShift, endDate, endShift);
                        Log.d(TAG, "goClick:pp " + list.size());
                        ReportByDateAdapter adapter = new ReportByDateAdapter(ViewReportByDateActivity.this, list);

                        for(ReportByDateModels model : list){
                            weightTotal += Double.valueOf(model.getWeight());
                            amountTotal += Double.valueOf(model.getAmount());
                        }
                        Log.d(TAG, "goClick: " + weightTotal);
                        amount.setText(String.valueOf(truncate(amountTotal)) + "Rs");
                        weight.setText(String.valueOf(weightTotal) + "Ltr");

                        recyclerView.setAdapter(adapter);
                    }
                }
                catch(Exception e){

                }

            }
        });

        buyers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewReportByDateActivity.this, UsersListActivity.class);
                intent.putExtra("From", "ViewReport");
                startActivity(intent);
            }
        });
    }

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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    private void createPdfWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
        try {
            createPdf();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    File pdfFile;
    private void createPdf() throws IOException, DocumentException {
        File docFolder = new File(Environment.getExternalStorageDirectory() + "/DairyDaily/ReportByDate");
        if((!docFolder.exists())){
            docFolder.mkdirs();
            Log.i(TAG, "Created a new directory for reporrtbydate");
        }
        String date = new SimpleDateFormat("dd-MM-YYYY").format(new Date());
        String pdfName = date + ".pdf";
        pdfFile = new File(docFolder.getAbsolutePath(), pdfName);

        OutputStream outputStream = new FileOutputStream(pdfFile);
        Document document = new Document(PageSize.A4);
        PdfPTable table = new PdfPTable(new float[]{2,2,2,2,2});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(20);
        table.setTotalWidth(PageSize.A4.getWidth());

        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell("Date");
        table.addCell("Session");
        table.addCell("Rate(Rs/Ltr)");
        table.addCell("Weight(Ltr)");
        table.addCell("Amount(Rs)");

        Paper.init(ViewReportByDateActivity.this);

        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();

        for(int i = 0; i<cells.length; i++){
            cells[i].setBackgroundColor(BaseColor.WHITE);
        }
        for(int j = 0; j <list.size(); j++){
            table.addCell(String.valueOf(list.get(j).getDate()));
            table.addCell(list.get(j).getShift());
            table.addCell(list.get(j).getRate());
            table.addCell(list.get(j).getWeight());
            table.addCell(list.get(j).getAmount());
            Log.d(TAG, "Date: " + list.get(j).getDate());
            Log.d(TAG, "Amount: " + list.get(j).getAmount());
            Log.d(TAG, "Shift: " + list.get(j).getShift());
            Log.d(TAG, "Weight: " + list.get(j).getWeight());
            Log.d(TAG, "Rate: " + list.get(j).getRate());
        }

        PdfWriter.getInstance(document, outputStream);
        document.open();
        Font f  = new Font(Font.FontFamily.HELVETICA, 17.0f, Font.BOLD, BaseColor.BLACK);
        Font f1 = new Font(Font.FontFamily.HELVETICA, 17.0f, Font.BOLDITALIC, BaseColor.BLACK);
        document.add(new Paragraph("DAIRYDAILY APP\n\n",f));
        Paragraph header = new Paragraph("Report By Date",f1);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        Paragraph p1 = new Paragraph("Username: " + Paper.book().read(Prevalent.name) + "\nPhone Number: " + Paper.book().read(Prevalent.phone_number),f);
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);

        document.add(new Paragraph(date,f));
        document.add(new Paragraph("ID: " + idInt,f));
        document.add(new Paragraph("Name: " +buyers.getText().toString(),f));
        document.add(new Paragraph("Mobile Number: "+dbHelper.getBuyerPhone(idInt),f));
        Paragraph p = new Paragraph(startDate + " - " + endDate + "\n\n",f);
        p.setAlignment(Element.ALIGN_CENTER);
        document.add(p);
        document.add(table);

        PdfPTable table1 = new PdfPTable(new float[]{2,2});
        table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table1.getDefaultCell().setFixedHeight(20);
        table1.setTotalWidth(PageSize.A4.getWidth());

        table1.setWidthPercentage(100);
        table1.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table1.addCell("Total Amount: " + truncate(amountTotal));
        table1.addCell("Total Weight: " + truncate(weightTotal));
        document.add(table1);

        document.add(new Paragraph("DairyDaily Download App Now:\nHttps://www.dariyda.com",f));
        document.close();
        previewPdf();
    }

    private void previewPdf() {
        PackageManager manager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("application/pdf");
        List list = manager.queryIntentActivities(intent, manager.MATCH_DEFAULT_ONLY);

        if(list.size()>0){
            Intent newIntent = new Intent();
            newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            newIntent.setAction(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(ViewReportByDateActivity.this, getApplicationContext().getPackageName() + ".provider", pdfFile);
            newIntent.setDataAndType(uri, "application/pdf");
            startActivity(newIntent);
        }
    }

    void initDashboard(){
        findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewReportByDateActivity.this, ProfileActivity.class));
            }
        });
        findViewById(R.id.dashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewReportByDateActivity.this, DashboardActivity.class));
                finish();
            }
        });
        findViewById(R.id.milk_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewReportByDateActivity.this, MilkHistoryActivity.class));
            }
        });
        findViewById(R.id.view_all_entry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewReportByDateActivity.this, ViewAllEntryActivity.class));
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Logout(ViewReportByDateActivity.this);
            }
        });
        findViewById(R.id.recover_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WarningDialog(ViewReportByDateActivity.this).show();
            }
        });
        findViewById(R.id.backup_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(ViewReportByDateActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                // Send user's phone number for verification
                Date dateIntermediate = new Date();
                String date = new SimpleDateFormat("dd/MM/YYYY").format(dateIntermediate);
                Paper.book().write(Prevalent.last_update, date);
                new BackupHandler(ViewReportByDateActivity.this);
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
                startActivity(new Intent(ViewReportByDateActivity.this, DeleteHistory.class));
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
                startActivity(new Intent(ViewReportByDateActivity.this, UpgradeToPremium.class));
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

    @Override
    public void onBackPressed() {
       // startActivity(new Intent(ViewReportByDateActivity.this, ViewBuyerReportActivity.class));
        finish();
    }
}
