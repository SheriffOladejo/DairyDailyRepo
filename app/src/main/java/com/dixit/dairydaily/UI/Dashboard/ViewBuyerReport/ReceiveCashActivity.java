package com.dixit.dairydaily.UI.Dashboard.ViewBuyerReport;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.InitDrawerBoard;
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
import com.dixit.dairydaily.Models.ReceiveCashModel;
import com.dixit.dairydaily.MyAdapters.ReceiveCashAdapter;
import com.dixit.dairydaily.Others.BackupHandler;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.Others.Logout;
import com.dixit.dairydaily.Others.Prevalent;
import com.dixit.dairydaily.Others.ReceiveCashDialog;
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

import static com.dixit.dairydaily.Others.UtilityMethods.getEndDate;
import static com.dixit.dairydaily.Others.UtilityMethods.getFirstname;
import static com.dixit.dairydaily.Others.UtilityMethods.getStartDate;
import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.Others.UtilityMethods.truncate;

public class ReceiveCashActivity extends InitDrawerBoard implements DatePickerDialog.OnDateSetListener{

    private static final int PERMISSION_REQUEST_CODE = 1;
    File pdfFile;
    TextView nameView;
    TextView start_date_text_view, end_date_text_view;
    RelativeLayout relativeLayout;

    private static final String TAG = "ReceiveCashActivity";

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    public static ArrayList<ReceiveCashModel> list;
    public static double creditTotal = 0;
    public static double debitTotal = 0;
    public static double remain = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        creditTotal = 0;
        debitTotal = 0;
        remain = 0;
    }

    private void showCalendarDialog1() {
        Dialog dialog = new Dialog(ReceiveCashActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_view_dialog);
        CalendarView cal = dialog.findViewById(R.id.calendar_view);
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
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
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showCalendarDialog2() {
        Dialog dialog = new Dialog(ReceiveCashActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_view_dialog);
        CalendarView cal = dialog.findViewById(R.id.calendar_view);
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
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
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    ProgressDialog loadingBar;

    ProgressBar progressBar;

    Button go, receive_cash, send_msg;
    public static RecyclerView recyclerView;
    public static ReceiveCashAdapter adapter;

    int idInt;
    String name;

    public static String startDate, endDate;

    DatePickerDialog startDatePickerDialog;
    DatePickerDialog endDatePickerDialog;

    public DbHelper dbHelper = new DbHelper(this);

    public static TextView totalCredit;
    public static TextView totalDebit;
    public static TextView remaining;
    RelativeLayout cal1, cal2;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_cash);

        startDate = getStartDate();
        endDate = getEndDate();

        getSupportActionBar().setTitle("Receive Cash");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        progressBar = new ProgressBar(this);

        loadingBar = new ProgressDialog(this);

        View view = findViewById(R.id.view);
        view.setVisibility(View.GONE);

        drawerLayout = findViewById(R.id.drawerlayout);
        cal1 = findViewById(R.id.cal1);
        cal2 = findViewById(R.id.cal2);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initDrawer();

        idInt = getIntent().getIntExtra("id", 0);
        name = getIntent().getStringExtra("name");

        start_date_text_view = findViewById(R.id.start_date_text_view);
        end_date_text_view = findViewById(R.id.end_date_text_view);
        go = findViewById(R.id.go);
        go.setVisibility(View.GONE);
        totalCredit = findViewById(R.id.totalCredit);
        totalDebit = findViewById(R.id.totalDebit);
        remaining = findViewById(R.id.remaining);
        receive_cash = findViewById(R.id.receive_cash);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        send_msg = findViewById(R.id.send_msg);
        nameView = findViewById(R.id.name);
        relativeLayout = findViewById(R.id.linearLayout4);
        relativeLayout.setVisibility(View.GONE);

        list = dbHelper.getReceiveCash(idInt, "", "");
        adapter = new ReceiveCashAdapter(ReceiveCashActivity.this, list);
        for(ReceiveCashModel model : list){
            creditTotal += Double.valueOf(model.getCredit());
            debitTotal += Double.valueOf(model.getDebit());
        }
        remain = creditTotal - debitTotal;
        totalCredit.setText(String.valueOf(truncate(creditTotal)) + "Rs");
        totalDebit.setText(String.valueOf(truncate(debitTotal)) + "Rs");
        remaining.setText(String.valueOf(truncate(-remain)) + "Rs");
        recyclerView.setAdapter(adapter);

        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_number = dbHelper.getBuyerPhone_Number(idInt);
                String toSend = "TOTAL DEBIT: " + truncate(debitTotal)+"Rs";
                toSend += "\nTOTAL CREDIT: " + truncate(creditTotal)+"Rs";
                toSend += "\nOUTSTANDING: " + truncate(-remain)+"Rs";
                toSend += "\nThank you for using DairyDaily!";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.putExtra("address", phone_number);
                i.putExtra("sms_body", toSend);
                i.setType("vnd.android-dir/mms-sms");
                startActivity(i);
            }
        });

        findViewById(R.id.pdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPdfWrapper();
            }
        });

        receive_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReceiveCashDialog(ReceiveCashActivity.this, idInt).show();
            }
        });

        nameView.setText(name);
        go.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        creditTotal = 0;
                        debitTotal=0;
                        remain=0;

                        if(idInt != -1){
                            if(name.equals("All Buyers") || name.equals("Not Found")){

                            }
                            else{
                                try{
                                    if(startDate.isEmpty() || endDate.isEmpty()){
                                        Toast.makeText(ReceiveCashActivity.this, "Operation failed", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        list = dbHelper.getReceiveCash(idInt, startDate, endDate);
                                        Log.d("Sheriff", "receive cash: " + list.size());
                                        ReceiveCashAdapter adapter = new ReceiveCashAdapter(ReceiveCashActivity.this, list);
                                        for(ReceiveCashModel model : list){
                                            creditTotal += Double.valueOf(model.getCredit());
                                            debitTotal += Double.valueOf(model.getDebit());
                                        }
                                        remain = creditTotal - debitTotal;
                                        totalCredit.setText(String.valueOf(truncate(creditTotal)) + "Rs");
                                        totalDebit.setText(String.valueOf(truncate(debitTotal)) + "Rs");
                                        remaining.setText(String.valueOf(truncate(-remain)) + "Rs");
                                        recyclerView.setAdapter(adapter);
                                    }
                                }
                                catch(Exception e){
                                    Log.d("Sheriff", "receive cash: " + e.getMessage());
                                }
                            }
                        }
                    }
                }
        );

        start_date_text_view.setText(startDate);
        end_date_text_view.setText(endDate);

        cal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarDialog1();
            }
        });
        cal2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarDialog2();
            }
        });

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
        File docFolder = new File(Environment.getExternalStorageDirectory() + "/DairyDaily/ReceiveCash");
        if((!docFolder.exists())){
            docFolder.mkdirs();
            Log.i(TAG, "Created a new directory for invoice");
        }
        String date;
        try{
            DateFormat df = new DateFormat();
            date = df.format("yyyy-MM-dd", new Date()).toString();
        }
        catch(Exception e){
            date = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
        }
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
        table.addCell("Date");
        table.addCell("Title");
        table.addCell("Debit(Rs)");
        table.addCell("Credit(Rs)");
        table.addCell("Shift");

        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();

        for(int i = 0; i<cells.length; i++){
            cells[i].setBackgroundColor(BaseColor.WHITE);
        }
        for(int j = 0; j <list.size(); j++){
            table.addCell(String.valueOf(list.get(j).getDate()));
            table.addCell(list.get(j).getTitle());
            table.addCell(list.get(j).getDebit());
            table.addCell(list.get(j).getCredit());
            table.addCell(list.get(j).getShift());
        }

        PdfWriter.getInstance(document, outputStream);
        document.open();
        Font f  = new Font(Font.FontFamily.HELVETICA, 17.0f, Font.BOLD, BaseColor.BLACK);
        Font f1 = new Font(Font.FontFamily.HELVETICA, 17.0f, Font.BOLDITALIC, BaseColor.BLACK);
        document.add(new Paragraph("DAIRYDAILY APP\n\n",f));
        Paragraph header = new Paragraph("Receive Cash",f1);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);
        Paragraph p1 = new Paragraph("Username: " + Paper.book().read(Prevalent.name) + "\nPhone Number: " + Paper.book().read(Prevalent.phone_number),f);
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(new Paragraph(date,f));
        document.add(new Paragraph("ID: " + idInt + "\nName: " + name,f));
        Paragraph range = new Paragraph(startDate + " - " + endDate + "\n\n",f);
        range.setAlignment(Element.ALIGN_CENTER);
        document.add(range);
        document.add(table);
        PdfPTable table1 = new PdfPTable(new float[]{2,2,2});
        table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table1.getDefaultCell().setFixedHeight(20);
        table1.setTotalWidth(PageSize.A4.getWidth());

        table1.setWidthPercentage(100);
        table1.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table1.addCell("Balance(Rs): " + truncate(remain));
        table1.addCell("Total Debit(Rs): " + truncate(debitTotal));
        table1.addCell("Total Credit(Rs): " + truncate(creditTotal));
        document.add(table1);

        String link = "http://play.google.com/store/apps/details?id=" + getPackageName();
        document.add(new Paragraph("Download DairyDaily app from google playstore:\n" + link ,f));
        document.close();
        toast(ReceiveCashActivity.this, "PDF saved to " + docFolder.getPath() + "/" +date + ".pdf");
        loadingBar.dismiss();
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
            Uri uri = FileProvider.getUriForFile(ReceiveCashActivity.this, getApplicationContext().getPackageName() + ".provider", pdfFile);
            newIntent.setDataAndType(uri, "application/pdf");
            startActivity(newIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.printer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;
        int id = item.getItemId();
        if (id == R.id.printer) {
            if(!list.isEmpty()){
                //Collections.reverse(list);
                String line = "------------------------------";
                Date dateIntermediate = new Date();
                String date = new SimpleDateFormat("dd/MM/YYYY").format(dateIntermediate);
                String toPrint ="\nID |" +idInt + "   " + dbHelper.getBuyerName(idInt) + "\n"+date + "\n"+ "All History" + "\n   Date   |"  + "Title|" + "Debit |" + "Credit|\n" +line + "\n";

                for(ReceiveCashModel object : list){
                    String title = StringUtils.rightPad(getFirstname(StringUtils.truncate(object.getTitle(), 5)), 5, "");
                    String credit = StringUtils.rightPad(StringUtils.truncate(object.getCredit(),6), 6, "");
                    String debit = StringUtils.rightPad(StringUtils.truncate(object.getDebit(),6), 6, "");
                    String datee = object.getDate();
                    String shift = object.getShift().substring(0,1);
                    toPrint += datee+ "|"+title + "|" + StringUtils.rightPad(truncate(Double.valueOf(debit)), 6, "") + "|" + StringUtils.rightPad(truncate(Double.valueOf(credit)), 6, "") + "|\n";
                }
                toPrint += line + "\n";
                toPrint += "TOTAL CREDIT: "+ truncate(creditTotal) + "Rs\n";
                toPrint += "TOTAL DEBIT: " + truncate(debitTotal) + "Rs\n";
                toPrint += "AMOUNT REMAINING: " + truncate(-remain) + "Rs\n";
                toPrint += line + "\n";
                toPrint += "       DAIRY DAILY APP";
                Log.d(TAG, "toPrint: "+toPrint);

                byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());
                if(DashboardActivity.bluetoothAdapter != null) {
                    if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                        try {
                            DashboardActivity.bluetoothConnectionService.write(mybyte);
                            Log.d(TAG, "onOptionsSelected: Printing with DashboardActivity BT");
                        } catch (Exception e) {
                            Log.d(TAG, "onOptionsSelected: Unable to print");
                            toast(ReceiveCashActivity.this, "Unable to print");
                        }
                    } else {
                        toast(ReceiveCashActivity.this, "Printer is not connected");
                    }
                }
                else{
                    toast(ReceiveCashActivity.this, "Bluetooth is off");
                }
            }
        }
        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(ReceiveCashActivity.this, ViewBuyerReportActivity.class));
        finish();
    }
}
