package com.dixit.dairydaily.MyAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dixit.dairydaily.Models.BuyerRegisterModel;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.ViewBuyerReport.BuyerRegisterActivity;
import com.dixit.dairydaily.UI.Dashboard.ViewBuyerReport.ReceiveCashActivity;

import java.util.ArrayList;

import static com.dixit.dairydaily.Others.UtilityMethods.getFirstname;

public class BuyerRegisterAdapter extends RecyclerView.Adapter<BuyerRegisterAdapter.ViewHolder> {

    Context context;
    ArrayList<BuyerRegisterModel> list;
    DbHelper helper;

    public BuyerRegisterAdapter(Context context, ArrayList<BuyerRegisterModel> list){
        this.context = context;
        this.list = list;
        helper = new DbHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.buyer_register_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String id = String.valueOf(list.get(i).getId());
        String name = list.get(i).getName();
        String weight = list.get(i).getWeight();
        String amount = list.get(i).getAmount();

        viewHolder.setData(id, name, weight, amount);
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = list.get(getAdapterPosition()).getId();
                    String name = list.get(getAdapterPosition()).getName();
                    context.startActivity(new Intent(context, ReceiveCashActivity.class).putExtra("id", id).putExtra("name", name));
                }
            });
        }

        void setData(String id, String name, String weight, String amount){
            TextView idView = view.findViewById(R.id.id);
            TextView nameView = view.findViewById(R.id.name);
            TextView weightView = view.findViewById(R.id.weight);
            TextView amountView = view.findViewById(R.id.amount);
            view.findViewById(R.id.options).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BuyerRegisterActivity.phone_number = helper.getBuyerPhone_Number(list.get(getAdapterPosition()).getId());
                    BuyerRegisterActivity.count += 1;
                    BuyerRegisterActivity.totalAmount = Double.valueOf(list.get(getAdapterPosition()).getAmount());
                    BuyerRegisterActivity.totalWeight = Double.valueOf(list.get(getAdapterPosition()).getWeight());
                }
            });

            idView.setText(id);
            nameView.setText(getFirstname(name));
            weightView.setText(weight);
            amountView.setText(amount);
        }
    }
}
