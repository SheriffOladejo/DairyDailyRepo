package com.juicebox.dairydaily.MyAdapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.juicebox.dairydaily.Models.RateChartModel;
import com.juicebox.dairydaily.R;

import java.util.List;

public class RateChartAdapter extends RecyclerView.Adapter<RateChartAdapter.ViewHolder> {

    private List<RateChartModel> list;

    public RateChartAdapter(List<RateChartModel> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rate_object_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String fat = list.get(i).getFat();
        String rate = list.get(i).getRate();

        viewHolder.setData(fat, rate);
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

        public void setData(String fat, String rate){
            TextView fatView = view.findViewById(R.id.fat);
            EditText rateEdit = view.findViewById(R.id.rate);

            fatView.setText(fat);
            rateEdit.setText(rate);
        }
    }
}
