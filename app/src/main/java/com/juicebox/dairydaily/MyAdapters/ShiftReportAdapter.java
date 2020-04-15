package com.juicebox.dairydaily.MyAdapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juicebox.dairydaily.Models.ShiftReportModel;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.ViewReport.ShiftReportActivity;

import java.util.ArrayList;

import static com.juicebox.dairydaily.Others.UtilityMethods.getFirstname;
import static com.juicebox.dairydaily.UI.Dashboard.ViewReport.ShiftReportActivity.count;
import static com.juicebox.dairydaily.UI.Dashboard.ViewReport.ShiftReportActivity.phone_number;

public class ShiftReportAdapter extends RecyclerView.Adapter<ShiftReportAdapter.ViewHolder> {

    Context context;
    ArrayList<ShiftReportModel> list;
    DbHelper helper;

    public ShiftReportAdapter(Context context, ArrayList<ShiftReportModel> list){
        this.context = context;
        this.list = list;
        helper = new DbHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shift_report_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        int id = list.get(i).getId();
        String fat = list.get(i).getFat();
        String snf = list.get(i).getSnf();
        String weight = list.get(i).getWeight();
        String amount = list.get(i).getAmount();
        String name = list.get(i).getName();
        String rate= list.get(i).getRate();

        viewHolder.setData(String.valueOf(id), fat, snf, weight, amount, name, rate);
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

        void setData(String id, String fat, String snf, String weight, String amount, String name, String rate){
            TextView rateView = view.findViewById(R.id.rate);
            TextView nameView = view.findViewById(R.id.name);
            TextView idView = view.findViewById(R.id.id);
            TextView snfView = view.findViewById(R.id.snf);
            TextView weightView = view.findViewById(R.id.weight);
            TextView amountView = view.findViewById(R.id.amount);
            view.findViewById(R.id.options).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phone_number = helper.getSellerPhone_Number(list.get(getAdapterPosition()).getId());
                    count +=1;
                    Log.d("ShiftReportAdapter", "ShiftReportActivity.phone_number: "+ phone_number + "  " + count);
                }
            });

            rateView.setText(rate);
            nameView.setText(getFirstname(name));
            idView.setText(id);
            snfView.setText(fat +"/"+snf);
            weightView.setText(weight);
            amountView.setText(amount);
        }
    }
}
