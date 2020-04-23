package com.dixit.dairydaily.UI.Dashboard.ViewBuyerReport;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
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
import com.dixit.dairydaily.Models.ReceiveCashListModel;
import com.dixit.dairydaily.MyAdapters.InvoiceAdapter;
import com.dixit.dairydaily.Others.BackupHandler;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.Others.Logout;
import com.dixit.dairydaily.Others.Prevalent;
import com.dixit.dairydaily.Others.WarningDialog;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.DeleteHistory;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.MilkHistoryActivity;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.ProfileActivity;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.UpgradeToPremium;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.ViewAllEntryActivity;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;

import static com.dixit.dairydaily.Others.UtilityMethods.getFirstname;
import static com.dixit.dairydaily.Others.UtilityMethods.toast;

public class InvoiceActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    double totalAmount = 0,totalWeight = 0;
//    ImageView start_date_image;
//    ImageView end_date_image;
//    TextView start_date_text_view, end_date_text_view;
    ConstraintLayout scrollview;
    private static final String TAG = "InvoiceActivity";
    public static RecyclerView recyclerView;
    public static InvoiceAdapter adapter;
    Button print;
    public static ArrayList<ReceiveCashListModel> list;
    DbHelper dbHelper = new DbHelper(this);
    File pdfFile;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    //String startDate, endDate;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        getSupportActionBar().setTitle("Invoice");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initDashboard();

        // Initialize widgets
        //start_date_image = findViewById(R.id.start_date_image_view);
        scrollview = findViewById(R.id.constraintlayout);
//        end_date_image = findViewById(R.id.end_date_image_view);
//        start_date_text_view = findViewById(R.id.start_date_text_view);
//        end_date_text_view = findViewById(R.id.end_date_text_view);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        print = findViewById(R.id.print);


        Paper.init(this);
        findViewById(R.id.pdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPdfWrapper();
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(list!=null){
                    //Collections.reverse(list);
                    String line = "----------------------------";
                    ArrayList<ReceiveCashListModel> toRemove = new ArrayList<>();
                    Date dateIntermediate = new Date();
                    String date = new SimpleDateFormat("dd/MM/YYYY").format(dateIntermediate);
                    String toPrint ="\n"+date + "\n"+ "\nID | "  + " NAME   |" + "WEIGHT|" + "AMOUNT|\n" +line + "\n";

                    for(ReceiveCashListModel object : list){
                        String amount = object.getDue();
                        String weight = object.getWeight();
                        if(amount.equals("0") || weight.equals("0")){
                            toRemove.add(object);
                        }
                    }
                    list.removeAll(toRemove);
                    for(ReceiveCashListModel object : list){
                        String id = StringUtils.rightPad(object.getId(), 3, "");
                        String name = StringUtils.rightPad(StringUtils.truncate(getFirstname(object.getName()), 9), 9, "");
                        String amount = object.getDue();
                        String weight = object.getWeight();
                        try{
                            totalAmount += Double.valueOf(amount);
                            totalWeight += Double.valueOf(weight);
                        }
                        catch(Exception e){

                        }
                        toPrint += id + "|"+name + "|" + StringUtils.rightPad(weight, 6, "") + "|" + StringUtils.rightPad(amount,6,"") + "|\n";
                    }
                    toPrint += line + "\nTOTAL AMOUNT: "+ totalAmount + "Rs\n";
                    toPrint += "TOTAL WEIGHT: " + totalWeight + "Ltr\n";
                    toPrint += line + "\n";
                    toPrint += "       DAIRY DAILY APP\n\n";
                    Log.d(TAG, "toPrint: "+toPrint);

                    byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());
                    if(DashboardActivity.bluetoothAdapter != null) {
                        if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                            try {
                                DashboardActivity.bluetoothConnectionService.write(mybyte);
                                Log.d(TAG, "onOptionsSelected: Printing with DashboardActivity BT");
                            } catch (Exception e) {
                                Log.d(TAG, "onOptionsSelected: Unable to print");
                                toast(InvoiceActivity.this, "Unable to print");
                            }
                        } else {
                            toast(InvoiceActivity.this, "Printer is not connected");
                        }
                    }
                    else{
                        toast(InvoiceActivity.this, "Bluetooth is off");
                    }
                }
            }
        });
        list = dbHelper.getReceiveCashList();
        adapter = new InvoiceAdapter(InvoiceActivity.this, list);
        recyclerView.setAdapter(adapter);


        //final DatePickerDialog startDatePickerDialog = new DatePickerDialog(this), endDatePickerDialog = new DatePickerDialog(this);
