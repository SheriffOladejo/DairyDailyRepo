package com.dixit.dairydaily.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dixit.dairydaily.Others.DataRetrievalHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hanks.passcodeview.PasscodeView;
import com.dixit.dairydaily.Others.Prevalent;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;

import javax.security.auth.callback.PasswordCallback;

import io.paperdb.Paper;

import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.Others.UtilityMethods.useSnackBar;


public class PasscodeViewClass extends AppCompatActivity {

    PasscodeView passcodeView;
    RelativeLayout relativeLayout;
    private static final String TAG = "PasscodeViewClass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode_view);

        Paper.init(this);
        //new DataRetrievalHandler(PasscodeViewClass.this);

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
