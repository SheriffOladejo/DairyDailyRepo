package com.juicebox.dairydaily.MyAdapters;

import android.app.Activity;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.juicebox.dairydaily.Models.CustomerModels;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.R;
import com.juicebox.dairydaily.UI.Dashboard.BuyMilk.MilkBuyEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.Customers.AddCustomers;
import com.juicebox.dairydaily.UI.Dashboard.Customers.CustomersActivity;
import com.juicebox.dairydaily.UI.Dashboard.DrawerLayout.ViewAllEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.ProductSale.ProductSaleActivity;
import com.juicebox.dairydaily.UI.Dashboard.SellMilk.MilkSaleEntryActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport.ReceiveCashActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewBuyerReport.ViewReportByDateActivity;
import com.juicebox.dairydaily.UI.Dashboard.ViewReport.CustomerReportActivity;
import com.juicebox.dairydaily.UI.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.juicebox.dairydaily.Others.UtilityMethods.getFirstname;
import static com.juicebox.dairydaily.Others.UtilityMethods.getLastname;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> implements Filterable {

    private List<CustomerModels> list;
    private List<CustomerModels> listFull;
    private DbHelper dbHelper;
    private static final String TAG = "CustomerAdapter";
    private String shift, date;
    private String from;

    private Context context;

    public UsersListAdapter(List<CustomerModels> list, Context context, String from, String shift, String date){
        this.context = context;
        this.shift = shift;
        this.date = date;
        this.from = from;
        this.list = list;
        listFull = new ArrayList<>(list);
        dbHelper = new DbHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.customer_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String name = list.get(i).getName();
        String phone_number = list.get(i).getPhone_number();
        int id = list.get(i).getId();
        String status = list.get(i).getStatus();

        viewHolder.setData(name, phone_number, id, status);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CustomerModels> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(listFull);
            }
            else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(CustomerModels item: listFull){
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder {

        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String name = list.get(getAdapterPosition()).getName();
                    String type = list.get(getAdapterPosition()).getStatus();
                    String phone_number = list.get(getAdapterPosition()).getPhone_number();
                    Intent intent = new Intent(context, MilkSaleEntryActivity.class);
                    Intent intent2 = new Intent(context, MilkBuyEntryActivity.class);

                    int id2 = dbHelper.getSellerId(name, phone_number);
                    intent2.putExtra("name", name);
                    intent2.putExtra("passed", true);
                    intent2.putExtra("Shift", shift);
                    intent2.putExtra("Date", date);
                    intent2.putExtra("id", id2);

                    int id = dbHelper.getBuyerId(name, phone_number);
                    intent.putExtra("name", name);
                    intent.putExtra("passed", true);
                    intent.putExtra("Shift", shift);
                    intent.putExtra("Date", date);
                    intent.putExtra("id", id);

                    Log.d(TAG, "onMilkBuyClick: " + intent2.getStringExtra(from));
                    Log.d(TAG, "onMilkBuyClick: " + id2);

                    if(from.equals("MilkSaleEntryActivity")){
                        context.startActivity(intent);
                        ((Activity)context).finish();
                    }
                    else if(from.equals("MilkBuyEntryActivity")){
                        context.startActivity(intent2);
                        ((Activity)context).finish();
                    }
                    else if(from.equals("ReceiveCash")){
                        context.startActivity(new Intent(context, ReceiveCashActivity.class).putExtra("id", dbHelper.getBuyerId(name, phone_number)).putExtra("name", name));
                        ((Activity)context).finish();
                    }
                    else if(from.equals("ViewReport")){
                        context.startActivity(new Intent(context, ViewReportByDateActivity.class).putExtra("id", dbHelper.getBuyerId(name, phone_number)).putExtra("name", name));
                        ((Activity)context).finish();
                    }
                    else if(from.equals("CustomerReportActivity")) {
                        context.startActivity(new Intent(context, CustomerReportActivity.class).putExtra("id", dbHelper.getSellerId(name, phone_number)).putExtra("name", name));
                        ((Activity)context).finish();
                    }
                    else if(from.equals("ProductSaleActivity")) {
                        context.startActivity(new Intent(context, ProductSaleActivity.class).putExtra("id", dbHelper.getBuyerId(name, phone_number)).putExtra("name", name));
                        ((Activity) context).finish();
                    }
                    else if(from.equals("ViewAllEntryActivity")) {
                        context.startActivity(new Intent(context, ViewAllEntryActivity.class).putExtra("id", dbHelper.getSellerId(name, phone_number)).putExtra("name", name));
                        ((Activity) context).finish();
                    }
                }
            });
        }

        private void setData(String name, String phone_number, int id, final String status){

            String[] options = {"Chat", "Edit", "Delete"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Select Action");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch(which){
                        case 0:
                            Toast.makeText(context, "Chat", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
                            int idToPass = list.get(getAdapterPosition()).getId();
                            String lastNameToPass = getLastname(list.get(getAdapterPosition()).getName());
                            String firstNameToPass = getFirstname(list.get(getAdapterPosition()).getName());
                            String phoneNumberToPass = list.get(getAdapterPosition()).getPhone_number();
                            String addressToPass = list.get(getAdapterPosition()).getAddress();
                            String statusToPass = list.get(getAdapterPosition()).getStatus();

                            Intent intent = new Intent(context, AddCustomers.class);
                            intent.putExtra("ID", idToPass);
                            intent.putExtra("firstname", firstNameToPass);
                            intent.putExtra("lastname", lastNameToPass);
                            intent.putExtra("phone_number", phoneNumberToPass);
                            intent.putExtra("address", addressToPass);
                            intent.putExtra("status", statusToPass);
                            intent.putExtra("update", true);
                            context.startActivity(intent);
                            break;
                        case 2:
                            int id = list.get(getAdapterPosition()).getId();
                            String status = list.get(getAdapterPosition()).getStatus();
                            if(status.equals("Buyer")){
                                dbHelper.deleteBuyer(id);
                                Log.d(TAG, "onClick: buyer" + id);
                                context.startActivity(new Intent(context, CustomersActivity.class));
                            }
                            else if(status.equals("Seller")){
                                dbHelper.deleteSeller(id);
                                Log.d(TAG, "onClick: seller" + id);
                                context.startActivity(new Intent(context, CustomersActivity.class));
                            }
                            Log.d(TAG, "onClick: " + id);
                            break;
                    }
                }
            });

            TextView nameView = view.findViewById(R.id.name);
            TextView phoneNumberView = view.findViewById(R.id.phone_number);
            TextView idView = view.findViewById(R.id.id);
            ImageView imageView = view.findViewById(R.id.options);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.show();
                }
            });

            nameView.setText(name);
            phoneNumberView.setText(phone_number);
            idView.setText(String.valueOf(id));
        }

    }
}
