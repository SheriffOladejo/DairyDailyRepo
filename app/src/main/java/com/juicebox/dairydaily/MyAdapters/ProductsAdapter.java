package com.juicebox.dairydaily.MyAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.juicebox.dairydaily.Models.AddProductModel;
import com.juicebox.dairydaily.Others.ProductSpinnerItem;
import com.juicebox.dairydaily.Others.SpinnerItem;
import com.juicebox.dairydaily.R;

import java.util.ArrayList;

public class ProductsAdapter extends ArrayAdapter<AddProductModel> {
    public ProductsAdapter(Context context, ArrayList<AddProductModel> list) {
        super(context, 0, list);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull  ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.product_layout, parent, false
            );
        }

        TextView textView = convertView.findViewById(R.id.device_text);
        AddProductModel item = getItem(position);
        if(item != null){
            textView.setText(item.getProduct_name());

        }
        return convertView;
    }
}
