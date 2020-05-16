package com.dixit.dairydaily.UI.Login;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.dixit.dairydaily.Others.DataRetrievalHandler;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.Others.Prevalent;
import com.dixit.dairydaily.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import io.paperdb.Paper;

import static com.dixit.dairydaily.Others.UtilityMethods.hideKeyboard;
import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.Others.UtilityMethods.useSnackBar;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // Array lists for state and city autocompletetextviews
    ArrayList<String> listState=new ArrayList<String>();
    // for listing all cities
    ArrayList<String> listCity=new ArrayList<String>();
    // access all auto complete text views
    AutoCompleteTextView actState;
    AutoCompleteTextView actCity;
    public static String expiry = "";

    private boolean suspended = false;

    private int duration;
    private ScrollView login_view;
    private ScrollView sign_up_view;
    private boolean fadedCheck = false;
    private FirebaseAuth mAuth;

    private FrameLayout frameLayout;

    DbHelper helper;

    private ProgressDialog progressDialog;

    // login section variable declarations
    public static String login_phone_number;
    String login_password;
    boolean remember_me = false;
    boolean forgot = false;
    boolean updatePassword = false;
    boolean control = true;
    String login_country_code_string;

    // Sign up section variable declarations
    String firstname_string;
    String lastname_string;
    String sign_up_phone_number;
    String sign_up_password;
    String confirm_password;
    String street_address;
    String country_code_sign_up;
    String offlinePassword;
    String email;
    String state;
    String city;

    private static Activity activity;
    private boolean new_user = true;

    // Sign up widgets
    EditText signupPhoneNumber;
    EditText firstname;
    EditText lastname;
    EditText signupPassword;
    EditText confirmPasswrd;
    EditText address;
    Button signup;
    EditText signup_country_code;
    EditText offline_password;
    EditText emailWidget;
    ImageView logoImage;

    private static Long downloadId;
    StorageReference ref1;
    String url;

    // login widgets
    EditText loginPhoneNumber;
    EditText loginPassword;
    CheckBox rememberMe;
    Button login;
    EditText login_country_code;
    TextView forgot_password;

    Toolbar toolbar;
    TextView login_signup;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    //toast(LoginActivity.this, "Permission granted");
                }
                else{
                    toast(LoginActivity.this, "Permission Denied");
                }
                break;
            case 2:
                if((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){

                }
                else{
                    toast(LoginActivity.this, "External storage write permission Denied");
                }
            default:
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Paper.init(this);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        login_view = findViewById(R.id.login_view);
        sign_up_view = findViewById(R.id.sign_up_view);
        toolbar = findViewById(R.id.toolbar);
        login_signup = findViewById(R.id.login_signup);
        login_signup.setText("Sign Up");

        activity = this;

        setSupportActionBar(toolbar);

        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        frameLayout = findViewById(R.id.framelayout);
        helper = new DbHelper(this);

        progressDialog = new ProgressDialog(this);

        // login widgets
        loginPhoneNumber = findViewById(R.id.login_phone_number);
        login_country_code = findViewById(R.id.login_country_code);
        loginPhoneNumber.requestFocus();
        loginPassword = findViewById(R.id.login_password);
        rememberMe = findViewById(R.id.remember_me);
        rememberMe.setChecked(true);
        login = findViewById(R.id.login_button);
        forgot_password = findViewById(R.id.forot_password);

        // sign up widgets
        signupPhoneNumber = findViewById(R.id.phone_number);
        offline_password = findViewById(R.id.offline_password);
        firstname = findViewById(R.id.first_name);
        lastname = findViewById(R.id.last_name);
        signupPassword = findViewById(R.id.password);
        confirmPasswrd = findViewById(R.id.confirm_password);
        address = findViewById(R.id.street_address);
        signup = findViewById(R.id.sign_up_button);
        signup_country_code = findViewById(R.id.country_code);
        emailWidget = findViewById(R.id.email);
        logoImage = findViewById(R.id.logo_image);
        actState = findViewById(R.id.actState);
        actCity = findViewById(R.id.actCity);

        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_rotate);
        logoImage.startAnimation(hyperspaceJumpAnimation);

        // Initially hide the sign up view
        sign_up_view.setVisibility(View.GONE);

        duration = getResources().getInteger(
                android.R.integer.config_longAnimTime);

        login_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fadedCheck){
                    crossfade(sign_up_view, login_view, duration);
                    fadedCheck = false;
                    login_signup.setText("Sign Up");
                }
                else{
                    crossfade(login_view, sign_up_view, duration);
                    fadedCheck = true;
                    login_signup.setText("Login");
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login_phone_number = loginPhoneNumber.getText().toString();
                login_password = loginPassword.getText().toString();
                remember_me = rememberMe.isChecked();
                login_country_code_string = login_country_code.getText().toString();
                // concat number to country code provided
                login_country_code_string += login_phone_number;
                login_phone_number = login_country_code_string;

                if(!forgot && control){
                    if(!isSuspended()){
                        verifyLoginInput();
                    }
                }
                else if(updatePassword && !control){
                    progressDialog.setMessage("Updating Password...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    String password = loginPhoneNumber.getText().toString();
                    String confirm_password = loginPassword.getText().toString();
                    if(password.isEmpty() || confirm_password.isEmpty()){
                        progressDialog.dismiss();
                        useSnackBar("Fields should not be empty", frameLayout);
                    }
                    if(!password.equals(confirm_password)){
                        useSnackBar("Passwords don't match", frameLayout);
                        progressDialog.dismiss();
                    }
                    else{
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(login_phone_number).child("Password");
                        ref.setValue(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    loginPhoneNumber.setText("");
                                    loginPassword.setText("");
                                    loginPassword.setHint("Password");
                                    loginPhoneNumber.setHint("Phone Number");
                                    login.setText("Login");
                                    updatePassword = false;
                                    forgot = false;
                                    control = true;
                                    rememberMe.setVisibility(View.VISIBLE);
                                    loginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                    login_country_code.setVisibility(View.VISIBLE);
                                    progressDialog.dismiss();
                                    useSnackBar("Password updated!", frameLayout);
                                }
                                else{
                                    progressDialog.dismiss();
                                    useSnackBar("Unable to update password", frameLayout);
                                }
                            }
                        });
                    }
                }
                else{
                    retrieveEmail();
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifySignupInput();
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPhoneNumber.setHint("Phone Number");
                loginPassword.setHint("Email Address");
                login.setText("Send OTP");
                forgot = true;
                rememberMe.setVisibility(View.INVISIBLE);
                loginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                loginPhoneNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                useSnackBar("You'll receive an OTP to the registered number", frameLayout);
            }
        });
        callAll();

        String remember = Paper.book().read(Prevalent.remember_me);
        if(remember != null){
            if(remember.equals("true")){
                startActivity(new Intent(LoginActivity.this, PasscodeViewClass.class));
                finish();
            }
        }

        hideKeyboard(this);

        mAuth = FirebaseAuth.getInstance();
    }

    private boolean isSuspended(){
        final DatabaseReference[] checkActiveState = {FirebaseDatabase.getInstance().getReference().child("Users").child(login_phone_number).child("Active")};
        checkActiveState[0].addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String active = dataSnapshot.getValue().toString();
                    if(active.equals("false")){
                        suspended = true;
                        progressDialog.dismiss();
                        checkActiveState[0] = null;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(suspended){
            toast(LoginActivity.this, "Your account has been suspended, contact the admin.");
            startActivity(new Intent(activity, LoginActivity.class));
            finish();
        }
        return suspended;
    }

    private void verifyLoginInput(){
        hideKeyboard(LoginActivity.this);
        Paper.book().write(Prevalent.phone_number, login_phone_number);
        new DataRetrievalHandler(LoginActivity.this);

        if(login_phone_number.isEmpty() || login_password.isEmpty() || login_country_code_string.isEmpty()){
            useSnackBar("All fields are required", frameLayout);
        }
        else{
            progressDialog.setMessage("Logging in...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // We first want to check if the phone number exists and if the password matches.
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(login_phone_number);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        //toast(LoginActivity.this, logged_in + " " + suspended);
                        if(!suspended){
                            String password = dataSnapshot.child("Password").getValue().toString();
                            if(password.equals(login_password)){

                                if(remember_me){
                                    Paper.book().write(Prevalent.remember_me, "true");
                                }

                                String firstname = dataSnapshot.child("Firstname").getValue().toString();
                                String lastname = dataSnapshot.child("Lastname").getValue().toString();
                                String phone_number = dataSnapshot.child("Phone Number").getValue().toString();
                                String email = dataSnapshot.child("Email").getValue().toString();
                                String city = dataSnapshot.child("City").getValue().toString();
                                String address = dataSnapshot.child("Address").getValue().toString();
                                String state = dataSnapshot.child("State").getValue().toString();
                                String offline_password = dataSnapshot.child("Offline Password").getValue().toString();
                                String expiry_date = dataSnapshot.child("Expiry Date").getValue().toString();
                                String last_backup;
                                String default_device;
                                try{
                                    last_backup = dataSnapshot.child("Last backup").getValue().toString();
                                    default_device = dataSnapshot.child("Default Printer").getValue().toString();
                                }
                                catch(Exception e){
                                    last_backup = "";
                                    default_device = "";
                                }

                                Paper.book().write(Prevalent.offline_password, offline_password);
                                Paper.book().write(Prevalent.name, firstname + " " + lastname);
                                Paper.book().write(Prevalent.has_account, "true");
                                Paper.book().write(Prevalent.phone_number, phone_number);
                                Paper.book().write(Prevalent.city, city);
                                Paper.book().write(Prevalent.email, email);
                                Paper.book().write(Prevalent.address, address);
                                Paper.book().write(Prevalent.selected_device, default_device);
                                Paper.book().write(Prevalent.expiry_date, expiry_date);
                                Paper.book().write(Prevalent.last_update, last_backup);
                                Paper.book().write(Prevalent.state,state);
                                helper.setExpiryDate(expiry_date);

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Pricing");
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            String starter = dataSnapshot.child("Starter Plan").getValue().toString();
                                            String spark = dataSnapshot.child("Spark Plan").getValue().toString();
                                            String enterprise = dataSnapshot.child("Enterprise Plan").getValue().toString();
                                            try{
                                                double starter_double = Double.valueOf(starter);
                                                double spark_double = Double.valueOf(spark);
                                                double enterprise_double = Double.valueOf(enterprise);
                                                Paper.book().write(Prevalent.starter, starter_double+"");
                                                Paper.book().write(Prevalent.spark, spark_double+"");
                                                Paper.book().write(Prevalent.enterprise, enterprise_double+"");
                                            }
                                            catch(Exception e){
                                                Toast.makeText(LoginActivity.this, "Error retrieving pricing plans. Contact the admin.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                StorageReference ref = FirebaseStorage.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number)).child("Rate File");
                                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if(task.isSuccessful()){
                                            String url = task.getResult().toString();
                                            //Toast.makeText(LoginActivity.this, "Downloading file...", Toast.LENGTH_SHORT).show();
                                            DownloadFile(LoginActivity.this, "Rate File", ".csv", "/dairyDaily", url);
                                        }
                                        else{
                                            //Toast.makeText(LoginActivity.this, "Something went wrong when downloading the file.", Toast.LENGTH_SHORT).show();
                                            ref1 = FirebaseStorage.getInstance().getReference().child("Rate Chart").child("Rate File");
                                            ref1.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if(task.isSuccessful()){
                                                        url = task.getResult().toString();
                                                        //Toast.makeText(LoginActivity.this, "Downloading file...", Toast.LENGTH_SHORT).show();
                                                        DownloadFile(LoginActivity.this, "Rate File", ".csv", "/dairyDaily", url);
                                                    }
                                                    else{
                                                        //Toast.makeText(LoginActivity.this, "Something went wrong when downloading the file.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                            else{
                                try{
                                progressDialog.dismiss();
                                useSnackBar("Incorrect password", frameLayout);}
                                catch(Exception e){

                                }
                            }
                        }
                        else{
                            progressDialog.dismiss();
                            toast(LoginActivity.this, "Your account has been suspended. Contact the admin");
                        }
                    }
                    else{
                        progressDialog.dismiss();
                        useSnackBar("Phone not found", frameLayout);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    useSnackBar("An error occurred", frameLayout);
                }
            });
        }

    }

    private void verifySignupInput(){
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        hideKeyboard(LoginActivity.this);
        firstname_string = firstname.getText().toString();
        street_address = address.getText().toString();
        lastname_string = lastname.getText().toString();
        sign_up_phone_number = signupPhoneNumber.getText().toString();
        sign_up_password = signupPassword.getText().toString();
        confirm_password = confirmPasswrd.getText().toString();
        country_code_sign_up = signup_country_code.getText().toString();
        offlinePassword = offline_password.getText().toString();
        email = emailWidget.getText().toString();
        city = actCity.getText().toString();
        state = actState.getText().toString();

        if(sign_up_password.length() < 5){
            useSnackBar("Password is too short", frameLayout);
        }
        else{
            if(firstname_string.isEmpty())
                firstname.setError("Required");

            else if(city.isEmpty())
                actCity.setError("Required");

            else if(state.isEmpty())
                actState.setError("Required");

            else if(email.isEmpty())
                emailWidget.setError("Required");

            else if(lastname_string.isEmpty())
                lastname.setError("Required");

            else if(sign_up_phone_number.isEmpty())
                signupPhoneNumber.setError("Required");

            else if(sign_up_password.isEmpty())
                signupPassword.setError("Required");

            else if(confirm_password.isEmpty())
                confirmPasswrd.setError("Required");

            else if(country_code_sign_up.isEmpty())
                signup_country_code.setError("Required");
            else if(offlinePassword.isEmpty())
                offline_password.setError("Required");

            else if(street_address.isEmpty())
                address.setError("Required");
            else if(offline_password.length() > 4 || offline_password.length() < 4){
                offline_password.setError("Offline Password length should be 4");
            }
            else if(city.isEmpty())
                actCity.setError("Required");
            else if(state.isEmpty())
                actState.setError("Required");
                // Remember to add else if for state and city
            else {
                if(sign_up_password.equals(confirm_password)){
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(sign_up_phone_number);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                useSnackBar("A user with these credentials already exists.", frameLayout);
                                progressDialog.dismiss();
                            }
                            else{
                                mAuth.createUserWithEmailAndPassword(email, sign_up_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            Intent intent = new Intent(LoginActivity.this, SendOtpActivity.class);
                                            intent.putExtra("phoneNumber", sign_up_phone_number);
                                            intent.putExtra("Firstname", firstname_string);
                                            intent.putExtra("Lastname", lastname_string);
                                            intent.putExtra("Password", sign_up_password);
                                            intent.putExtra("Address", street_address);
                                            intent.putExtra("Country Code", country_code_sign_up);
                                            intent.putExtra("Offline Password", offlinePassword);
                                            intent.putExtra("Email", email);
                                            intent.putExtra("City", city);
                                            intent.putExtra("State", state);
                                            intent.putExtra("Date Created", System.currentTimeMillis()+"");
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                else{
                    progressDialog.dismiss();
                    useSnackBar("Passwords don't match", frameLayout);
                }

            }

        }
    }

    private void DownloadFile(Context context, String fileName, String fileExtension, String destinationDirectoy, String url) {
        if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
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
                DbHelper helper = new DbHelper(LoginActivity.this);
                helper.clearSNFTable();
                helper.createSNFTable(Environment.getExternalStorageDirectory() + "/Download/Rate File.csv");
                try{
                    progressDialog.dismiss();
                }
                catch(Exception e){}
                startActivity(new Intent(LoginActivity.this, PasscodeViewClass.class));
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    // Method that retrieves user email
    private void retrieveEmail(){
        login_country_code.setVisibility(View.INVISIBLE);
        rememberMe.setVisibility(View.INVISIBLE);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        login_phone_number = loginPhoneNumber.getText().toString();
        login_country_code_string = login_country_code.getText().toString();
        login_country_code_string += login_phone_number;
        login_phone_number = login_country_code_string;
        // This will now hold the user email
        login_password = loginPassword.getText().toString();

        if(login_password.isEmpty() || login_country_code_string.isEmpty() || login_phone_number.isEmpty()){
            useSnackBar("All fields are required", frameLayout);
            progressDialog.dismiss();
        }
        else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(login_phone_number);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String retrievedEmail = dataSnapshot.child("Email").getValue().toString();
                        if(login_password.equals(retrievedEmail)){
                            progressDialog.dismiss();
                            loginPhoneNumber.setText("");
                            loginPassword.setText("");
                            loginPhoneNumber.setHint("Password");
                            loginPassword.setHint("Confirm Password");
                            login.setText("Update");
                            updatePassword = true;
                            forgot = false;
                            control = false;
                            loginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            loginPhoneNumber.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            useSnackBar("Update your password", frameLayout);
                        }
                        else{
                            progressDialog.dismiss();
                            useSnackBar("Email not found", frameLayout);
                        }
                    }
                    else{
                        progressDialog.dismiss();
                        useSnackBar("Phone Number not found", frameLayout);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void callAll()
    {
        obj_list();
        addCity();
        addState();
    }

    // Get the content of cities.json from assets directory and store it as string
    public String getJson()
    {
        String json=null;
        try
        {
            InputStream is = getAssets().open("cities.json");
            // is there any content in the file
            int size = is.available();
            byte[] buffer = new byte[size];
            // read values in the byte array
            is.read(buffer);
            // close the stream --- very important
            is.close();
            // convert byte to string
            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return json;
        }
        return json;
    }

    // This add all JSON object's data to the respective lists
    void obj_list() {
        // Exceptions are returned by JSONObject when the object cannot be created
        try {
            // Convert the string returned to a JSON object
            JSONObject jsonObject = new JSONObject(getJson());
            // Get Json array
            JSONArray array = jsonObject.getJSONArray("array");
            // Navigate through an array item one by one
            for (int i = 0; i < array.length(); i++) {
                // select the particular JSON data
                JSONObject object = array.getJSONObject(i);
                String city = object.getString("name");
                String state = object.getString("state");
                // add to the lists in the specified format
                listCity.add(city);
                listState.add(state);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // The second auto complete text view
    void addCity()
    {
        Set<String> set = new HashSet<>(listCity);
        adapterSettingCity(new ArrayList(set));
    }

    // The third auto complete text view
    void addState()
    {
        Set<String> set = new HashSet<String>(listState);
        adapterSetting(new ArrayList(set));
    }

    void adapterSettingCity(ArrayList arrayList)
    {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,arrayList);
        actCity.setAdapter(adapter);
    }

    // setting adapter for auto complete text views
    void adapterSetting(ArrayList arrayList)
    {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,arrayList);
        actState.setAdapter(adapter);

        hideKeyboard(LoginActivity.this);
    }


    // Method to fade in views
    private void crossfade(final View initialView, View nextView, int duration) {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        nextView.setAlpha(0f);
        nextView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on  1the view.
        nextView.animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        initialView.animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        initialView.setVisibility(View.GONE);
                    }
                });
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.sign_up_text, menu);
//        return true;
//    }

    @Override
    public void onBackPressed() {
        finish();
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId() == R.id.sign_up){
//            if(fadedCheck){
//                crossfade(sign_up_view, login_view, duration);
//                fadedCheck = false;
//                login_signup.setText("Login");
//            }
//            else{
//                crossfade(login_view, sign_up_view, duration);
//                fadedCheck = true;
//                login_signup.setText("Sign Up");
//            }
//        }
//        return true;
//    }
}
