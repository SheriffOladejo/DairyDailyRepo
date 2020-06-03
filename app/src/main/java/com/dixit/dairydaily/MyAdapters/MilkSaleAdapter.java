package com.dixit.dairydaily.MyAdapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dixit.dairydaily.Models.DailySalesObject;
import com.dixit.dairydaily.Others.BackupHandler;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;
import com.dixit.dairydaily.UI.Dashboard.SellMilk.MilkSaleEntryActivity;

import java.nio.charset.Charset;
import java.util.List;

import static com.dixit.dairydaily.Others.UtilityMethods.getFirstname;
import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.Others.UtilityMethods.truncate;

public class
MilkSaleAdapter extends RecyclerView.Adapter<MilkSaleAdapter.ViewHolder> {

    List<DailySalesObject> list;
    Context context;
    private static final String TAG = "MilkSaleAdapter";
    DbHelper helper;

    public MilkSaleAdapter(List<DailySalesObject> list, Context context){
        this.context = context;
        this.list = list;
        helper = new DbHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.milk_sale_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String name = list.get(i).getBuyerName();
        double weight = list.get(i).getWeight();
        double rate = list.get(i).getRate();
        double amount = list.get(i).getAmount();
        int id = list.get(i).getId();
        double fat = list.get(i).getRate();
        String snf = "0";
        String sr = String.valueOf(i+1);
        Log.d(TAG, "onBindViewHolder" + String.valueOf(weight));

        viewHolder.setData(sr, name, weight, rate, amount, id, fat, snf);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        AlertDialog.Builder builder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    builder.show();
                    return true;
                }
            });
        }

        void setData(String sr, String name, double weight, double rate, double amount, int id, double fat, String snf){
            TextView srView = view.findViewById(R.id.sr);
            TextView nameView = view.findViewById(R.id.name);
            TextView weightView = view.findViewById(R.id.weight);
            TextView snfView = view.findViewById(R.id.snf);
            TextView rateView = view.findViewById(R.id.rate);
            TextView amountView = view.findViewById(R.id.amount);
            ImageView optionsView = view.findViewById(R.id.options);

            String[] options = {"Edit", "Delete", "Print"};
            builder = new AlertDialog.Builder(context);
            builder.setTitle("Select Action");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    int unique_Id = list.get(getAdapterPosition()).getUnique_id();
                    int user_Id = list.get(getAdapterPosition()).getId();
                    String name = list.get(getAdapterPosition()).getBuyerName();
                    double rate = list.get(getAdapterPosition()).getRate();
                    double amount = list.get(getAdapterPosition()).getAmount();
                    double weight = list.get(getAdapterPosition()).getWeight();
                    String shift = list.get(getAdapterPosition()).getShift();
                    String date = list.get(getAdapterPosition()).getDate();

                    switch(which){
                        case 0:

                            MilkSaleEntryActivity.id.setText(user_Id+"");
                            MilkSaleEntryActivity.weight.setText(weight+"");
                            MilkSaleEntryActivity.amount_display.setText(amount+"");
                            MilkSaleEntryActivity.wantToUpdate = true;
                            MilkSaleEntryActivity.unique_id = unique_Id;
                            MilkSaleEntryActivity.user_id = user_Id;
                            dialog.dismiss();
                            break;
                        case 1:
                            helper.deleteMilkSaleEntry(unique_Id, false);
                            helper.deleteReceiveCash(user_Id, "delete_from_milk_sale",date);
                            list = MilkSaleEntryActivity.dbHelper.getDailySalesData(date, shift);
                            Log.d(TAG, "list size: " + list.size());
                            MilkSaleEntryActivity.weightTotal = 0;
                            MilkSaleEntryActivity.amountTotal = 0;
                            MilkSaleEntryActivity.averageFat = 0;
                            for(DailySalesObject model : list){
                                MilkSaleEntryActivity.weightTotal += Double.valueOf(model.getWeight());
                                MilkSaleEntryActivity.amountTotal += Double.valueOf(model.getAmount());
                                MilkSaleEntryActivity.averageFat += Double.valueOf(model.getRate());
                            }
                            MilkSaleEntryActivity.totalAmount.setText(String.valueOf(truncate(MilkSaleEntryActivity.amountTotal)) + "Rs");
                            MilkSaleEntryActivity.totalWeight.setText(String.valueOf(truncate(MilkSaleEntryActivity.weightTotal)) + "Ltr");
                            MilkSaleEntryActivity.fatAverage.setText(String.valueOf(truncate(MilkSaleEntryActivity.averageFat/list.size())) + "%");
                            MilkSaleAdapter adapter = new MilkSaleAdapter(list, context);
                            MilkSaleEntryActivity.recyclerView.setAdapter(adapter);
                            new BackupHandler(context);
                            dialog.dismiss();
                            break;
                        case 2:
                            String toPrint ="\n\n\n"+ date + "\n" + "ID " + user_Id + " " + name + "\n" +
                                    "SHIFT   | " + shift  +
                                    "\nWEIGHT  | " + truncate(weight) + "Ltr\nRATE    | " + rate + "Rs/Ltr\n" + "TOTAL RS| " + truncate(amount) + "Rs\n      DAIRYDAILY APP\n\n\n";
                            Log.d(TAG, "toPrint: " + toPrint);
                            byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());

                            if(DashboardActivity.bluetoothAdapter != null) {
                                if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                                    try {
                                        DashboardActivity.bluetoothConnectionService.write(mybyte);
                                        Log.d(TAG, "onOptionsSelected: Printing with DashboardActivity BT");
                                    } catch (Exception e) {
                                        Log.d(TAG, "onOptionsSelected: Unable to print");
                                        toast(context, "Unable to print");
                                    }
                                } else {
                                    toast(context, "Printer is not connected");
                                }
                            }
                            else{
                                toast(context, "Bluetooth is off");
                            }
                        default:
                            dialog.dismiss();
                    }
                }
            });

            optionsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.show();
                }
            });

            String idString = String.valueOf(id);
            String fatString = String.valueOf(fat);

            srView.setText(sr);
            nameView.setText(idString + "." + getFirstname(name));
            weightView.setText(truncate(weight));
            snfView.setText(String.valueOf(fatString + "/" +snf));
            rateView.setText(truncate(rate));
            amountView.setText(truncate(amount));
        }
    }
}
