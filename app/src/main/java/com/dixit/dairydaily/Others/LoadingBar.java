package com.dixit.dairydaily.Others;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.dixit.dairydaily.R;

public class LoadingBar extends Dialog {
    public LoadingBar(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_bar);
        ProgressBar progressBar = findViewById(R.id.progressbar);
    }


}
