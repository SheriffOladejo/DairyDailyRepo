package com.juicebox.dairydaily.MyAdapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juicebox.dairydaily.Models.BuyerRegisterModel;
import com.juicebox.dairydaily.Models.ReceiveCashListModel;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport.ReceiveCashActivity;

import java.util.ArrayList;

import static com.juicebox.dairydaily.Others.UtilityMethods.getFirstname;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {

    Context context;
    ArrayList<ReceiveCashListModel> list;

    public InvoiceAdapter(Context context, ArrayList<ReceiveCashListModel> list){
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
        String amount = list.get(i).getDue();
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
            if(weight.equals("")){
                weightView.setText("0");
            }
            else{
                weightView.setText(""+truncate(Double.valueOf(weight)));
            }
            if(amount.equals("")){
                amountView.setText("0");
            }
            else{
                amountView.setText(""+truncate(Double.valueOf(amount)));
            }
        }
    }
}
