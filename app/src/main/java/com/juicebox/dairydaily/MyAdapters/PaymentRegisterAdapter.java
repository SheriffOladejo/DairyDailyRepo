package com.juicebox.dairydaily.MyAdapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.juicebox.dairydaily.Models.PaymentRegisterModel;
import com.juicebox.dairydaily.R;

import java.util.ArrayList;

import static com.juicebox.dairydaily.Others.UtilityMethods.getFirstname;
import static com.juicebox.dairydaily.UI.Dashboard.ViewReport.PaymentRegisterActivity.selectedUsers;

public class PaymentRegisterAdapter extends RecyclerView.Adapter<PaymentRegisterAdapter.ViewHolder> {
    Context context;
    ArrayList<PaymentRegisterModel> list;

    public PaymentRegisterAdapter(Context context, ArrayList<PaymentRegisterModel> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.payment_register_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String id = String.valueOf(list.get(i).getId());
        String weight = list.get(i).getWeight();
        String amount = list.get(i).getAmount();
        String name = list.get(i).getName();
        viewHolder.setData(id, weight, amount, name);
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
            itemView.findViewById(R.id.checkbox).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PaymentRegisterModel model = list.get(getAdapterPosition());
                    selectedUsers = new ArrayList<>();
                    selectedUsers.add(model);
                    Log.d("PaymentRegisterAdapter", "Added");
                }
            });
        }

        void setData(String id, String weight, String amount, String name){
            TextView idView = view.findViewById(R.id.id);
            TextView weightView = view.findViewById(R.id.weight);
            TextView amountView = view.findViewById(R.id.amount);
            TextView nameView = view.findViewById(R.id.name);
            CheckBox checkBox = view.findViewById(R.id.checkbox);

            nameView.setText(getFirstname(name));
            idView.setText(id);
            weightView.setText(weight);
            amountView.setText(amount);
        }
    }
}
