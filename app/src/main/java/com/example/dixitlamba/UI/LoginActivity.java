package com.example.dixitlamba.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import com.example.dixitlamba.Others.Prevalent;
import com.example.dixitlamba.R;
import com.example.dixitlamba.UI.Dashboard.BuyMilk.BuyMilkActivity;
import com.example.dixitlamba.UI.Dashboard.DashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import io.paperdb.Paper;

import static com.example.dixitlamba.Others.UtilityMethods.hideKeyboard;
import static com.example.dixitlamba.Others.UtilityMethods.useSnackBar;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // Array lists for state and city autocompletetextviews
    ArrayList<String> listState=new ArrayList<String>();
    // for listing all cities
    ArrayList<String> listCity=new ArrayList<String>();
    // access all auto complete text views
    AutoCompleteTextView actState;
    AutoCompleteTextView actCity;

    private int duration;
    private ScrollView login_view;
    private ScrollView sign_up_view;
    private boolean fadedCheck = false;
    private FirebaseAuth mAuth;

    private FrameLayout frameLayout;

    private ProgressDialog progressDialog;

    // login section variable declarations
    String login_phone_number;
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

    // login widgets
    EditText loginPhoneNumber;
    EditText loginPassword;
    CheckBox rememberMe;
    Button login;
    Button work_offline;
    EditText login_country_code;
    TextView forgot_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        login_view = findViewById(R.id.login_view);
        sign_up_view = findViewById(R.id.sign_up_view);

        frameLayout = findViewById(R.id.framelayout);

        progressDialog = new ProgressDialog(this);

        // login widgets
        loginPhoneNumber = findViewById(R.id.login_phone_number);
        login_country_code = findViewById(R.id.login_country_code);
        loginPhoneNumber.requestFocus();
        loginPassword = findViewById(R.id.login_password);
        rememberMe = findViewById(R.id.remember_me);
        login = findViewById(R.id.login_button);
        work_offline = findViewById(R.id.work_offline);
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

        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_sequential);
        logoImage.startAnimation(hyperspaceJumpAnimation);

        // Initially hide the sign up view
        sign_up_view.setVisibility(View.GONE);

        duration = getResources().getInteger(
                android.R.integer.config_longAnimTime);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!forgot && control){
                    verifyLoginInput();
                }
                else if(updatePassword && !control){
                    progressDialog.setTitle("Updating Password...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    String password = loginPhoneNumber.getText().toString();
                    String confirm_password = loginPassword.getText().toString();
                    if(!password.equals(confirm_password)){
                        useSnackBar("Passwords don't match", frameLayout);
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

        work_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, BuyMilkActivity.class));
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
                login.setText("Retrieve");
                forgot = true;
                rememberMe.setVisibility(View.INVISIBLE);
                loginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                loginPhoneNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                useSnackBar("Enter your phone number and email address", frameLayout);
            }
        });
        callAll();

        Paper.init(this);

        String remember = Paper.book().read(Prevalent.remember_me);
        if(remember == "True"){
            startActivity(new Intent(LoginActivity.this, BuyMilkActivity.class));
            finish();
        }

        hideKeyboard(LoginActivity.this);

        mAuth = FirebaseAuth.getInstance();
    }

    // Method that verifies user input(login)
    private void verifyLoginInput(){
        hideKeyboard(LoginActivity.this);

        login_phone_number = loginPhoneNumber.getText().toString();
        login_password = loginPassword.getText().toString();
        remember_me = rememberMe.isChecked();
        login_country_code_string = login_country_code.getText().toString();
        // concat number to country code provided
        login_country_code_string += login_phone_number;
        login_phone_number = login_country_code_string;

        if(remember_me){
            Paper.book().write(Prevalent.remember_me, "True");
        }

        if(login_phone_number.isEmpty() || login_password.isEmpty() || login_country_code_string.isEmpty()){
            useSnackBar("All fields are required", frameLayout);
        }
        else{
            progressDialog.setTitle("Logging in...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // We first want to check if the phone number exists and if the password matches.
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(login_phone_number);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String password = dataSnapshot.child("Password").getValue().toString();
                        if(password.equals(login_password)){
                            progressDialog.dismiss();
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        }
                        else{
                            progressDialog.dismiss();
                            useSnackBar("Incorrect password", frameLayout);
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

    // Method that verifies sign up input
    private void verifySignupInput(){
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
                // Remember to add else if for state and city
            else {
                if(sign_up_password.equals(confirm_password)){

                    progressDialog.setTitle("Verifying Email...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(email, sign_up_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressDialog.dismiss();
                                            useSnackBar("Check your mail for verification", frameLayout);
                                        }
                                        else{
                                            progressDialog.dismiss();
                                            useSnackBar("Unable to verify email", frameLayout);
                                        }
                                    }
                                });
                            }
                            else{
                                mAuth.signInWithEmailAndPassword(email, sign_up_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            if(mAuth.getCurrentUser().isEmailVerified()){
                                                Log.d(TAG,  "Email: " + mAuth.getCurrentUser().getEmail());
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
                                                startActivity(intent);
                                                finish();
                                                // Remember to add state and city values too
                                            }
                                        }
                                        else{
                                            progressDialog.dismiss();
                                            useSnackBar(task.getException().getMessage(), frameLayout);
                                        }
                                    }
                                });
                            }
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

    // Method that retrieves user email
    private void retrieveEmail(){
        login_country_code.setVisibility(View.INVISIBLE);
        rememberMe.setVisibility(View.INVISIBLE);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        login_phone_number = loginPhoneNumber.getText().toString();
        login_country_code_string = login_country_code.getText().toString();
        login_country_code_string += login_phone_number;
        login_phone_number = login_country_code_string;
        // This will now hold the user email
        login_password = loginPassword.getText().toString();

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
        adapterSetting(listCity);
    }

    // The third auto complete text view
    void addState()
    {
        Set<String> set = new HashSet<String>(listState);
        adapterSetting(new ArrayList(set));
    }

    // setting adapter for auto complete text views
    void adapterSetting(ArrayList arrayList)
    {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,arrayList);
        actState.setAdapter(adapter);
        actCity.setAdapter(adapter);

        hideKeyboard(LoginActivity.this);
    }


    // Method to fade in views
    private void crossfade(final View initialView, View nextView, int duration) {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        nextView.setAlpha(0f);
        nextView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_up_text, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.sign_up){
            if(fadedCheck){
                crossfade(sign_up_view, login_view, duration);
                fadedCheck = false;
            }
            else{
                crossfade(login_view, sign_up_view, duration);
                fadedCheck = true;
            }
            return true;
        }
        return true;
    }
}
