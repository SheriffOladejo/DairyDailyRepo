package com.example.dairydaily.Others;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.dairydaily.R;
import com.example.dairydaily.UI.Dashboard.BuyMilk.BuyMilkActivity;
import com.example.dairydaily.UI.SendOtpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import static com.example.dairydaily.Others.UtilityMethods.useSnackBar;

public class PhoneAuth extends AppCompatActivity {

    private static final String TAG = "PhoneAuth";
    private LinearLayout linearLayout;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String mVerificationId;
    FirebaseAuth mAuth;
    String code;

    // Primary method that sends the verifies the code sent to user.
    private void sendVerificatioCode(String number){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, number);
        signInWithPhoneAuthCredential(credential);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        final EditText text = findViewById(R.id.phone_number);
        final String[] phoneNumber = new String[1];
        final EditText phoneCode = findViewById(R.id.otp);
        Button otpButton = findViewById(R.id.otpButton);
        mAuth = FirebaseAuth.getInstance();

        linearLayout = findViewById(R.id.linearlayout);


        Button button = findViewById(R.id.button);

        final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                code = phoneAuthCredential.getSmsCode();
                if(code!= null){
                    sendVerificatioCode(code);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked if an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // An Error 419 means too many requests have been made and you ahve to upgrade plan.
                    useSnackBar("Error 419", linearLayout);
                }
               useSnackBar("Unable to Sign Up. Invalid Phone Number", linearLayout);
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = forceResendingToken;

            }
        };
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phoneNumber[0] = text.getText().toString();

                if(phoneNumber[0].isEmpty()){
                    text.setError("Number is required");
                    text.requestFocus();
                }

                else{
                    Intent intent = new Intent(PhoneAuth.this, SendOtpActivity.class);
                    intent.putExtra("phoneNumber", phoneNumber[0]);
                    startActivity(intent);
                }
            }
        });

        otpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code =phoneCode.getText().toString();
                if(code.isEmpty()){
                    phoneCode.setError("Enter code");
                    phoneCode.requestFocus();
                }
                sendVerificatioCode(code);
            }
        });
    }

    // Method that signs user in with verifcation code.
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        // Credential is created with a phone number and verification id
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(PhoneAuth.this, BuyMilkActivity.class));
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            useSnackBar("Sign in failed", linearLayout);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                useSnackBar("Invalid verification code", linearLayout);
                            }
                        }
                    }
                });
    }
}
