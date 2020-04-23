package com.dixit.dairydaily.UI.Dashboard.DrawerLayout;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.dixit.dairydaily.Models.MilkHistoryObject;
import com.dixit.dairydaily.MyAdapters.MilkHistoryAdapter;
import com.dixit.dairydaily.Others.BackupHandler;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.Others.Logout;
import com.dixit.dairydaily.Others.Prevalent;
import com.dixit.dairydaily.Others.WarningDialog;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;

import static com.dixit.dairydaily.Others.UtilityMethods.getEndDate;
import static com.dixit.dairydaily.Others.UtilityMethods.getStartDate;
import static com.dixit.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.dixit.dairydaily.Others.UtilityMethods.truncate;

public class MilkHistoryActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    File pdfFile;
    RelativeLayout cal1, cal2;
    TextView weightTotal, amountTotal;
    TextView start_date_text_view, end_date_text_view;
    RecyclerView recyclerView;
    ConstraintLayout scrollview;
    Button print;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    private static final String TAG = "MilkHistoryActivity";

    ArrayList<MilkHistoryObject> list;
    DbHelper helper = new DbHelper(this);

    String startDate, endDate;

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.milk_history);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        hideKeyboard(MilkHistoryActivity.this);

        getSupportActionBar().setTitle("Milk History");
        //getSupportActionBar().setHomeButtonEnabled(true);
        // Initialize widgets
        navigationView = findViewById(R.id.nav_view);
        weightTotal = findViewById(R.id.weightTotal);
        amountTotal = findViewById(R.id.amountTotal);
        scrollview = findViewById(R.id.constraintlayout);
        cal1= findViewById(R.id.cal1);
        cal2 = findViewById(R.id.cal2);
        start_date_text_view = findViewById(R.id.start_date_text_view);
        end_date_text_view = findViewById(R.id.end_date_text_view);
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        print = findViewById(R.id.print);

        startDate = getStartDate();
        endDate = getEndDate();

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

        drawerLayout = findViewById(R.id.drawerlayout);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.dashboard:
                        startActivity(new Intent(MilkHistoryActivity.this, DashboardActivity.class));
                        finish();
                        return true;
                    default:
                        return false;
                }
            }
        });

        findViewById(R.id.pdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPdfWrapper();
            }
        });


        Log.d("MilkHistory", "startDate, endDate: " + startDate + " " + endDate);
        list = helper.getMilkHistory(startDate, endDate);
        MilkHistoryAdapter adapter = new MilkHistoryAdapter(MilkHistoryActivity.this, list);
        recyclerView.setAdapter(adapter);
        double totalAmount = 0, totalWeight = 0;
        for(MilkHistoryObject object : list){
            totalAmount += Double.valueOf(object.getAmount());
            totalWeight += Double.valueOf(object.getWeight());
        }
        amountTotal.setText(truncate(totalAmount)+"Rs");
        weightTotal.setText(truncate(totalWeight)+"Rs");

        initDashboard();
        // Hook up the "Go" button
        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String startDate = start_date_text_view.getText().toString();
                String endDate = end_date_text_view.getText().toString();
                list = helper.getMilkHistory(startDate, endDate);
                double totalAmount = 0, totalWeight = 0;
                for(MilkHistoryObject object : list){
                    totalAmount += Double.valueOf(object.getAmount());
                    totalWeight += Double.valueOf(object.getWeight());
                }
                amountTotal.setText(String.valueOf(totalAmount)+"Rs");
                weightTotal.setText(String.valueOf(totalWeight)+"Ltr");
                MilkHistoryAdapter adapter = new MilkHistoryAdapter(MilkHistoryActivity.this, list);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private void showCalendarDialog1() {
        Dialog dialog = new Dialog(MilkHistoryActivity.this);
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
        Dialog dialog = new Dialog(MilkHistoryActivity.this);
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
            e.printStackTrace();
        }
    }

    private void createPdf() throws IOException, DocumentException {
        File docFolder = new File(Environment.getExternalStorageDirectory() + "/DairyDaily/ShiftReport");
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
        table.addCell("Weight(Ltr)");
        table.addCell("Amount(Rs)");
        table.addCell("Fat(%)");

        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();

        for(int i = 0; i<cells.length; i++){
            cells[i].setBackgroundColor(BaseColor.WHITE);
        }
        for(int j = 0; j <list.size(); j++){
            table.addCell(String.valueOf(list.get(j).getDate()));
            table.addCell(list.get(j).getSession());
            table.addCell(list.get(j).getWeight());
            table.addCell(list.get(j).getAmount());
            table.addCell(list.get(j).getFat());
        }

        PdfWriter.getInstance(document, outputStream);
        document.open();
        Font f  = new Font(Font.FontFamily.HELVETICA, 17.0f, Font.BOLD, BaseColor.BLACK);
        Font f1 = new Font(Font.FontFamily.HELVETICA, 17.0f, Font.BOLDITALIC, BaseColor.BLACK);
        document.add(new Paragraph("DAIRYDAILY APP\n\n",f));
        Paragraph header = new Paragraph("Milk History",f1);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);
        Paragraph p1 = new Paragraph("Username: " + Paper.book().read(Prevalent.name) + "\nPhone Number: " + Paper.book().read(Prevalent.phone_number),f);
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);
        document.add(new Paragraph("Date: "+date,f));
        Paragraph range = new Paragraph(startDate + " - " + endDate + "\n\n",f);
        range.setAlignment(Element.ALIGN_CENTER);
        document.add(range);

        document.add(table);

        PdfPTable table1 = new PdfPTable(new float[]{2,2,2,2,2,2});
        table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table1.getDefaultCell().setFixedHeight(20);
        table1.setTotalWidth(PageSize.A4.getWidth());

        table1.setWidthPercentage(100);
        table1.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table1.addCell("Total Weight: " + weightTotal.getText().toString());
        table1.addCell("Total Amount: " + amountTotal.getText().toString());
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
            Uri uri = FileProvider.getUriForFile(MilkHistoryActivity.this, getApplicationContext().getPackageName() + ".provider", pdfFile);
            newIntent.setDataAndType(uri, "application/pdf");
            startActivity(newIntent);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    void initDashboard(){
        findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MilkHistoryActivity.this, ProfileActivity.class));
            }
        });
        findViewById(R.id.dashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MilkHistoryActivity.this, DashboardActivity.class));
                finish();
            }
        });
        findViewById(R.id.view_all_entry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MilkHistoryActivity.this, ViewAllEntryActivity.class));
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Logout(MilkHistoryActivity.this);
            }
        });
        findViewById(R.id.recover_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WarningDialog(MilkHistoryActivity.this).show();
            }
        });
        findViewById(R.id.backup_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MilkHistoryActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                // Send user's phone number for verification
                Date dateIntermediate = new Date();
                String date;
                try{
                    DateFormat df = new DateFormat();
                    date = df.format("yyyy-MM-dd", dateIntermediate).toString();
                }
                catch(Exception e){
                    date = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
                }

                Paper.book().write(Prevalent.last_update, date);
                new BackupHandler(MilkHistoryActivity.this);
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
                startActivity(new Intent(MilkHistoryActivity.this, DeleteHistory.class));
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
                startActivity(new Intent(MilkHistoryActivity.this, UpgradeToPremium.class));
            }
        });
        findViewById(R.id.legal_policies).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
