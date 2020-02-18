package com.juicebox.dairydaily.MyAdapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juicebox.dairydaily.Models.ReceiveCashModel;
import com.juicebox.dairydaily.Others.BackupHandler;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.ReceiveCashDialog;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.DashboardActivity;
import com.juicebox.dairydaily.UI.Dashboard.SellMilk.MilkSaleEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport.ReceiveCashActivity;

import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.juicebox.dairydaily.Others.UtilityMethods.toast;
import static com.juicebox.dairydaily.Others.UtilityMethods.truncate;

public class ReceiveCashAdapter extends RecyclerView.Adapter<ReceiveCashAdapter.ViewHolder> {

    Context context;
    ArrayList<ReceiveCashModel> list;
    private static final String TAG = "ReceiveCashAdapter";
    DbHelper helper;

    public ReceiveCashAdapter(Context context, ArrayList<ReceiveCashModel> list){
        this.list = list;
        this.context = context;
        helper = new DbHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.receive_cash_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String date = list.get(i).getDate();
        String title = list.get(i).getTitle();
        String credit = list.get(i).getCredit();
        String debit = list.get(i).getDebit();

        viewHolder.setData(date, title, credit, debit);
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

        void setData(String date, String title, String credit, String debit){
            TextView dateView = view.findViewById(R.id.date);
            TextView titleView = view.findViewById(R.id.title);
            TextView creditView = view.findViewById(R.id.credit);
            TextView debitView = view.findViewById(R.id.debit);

            ImageView optionsView = view.findViewById(R.id.options);

            String[] options = {"Edit", "Delete", "Print"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Select Action");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    int user_Id = list.get(getAdapterPosition()).getId();
                    String title = list.get(getAdapterPosition()).getTitle();
                    String credit = list.get(getAdapterPosition()).getCredit();
                    String debit = list.get(getAdapterPosition()).getDebit();
                    String date = list.get(getAdapterPosition()).getDate();
                    int unique_id = list.get(getAdapterPosition()).getUnique_Id();

                    switch(which){
                        case 0:
                            new ReceiveCashDialog(context,unique_id, user_Id, date, title, debit, credit).show();
                            dialog.dismiss();
                            break;
                        case 1:
                            helper.deleteReceiveCash(unique_id);
                            new BackupHandler(context);
                            ReceiveCashActivity.list = helper.getReceiveCash(user_Id, ReceiveCashActivity.startDate, ReceiveCashActivity.endDate);
                            ReceiveCashAdapter adapter = new ReceiveCashAdapter(context, ReceiveCashActivity.list);
                            ReceiveCashActivity.recyclerView.setAdapter(adapter);
                            dialog.dismiss();
//                            context.startActivity(new Intent(context, ReceiveCashActivity.class));
//                            dialog.dismiss();
                            break;
                        case 2:
                            String toPrint ="\n\n\n"+ date + "\n" + "ID " + user_Id + " " + helper.getBuyerName(user_Id) + "\n" +
                                    "TITLE   | " + title  +
                                    "\nCREDIT  | " + truncate(Double.valueOf(credit)) + "Rs\nDEBIT   | " + Double.valueOf(debit) + "Rs";
                            toPrint += "\n\n      DAIRYDAILY APP";
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

            dateView.setText(date);
            titleView.setText(title);
            creditView.setText(credit);
            debitView.setText(debit);
        }
    }
}
