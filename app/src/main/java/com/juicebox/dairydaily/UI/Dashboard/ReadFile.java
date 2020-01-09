package com.juicebox.dairydaily.UI.Dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.juicebox.dairydaily.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ReadFile extends AppCompatActivity {

    private static final String TAG = "ReadFile";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_file);

        InputStream inputStream;
        String[] ids;
        inputStream = getResources().openRawResource(R.raw.book1);
        List<String> list = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try{
            String csvLine;
            while((csvLine = reader.readLine()) != null){
                ids = csvLine.split(",");
                list.add(ids[0]);
                //Log.d(TAG, "Column 1: " + ids[0]);
            }
        }
        catch(Exception e){
            Log.d(TAG, e.getMessage());
        }

    }
}
