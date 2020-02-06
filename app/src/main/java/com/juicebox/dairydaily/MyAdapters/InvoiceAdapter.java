package com.juicebox.dairydaily.MyAdapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juicebox.dairydaily.Models.BuyerRegisterModel;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport.ReceiveCashActivity;

import java.util.ArrayList;

import static com.juicebox.dairydaily.Others.UtilityMethods.getFirstname;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {

    Context context;
    ArrayList<BuyerRegisterModel> list;

    public InvoiceAdapter(Context context, ArrayList<BuyerRegisterModel> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.invoice_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String id = String.valueOf(list.get(i).getId());
        String name = list.get(i).getName();
        String weight = list.get(i).getWeight();
        String amount = list.get(i).getAmount();
        String sr = String.valueOf(i+1);

        viewHolder.setData(sr, id, name, weight, amount);
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

        void setData(String sr, String id, String name, String weight, String amount){
            TextView idView = view.findViewById(R.id.id);
            TextView nameView = view.findViewById(R.id.name);
            TextView weightView = view.findViewById(R.id.weight);
            TextView amountView = view.findViewById(R.id.amount);
            TextView status = view.findViewById(R.id.status);
            status.setText("Unpaid");

            idView.setText(sr);
            nameView.setText(id+"."+getFirstname(name));
            weightView.setText(weight);
            amountView.setText(amount);
        }
    }
}
