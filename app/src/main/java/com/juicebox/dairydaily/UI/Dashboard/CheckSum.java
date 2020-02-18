package com.juicebox.dairydaily.UI.Dashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.JsonParser;
import com.juicebox.dairydaily.Others.DbHelper;
import com.juicebox.dairydaily.Others.Prevalent;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.paperdb.Paper;

public class CheckSum extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    String custormerid="", orderId="", mid="", price="";
    DbHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        //initOrderId();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        helper = new DbHelper(this);

        Intent intent = getIntent();
        orderId = intent.getExtras().getString("orderId");
        custormerid = intent.getExtras().getString("customerId");
        price = intent.getExtras().getString("price");

        Paper.init(this);

        mid = "aNSZJV20122648162244"; // your merchant ID
        sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
        dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
// vollye , retrofit, asynch

    }

    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(CheckSum.this);

        //private String orderId , mid, custid, amt;
        String url ="http://coin2cash.club/sheriff/SheriffOladejo/Paytm/generateChecksum.php";
        String verifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
        // "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID"+orderId;
        String CHECKSUMHASH ="";

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }

        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(CheckSum.this);
            String param=
                    "MID="+mid+
                            "&ORDER_ID=" + orderId+
                            "&CUST_ID="+custormerid+
                            "&CHANNEL_ID=WAP"+
                            "&CALLBACK_URL="+verifyurl+
                            "&TXN_AMOUNT="+price+
                            "&WEBSITE=WEBSTAGING"+
                            "&INDUSTRY_TYPE_ID=Retail";

            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
            Log.d("CheckSum result >>:", "CheckSum Result"+jsonObject.toString());
            if(jsonObject != null){
                try {

                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.e("CheckSum result >>","Checksum hash: "+CHECKSUMHASH);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            PaytmPGService Service = PaytmPGService.getStagingService();
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
            paramMap.put("WEBSITE", "WEBSTAGING");
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
            String expiryDate="";
            if(status.equals("Txn Success")){
                Cursor data = helper.getExpiryDate();
                if(data.getCount() != 0){
                    while(data.moveToNext()){
                        expiryDate = data.getString(data.getColumnIndex("Date"));
                        Log.d(TAG, "Expiry date from cursor: " + expiryDate);
                    }
                }
                Calendar c = Calendar.getInstance();
                c.setTime(new Date(Long.valueOf(expiryDate)));
                if(price.equals("149.99"))
                    c.add(Calendar.DATE, 100);
                else if(price.equals("249.99"))
                    c.add(Calendar.DATE, 200);
                else if(price.equals("449.99"))
                    c.add(Calendar.DATE, 370);
                helper.setExpiryDate(String.valueOf(c.getTime().getTime()));
                Toast.makeText(CheckSum.this, "Payment Successful.", Toast.LENGTH_SHORT).show();
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