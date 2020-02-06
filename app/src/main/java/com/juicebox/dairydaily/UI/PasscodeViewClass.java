package com.juicebox.dairydaily.UI;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.hanks.passcodeview.PasscodeView;
import com.juicebox.dairydaily.Others.DataRetrievalHandler;
import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.Others.SpinnerItem;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;

import java.util.ArrayList;

import io.paperdb.Paper;

import static com.juicebox.dairydaily.Others.UtilityMethods.useSnackBar;


public class PasscodeViewClass extends AppCompatActivity {

    PasscodeView passcodeView;
    RelativeLayout relativeLayout;
    private static final String TAG = "PasscodeViewClass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode_view);

        Paper.init(this);

        String success = "";
        try{
            success = getIntent().getStringExtra("Success");
            if(!success.equals("")){
                useSnackBar("Passcode changed successfully", relativeLayout);
            }
        }
        catch(Exception e){

        }

        passcodeView = findViewById(R.id.passcodeview);
        findViewById(R.id.change_passcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PasscodeViewClass.this, ChangePasscodeActivity.class));
            }
        });
        getSupportActionBar().hide();

        relativeLayout = findViewById(R.id.relative_layout);

        passcodeView.setFirstInputTip("Enter 4-digit passcode.");
        passcodeView.setWrongInputTip("Wrong Passcode");

        String offline_password = Paper.book().read(Prevalent.offline_password);
        if(offline_password != null){
            Log.d("PassCodeVIew", "change password: " + offline_password);
            passcodeView.setPasscodeLength(4).setLocalPasscode(offline_password).setListener(new PasscodeView.PasscodeViewListener() {
                @Override
                public void onFail() {
                    useSnackBar("Wrong passcode", relativeLayout);
                }

                @Override
                public void onSuccess(String number) {
                    startActivity(new Intent(PasscodeViewClass.this, DashboardActivity.class));
                    finish();
                }
            });
        }
        else{
            passcodeView.setPasscodeLength(4);
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PasscodeViewClass.this, LoginActivity.class));
        finish();
    }
}
