package com.dixit.dairydaily.MyAdapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dixit.dairydaily.Models.AddProductModel;
import com.dixit.dairydaily.Others.BackupHandler;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.Others.ReceiveCashDialog;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.AddProducts.AddProductActivity;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;
import com.dixit.dairydaily.UI.Dashboard.ViewBuyerReport.ReceiveCashActivity;

import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.Others.UtilityMethods.truncate;

public class AddProductAdapter extends RecyclerView.Adapter<AddProductAdapter.ViewHolder> {

    Context context;
    ArrayList<AddProductModel> list;
    DbHelper helper;

    public AddProductAdapter(Context context, ArrayList<AddProductModel> list){
        this.context = context;
        this.list = list;
        helper = new DbHelper(this.context);
        list.remove(0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_product_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String product_name = list.get(i).getProduct_name();
        String rate = list.get(i).getRate();
        String sr = String.valueOf(i+1);
        viewHolder.setData(sr, product_name, rate);
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

            ImageView optionsView = view.findViewById(R.id.options);

            String[] options = {"Edit", "Delete", "Print"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Select Action");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String product_name = list.get(getAdapterPosition()).getProduct_name();
                    String rate = list.get(getAdapterPosition()).getRate();
                    String id= list.get(getAdapterPosition()).getId();

                    switch(which){
                        case 0:
                            AddProductActivity.product_name.setText(product_name);
                            AddProductActivity.rate.setText(rate);
                            AddProductActivity.id = id;
                            AddProductActivity.want_to_update = true;
                            dialog.dismiss();
                            break;
                        case 1:
                            helper.deleteProduct(id);
                            new BackupHandler(context);
                            list = helper.getProducts();
                            AddProductActivity.adapter = new AddProductAdapter(context, list);
                            AddProductActivity.recyclerView.setAdapter(AddProductActivity.adapter);
                            break;
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
        }

        public void setData(String sr, String product_name, String rate) {
            TextView srView = view.findViewById(R.id.sr);
            TextView nameView = view.findViewById(R.id.name);
            TextView rateView = view.findViewById(R.id.rate);

            srView.setText(sr);
            nameView.setText(product_name);
            rateView.setText(rate);
        }
    }
}
