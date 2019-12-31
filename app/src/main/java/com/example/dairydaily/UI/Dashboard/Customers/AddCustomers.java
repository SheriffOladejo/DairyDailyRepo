package com.example.dairydaily.UI.Dashboard.Customers;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.dairydaily.Others.DbHelper;
import com.example.dairydaily.R;

import static com.example.dairydaily.Others.UtilityMethods.convertFirstLetter;
import static com.example.dairydaily.Others.UtilityMethods.useSnackBar;

public class AddCustomers extends AppCompatActivity {

    private RadioButton buyer, seller;
    private EditText firstnameEdittext, lastnameEdittext, phone_numberEdittext, addressEdittext, idEditText;
    private Button save;
    private static final String TAG = "AddCustomers";
    private LinearLayout add_customer;

    private String firstname = "";
    private String lastname = "";
    private String phone_number="";
    private String address="";
    private String id="";
    private String status;
    int passId;

    private DbHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_customer);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        getSupportActionBar().setTitle("Add Customer");
        getSupportActionBar().setHomeButtonEnabled(true);

        dbHelper = new DbHelper(this);

        firstnameEdittext = findViewById(R.id.first_name);
        lastnameEdittext = findViewById(R.id.lastname);
        phone_numberEdittext = findViewById(R.id.phone_number);
        addressEdittext = findViewById(R.id.address);
        buyer = findViewById(R.id.buyer);
        seller = findViewById(R.id.seller);
        save = findViewById(R.id.save);
        idEditText = findViewById(R.id.id);
        add_customer = findViewById(R.id.add_customer);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(!seller.isChecked() && !buyer.isChecked()){
                useSnackBar("Select buyer or seller", add_customer);
            }
            else
                saveEntry();
            }
        });

        // Receive passed data from customer activity because we want to update
        firstname = getIntent().getStringExtra("firstname");
        lastname = getIntent().getStringExtra("lastname");
        address = getIntent().getStringExtra("address");
        status = getIntent().getStringExtra("status");
        phone_number = getIntent().getStringExtra("phone_number");
        passId = getIntent().getIntExtra("ID", 0);

        firstnameEdittext.setText(firstname);
        lastnameEdittext.setText(lastname);
        addressEdittext.setText(address);
        phone_numberEdittext.setText(phone_number);
        idEditText.setText(String.valueOf(passId));

    }

    private void saveEntry(){
        firstname = firstnameEdittext.getText().toString();
        lastname = lastnameEdittext.getText().toString();
        phone_number = phone_numberEdittext.getText().toString();
        address = addressEdittext.getText().toString();
        id = idEditText.getText().toString();

        if(firstname.isEmpty())
            firstnameEdittext.setError("Required");
        else if(lastname.isEmpty())
            lastnameEdittext.setError("Required");
        else if(phone_number.isEmpty())
            phone_numberEdittext.setError("Required");
        else if(address.isEmpty())
            addressEdittext.setError("Required");
        else if(id.isEmpty())
            idEditText.setError("Required");

        else{
            String fullname = firstname + " " + lastname;

            if(seller.isChecked()){
                status = "Seller";
                Cursor sellers = dbHelper.getAllSellers();
                boolean exists = false;
                if(sellers.getCount() != 0){
                    while(sellers.moveToNext()){
                        if(sellers.getInt(0) == Integer.valueOf(id)){
                            Toast.makeText(AddCustomers.this, "ID has already been taken", Toast.LENGTH_SHORT).show();
                            exists = true;
                            break;
                        }
                    }
                }
                if(!exists){
                    if(dbHelper.addSeller(Integer.valueOf(id), convertFirstLetter(fullname), phone_number, address, status)){
                        startActivity(new Intent(AddCustomers.this, CustomersActivity.class));
                        finish();
                    }
                    else{
                        Toast.makeText(AddCustomers.this, "Unable to add customer", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            else if(buyer.isChecked()){
                status = "Buyer";
                Cursor buyers = dbHelper.getAllBuyers();
                boolean exists = false;
                if(buyers.getCount() != 0){
                    while(buyers.moveToNext()){
                        if(buyers.getInt(0) == Integer.valueOf(id)){
                            Toast.makeText(AddCustomers.this, "ID has already been taken", Toast.LENGTH_SHORT).show();
                            exists = true;
                            break;
                        }
                    }
                }
                if(!exists){
                    if(dbHelper.addBuyer(Integer.valueOf(id) ,convertFirstLetter(fullname), phone_number, address, status)){
                        startActivity(new Intent(AddCustomers.this, CustomersActivity.class));
                        Log.d(TAG, "saveEntry: " +status);
                        finish();
                    }
                    else
                        Toast.makeText(AddCustomers.this, "Unable to add customer", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(!seller.isChecked() && !buyer.isChecked()){
            useSnackBar("Select buyer or seller", add_customer);
        }
        else{
            saveEntry();
            startActivity(new Intent(AddCustomers.this, CustomersActivity.class));
        }
    }
}
