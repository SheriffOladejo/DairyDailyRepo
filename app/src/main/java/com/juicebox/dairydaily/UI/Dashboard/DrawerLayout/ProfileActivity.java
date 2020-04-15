package com.juicebox.dairydaily.UI.Dashboard.DrawerLayout;

import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.Prevalent;
import com.juicebox.dairydaily.R;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import io.paperdb.Paper;

public class ProfileActivity extends AppCompatActivity {

    Button name, addresss, email, phone_number, city, state, update, printer, days_remaining, expiry_date;
    int day, month, year;
    String day_in_string, month_in_string, year_in_string;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        Paper.init(this);
        getSupportActionBar().setTitle("User Profile");

        RelativeLayout r1 = findViewById(R.id.relativeLayout3);
        RelativeLayout r2 = findViewById(R.id.relativeLayout4);
        name = findViewById(R.id.name);
        addresss = findViewById(R.id.address);
        email = findViewById(R.id.email);
        phone_number = findViewById(R.id.phone_number);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        update = findViewById(R.id.lastupdated);
        printer = findViewById(R.id.default_printer);
        expiry_date = findViewById(R.id.expiry_date);
        days_remaining = findViewById(R.id.days_remaining);

        String nameString = Paper.book().read(Prevalent.name);
        String addressString = Paper.book().read(Prevalent.address);
        String emailString= Paper.book().read(Prevalent.email);
        String phoneString = Paper.book().read(Prevalent.phone_number);
        String cityString = Paper.book().read(Prevalent.city);
        String stateString = Paper.book().read(Prevalent.state);
        String updateString = Paper.book().read(Prevalent.last_update);
        String printerString = Paper.book().read(Prevalent.selected_device);

        String expiry = "";
        DbHelper helper = new DbHelper(ProfileActivity.this);
        Cursor data = helper.getExpiryDate();
        if(data.getCount() != 0){
            while(data.moveToNext()){
                expiry = data.getString(data.getColumnIndex("Date"));
            }
        }
        if(expiry.equals("")){

        }
        else{
            String ex;
            try{
                DateFormat df = new DateFormat();
                ex = df.format("dd/MM/yyyy", Long.valueOf(expiry)).toString();
            }
            catch(Exception e){
                ex = new SimpleDateFormat("dd/MM/YYYY").format(Long.valueOf(expiry));
            }
            expiry_date.setText(ex);
            day_in_string = ex.substring(0,2);
            month_in_string = ex.substring(3,5);
            year_in_string = ex.substring(6,10);

            if(day_in_string.charAt(0) == '0'){
                day = Integer.valueOf(day_in_string.substring(1,2));
            }
            else{
                day = Integer.valueOf(day_in_string);
            }

            if(month_in_string.charAt(0) == '0'){
                month = Integer.valueOf(month_in_string.substring(1,2));
            }
            else{
                month = Integer.valueOf(month_in_string);
            }
            year = Integer.valueOf(year_in_string);
            long d = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate e = LocalDate.of(year, month, day);
                LocalDate now = LocalDate.now();
                d = ChronoUnit.DAYS.between(now, e);
            }
            days_remaining.setText(""+d);
        }


        Animation slide = AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.image_slide_left);
        r1.startAnimation(slide);
        r2.startAnimation(slide);

        if(updateString == null){
            update.setText("");
        }
        else{
            update.setText(updateString);
        }
        if(nameString == null){
            name.setText("");
        }
        else{
            name.setText(nameString);
        }
        if(addressString == null){
            addresss.setText("");
        }
        else{
            addresss.setText(addressString);
        }
        if(emailString == null){
            email.setText("");
        }
        else{
            email.setText(emailString);
        }
        if(phoneString == null){
            phone_number.setText("");
        }
        else{
            phone_number.setText(phoneString);
        }if(cityString == null){
            city.setText("");
        }
        else{
            city.setText(cityString);
        }if(stateString == null){
            state.setText("");
        }
        else{
            state.setText(stateString);
        }
        if(printerString == null){
            printer.setText("Nothing");
        }
        else{
            printer.setText(printerString);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.close, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.close){
            finish();
            return true;
        }
        else{
            return false;
        }
    }
}
