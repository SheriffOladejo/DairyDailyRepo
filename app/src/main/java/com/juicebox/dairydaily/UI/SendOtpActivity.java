package com.juicebox.dairydaily.UI;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.juicebox.dairydaily.CowChart.CowSNF;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

import static com.juicebox.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.juicebox.dairydaily.Others.UtilityMethods.useSnackBar;

public class SendOtpActivity extends AppCompatActivity {

    private static final String TAG = "SendOtpActivity";
    LinearLayout linearLayout;
    String otpCode;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String mVerificationId;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    TextView countDownTimer;

    String firstname;
    String lastname;
    String address;
    String password;
    String phoneNumber;
    String offline_password;
    String country_code;
    String email;
    String city;
    String state;

    DbHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);

        // Receive values from previous activity
        firstname = getIntent().getStringExtra("Firstname");
        lastname = getIntent().getStringExtra("Lastname");
        address = getIntent().getStringExtra("Address");
        password = getIntent().getStringExtra("Password");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        offline_password = getIntent().getStringExtra("Offline Password");
        country_code = getIntent().getStringExtra("Country Code");
        email = getIntent().getStringExtra("Email");
        city = getIntent().getStringExtra("City");
        state = getIntent().getStringExtra("State");
        country_code += phoneNumber;
        phoneNumber = country_code;

        helper = new DbHelper(this);

        Log.d(TAG, "Phone Number" + phoneNumber);
        countDownTimer = findViewById(R.id.count_down_text);

        final EditText otp = findViewById(R.id.otp_edit_text);
        otp.requestFocus();
        Button send = findViewById(R.id.send_otp_button);
        linearLayout = findViewById(R.id.send_otp_layout);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        // Send user's phone number for verification
        final CountDownTimer timer = new CountDownTimer(45000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressDialog.setTitle("Waiting for OTP...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                countDownTimer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                countDownTimer.setText("");
                progressDialog.dismiss();
            }
        };

        final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                otpCode = phoneAuthCredential.getSmsCode();
                if(otpCode!= null){
                    sendVerificationCode(otpCode);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked if an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.d(TAG, "onVerificationFailed: " + "Ran out of quota");
                    useSnackBar("Ran out of quota", linearLayout);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // An Error 419 means too many requests have been made and you ahve to upgrade plan.
                    Log.d(TAG, "onVerificationFailed: " + "Error 419");
                    useSnackBar("Error 419", linearLayout);
                }
                Log.d(TAG, "onVerificationFailed: " + e.getMessage());
                useSnackBar("Unable to Sign Up. Invalid Phone Number", linearLayout);
                progressDialog.dismiss();
                startActivity(new Intent(SendOtpActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                progressDialog.dismiss();
                timer.cancel();
                countDownTimer.setText("");

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = forceResendingToken;

            }
        };

        timer.start();
        // Send user's phone number for verification
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                45,
                TimeUnit.SECONDS,
                SendOtpActivity.this,
                mCallbacks);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(SendOtpActivity.this);
                otpCode = otp.getText().toString();
                if(otpCode.isEmpty())
                    otp.setError("OTP is required");
                ProgressDialog progressDialog = new ProgressDialog(SendOtpActivity.this);
                progressDialog.setTitle("Verifying");
                progressDialog.setCancelable(false);
                progressDialog.show();
                sendVerificationCode(otpCode);
            }
        });
    }

    // Primary method that sends the verifies the code sent to user.
    private void sendVerificationCode(String number){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, number);
        signInWithPhoneAuthCredential(credential);
    }

    // Method that signs user in with verification code.
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        // Credential is created with a phone number and verification id
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            uploadDetailsToCloud();
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            useSnackBar("Operation Failed", linearLayout);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                useSnackBar("Invalid verification code", linearLayout);
                            }
                        }
                    }
                });
    }

    private void uploadDetailsToCloud(){

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 31);
        helper.setExpiryDate(String.valueOf(c.getTime().getTime()));

        Paper.init(SendOtpActivity.this);
        HashMap<String, Object> details = new HashMap<>();
        details.put("Firstname", firstname);
        details.put("Lastname", lastname);
        details.put("Phone Number", phoneNumber);
        details.put("Password", password);
        details.put("Last Backup", "");
        details.put("Default Printer", "");
        details.put("Offline Password", offline_password);
        details.put("Email", email);
        details.put("City", city);
        details.put("State", state);
        details.put("Address", address);
        details.put("Expiry Date", String.valueOf(c.getTime().getTime()));

        Paper.book().write(Prevalent.offline_password, offline_password);
        Paper.book().write(Prevalent.has_account, "True");
        Paper.book().write(Prevalent.name, firstname + " " + lastname);
        Paper.book().write(Prevalent.phone_number, phoneNumber);
        Paper.book().write(Prevalent.city, city);
        Paper.book().write(Prevalent.email, email);
        Paper.book().write(Prevalent.state,state);
        Paper.book().write(Prevalent.address, address);
        Paper.book().write(Prevalent.offline_password, offline_password);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(phoneNumber);

        ref.updateChildren(details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    helper.createSNFTable("");

                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date());
                    c.add(Calendar.DATE, 31);
                    helper.setExpiryDate(String.valueOf(c.getTime().getTime()));
                    startActivity(new Intent(SendOtpActivity.this, DashboardActivity.class));
                    finish();
                }
                else{
                    progressDialog.dismiss();
                    useSnackBar("Sign up failed", linearLayout);
                    startActivity(new Intent(SendOtpActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
