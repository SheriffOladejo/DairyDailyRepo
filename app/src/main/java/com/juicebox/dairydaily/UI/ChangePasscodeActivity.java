package com.juicebox.dairydaily.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.R;

import io.paperdb.Paper;

import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.useSnackBar;

public class ChangePasscodeActivity extends AppCompatActivity {

    EditText current_passcode, passcode, confirm_passcode;
    String current_passcode_string, passcode_string, confirm_passcode_string;
    String retrievedPasscode = "";
    String no_passcode = "";
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passcode);

        Paper.init(ChangePasscodeActivity.this);

        current_passcode = findViewById(R.id.current_passcode);
        passcode = findViewById(R.id.passcode);
        confirm_passcode = findViewById(R.id.confirm_passcode);
        linearLayout = findViewById(R.id.linearlayout);

        retrievedPasscode = Paper.book().read(Prevalent.offline_password);
        no_passcode = getIntent().getStringExtra("NoPasscode");
        Log.d("ChangePasscodeActivity ", "Retrived code: "+retrievedPasscode);
        if(no_passcode != null){
            current_passcode.setEnabled(false);
            current_passcode.setClickable(false);
        }

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_passcode_string = current_passcode.getText().toString();
                passcode_string = passcode.getText().toString();
                confirm_passcode_string = confirm_passcode.getText().toString();

                Log.d("MyActivity", "Current passcode: " + current_passcode_string );

                if(retrievedPasscode == null){
                    if(passcode_string.length() <4 || passcode_string.length()>4 || confirm_passcode_string.length() <4 || confirm_passcode_string.length()>4){
                        toast(ChangePasscodeActivity.this, "Passcode length should be 4");
                    }
                    else{
                        if(passcode_string.equals(confirm_passcode_string)){
                            Paper.book().write(Prevalent.offline_password, passcode_string);
                            startActivity(new Intent(ChangePasscodeActivity.this, PasscodeViewClass.class).putExtra("Success", "Success"));
                            finish();
                        }
                        else{
                            toast(ChangePasscodeActivity.this, "Passcode and Confirm Passcode don't match");
                        }
                    }

                }

                else{
                    if(current_passcode_string.equals(retrievedPasscode)){
                        if(passcode_string.equals(confirm_passcode_string)){
                            Paper.book().write(Prevalent.offline_password, passcode_string);
                            startActivity(new Intent(ChangePasscodeActivity.this, PasscodeViewClass.class).putExtra("Success", "Success"));
                            finish();
                        }
                        else{
                            useSnackBar("Passcode and Confirm Passcode don't match", linearLayout);
                        }
                    }
                    else{
                        Log.d("ChangePasscode", "ChangePasscode: old passcode " + retrievedPasscode);
                        toast(ChangePasscodeActivity.this, "Current passcode is wrong");
                    }
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChangePasscodeActivity.this, LoginActivity.class));
        finish();
    }
}
