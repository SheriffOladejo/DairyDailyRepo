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

import com.dixit.dairydaily.Models.DailyBuyObject;
import com.dixit.dairydaily.Others.BackupHandler;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.BuyMilk.MilkBuyEntryActivity;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;

import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.dixit.dairydaily.Others.UtilityMethods.getFirstname;
import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.Others.UtilityMethods.truncate;
import static com.dixit.dairydaily.UI.Dashboard.BuyMilk.MilkBuyEntryActivity.fatAverage;

public class MilkBuyAdapter extends RecyclerView.Adapter<MilkBuyAdapter.ViewHolder> {

    Context context;
    ArrayList<DailyBuyObject> list;
    private static final String TAG = "MilkBuyAdapter";
    DbHelper helper;

    public MilkBuyAdapter(Context context, ArrayList<DailyBuyObject> list){
        this.list = list;
        this.context = context;
        helper=new DbHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.milk_buy_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String name = list.get(i).getSellerName();
        double weight = list.get(i).getWeight();
        double rate = list.get(i).getRate();
        double snf = list.get(i).getSnf();
        double amount = list.get(i).getAmount();
        double fat = list.get(i).getFat();
        int id = list.get(i).getId();
        String sr = String.valueOf(i+1);
        Log.d(TAG, "onBindViewHolder: " + name);

        viewHolder.setData(sr, name, weight, snf, rate, amount, id, fat);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        AlertDialog.Builder builder;

        public ViewHolder(@NonNull View itemView){
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

        void setData(String sr, String name, double weight, double snf, double rate, double amount, int id, double fat){
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

                    int unique_Id = list.get(getAdapterPosition()).getUnique_Id();
                    int user_Id = list.get(getAdapterPosition()).getId();
                    String name = list.get(getAdapterPosition()).getSellerName();
                    double fat = list.get(getAdapterPosition()).getFat();
                    double snf = list.get(getAdapterPosition()).getSnf();
                    String type = list.get(getAdapterPosition()).getType();
                    double rate = list.get(getAdapterPosition()).getRate();
                    double amount = list.get(getAdapterPosition()).getAmount();
                    double weight = list.get(getAdapterPosition()).getWeight();
                    String shift = list.get(getAdapterPosition()).getShift();
                    String date = list.get(getAdapterPosition()).getDate();

                    switch(which){
                        case 0:

                            MilkBuyEntryActivity.id.setText(user_Id+"");
                            MilkBuyEntryActivity.weight.setText(weight+"");
                            MilkBuyEntryActivity.amount_display.setText(amount+"");
                            MilkBuyEntryActivity.rate_display.setText(rate+"");
                            MilkBuyEntryActivity.unique_id = unique_Id;
                            MilkBuyEntryActivity.cow_button.setChecked(true);
                            MilkBuyEntryActivity.type=type;
                            MilkBuyEntryActivity.fat.setText(fat+"");
                            MilkBuyEntryActivity.snf.setText(snf+"");
                            MilkBuyEntryActivity.wantToUpdate = true;
                            dialog.dismiss();
                            break;
                        case 1:
                            int unique_Id1 = list.get(getAdapterPosition()).getUnique_Id();
                            helper.deleteMilkBuyEntry(unique_Id1, false);
                            list = MilkBuyEntryActivity.dbHelper.getDailyBuyData(date, shift);
                            Log.d(TAG, "list size: " + list.size());
                            MilkBuyEntryActivity.weightTotal = 0;
                            MilkBuyEntryActivity.amountTotal = 0;
                            MilkBuyEntryActivity.averageFat = 0;
                            for(DailyBuyObject model : list){
                                MilkBuyEntryActivity.weightTotal += Double.valueOf(model.getWeight());
                                MilkBuyEntryActivity.amountTotal += Double.valueOf(model.getAmount());
                                MilkBuyEntryActivity.averageFat += Double.valueOf(model.getFat());
                            }
                            MilkBuyEntryActivity.totalAmount.setText(String.valueOf(truncate(MilkBuyEntryActivity.amountTotal)) + "Rs");
                            MilkBuyEntryActivity.totalWeight.setText(String.valueOf(truncate(MilkBuyEntryActivity.weightTotal)) + "Ltr");
                            fatAverage.setText(String.valueOf(truncate(MilkBuyEntryActivity.averageFat/list.size())) + "%");
                            MilkBuyAdapter adapter = new MilkBuyAdapter(context, list);
                            MilkBuyEntryActivity.recyclerView.setAdapter(adapter);
                            new BackupHandler(context);
                            dialog.dismiss();
                            break;
                        case 2:
                            String toPrint ="\n"+ date + "\n" + "ID " + user_Id + "    " + name + "\n" +
                                    "SHIFT  | " + shift + "\n" + "TYPE   | " + type + "\nFAT %  | " + truncate(fat) + "\nSNF/CLR| " + truncate(snf) +
                                    "\nWEIGHT | " + weight + " Ltr\nRATE   | " + rate + "Rs/Ltr" + "\nAmount | " + amount + "Rs\n";
                            toPrint += "\n      DAIRYDAILY APP\n\n";
                            Log.d(TAG, "toPrint: " + toPrint);
                            byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());

                            if (DashboardActivity.bluetoothAdapter != null) {
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
                            } else {
                                toast(context, "Bluetooth is off");
                            }
                            dialog.dismiss();
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
