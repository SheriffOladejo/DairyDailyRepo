package com.juicebox.dairydaily.MyAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juicebox.dairydaily.Models.ReportByDateModels;
import com.juicebox.dairydaily.R;

import java.util.ArrayList;

import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;

public class  ReportByDateAdapter extends RecyclerView.Adapter<ReportByDateAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ReportByDateModels> list;

    public ReportByDateAdapter(Context context, ArrayList<ReportByDateModels> list){
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.report_by_date_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String date = list.get(i).getDate();
        String rate = truncate(Double.valueOf(list.get(i).getRate()));
        String weight = truncate(Double.valueOf(list.get(i).getWeight()));
        String amount = truncate(Double.valueOf(list.get(i).getAmount()));
        Log.d("ReportByDateAdapter", "i Size: " +i);

        viewHolder.setData(date, rate, weight, amount);
    }

    @Override
    public int getItemCount() {
        Log.d("ReportByDateAdapter", "List Size: " +list.size());
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        void setData(String date, String rate, String weight, String amount){
            TextView dateView = view.findViewById(R.id.date);
            TextView rateView = view.findViewById(R.id.rate);
            TextView weightView = view.findViewById(R.id.weight);
            TextView amountView = view.findViewById(R.id.amount);

            Log.d("ReportByDateAdapter", "setData: " + date);

            dateView.setText(date);
            rateView.setText(rate);
            weightView.setText(weight);
            amountView.setText(amount);
        }
    }
}
