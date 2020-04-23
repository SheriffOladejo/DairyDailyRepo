package com.dixit.dairydaily.UI.Dashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.dixit.dairydaily.Others.DbHelper;
import com.dixit.dairydaily.Others.Prevalent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
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
//        setContentView(R.layout.activity_main);
        //initOrderId();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Paper.init(this);
        helper = new DbHelper(this);

        Intent intent = getIntent();
        orderId = intent.getExtras().getString("orderId");
        custormerid = intent.getExtras().getString("customerId");
        price = intent.getExtras().getString("price");

        mid = "avFYoj33073251095335"; // your merchant ID

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
        client.post(url, requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                toast(CheckSum.this, "Failure");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject1 = new JSONObject(responseString);
                    if(jsonObject1.has("CHECKSUMHASH")){
                        CHECKSUMHASH = jsonObject1.getString("CHECKSUMHASH");
                        PaytmPGService Service = PaytmPGService.getProductionService();
                        // when app is ready to publish use production service
                        // PaytmPGService  Service = PaytmPGService.getProductionService();

                        // now call paytm service here
                        //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
                        HashMap<String, String> paramMap = new HashMap<String, String>();
                        //these are mandatory parameters
                        paramMap.put("MID", mid);
                        paramMap.put("ORDER_ID", orderId);
                        paramMap.put("CUST_ID", custormerid);
                        paramMap.put("CHANNEL_ID", "WAP");
                        paramMap.put("TXN_AMOUNT", price);
                        paramMap.put("WEBSITE", "DEFAULT");

                        paramMap.put("CALLBACK_URL" ,verifyurl);
                        //paramMap.put( "EMAIL" , Paper.book().read(Prevalent.email));
                        //paramMap.put( "MOBILE_NO" , Paper.book().read(Prevalent.phone_number));
                        paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
                        //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
                        paramMap.put("INDUSTRY_TYPE_ID", "Retail");

                        PaytmOrder Order = new PaytmOrder(paramMap);
                        Log.e("checksum ", "param "+ paramMap.toString());
                        Service.initialize(Order,null);
                        // start payment service call here
                        Service.startPaymentTransaction(CheckSum.this, true, true,
                                CheckSum.this  );
                        //toast(CheckSum.this, CHECKSUMHASH);
                    }
                    else{
                        toast(CheckSum.this, "Checksum not available");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        //sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
        //dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
// vollye , retrofit, asynch

    }

    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(CheckSum.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }

        protected String doInBackground(ArrayList<String>... alldata) {

//            JSONParser jsonParser = new JSONParser(CheckSum.this);
//            String param=
//                    "MID="+mid+
//                            "&ORDER_ID=" + orderId+
//                            "&CUST_ID="+custormerid+
//                            "&CHANNEL_ID=WAP"+
//                            "&CALLBACK_URL="+verifyurl+
//                            "&TXN_AMOUNT="+price+
//                            "&WEBSITE=DEFAULT"+
//                            "&INDUSTRY_TYPE_ID=Retail";
//
//            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
//            //Log.d("CheckSum result >>:", "CheckSum Result"+jsonObject.toString());
//            if(jsonObject != null){
//                try {
//                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
//                    Log.e("CheckSum result >>","Checksum hash: "+CHECKSUMHASH);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }

//            RequestParams requestParams = new RequestParams();
//            requestParams.put("MID", mid);
//            requestParams.put("ORDER_ID", orderId);
//            requestParams.put("CUST_ID", custormerid);
//            requestParams.put("CHANNEL_ID", "WAP");
//            requestParams.put("CALLBACK_URL", verifyurl);
//            requestParams.put("TXN_AMOUNT", price);
//            requestParams.put("WEBSITE", "DEFAULT");
//            requestParams.put("INDUSTRY_TYPE_ID", "Retail");
//            AsyncHttpClient client = new AsyncHttpClient();
//            client.post(url, requestParams, new TextHttpResponseHandler() {
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                    toast(CheckSum.this, "Failure");
//                }
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                    try {
//                        JSONObject jsonObject1 = new JSONObject(responseString);
//                        if(jsonObject1.has("CHECKSUMHASH")){
//                            CHECKSUMHASH = jsonObject1.getString("CHECKSUMHASH");
//                            toast(CheckSum.this, CHECKSUMHASH);
//                        }
//                        else{
//                            toast(CheckSum.this, "Checksum not available");
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

            return CHECKSUMHASH;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            PaytmPGService Service = PaytmPGService.getProductionService();
            // when app is ready to publish use production service
            // PaytmPGService  Service = PaytmPGService.getProductionService();

            // now call paytm service here
            //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
            HashMap<String, String> paramMap = new HashMap<String, String>();
            //these are mandatory parameters
            paramMap.put("MID", mid);
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", custormerid);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", price);
            paramMap.put("WEBSITE", "DEFAULT");

            paramMap.put("CALLBACK_URL" ,verifyurl);
            //paramMap.put( "EMAIL" , Paper.book().read(Prevalent.email));
            //paramMap.put( "MOBILE_NO" , Paper.book().read(Prevalent.phone_number));
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");

            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,null);
            // start payment service call here
            Service.startPaymentTransaction(CheckSum.this, true, true,
                    CheckSum.this  );
        }

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
                if(price.equals(""+Prevalent.starter))
                    c.add(Calendar.DATE, 100);
                else if(price.equals(""+Prevalent.spark))
                    c.add(Calendar.DATE, 200);
                else if(price.equals(""+Prevalent.enterprise))
                    c.add(Calendar.DATE, 370);
                helper.setExpiryDate(String.valueOf(c.getTime().getTime()));
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

                ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(CheckSum.this, "Payment Successful.", Toast.LENGTH_SHORT).show();
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