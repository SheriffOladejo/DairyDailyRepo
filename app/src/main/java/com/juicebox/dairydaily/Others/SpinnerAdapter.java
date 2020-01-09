package com.juicebox.dairydaily.Others;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.juicebox.dairydaily.R;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<SpinnerItem> {
    public SpinnerAdapter(Context context, ArrayList<SpinnerItem> spinnerList){
        super(context, 0, spinnerList);
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
                    R.layout.custom_spinner_layout, parent, false
            );
        }

        TextView textView = convertView.findViewById(R.id.device_text);
        SpinnerItem device = getItem(position);
        if(device != null){
            textView.setText(device.getDevice_name());
        }
        return convertView;
    }
}
