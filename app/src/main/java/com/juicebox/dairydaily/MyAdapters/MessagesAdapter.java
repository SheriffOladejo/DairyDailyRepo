package com.juicebox.dairydaily.MyAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juicebox.dairydaily.Models.MessagesModel;
import com.juicebox.dairydaily.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    Context context;
    ArrayList<MessagesModel> list;

    public MessagesAdapter(Context context, ArrayList<MessagesModel> list){
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String message = list.get(i).getMessage();
        String time = list.get(i).getTime();
        String status = list.get(i).getStatus();

        String date = new SimpleDateFormat("dd/MM/YYYY").format(Long.valueOf(time));
        viewHolder.setData(date, message);
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

        void setData(String date, String message){
            TextView dateView = view.findViewById(R.id.date);
            TextView messageView = view.findViewById(R.id.message);
            dateView.setText(date);
            messageView.setText(message);
        }
    }
}
