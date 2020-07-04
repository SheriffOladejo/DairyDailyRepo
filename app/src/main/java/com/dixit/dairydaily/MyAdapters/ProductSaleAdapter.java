package com.dixit.dairydaily.MyAdapters;

import android.app.AlertDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dixit.dairydaily.Models.DailyBuyObject;
import com.dixit.dairydaily.Models.ProductSaleModel;
import com.dixit.dairydaily.Others.BackupHandler;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.AddProducts.AddProductActivity;
import com.dixit.dairydaily.UI.Dashboard.BuyMilk.MilkBuyEntryActivity;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;
import com.dixit.dairydaily.UI.Dashboard.DrawerLayout.ProfileActivity;
import com.dixit.dairydaily.UI.Dashboard.ProductSale.ProductSaleActivity;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.dixit.dairydaily.Others.UtilityMethods.getFirstname;
import static com.dixit.dairydaily.Others.UtilityMethods.toast;
import static com.dixit.dairydaily.Others.UtilityMethods.truncate;

public class ProductSaleAdapter extends RecyclerView.Adapter<ProductSaleAdapter.Viewholder> {

    ArrayList<ProductSaleModel> list;
    Context context;
    DbHelper helper;
    public static String id;

    public ProductSaleAdapter(Context context, ArrayList<ProductSaleModel> list){
        this.context = context;
        this.list = list;
        helper = new DbHelper(this.context);
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_sale_layout, viewGroup, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder viewholder, int i) {
        int id = list.get(i).getId();
        String name = list.get(i).getName();
        String product = list.get(i).getProduct_name();
        String units = list.get(i).getUnits();
        String amount = list.get(i).getAmount();
        String date = list.get(i).getDate();

        viewholder.setData(date, id, name, product, units, amount);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        View view;
        public Viewholder(@NonNull View itemView) {
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
                    String units = list.get(getAdapterPosition()).getUnits();
                    String amount = list.get(getAdapterPosition()).getAmount();
                    String name = list.get(getAdapterPosition()).getName();
                    id= list.get(getAdapterPosition()).getId()+"";
                    String buyer_id = helper.getBuyerId(name)+"";

                    switch(which){
                        case 0:
                            ProductSaleActivity.id.setText(buyer_id);
                            ProductSaleActivity.units.setText(units);
                            ProductSaleActivity.all_buyers.setText(name);
                            ProductSaleActivity.amount.setText(amount);
                            ProductSaleActivity.want_to_update = true;
                            dialog.dismiss();
                            new BackupHandler(context);
                            break;
                        case 1:
                            helper.deleteProductSale(id);
                            ProductSaleActivity.productSaleAdapter = new ProductSaleAdapter(context, helper.getProductSale());
                            ProductSaleActivity.recyclerView.setAdapter(ProductSaleActivity.productSaleAdapter);
                            dialog.dismiss();
                            new BackupHandler(context);
                            break;
                        case 2:
                            String date;
                            Date dateIntermediate = new Date();
                            try{
                                DateFormat df = new DateFormat();
                                date = df.format("yyyy-MM-dd", dateIntermediate).toString();
                            }
                            catch(Exception e){
                                date = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
                            }
                            String toPrint ="\n" + date + "\n" +
                                    "Buyer: " + name + "\n" +
                                    "Units: " + units + "\n" +
                                    "Rate: " + Double.valueOf(amount) / Integer.valueOf(units) + "Rs/Unit\n" +
                                    "Product: " + product_name + "\n" +
                                    "Amount: " + amount +"\n\n";
                            Log.d("ProductSaleAdapter", "toPrint:"+toPrint);

                            byte[] mybyte = toPrint.getBytes(Charset.defaultCharset());

                            if(DashboardActivity.bluetoothAdapter != null) {
                                if (DashboardActivity.bluetoothAdapter.isEnabled() && DashboardActivity.bluetoothDevice != null) {
                                    DashboardActivity.bluetoothConnectionService.write(mybyte);
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
        }

        void setData(String date, int id, String name, String product, String units, String amount){
            TextView dateView = view.findViewById(R.id.date);
            TextView nameView = view.findViewById(R.id.name);
            TextView productView = view.findViewById(R.id.product_name);
            TextView unitsView = view.findViewById(R.id.units);
            TextView amountView = view.findViewById(R.id.amount);

            dateView.setText(date);
            nameView.setText(getFirstname(name));
            productView.setText(getFirstname(product));
            unitsView.setText(units);
            amountView.setText(amount);
        }
    }
}
