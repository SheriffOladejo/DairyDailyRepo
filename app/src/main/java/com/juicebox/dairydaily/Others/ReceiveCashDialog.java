package com.juicebox.dairydaily.Others;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport.ReceiveCashActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.juicebox.dairydaily.Others.UtilityMethods.toast;

public class ReceiveCashDialog extends Dialog implements View.OnClickListener{

    Context context;
    EditText title, amount;
    Button save;
    DbHelper dbHelper;
    int id;

    String date, titleString, debit, credit;
    boolean wantToUpdate = false;


    public ReceiveCashDialog(Context context, int id) {
        super(context);
        this.context = context;
        dbHelper = new DbHelper(this.context);
        this.id = id;
    }

    public ReceiveCashDialog(Context context, int id, String date, String title, String debit, String credit){
        super(context);
        dbHelper = new DbHelper(context);
        this.context = context;
        this.id = id;
        this.date = date;
        this.titleString = title;
        this.debit = debit;
        this.credit = credit;
        wantToUpdate = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.receive_cash_dialog);
        title = findViewById(R.id.title);
        amount = findViewById(R.id.amount);
        save = findViewById(R.id.save);
        if(wantToUpdate){
            title.setText(titleString);
            amount.setText(credit);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleString = title.getText().toString();
                String amountString = amount.getText().toString();
                Date dateIntermediate = new Date();
                String date = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);

                if(wantToUpdate){
                    dbHelper.updateReceiveCash(id, date, titleString, debit, amountString);
                }

                if(titleString.isEmpty() || amountString.isEmpty()){
                    toast(context, "Fields should be filled");
                }
                else{
                    if(!dbHelper.addReceiveCash(id, date, amountString, "0", titleString)){
                        toast(context, "Operation failed");
                        dismiss();
                    }
                    else{
                        context.startActivity(new Intent(context, ReceiveCashActivity.class));
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
