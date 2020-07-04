package com.dixit.dairydaily.UI.Dashboard.ViewBuyerReport;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.dixit.dairydaily.Models.BuyerRegisterModel;
import com.dixit.dairydaily.MyAdapters.BuyerRegisterAdapter;
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

import static com.dixit.dairydaily.Others.UtilityMethods.getEndDate;
import static com.dixit.dairydaily.Others.UtilityMethods.getFirstname;
import static com.dixit.dairydaily.Others.UtilityMethods.getStartDate;
import static com.dixit.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.Others.UtilityMethods.truncate;

public class BuyerRegisterActivity extends InitDrawerBoard implements DatePickerDialog.OnDateSetListener {

    TextView start_date_text_view, end_date_text_view;
    RecyclerView recyclerView;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private File pdfFile;

    public static String phone_number;
    public static int count = 0;
    public static double totalWeight;
    public static double totalAmount;

    static BuyerRegisterAdapter adapter;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    static ConstraintLayout scrollview;
    RelativeLayout cal1, cal2;

    Button print;

    private static final String TAG = "BuyerRegisterActivity";

    ArrayList<BuyerRegisterModel> list;
    DbHelper dbHelper = new DbHelper(this);

    String startDate, endDate;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_register);

        getSupportActionBar().setTitle("Buyer Register");
        getSupportActionBar().setHomeButtonEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        hideKeyboard(this);

        // Initialize widgets
        cal1 = findViewById(R.id.cal1);
        cal2 = findViewById(R.id.cal2);
        scrollview = findViewById(R.id.constraintlayout);
        start_date_text_view = findViewById(R.id.start_date_text_view);
        end_date_text_view = findViewById(R.id.end_date_text_view);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = dbHelper.getBuyerRegister("", "");
        adapter = new BuyerRegisterAdapter(BuyerRegisterActivity.this, list);
        recyclerView.setAdapter(adapter);

        print = findViewById(R.id.print);

        initDrawer();
        phone_number = Paper.book().read(Prevalent.phone_number);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        findViewById(R.id.send_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!phone_number.equals("") && count == 1){
                    String toSend = "TOTAL AMOUNT: " + truncate(Double.valueOf(totalAmount))+"Rs";
                    toSend += "\nTOTAL WEIGHT: " + truncate(Double.valueOf(totalWeight))+"Ltr";
                    toSend += "\nThank you for using DairyDaily!";

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.putExtra("address", phone_number);
                    i.putExtra("sms_body", toSend);
                    i.setType("vnd.android-dir/mms-sms");
                    startActivity(i);
                    phone_number = "";
                    count = 0;
                }
                else{
                    toast(BuyerRegisterActivity.this, "Can send to one numbe at once");
                }
            }
        });

        findViewById(R.id.pdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPdfWrapper();
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!list.isEmpty()){
                    ArrayList<BuyerRegisterModel> toRemove = new ArrayList<>();
                    String line = "----------------------------";
                    Date dateIntermediate = new Date();
                    String date = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
                    String toPrint ="\n\n"+date+"\n"+ startDate + " To " + endDate + "\nID | "  + " NAME   |" + "WEIGHT|" + "AMOUNT|\n" +line + "\n";

                    double totalAmount = 0,totalWeight = 0;
                    for(BuyerRegisterModel object : list){
                        String amount = object.getAmount();
                        if(amount.equals("0"))
                            toRemove.add(object);
                    }
                    list.removeAll(toRemove);
                    for(BuyerRegisterModel object: list){
                        int id = object.getId();
                        String name = StringUtils.rightPad(StringUtils.truncate(getFirstname(object.getName()), 9), 9, "");
                        String amount = StringUtils.rightPad(StringUtils.truncate(object.getAmount(),6), 6, "");
                        String weight = StringUtils.rightPad(object.getWeight(),6,"");
                        totalAmount += Double.valueOf(amount);
                        totalWeight += Double.valueOf(weight);
                        toPrint += StringUtils.rightPad(""+id, 3, "") + "|"+name + "|" + weight + "|" + amount + "|\n";
                    }
                    toPrint += line + "\n";
                    toPrint += "TOTAL AMOUNT: "+ truncate(totalAmount) + "Rs\n";
                    toPrint += "TOTAL WEIGHT: " + truncate(totalWeight) + "Ltr\n";
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
                                toast(BuyerRegisterActivity.this, "Unable to print");
                            }
                        } else {
                            toast(BuyerRegisterActivity.this, "Printer is not connected");
                        }
                    }
                    else{
                        toast(BuyerRegisterActivity.this, "Bluetooth is off");
                    }
                }
            }
        });

        startDate = getStartDate();
        endDate = getEndDate();

        start_date_text_view.setText(startDate);
        end_date_text_view.setText(endDate);

        list = dbHelper.getBuyerRegister(startDate, endDate);
        adapter = new BuyerRegisterAdapter(BuyerRegisterActivity.this, list);
        recyclerView.setAdapter(adapter);

        // Hook up the "Go" button
        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startDate = start_date_text_view.getText().toString();
                String endDate = end_date_text_view.getText().toString();
                list = dbHelper.getBuyerRegister(startDate, endDate);
                BuyerRegisterAdapter adapter = new BuyerRegisterAdapter(BuyerRegisterActivity.this, list);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private void showCalendarDialog1() {
        Dialog dialog = new Dialog(BuyerRegisterActivity.this);
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
        Dialog dialog = new Dialog(BuyerRegisterActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_view_dialog);
        CalendarView cal = dialog.findViewById(R.id.calendar_view);
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
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
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void createPdfWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
        try {
            createPdf();
        } catch (Exception e) {
            Log.d(TAG, "exception: " +e.getMessage());
        }
    }

    private void createPdf() throws IOException, DocumentException {
        File docFolder = new File(Environment.getExternalStorageDirectory() + "/DairyDaily/BuyerRegister");
        if((!docFolder.exists())){
            docFolder.mkdirs();
            Log.i(TAG, "Created a new directory for buyerregister");
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

        ArrayList<BuyerRegisterModel> toRemove = new ArrayList<>();
        for(BuyerRegisterModel object : list){
            String amount = object.getAmount();
            if(amount.equals("0"))
                toRemove.add(object);
        }
        list.removeAll(toRemove);

        OutputStream outputStream = new FileOutputStream(pdfFile);
        Document document = new Document(PageSize.A4);
        PdfPTable table = new PdfPTable(new float[]{2,2,2,2});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(20);
        table.setTotalWidth(PageSize.A4.getWidth());

        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell("ID");
        table.addCell("Name");
        table.addCell("Total Weight(Ltr)");
        table.addCell("Total Amount(Rs)");

        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();

        for(int i = 0; i<cells.length; i++){
            cells[i].setBackgroundColor(BaseColor.WHITE);
        }
        totalAmount = 0;
        totalWeight = 0;
        for(int j = 0; j <list.size(); j++){
            table.addCell(String.valueOf(list.get(j).getId()));
            table.addCell(list.get(j).getName());
            table.addCell(list.get(j).getWeight());
            table.addCell(list.get(j).getAmount());
            totalAmount += Double.valueOf(list.get(j).getAmount());
            totalWeight += Double.valueOf(list.get(j).getWeight());
        }

        PdfWriter.getInstance(document, outputStream);
        document.open();
        Font f  = new Font(Font.FontFamily.HELVETICA, 17.0f, Font.BOLD, BaseColor.BLACK);
        Font f1 = new Font(Font.FontFamily.HELVETICA, 17.0f, Font.BOLDITALIC, BaseColor.BLACK);
        document.add(new Paragraph("DAIRYDAILY APP\n\n",f));
        Paragraph header = new Paragraph("Buyer Register",f1);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);
        Paragraph p1 = new Paragraph("Username: " + Paper.book().read(Prevalent.name) + "\nPhone Number: " + Paper.book().read(Prevalent.phone_number),f);
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(new Paragraph("Date: "+date,f));
        Paragraph range = new Paragraph(startDate + " - " + endDate + "\n\n",f);
        range.setAlignment(Element.ALIGN_CENTER);
        document.add(range);
        document.add(table);

        PdfPTable table1 = new PdfPTable(new float[]{2,2});
        table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table1.getDefaultCell().setFixedHeight(20);
        table1.setTotalWidth(PageSize.A4.getWidth());
        table1.setWidthPercentage(100);
        table1.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table1.addCell("Total Weight: " + truncate(Double.valueOf(totalWeight)));
        table1.addCell("Total Amount: " + truncate(Double.valueOf(totalAmount)));
        document.add(table1);

        String link = "http://play.google.com/store/apps/details?id=" + getPackageName();
        document.add(new Paragraph("Download DairyDaily app from google playstore:\n" + link ,f));
        document.close();
        toast(BuyerRegisterActivity.this, "PDF saved to " + docFolder.getPath() + "/" + date + ".pdf");
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
            Uri uri = FileProvider.getUriForFile(BuyerRegisterActivity.this, getApplicationContext().getPackageName() + ".provider", pdfFile);
            newIntent.setDataAndType(uri, "application/pdf");
            startActivity(newIntent);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(BuyerRegisterActivity.this, ViewBuyerReportActivity.class));
        finish();
    }
}
