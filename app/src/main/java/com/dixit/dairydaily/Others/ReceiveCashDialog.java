package com.dixit.dairydaily.Others;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.dixit.dairydaily.Models.ReceiveCashModel;
import com.dixit.dairydaily.MyAdapters.InvoiceAdapter;
import com.dixit.dairydaily.MyAdapters.ReceiveCashAdapter;
import com.dixit.dairydaily.MyAdapters.ReceiveCashListAdapter;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.ViewBuyerReport.InvoiceActivity;
import com.dixit.dairydaily.UI.Dashboard.ViewBuyerReport.ReceiveCashActivity;
import com.dixit.dairydaily.UI.Dashboard.ViewBuyerReport.ReceiveCashList;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.Others.UtilityMethods.truncate;

public class ReceiveCashDialog extends Dialog implements View.OnClickListener{

    Context context;
    EditText title, amount;
    Button save;
    DbHelper dbHelper;
    int id;
    int unique_id;

    ProgressBar progressBar;

    String date, titleString, debit, credit;
    boolean wantToUpdate = false;


    public ReceiveCashDialog(Context context, int id) {
        super(context);
        this.context = context;
        dbHelper = new DbHelper(this.context);
        this.id = id;
    }

    public ReceiveCashDialog(Context context,int unique_id, int id, String date, String title, String debit, String credit){
        super(context);
        this.unique_id = unique_id;
        this.context = context;
        dbHelper = new DbHelper(this.context);
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
                String date;
                try{
                    DateFormat df = new DateFormat();
                    date = df.format("yyyy-MM-dd", dateIntermediate).toString();
                }
                catch(Exception e){
                    date = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
                }


                if(titleString.isEmpty() || amountString.isEmpty()){
                    toast(context, "Fields should be filled");
                }
                else{
                    if(wantToUpdate){
                        //Log.d("ReceiveCashDialog", "Trying to update " + unique_id + "  " + date);
                        dbHelper.updateReceiveCash(unique_id, id, date, titleString, debit, amountString);
                        new BackupHandler(context);
                        ReceiveCashActivity.list = dbHelper.getReceiveCash(id, "", "");
                        ReceiveCashAdapter adapter = new ReceiveCashAdapter(context, ReceiveCashActivity.list);
                        ReceiveCashActivity.recyclerView.setAdapter(ReceiveCashActivity.adapter);
                        ReceiveCashActivity.creditTotal = 0;
                        ReceiveCashActivity.debitTotal = 0;
                        ReceiveCashActivity.remain=0;

                        for(ReceiveCashModel model : ReceiveCashActivity.list){
                            ReceiveCashActivity.creditTotal += Double.valueOf(model.getCredit());
                            ReceiveCashActivity.debitTotal += Double.valueOf(model.getDebit());
                        }

                        ReceiveCashActivity.remain = ReceiveCashActivity.creditTotal - ReceiveCashActivity.debitTotal;
                        ReceiveCashActivity.totalCredit.setText(String.valueOf(truncate(ReceiveCashActivity.creditTotal)) + "Rs");
                        ReceiveCashActivity.totalDebit.setText(String.valueOf(truncate(ReceiveCashActivity.debitTotal)) + "Rs");
                        ReceiveCashActivity.remaining.setText(String.valueOf(truncate(-ReceiveCashActivity.remain)) + "Rs");
                        InvoiceActivity.list = dbHelper.getReceiveCashList();
                        InvoiceActivity.adapter = new InvoiceAdapter(getContext(), InvoiceActivity.list);
                        InvoiceActivity.recyclerView.setAdapter(InvoiceActivity.adapter);
                        dismiss();
                    }
                    else{
                        Log.d("ReceiveCashDialog", "Trying to add new");
                        if(!dbHelper.addReceiveCash(id, date, amountString, "0", titleString)){
                            toast(context, "Operation failed");
                            dismiss();
                        }
                        else{
                            new BackupHandler(context);
                            ReceiveCashActivity.list = dbHelper.getReceiveCash(id, "", "");
                            ReceiveCashAdapter adapter = new ReceiveCashAdapter(context, ReceiveCashActivity.list);
                            ReceiveCashActivity.recyclerView.setAdapter(adapter);
                            ReceiveCashList.list = dbHelper.getReceiveCashList();
                            ReceiveCashList.adapter = new ReceiveCashListAdapter(getContext(), ReceiveCashList.list);
                            ReceiveCashList.recyclerView.setAdapter(ReceiveCashList.adapter);
                            ReceiveCashActivity.list = dbHelper.getReceiveCash(id, "", "");
                            ReceiveCashActivity.adapter = new ReceiveCashAdapter(context, ReceiveCashActivity.list);
                            ReceiveCashActivity.recyclerView.setAdapter(ReceiveCashActivity.adapter);
                            ReceiveCashActivity.creditTotal = 0;
                            ReceiveCashActivity.debitTotal = 0;
                            for(ReceiveCashModel model : ReceiveCashActivity.list){
                                ReceiveCashActivity.creditTotal += Double.valueOf(model.getCredit());
                                ReceiveCashActivity.debitTotal += Double.valueOf(model.getDebit());
                            }
                            ReceiveCashActivity.remain =0;
                            ReceiveCashActivity.remain = ReceiveCashActivity.creditTotal - ReceiveCashActivity.debitTotal;
                            ReceiveCashActivity.totalCredit.setText(String.valueOf(truncate(ReceiveCashActivity.creditTotal)) + "Rs");
                            ReceiveCashActivity.totalDebit.setText(String.valueOf(truncate(ReceiveCashActivity.debitTotal)) + "Rs");
                            ReceiveCashActivity.remaining.setText(String.valueOf(truncate(-ReceiveCashActivity.remain)) + "Rs");
                            dismiss();
                            //context.startActivity(new Intent(context, ReceiveCashActivity.class));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
