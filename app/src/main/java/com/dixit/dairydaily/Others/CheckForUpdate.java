package com.dixit.dairydaily.Others;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;

public class CheckForUpdate extends AsyncTask<Void, String, String> {

    Context context;

    public CheckForUpdate(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {

        String newVersion = null;
        try {
            newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName() + "&hl=it")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select(".hAyfc .htlgb")
                    .get(7)
                    .ownText();
            return newVersion;
        } catch (Exception e) {
            return newVersion;
        }
    }

    @Override
    protected void onPostExecute(String onlineVersion) {
        super.onPostExecute(onlineVersion);
        Log.d("update", "Current version " + "2.0 " + "playstore version " + onlineVersion);
        if (onlineVersion != null && !onlineVersion.isEmpty()) {
//            if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
//                //show dialog
//            }
        }
    }
}
