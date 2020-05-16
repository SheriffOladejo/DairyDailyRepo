package com.dixit.dairydaily.UI.Dashboard.DrawerLayout;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.dixit.dairydaily.Others.BackupHandler;
import com.dixit.dairydaily.Others.DataRetrievalHandler;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.Others.Logout;
import com.dixit.dairydaily.Others.Prevalent;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import io.paperdb.Paper;

public class InitDrawerBoard extends AppCompatActivity {


    LinearLayout dashboard, profile, upgrade_to_premium, view_all_entry,
            milk_history, settings, logout, update_rate_charts, erase_milk_history,
            retrieve_data, backup_data;
    public static ProgressDialog rpd, bpd;
    ImageView arrow;
    boolean[] arrowClicked = {false};
    ProgressDialog progressDialog;
    private static long downloadId;

    public InitDrawerBoard(){}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_board);
    }

    public void initDrawer(){
        profile = findViewById(R.id.profile);
        dashboard = findViewById(R.id.dashboard);
        upgrade_to_premium = findViewById(R.id.upgrade);
        view_all_entry = findViewById(R.id.view_all_entry);
        milk_history = findViewById(R.id.milk_history);
        settings = findViewById(R.id.settings);
        logout = findViewById(R.id.logout);
        update_rate_charts = findViewById(R.id.update_rate_charts);
        erase_milk_history = findViewById(R.id.erase_milk_history);
        arrow = findViewById(R.id.arrow);
        retrieve_data = findViewById(R.id.retrieve_data);
        backup_data = findViewById(R.id.backup_data);
        update_rate_charts.setVisibility(View.GONE);
        erase_milk_history.setVisibility(View.GONE);
        backup_data.setVisibility(View.GONE);
        retrieve_data.setVisibility(View.GONE);

        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        progressDialog = new ProgressDialog(InitDrawerBoard.this);
        progressDialog.setMessage("Updating...");
        progressDialog.setCancelable(true);

        retrieve_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(InitDrawerBoard.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.warning);
                dialog.setCancelable(true);
                TextView cancel = dialog.findViewById(R.id.cancel);
                TextView continueText = dialog.findViewById(R.id.continues);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                continueText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rpd = new ProgressDialog(InitDrawerBoard.this);
                        rpd.setMessage("Retrieving...");
                        rpd.setCancelable(false);
                        rpd.show();
                        new DataRetrievalHandler(InitDrawerBoard.this);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        backup_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpd = new ProgressDialog(InitDrawerBoard.this);
                bpd.setMessage("Backing Up...");
                bpd.setCancelable(true);
                bpd.show();
                new BackupHandler(InitDrawerBoard.this);
            }
        });

        erase_milk_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InitDrawerBoard.this, DeleteHistory.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InitDrawerBoard.this, ProfileActivity.class));
            }
        });

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InitDrawerBoard.this, DashboardActivity.class));
            }
        });

        upgrade_to_premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InitDrawerBoard.this, UpgradeToPremium.class));
            }
        });

        view_all_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InitDrawerBoard.this, ViewAllEntryActivity.class));
            }
        });

        milk_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InitDrawerBoard.this, MilkHistoryActivity.class));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrowClicked[0]){
                    erase_milk_history.setVisibility(View.GONE);
                    update_rate_charts.setVisibility(View.GONE);
                    backup_data.setVisibility(View.GONE);
                    retrieve_data.setVisibility(View.GONE);
                    arrowClicked[0] = false;
                    arrow.setImageResource(R.drawable.ic_drop_down);
                }
                else{
                    arrow.setImageResource(R.drawable.drop_down);
                    erase_milk_history.setVisibility(View.VISIBLE);
                    update_rate_charts.setVisibility(View.VISIBLE);
                    backup_data.setVisibility(View.VISIBLE);
                    retrieve_data.setVisibility(View.VISIBLE);
                    arrowClicked[0] = true;
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Logout(InitDrawerBoard.this);
            }
        });

        update_rate_charts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                StorageReference ref = FirebaseStorage.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number)).child("Rate File");
                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            String url = task.getResult().toString();
                            Toast.makeText(InitDrawerBoard.this, "Downloading file...", Toast.LENGTH_SHORT).show();
                            DownloadFile(InitDrawerBoard.this, "Rate File", ".csv", "/dairyDaily", url);
                        }
                        else{
                            Toast.makeText(InitDrawerBoard.this, "Something went wrong when downloading the file.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });

    }

    private void DownloadFile(Context context, String fileName, String fileExtension, String destinationDirectoy, String url) {
        File file = new File(Environment.getExternalStorageDirectory() + "/Download/", "Rate File.csv");
        file.delete();
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
                DbHelper helper = new DbHelper(InitDrawerBoard.this);
                helper.clearSNFTable();
                helper.createSNFTable(Environment.getExternalStorageDirectory() + "/Download/Rate File.csv");
                File file = new File(Environment.getExternalStorageDirectory() + "/Download/", "Rate File.csv");
                file.delete();
                progressDialog.dismiss();
                Toast.makeText(context, "Chart Updated", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }
}