//        startDate = getStartDate();
//        endDate = getEndDate();
//
//        start_date_text_view.setText(startDate);
//        end_date_text_view.setText(endDate);
//
//        start_date_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startDatePickerDialog.show();
//            }
//        });
//        end_date_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                endDatePickerDialog.show();
//            }
//        });
//
//        startDatePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                if(String.valueOf(month).length() == 1){
//                    if(String.valueOf(dayOfMonth).length() == 1){
//                        startDate = year + "-0" + (month+1) + "-" + "0"+dayOfMonth;
//                        start_date_text_view.setText(startDate);
//                    }
//                    else{
//                        startDate = year + "-0" + (month+1) + "-" + dayOfMonth;
//                        start_date_text_view.setText(startDate);
//                    }
//                }
//                else{
//                    startDate = year + "-" + (month+1) + "-" + dayOfMonth;
//                    start_date_text_view.setText(startDate);
//                }
//            }
//        });
//
//        endDatePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                if(String.valueOf(month).length() == 1){
//                    if(String.valueOf(dayOfMonth).length() == 1){
//                        endDate = year+ "-0" + (month+1) + "-" + "0"+dayOfMonth;
//                        end_date_text_view.setText(endDate);
//                    }
//                    else{
//                        endDate = year + "-0" + (month+1) + "-" + dayOfMonth;
//                        end_date_text_view.setText(endDate);
//                    }
//                }
//                else{
//                    endDate = year + "-" + (month+1) + "-" + dayOfMonth;
//                    end_date_text_view.setText(endDate);
//                }
//            }
//        });
//        list = dbHelper.getInvoice("", "");
//        InvoiceAdapter adapter = new InvoiceAdapter(InvoiceActivity.this, list);
//        recyclerView.setAdapter(adapter);

