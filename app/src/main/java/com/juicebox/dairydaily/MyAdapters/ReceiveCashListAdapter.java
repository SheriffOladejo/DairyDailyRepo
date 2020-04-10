package com.juicebox.dairydaily.MyAdapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.juicebox.dairydaily.Models.ReceiveCashListModel;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport.ReceiveCashActivity;

import java.util.ArrayList;

import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;


public class ReceiveCashListAdapter extends RecyclerView.Adapter<ReceiveCashListAdapter.ViewHolder> implements AdapterView.OnItemClickListener {

    ArrayList<ReceiveCashListModel> list;
    Context context;
    String id,name;

    public ReceiveCashListAdapter(Context context, ArrayList<ReceiveCashListModel> list){
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.receive_cash_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        id = list.get(i).getId();
        name = list.get(i).getName();
        String debit = list.get(i).getDue();
        String weight = list.get(i).getWeight();
        viewHolder.setData(id, name, weight, debit);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        context.startActivity(new Intent(context, ReceiveCashActivity.class).putExtra("id", Integer.valueOf(list.get(position).getId())).putExtra("name", name));
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ReceiveCashActivity.class).putExtra("id", Integer.valueOf(list.get(getAdapterPosition()).getId())).putExtra("name", list.get(getAdapterPosition()).getName()));
                }
            });
        }

        public void setData(String id, String name, String weight, String amount){
            TextView idView = view.findViewById(R.id.id);
            TextView nameView = view.findViewById(R.id.name);
            TextView weightView = view.findViewById(R.id.weight);
            TextView amountView = view.findViewById(R.id.due);
            nameView.setText(name);
            idView.setText(""+id);
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
