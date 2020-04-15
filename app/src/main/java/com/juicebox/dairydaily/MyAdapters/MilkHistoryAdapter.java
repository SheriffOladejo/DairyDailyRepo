package com.juicebox.dairydaily.MyAdapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juicebox.dairydaily.Models.MilkHistoryObject;
import com.juicebox.dairydaily.R;

import java.util.ArrayList;

public class MilkHistoryAdapter extends RecyclerView.Adapter<MilkHistoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<MilkHistoryObject> list;

    public MilkHistoryAdapter(Context context, ArrayList<MilkHistoryObject> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.milk_history_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String date = list.get(i).getDate();
        String session = list.get(i).getSession();
        String weight = list.get(i).getWeight();
        String amount = list.get(i).getAmount();
        String fat = list.get(i).getFat();
        viewHolder.setData(date, session, weight, amount, fat);
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

        void setData(String date, String session, String weight, String amount, String fat){
            TextView dateView = view.findViewById(R.id.date);
            ImageView sessionView = view.findViewById(R.id.session);
            TextView weightView = view.findViewById(R.id.totalWeight);
            TextView amountView = view.findViewById(R.id.totalAmount);
            TextView fatView = view.findViewById(R.id.fatAverage);

            dateView.setText(date);
            if(weight.equals("0.0")){
                weightView.setText("-");
            }
            else{
                weightView.setText(weight);
            }
            if(amount.equals("0.0")){
                amountView.setText("-");
            }
            else{
                amountView.setText(amount);
            }
            if(fat.equals("0.0")){
                fatView.setText("-");
            }
            else{
                fatView.setText(fat);
            }
            if(session.equals("Morning"))
                sessionView.setImageResource(R.drawable.ic_lights);
            else if(session.equals("Evening"))
                sessionView.setImageResource(R.drawable.ic_moon_phase_outline);
        }
    }
}
