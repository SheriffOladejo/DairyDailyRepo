package com.juicebox.dairydaily.MyAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juicebox.dairydaily.Models.ViewAllEntryModel;
import com.juicebox.dairydaily.R;

import java.util.ArrayList;

public class ViewAllEntryAdapter extends RecyclerView.Adapter<ViewAllEntryAdapter.ViewHolder> {

    private ArrayList<ViewAllEntryModel> list;
    Context context;

    public ViewAllEntryAdapter(ArrayList<ViewAllEntryModel> list, Context context){
        this.context = context;
        this.list =list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_all_entry_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String session = list.get(i).getSession();
        String fat = list.get(i).getFat();
        String snf = list.get(i).getSnf();
        String rate = list.get(i).getRate();
        String weight = list.get(i).getWeight();
        String amount = list.get(i).getAmount();
        String bonus = list.get(i).getBonus();
        String date = list.get(i).getDate();

        viewHolder.setData(date, session, fat, snf, rate, weight, amount, bonus);
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

        void setData(String date, String session, String fat, String snf, String rate, String weight, String amount, String bonus){
            TextView dateView = view.findViewById(R.id.date);
            ImageView sessionView = view.findViewById(R.id.session);
            TextView fatView = view.findViewById(R.id.fat_snf);
            TextView rateView = view.findViewById(R.id.rate);
            TextView weightView = view.findViewById(R.id.weight);
            TextView amountView = view.findViewById(R.id.amount);
            TextView bonusView = view.findViewById(R.id.bonus);

            dateView.setText(date);
            fatView.setText(fat + "/" + snf);
            rateView.setText(rate);
            weightView.setText(weight);
            amountView.setText(amount);
            bonusView.setText(bonus);
            if(session.equals("Morning"))
                sessionView.setImageResource(R.drawable.ic_lights);
            else if(session.equals("Evening"))
                sessionView.setImageResource(R.drawable.ic_moon_phase_outline);
        }
    }
}
