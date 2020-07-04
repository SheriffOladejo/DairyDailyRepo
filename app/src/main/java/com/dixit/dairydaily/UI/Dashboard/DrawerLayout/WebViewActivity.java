package com.dixit.dairydaily.UI.Dashboard.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.Others.Prevalent;
import com.dixit.dairydaily.R;
import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import io.paperdb.Paper;

import static com.dixit.dairydaily.Others.UtilityMethods.toast;

public class WebViewActivity extends AppCompatActivity {

    String price;
    WebView webView;
    DbHelper helper;

    public static String payment_request_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView = findViewById(R.id.webview);
        payment_request_id = getIntent().getStringExtra("payment_request_id");

        helper = new DbHelper(this);
        webView.getSettings().setJavaScriptEnabled(true);
        price =getIntent().getStringExtra("price");
        webView.loadUrl(getIntent().getStringExtra("longurl"), null);
    }

    void getPaymentDetails(){
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("X-Api-Key", "5714771299205dd0ad115577bae8ae6d");
        asyncHttpClient.addHeader("X-Auth-Token", "b7493c0687cd296dba71a10c0d87d899");
        if(payment_request_id.equals("")){
            Toast.makeText(WebViewActivity.this, "Payment request id is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = "https://www.instamojo.com/api/1.1/payment-requests/" + payment_request_id + "/";
        asyncHttpClient.get(url, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TAG", "order response failure " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject payment_request = jsonObject.getJSONObject("payment_request");
                    JSONArray payments = payment_request.getJSONArray("payments");
                    String status = payments.getJSONObject(0).getString("status");
                    String amount = payments.getJSONObject(0).getString("amount");
                    if(status.equals("Credit")){
                        Calendar c = Calendar.getInstance();
                        c.setTime(new Date());
                        if(price.equals(""+Paper.book().read(Prevalent.starter)))
                            c.add(Calendar.DATE, 90);
                        else if(price.equals(""+Paper.book().read(Prevalent.spark)))
                            c.add(Calendar.DATE, 180);
                        else if(price.equals(""+Paper.book().read(Prevalent.enterprise)))
                            c.add(Calendar.DATE, 365);
                        helper.setExpiryDate(String.valueOf(c.getTime().getTime()));
                        Paper.book().write(Prevalent.expiry_date, ""+c.getTime().getTime());
                        Toast.makeText(WebViewActivity.this, ""+c.getTime(), Toast.LENGTH_LONG).show();
                        String date = new SimpleDateFormat("dd/MM/YYYY").format(new Date());
                        HashMap<String, Object> map = new HashMap<>();
                        JSONObject jsonObject1= new JSONObject();
                        try {
                            jsonObject1.put("Transaction Date", date);
                            jsonObject1.put("Transaction Amount", price);
                            jsonObject1.put("Expiry Date", ""+c.getTime().getTime());
                            map.put("Payment History", jsonObject1.toString());
                        } catch (JSONException e) {
                            toast(WebViewActivity.this, "Unable to create JSONObject");
                        }
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number)).child("Payment History").child(""+System.currentTimeMillis());
                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number)).child("Expiry Date");
                        ref1.setValue(""+c.getTime().getTime());
                        DashboardActivity.isExpired = false;

                        ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(WebViewActivity.this, "Payment Successful.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(WebViewActivity.this, DashboardActivity.class));
                                    finish();
                                }
                            }
                        });
                    }
                    else{
                        toast(WebViewActivity.this, "Payment failed");
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getPaymentDetails();
    }
}
