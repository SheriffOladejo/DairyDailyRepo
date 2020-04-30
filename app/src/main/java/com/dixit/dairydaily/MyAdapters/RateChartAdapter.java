package com.dixit.dairydaily.MyAdapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.dixit.dairydaily.Models.RateChartModel;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.Others.Prevalent;
import com.dixit.dairydaily.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.Others.UtilityMethods.truncate;

public class RateChartAdapter extends RecyclerView.Adapter<RateChartAdapter.ViewHolder> {

    private List<RateChartModel> list;
    private Context context;
    public static boolean isUploaded = false;

    public RateChartAdapter(Context context, List<RateChartModel> list){
        this.list = list;
        this.context = context;

    }

    public RateChartAdapter(Context context){
        this.context = context;
        this.list = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rate_object_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String fat;
        String rate;
        fat = list.get(i).getFat();
        rate = list.get(i).getRate();
        if(rate != null){
            if(!rate.equals("")){
                viewHolder.setData(fat, rate);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private static final String TAG = "RateChartAdapter";

    public class ViewHolder extends RecyclerView.ViewHolder {

        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            EditText text = view.findViewById(R.id.rate);
            text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String rate = s.toString();
                    try{
                        truncate(Double.valueOf(rate));
                        String fat = list.get(getAdapterPosition()).getFat();
                        String snf = list.get(0).getFat();
                        DbHelper dbHelper = new DbHelper(context);
                        dbHelper.updatetRateChart(rate, fat, snf);
                    }
                    catch(Exception e){
                        //toast(context, "Invalid values");
                    }
                }
            });

        }

        public void setData(String fat, String rate){
            TextView fatView = view.findViewById(R.id.fat);
            EditText rateEdit = view.findViewById(R.id.rate);
            fatView.setVisibility(View.VISIBLE);
            rateEdit.setVisibility(View.VISIBLE);
            fatView.setText(fat);
            rateEdit.setText(rate);
        }
    }
}
