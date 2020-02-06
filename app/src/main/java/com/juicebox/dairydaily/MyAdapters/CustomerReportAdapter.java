package com.juicebox.dairydaily.MyAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juicebox.dairydaily.Models.CustomerReportModel;
import com.juicebox.dairydaily.R;

import java.util.ArrayList;

public class CustomerReportAdapter extends RecyclerView.Adapter<CustomerReportAdapter.ViewHolder> {
    Context context;
    ArrayList<CustomerReportModel> list;
    public CustomerReportAdapter(Context context, ArrayList<CustomerReportModel> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.customer_report_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String date = list.get(i).getDate();
        String fat = list.get(i).getFat();
        String snf = list.get(i).getSnf();
        String weight = list.get(i).getWeight();
        String amount = list.get(i).getAmount();

        viewHolder.setData(date, fat, snf, weight, amount);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        void setData(String date, String fat, String snf, String weight, String amount){
            TextView dateView = view.findViewById(R.id.date);
            TextView fatView = view.findViewById(R.id.fat);
            TextView snfView = view.findViewById(R.id.snf);
            TextView weightView = view.findViewById(R.id.weight);
            TextView amountView = view.findViewById(R.id.amount);

            dateView.setText(date);
            fatView.setText(fat);
            snfView.setText(snf);
            weightView.setText(weight);
            amountView.setText(amount);
        }
    }
}
