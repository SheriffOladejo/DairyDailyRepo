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
                        uploadRateFile();
                    }
                    catch(Exception e){
                        //toast(context, "Invalid values");
                    }
                }
            });

        }

        public void uploadRateFile(){
            DbHelper helper = new DbHelper(context);
            Cursor data = helper.getSNFTable();
            String total = "";
            while(data.moveToNext()){
                String snf76 = data.getString(data.getColumnIndex("SNF76"));
                String snf77 = data.getString(data.getColumnIndex("SNF77"));
                String snf78 = data.getString(data.getColumnIndex("SNF78"));
                String snf79 = data.getString(data.getColumnIndex("SNF79"));
                String snf80 = data.getString(data.getColumnIndex("SNF80"));
                String snf81 = data.getString(data.getColumnIndex("SNF81"));
                String snf82 = data.getString(data.getColumnIndex("SNF82"));
                String snf83 = data.getString(data.getColumnIndex("SNF83"));
                String snf84 = data.getString(data.getColumnIndex("SNF84"));
                String snf85 = data.getString(data.getColumnIndex("SNF85"));
                String snf86 = data.getString(data.getColumnIndex("SNF86"));
                String snf87 = data.getString(data.getColumnIndex("SNF87"));
                String snf88 = data.getString(data.getColumnIndex("SNF88"));
                String snf89 = data.getString(data.getColumnIndex("SNF89"));
                String snf90 = data.getString(data.getColumnIndex("SNF90"));
                String snf91 = data.getString(data.getColumnIndex("SNF91"));
                String snf92 = data.getString(data.getColumnIndex("SNF92"));
//                String snf93 = data.getString(data.getColumnIndex("SNF93"));
//                String snf94 = data.getString(data.getColumnIndex("SNF94"));
//                String snf95 = data.getString(data.getColumnIndex("SNF95"));
                total += snf76 + "," + snf77+","+ snf78+","+ snf79+","+ snf80+","+ snf81+","+ snf82+","+ snf83+","+ snf84+","+ snf85+","
                        + snf86+","+ snf87+","+ snf88+","+ snf89+","+ snf90+","+ snf91+","+ snf92+""+ "\n";

            }
            Log.d(TAG, "total" + total);
            try{
                File path = new File(Environment.getExternalStorageDirectory() + "/Download/", "Rate File.csv");
                FileOutputStream fileOutputStream = new FileOutputStream(path);
                fileOutputStream.write(total.getBytes());
                String filename = "Rate File";

                StorageReference ref = FirebaseStorage.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number)).child(filename);
                ref.putFile(Uri.fromFile(path)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            //toast(context, "Rate chart updated");
                            //path.delete();
                        }
                        else{
                            //Toast.makeText(context, "Something went wrong while uploading the file." + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                fileOutputStream.close();
            }
            catch(Exception e){
                toast(context, "Write failed");
            }
            isUploaded = true;
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
