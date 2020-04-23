package com.dixit.dairydaily.Others;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.dixit.dairydaily.R;

public class WarningDialog  extends Dialog implements View.OnClickListener {

    Context context;
    TextView cancel, continues;

    public WarningDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.warning);
        cancel = findViewById(R.id.cancel);
        continues = findViewById(R.id.continues);
        cancel.setOnClickListener(this);
        continues.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.cancel)
            dismiss();
        else if(v.getId() == R.id.continues){
            ProgressDialog pd = new ProgressDialog(context);
            new CountDownTimer(4000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    pd.setTitle("Retrieving Data");
                    pd.setMessage("Please Wait...");
                    pd.setCancelable(false);
                    pd.show();
                }

                @Override
                public void onFinish() {
                    pd.dismiss();
                }
            }.start();
            new DataRetrievalHandler(context);
            dismiss();
        }
    }
}
