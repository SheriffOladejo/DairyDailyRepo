package com.juicebox.dairydaily.MyAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juicebox.dairydaily.Models.ProductSaleModel;
import com.juicebox.dairydaily.R;

import java.util.ArrayList;

import static com.juicebox.dairydaily.Others.UtilityMethods.getFirstname;

public class ProductSaleAdapter extends RecyclerView.Adapter<ProductSaleAdapter.Viewholder> {

    ArrayList<ProductSaleModel> list;
    Context context;

    public ProductSaleAdapter(Context context, ArrayList<ProductSaleModel> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_sale_layout, viewGroup, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder viewholder, int i) {
        int id = list.get(i).getId();
        String name = list.get(i).getName();
        String product = list.get(i).getProduct_name();
        String units = list.get(i).getUnits();
        String amount = list.get(i).getAmount();
        String sr = String.valueOf(i+1);

        viewholder.setData(sr, id, name, product, units, amount);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        View view;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        void setData(String sr, int id, String name, String product, String units, String amount){
            TextView srView = view.findViewById(R.id.sr);
            TextView nameView = view.findViewById(R.id.name);
            TextView productView = view.findViewById(R.id.product_name);
            TextView unitsView = view.findViewById(R.id.units);
            TextView amountView = view.findViewById(R.id.amount);

            srView.setText(sr);
            nameView.setText(String.valueOf(id) + "." + getFirstname(name));
            productView.setText(getFirstname(product));
            unitsView.setText(units);
            amountView.setText(amount);
        }
    }
}
