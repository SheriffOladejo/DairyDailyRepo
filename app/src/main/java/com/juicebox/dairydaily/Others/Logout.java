package com.juicebox.dairydaily.Others;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.LoginActivity;

import java.util.HashMap;

import io.paperdb.Paper;

import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.useSnackBar;

public class Logout extends AppCompatActivity {
    Context context;
    DbHelper helper;
    private static final String TAG = "Logout";
    public Logout(Context context){
        this.context = context;
        helper = new DbHelper(context);
        logout();
    }

    public void logout(){
        ProgressDialog pd = new ProgressDialog(context);
        pd.setTitle("Logging Out");
        pd.setMessage("Please Wait...");
        pd.setCancelable(false);
        pd.show();

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
                details.put("Expiry Date", DashboardActivity.expiryDate);
                //Paper.book().destroy();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number));

                ref.updateChildren(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Paper.book().write(Prevalent.remember_me, "False");
                            Log.d(TAG,"logout remember: " + Paper.book().read(Prevalent.remember_me));
                            Paper.book().write(Prevalent.has_account, "True");
                            pd.dismiss();
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
