package com.dixit.dairydaily.UI.Dashboard.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.Others.Prevalent;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import io.paperdb.Paper;

import static com.dixit.dairydaily.Others.UtilityMethods.toast;

public class UpgradeToPremium extends AppCompatActivity {

    TextView expiry_date, price, price1, price2;
    DbHelper helper;

    boolean isInternetConnected = false;

    public void checkInternetConnect(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if(manager != null){
            boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
            if(is3g)
                isInternetConnected = true;
            boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
            if(isWifi)
                isInternetConnected = true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upgrade_premium);
        expiry_date = findViewById(R.id.expiry_date);

        checkInternetConnect();

        Paper.init(this);

        String orderId = String.valueOf(System.currentTimeMillis());
        String customerId = String.valueOf(System.currentTimeMillis());
        price = findViewById(R.id.price);
        price1 = findViewById(R.id.price1);
        price2 = findViewById(R.id.price2);

        price.setText(""+Paper.book().read(Prevalent.starter));
        price1.setText(""+Paper.book().read(Prevalent.spark));
        price2.setText(""+Paper.book().read(Prevalent.enterprise));

        helper = new DbHelper(this);

        String date = "";

        Cursor data = helper.getExpiryDate();
        if(data.getCount() != 0){
            while(data.moveToNext()){
                date = data.getString(data.getColumnIndex("Date"));
            }
        }
        String format;
        try{
            DateFormat df = new DateFormat();
            format = df.format("yyyy-MM-dd", new Date(Long.valueOf(date))).toString();
        }
        catch(Exception e){
            format = new SimpleDateFormat("YYYY-MM-dd").format(new Date(Long.valueOf(date)));
        }

        expiry_date.setText("Expiry Date: " + format);

        findViewById(R.id.priceButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(UpgradeToPremium.this, CheckSum.class);
//                intent.putExtra("orderId", orderId);
//                intent.putExtra("customerId", customerId);
//                intent.putExtra("price", ""+Paper.book().read(Prevalent.starter));
//                startActivity(intent);
                getLongUrl(""+Paper.book().read(Prevalent.starter));
            }
        });
        findViewById(R.id.priceButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(UpgradeToPremium.this, CheckSum.class);
//                intent.putExtra("orderId", orderId);
//                intent.putExtra("customerId", customerId);
//                intent.putExtra("price", ""+Paper.book().read(Prevalent.spark));
//                startActivity(intent);
                getLongUrl(""+Paper.book().read(Prevalent.spark));
            }
        });
        findViewById(R.id.priceButton3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(UpgradeToPremium.this, CheckSum.class);
//                intent.putExtra("orderId", orderId);
//                intent.putExtra("customerId", customerId);
//                intent.putExtra("price", ""+Paper.book().read(Prevalent.enterprise));
//                startActivity(intent);
                getLongUrl(""+Paper.book().read(Prevalent.enterprise));
            }
        });
    }

    void getLongUrl(String price){
        final ProgressDialog dialog = new ProgressDialog(UpgradeToPremium.this);
        dialog.setMessage("Processing");
        dialog.setCancelable(false);
        dialog.show();
        Paper.init(UpgradeToPremium.this);
        String email = Paper.book().read(Prevalent.email);
        Log.d("TAG", "user email " + email);
        String username = Paper.book().read(Prevalent.name);
        String phoneNumber = Paper.book().read(Prevalent.phone_number);
        String purpose = "Payment for upgrading";

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("buyer_name", username);
        requestParams.put("email", email);
        requestParams.put("phone", phoneNumber);
        requestParams.put("date", System.currentTimeMillis()+"");
        requestParams.put("send_email", false);
        requestParams.put("send_sms", false);
        requestParams.put("amount", price+"");
        requestParams.put("webhook", "http://funtube.org/sheriff/SheriffOladejo/Instamojo/webhook.php");
        requestParams.put("redirect_url", "http://funtube.org/sheriff/SheriffOladejo/Instamojo/redirect_url.php");
        requestParams.put("purpose", purpose);
        requestParams.put("description", "Planet app payment");
        asyncHttpClient.addHeader("X-Api-Key", "5714771299205dd0ad115577bae8ae6d");
        asyncHttpClient.addHeader("X-Auth-Token", "b7493c0687cd296dba71a10c0d87d899");
        asyncHttpClient.post("https://www.instamojo.com/api/1.1/payment-requests/", requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TAG", "order response failure " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Log.d("TAG", "order response succss " + responseString);
                    String longUrl = "";
                    String payment_request_id = "";
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject payment_request = jsonObject.getJSONObject("payment_request");
                    longUrl= payment_request.getString("longurl");
                    payment_request_id = payment_request.getString("id");
                    dialog.dismiss();
                    startActivity(new Intent(UpgradeToPremium.this, WebViewActivity.class)
                            .putExtra("longurl", longUrl)
                            .putExtra("payment_request_id", payment_request_id)
                            .putExtra("price", price));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.close, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.close){
            if(DashboardActivity.showReminder){

            }
            finish();
            return true;
        }
        else{
            return false;
        }
    }
}