//        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String startDate = start_date_text_view.getText().toString();
//                String endDate = end_date_text_view.getText().toString();
//                list = dbHelper.getInvoice(startDate, endDate);
//                InvoiceAdapter adapter = new InvoiceAdapter(InvoiceActivity.this, list);
//                recyclerView.setAdapter(adapter);
//            }
//        });
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

    private void createPdf() throws IOException, DocumentException {
        File docFolder = new File(Environment.getExternalStorageDirectory() + "/DairyDaily/Invoice");
        if((!docFolder.exists())){
            docFolder.mkdirs();
            Log.i(TAG, "Created a new directory for invoice");
        }
        String date = new SimpleDateFormat("dd-MM-YYYY").format(new Date());
        String pdfName = date + ".pdf";

        pdfFile = new File(docFolder.getAbsolutePath(), pdfName);

        Paper.init(this);

        OutputStream outputStream = new FileOutputStream(pdfFile);
        Document document = new Document(PageSize.A4);
        PdfPTable table = new PdfPTable(new float[]{2,2,2,2,2});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(20);
        table.setTotalWidth(PageSize.A4.getWidth());

        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell("ID");
        table.addCell("Name");
        table.addCell("Weight(Ltr)");
        table.addCell("Amount(Rs)");
        table.addCell("Status");

        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();

        for(int i = 0; i<cells.length; i++){
            cells[i].setBackgroundColor(BaseColor.WHITE);
        }
        for(int j = 0; j <list.size(); j++){
            table.addCell(String.valueOf(list.get(j).getId()));
            table.addCell(list.get(j).getName());
            table.addCell(list.get(j).getWeight());
            table.addCell(list.get(j).getDue());
            table.addCell("Unpaid");
        }

        totalWeight = 0;
        totalAmount = 0;
        ArrayList<ReceiveCashListModel> toRemove = new ArrayList<>();
        for(ReceiveCashListModel object : list){
            String amount = object.getDue();
            String weight = object.getWeight();
            if(amount.equals("0") || weight.equals("0")){
                toRemove.add(object);
            }
        }
        list.removeAll(toRemove);
        for(ReceiveCashListModel object : list){
            String id = StringUtils.rightPad(object.getId(), 3, "");
            String name = StringUtils.rightPad(StringUtils.truncate(getFirstname(object.getName()), 9), 9, "");
            String amount = object.getDue();
            String weight = object.getWeight();
            try{
                totalAmount += Double.valueOf(amount);
                totalWeight += Double.valueOf(weight);
            }
            catch(Exception e){

            }
        }
        PdfPTable table1 = new PdfPTable(new float[]{2,2,2,2});
        table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table1.getDefaultCell().setFixedHeight(20);
        table1.setTotalWidth(PageSize.A4.getWidth());

        table1.setWidthPercentage(100);
        table1.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

        Log.d(TAG, "totalAmount: " + totalAmount);
        Log.d(TAG, "totalWeight: " + totalWeight);
        table1.addCell("Total Amount(Rs)");
        table1.addCell(totalAmount+"");
        table1.addCell("Total Weight(Ltr)");
        table1.addCell(totalWeight+"");

        PdfWriter.getInstance(document, outputStream);
        document.open();
        Font f  = new Font(Font.FontFamily.HELVETICA, 17.0f, Font.BOLD, BaseColor.BLACK);
        Font f1 = new Font(Font.FontFamily.HELVETICA, 17.0f, Font.BOLDITALIC, BaseColor.BLACK);
        document.add(new Paragraph("DAIRYDAILY APP\n\n",f));
        Paragraph header = new Paragraph("Invoice",f1);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);
        Paragraph p1 = new Paragraph("Username: " + Paper.book().read(Prevalent.name) + "\nPhone Number: " + Paper.book().read(Prevalent.phone_number),f);
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(new Paragraph("Date: "+date+"\n\n",f));
        document.add(table);
        document.add(table1);

        document.add(new Paragraph("DairyDaily Download App Now:\nHttps://www.google.playstore.com/DairyDaily",f));
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
            Uri uri = FileProvider.getUriForFile(InvoiceActivity.this, getApplicationContext().getPackageName() + ".provider", pdfFile);
            newIntent.setDataAndType(uri, "application/pdf");
            startActivity(newIntent);
        }
    }


    void initDashboard(){
        findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InvoiceActivity.this, ProfileActivity.class));
            }
        });
        findViewById(R.id.dashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InvoiceActivity.this, DashboardActivity.class));
                finish();
            }
        });
        findViewById(R.id.view_all_entry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InvoiceActivity.this, ViewAllEntryActivity.class));
            }
        });
        findViewById(R.id.milk_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InvoiceActivity.this, MilkHistoryActivity.class));
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Logout(InvoiceActivity.this);
            }
        });
        findViewById(R.id.recover_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WarningDialog(InvoiceActivity.this).show();
            }
        });
        findViewById(R.id.backup_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(InvoiceActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                // Send user's phone number for verification
                Date dateIntermediate = new Date();
                String date = new SimpleDateFormat("dd/MM/YYYY").format(dateIntermediate);
                Paper.book().write(Prevalent.last_update, date);
                new BackupHandler(InvoiceActivity.this);
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
                startActivity(new Intent(InvoiceActivity.this, DeleteHistory.class));
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
                startActivity(new Intent(InvoiceActivity.this, UpgradeToPremium.class));
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
        //startActivity(new Intent(InvoiceActivity.this, ViewBuyerReportActivity.class));
        finish();
    }
}
