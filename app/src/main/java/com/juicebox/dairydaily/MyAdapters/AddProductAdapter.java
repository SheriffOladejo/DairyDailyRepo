package com.juicebox.dairydaily.MyAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juicebox.dairydaily.Models.AddProductModel;
import com.juicebox.dairydaily.R;

import java.util.ArrayList;

public class AddProductAdapter extends RecyclerView.Adapter<AddProductAdapter.ViewHolder> {

    Context context;
    ArrayList<AddProductModel> list;

    public AddProductAdapter(Context context, ArrayList<AddProductModel> list){
        this.context = context;
        this.list = list;
        list.remove(0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_product_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String product_name = list.get(i).getProduct_name();
        String rate = list.get(i).getRate();
        String sr = String.valueOf(i+1);
        viewHolder.setData(sr, product_name, rate);
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

        public void setData(String sr, String product_name, String rate) {
            TextView srView = view.findViewById(R.id.sr);
            TextView nameView = view.findViewById(R.id.name);
            TextView rateView = view.findViewById(R.id.rate);

            srView.setText(sr);
            nameView.setText(product_name);
            rateView.setText(rate);
        }
    }
}
