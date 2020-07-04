package com.dixit.dairydaily.UI.Dashboard.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dixit.dairydaily.UI.Dashboard.DashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.Others.Prevalent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import cz.msebera.android.httpclient.Header;
import io.paperdb.Paper;

import static com.dixit.dairydaily.Others.UtilityMethods.toast;

public class CheckSum extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    //private String orderId , mid, custid, amt;
    String url ="http://coin2cash.club/sheriff/SheriffOladejo/Paytm/generateChecksum.php";
    String verifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
    // "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID"+orderId;
    String CHECKSUMHASH ="";
    String custormerid="", orderId="", mid="", price="";
    DbHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Paper.init(this);
        helper = new DbHelper(this);
        Intent intent = getIntent();
        orderId = intent.getExtras().getString("orderId");
        custormerid = intent.getExtras().getString("customerId");
        price = intent.getExtras().getString("price");

        mid = "aNSZJV20122648162244"; // your merchant ID

        RequestParams requestParams = new RequestParams();
        requestParams.put("MID", mid);
        requestParams.put("ORDER_ID", orderId);
        requestParams.put("CUST_ID", custormerid);
        requestParams.put("CHANNEL_ID", "WAP");
        requestParams.put("CALLBACK_URL", verifyurl);
        requestParams.put("TXN_AMOUNT", price);
        requestParams.put("WEBSITE", "DEFAULT");
        requestParams.put("INDUSTRY_TYPE_ID", "Retail");
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                toast(CheckSum.this, "Failure");
                toast(CheckSum.this, "response string "+responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject1 = new JSONObject(responseString);
                    if(jsonObject1.has("CHECKSUMHASH")){
                        CHECKSUMHASH = jsonObject1.getString("CHECKSUMHASH");
                        Log.d(TAG, "checksumhash " + CHECKSUMHASH);
                        PaytmPGService Service = PaytmPGService.getStagingService("");
                        //PaytmPGService Service = PaytmPGService.getStagingService();
                        // when app is ready to publish use production service
                        // PaytmPGService  Service = PaytmPGService.getProductionService();

                        // now call paytm service here
                        // below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
                        HashMap<String, String> paramMap = new HashMap<String, String>();
                        //these are mandatory parameters
                        paramMap.put("MID", mid);
                        paramMap.put("ORDER_ID", orderId);
                        paramMap.put("CUST_ID", custormerid);
                        paramMap.put("CHANNEL_ID", "WAP");
                        paramMap.put("TXN_AMOUNT", price);
                        paramMap.put("WEBSITE", "WEBSTAGING");
                        paramMap.put("CALLBACK_URL" ,verifyurl);
                        paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
                        paramMap.put("INDUSTRY_TYPE_ID", "Retail");

                        PaytmOrder Order = new PaytmOrder(paramMap);
                        Log.e("checksum ", "param "+ paramMap.toString());
                        Service.initialize(Order,null);
                        // start payment service call here
                        Service.startPaymentTransaction(CheckSum.this, true, true,
                                CheckSum.this  );
                    }
                    else{
                        toast(CheckSum.this, "Checksum not available");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static final String TAG = "CheckSum";
    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", "Paytm response " + bundle.toString());
        String status = bundle.getString("RESPMSG");
        if(status != null){
            if(status.equals("Txn Success")){
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
                Toast.makeText(CheckSum.this, ""+c.getTime(), Toast.LENGTH_LONG).show();
                String date = new SimpleDateFormat("dd/MM/YYYY").format(new Date());
                HashMap<String, Object> map = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Transaction Date", date);
                    jsonObject.put("Transaction Amount", price);
                    jsonObject.put("Expiry Date", ""+c.getTime().getTime());
                    map.put("Payment History", jsonObject.toString());
                } catch (JSONException e) {
                    toast(CheckSum.this, "Unable to create JSONObject");
                }
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number)).child("Payment History").child(""+System.currentTimeMillis());
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("Users").child(Paper.book().read(Prevalent.phone_number)).child("Expiry Date");
                ref1.setValue(""+c.getTime().getTime());
                DashboardActivity.isExpired = false;

                ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(CheckSum.this, "Payment Successful.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CheckSum.this, DashboardActivity.class));
                            finish();
                        }
                    }
                });

            }
            else{
                Toast.makeText(CheckSum.this, "Payment Failed. \nContact the app admin if your account is debited.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void networkNotAvailable() {
        toast(CheckSum.this, "Check your network and try again.");
        startActivity(new Intent(CheckSum.this, UpgradeToPremium.class));
        finish();
    }

    @Override
    public void clientAuthenticationFailed(String s) {

    }

    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  "+ s );
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true "+ s + "  s1 " + s1);
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("checksum ", " cancel call back respon  " );
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("checksum ", "  transaction cancel " );
    }


}