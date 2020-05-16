package com.dixit.dairydaily.Others;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.dixit.dairydaily.UI.Login.LoginActivity;

import java.util.HashMap;

import io.paperdb.Paper;

import static com.dixit.dairydaily.Others.UtilityMethods.toast;

public class Logout extends AppCompatActivity {
    Context context;
    DbHelper helper;
    private static final String TAG = "Logout";


    public Logout(Context context){
        this.context = context;
        helper = new DbHelper(context);
        FirebaseAuth.getInstance().signOut();
        logout();
    }

    public void logout(){
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Logging out...");
        pd.setCancelable(false);
        pd.show();

        Paper.book().write(Prevalent.remember_me, "false");
        Paper.book().write(Prevalent.has_account, "false");

        //Paper.book().write(Prevalent.offline_password, "");
        new BackupHandler(context);

        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                helper.clearReceiveCash();
                helper.clearBuffaloSNFTable();
                helper.clearMilkSaleTable();
                helper.clearSNFTable();
                helper.clearSellerTable();
                helper.clearBuyerTable();
                helper.clearMilkBuyTable();
                helper.clearProducts();
                helper.clearProductSale();
                helper.clearRate();
                helper.destroyDb();
                String lastUpdated = Paper.book().read(Prevalent.last_update);
                String defaultPrinter = Paper.book().read(Prevalent.selected_device);
                HashMap<String, Object> details = new HashMap<>();
                details.put("Last backup", lastUpdated);
                details.put("Default Printer", defaultPrinter);
                Cursor data = helper.getExpiryDate();
                String expiryDate = "";
                if(data.getCount() != 0){
                    while(data.moveToNext()){
                        expiryDate = data.getString(data.getColumnIndex("Date"));
                    }
                }
                details.put("Expiry Date", expiryDate);
                Paper.book().write(Prevalent.expiry_date, expiryDate);
                //
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));

                ref.updateChildren(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            pd.dismiss();
                            DashboardActivity.updated = false;
                            Paper.book().destroy();
                            context.startActivity(new Intent(context, LoginActivity.class));
                            finish();
                        }
                        else{
                            pd.dismiss();
                            toast(context, "Unable to logout, try again");
                        }
                    }
                });
            }
        }.start();
    }
}
