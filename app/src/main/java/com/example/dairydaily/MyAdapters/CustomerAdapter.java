package com.example.dairydaily.MyAdapters;

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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dairydaily.Models.CustomerModels;
import com.example.dairydaily.Others.DbHelper;
import com.example.dairydaily.R;
import com.example.dairydaily.UI.Dashboard.Customers.AddCustomers;
import com.example.dairydaily.UI.Dashboard.Customers.CustomersActivity;

import java.util.ArrayList;
import java.util.List;

import static com.example.dairydaily.Others.UtilityMethods.getFirstname;
import static com.example.dairydaily.Others.UtilityMethods.getLastname;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> implements Filterable {

    private List<CustomerModels> list;
    private List<CustomerModels> listFull;
    private DbHelper dbHelper;
    private static final String TAG = "CustomerAdapter";

    private Context context;

    public CustomerAdapter(List<CustomerModels> list, Context context){
        this.context = context;
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
                            if(status.equals("Buyer")){
                                dbHelper.deleteBuyer(idToPass);
                            }
                            else if(status.equals("Seller")){
                                dbHelper.deleteSeller(idToPass);
                            }
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
