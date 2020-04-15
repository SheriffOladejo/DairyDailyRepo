package com.juicebox.dairydaily.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hanks.passcodeview.PasscodeView;
import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;

import java.util.Date;

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

//        Calendar c = Calendar.getInstance();
//        c.setTime(new Date());
//        c.add(Calendar.DATE, 0);
//        toast(this, String.valueOf(c.getTime().getTime()));

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
