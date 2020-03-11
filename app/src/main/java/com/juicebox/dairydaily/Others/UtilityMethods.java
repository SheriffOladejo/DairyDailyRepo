package com.juicebox.dairydaily.Others;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.juicebox.dairydaily.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilityMethods {

    private static final String TAG = "UtilityMethods";

    // Utility function for creating and using a snackbar
    // Parameter snackBarMessage: Message to be displayed on the snackbar
    public static void useSnackBar(String snackBarMessage, View linearLayout){
        Snackbar snackbar = Snackbar.make(linearLayout, snackBarMessage, Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        TextView textView = snackView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    // Method for hiding the keyboard
    public static void hideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        // Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a windwo token from it
        if(view == null){
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // Converts the first letter in a string to uppercase
    public static String convertFirstLetter(String name){
        String result = "";
        String indexZero = name.substring(0, 1);
        result = indexZero.toUpperCase();
        for(int x = 1; x < name.length(); x++){
            result += name.charAt(x);
        }
        return result;
    }

    // Gets the first name from a fullname
    public static String getFirstname(String name){
        String firstname = "";
        for(int i =0; i < name.length(); i++){
            firstname += String.valueOf(name.charAt(i));
            if(String.valueOf(name.charAt(i)).equals(" ")){
                break;
            }
        }
        return firstname.trim();
    }

    // Returns the lastname from a fullname
    public static String getLastname(String name){
        String lastname = "";
        for(int i =0; i <= name.length() - 1; i++){
            if(String.valueOf(name.charAt(i)).equals(" ")){
                for(int j = i+1; j<name.length(); j++)
                    lastname += String.valueOf(name.charAt(j));
                break;
            }
        }
        return lastname.trim();
    }

    // Display custom toast message
    public static void toast(Context context, String message){
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast, null);
        TextView textView = view.findViewById(R.id.text);
        textView.setTextColor(context.getResources().getColor(R.color.black));
        textView.setText(message);
        view.setBackgroundColor(context.getResources().getColor(R.color.white));
        view.setBackgroundResource(R.drawable.rectangle_border);
        toast.setView(view);
        toast.show();
    }

    // truncate a double value
    public static double truncateDouble(double value){
        try{
            if(value != 0){
                return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
            else
                return 0;
        }
        catch (Exception e){
            return 0;
        }
    }

    // truncate a double value
    public static String truncate(double value){
       return String.format("%.2f", value);
    }

    // Return 1 for 1-10,return 2 for 11-20, return 3 for 21-31
    public static int getDateRange(){
        Date dateIntermediate = new Date();
        String date = new SimpleDateFormat("MMM dd, yyyy").format(dateIntermediate);
        Log.d("Utils", "utils: " + date);

        if(Integer.valueOf(date.substring(4,6)) <= 11){
            return 1;
        }
        else if(Integer.valueOf(date.substring(4,6)) <= 21 && Integer.valueOf(date.substring(4,6)) > 10)
            return 2;
        else if(Integer.valueOf(date.substring(4,6)) <= 31 && Integer.valueOf(date.substring(4,6)) > 20)
            return 3;
        else return 0;
    }

    public static String getEndDate(){
        Date dateIntermediate = new Date();
        final String dateText = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
        String endDay = "";
        String endDate = "";
        String endMonth = "";
        String todayDay = dateText.substring(8,10);
        String thisMonth = dateText.substring(5,7);
        String thisYear = dateText.substring(0,4);
        String endYear = "";

        if(todayDay.charAt(0) == '0'){
            todayDay = String.valueOf(todayDay.charAt(1));
        }

        if(Integer.valueOf(todayDay) <= 10 && Integer.valueOf(todayDay) >= 1){
            endDay = getLastDayOfPreviousMonth(thisMonth);
            if(thisMonth.equals("01")){
                endYear = String.valueOf((Integer.valueOf(thisYear) - 1));
                endMonth = String.valueOf(12);
            }
            else{
                if(thisMonth.charAt(0) == '0'){
                    endMonth ="0" + (Integer.valueOf(thisMonth) - 1);
                    endYear = thisYear;
                }
                else{
                    endMonth = String.valueOf((Integer.valueOf(thisMonth) - 1));
                    endYear = thisYear;
                }
            }
            endDate = endYear + "-" + endMonth + "-" + endDay;
            Log.d(TAG, "EndDate: "+ endDate);
            return endDate;
        }
        else if(Integer.valueOf(todayDay) <= 20 && Integer.valueOf(todayDay) >= 11){
            endDay = "10";
            endYear = thisYear;
            endMonth = thisMonth;
            endDate = endYear + "-" + endMonth + "-" + endDay;
            Log.d(TAG, "EndDate: "+ endDate);
            return endDate;
        }
        else if(Integer.valueOf(todayDay) <= 31 && Integer.valueOf(todayDay) >= 21){
            endDay = "20";
            endYear = thisYear;
            endMonth = thisMonth;
            endDate = endYear + "-" + endMonth + "-" + endDay;
            Log.d(TAG, "EndDate: "+ endDate);
            return endDate;
        }
        else
            return "";

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void checkBTPermissions(Activity context){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = context.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += context.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if(permissionCheck != 0){
                context.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
            else{
                Log.d(TAG, "checkBTpermissions: No need to check permissions. SDK version < LOLLIPOP");
            }
        }
    }

    public static String getStartDate(){
        Date dateIntermediate = new Date();
        String dateText1 = new SimpleDateFormat("dd/MM/YYYY").format(dateIntermediate);
        String dateText = new SimpleDateFormat("YYYY-MM-dd").format(dateIntermediate);
        Log.d("Utility", "getStartDate: dateText " + dateText);
        String startDate;
        String startDay;
        String todayDay = dateText.substring(8,10);
        String thisMonth = dateText.substring(5,7);
        String startMonth = "";
        String thisYear = dateText.substring(0,4);
        String startYear;


        if(todayDay.charAt(0) == '0'){
            todayDay = String.valueOf(todayDay.charAt(1));
            Log.d(TAG, "todayDay: " +todayDay);
        }


        if(Integer.valueOf(todayDay) <= 10 && Integer.valueOf(todayDay) >= 1){
            startDay = "21";
            Log.d(TAG, "todayDay: " +todayDay);

            if(thisMonth.charAt(0) == 0){
                startMonth ="0" + (Integer.valueOf(thisMonth) - 1);
            }
            else{
                startMonth = String.valueOf((Integer.valueOf(thisMonth) - 1));
                if(startMonth.length()==1){
                    startMonth = "0"+startMonth;
                }
            }

            if(thisMonth.equals("01")){
                startYear = String.valueOf((Integer.valueOf(thisYear) - 1));
                startMonth = "12";
            }
            else{
                startYear = thisYear;
            }
            startDate = startYear + "-" + startMonth + "-" + startDay;
            Log.d("Utility", "getStartDate: " + startDay);
            return startDate;
        }
        else if(Integer.valueOf(todayDay) <= 20 && Integer.valueOf(todayDay) >= 11){
            startDay = "01";
            startYear = thisYear;
            startMonth = thisMonth;
            startDate = startYear + "-" + startMonth + "-" + startDay;
            Log.d("Utility", "getStartDate: this " + startDay);
            return startDate;
        }
        else if(Integer.valueOf(todayDay) <= 31 && Integer.valueOf(todayDay) >= 21){
            startDay = "11";
            startYear = thisYear;
            startMonth = thisMonth;
            startDate = startYear + "-" + startMonth + "-" + startDay;
            Log.d("Utility", "getStartDate: " + startDay);
            return startDate;
        }
        else{
            Log.d("Utility", "getStartDate: NULL");
            return "";
        }
    }

    static String getLastDayOfPreviousMonth(String month){
        switch (month){
            case "01":
                return "31";
            case "02":
                return "31";
            case "03":
                return "29";
            case "04":
                return "31";
            case "05":
                return "30";
            case "06":
                return "31";
            case "07":
                return "30";
            case "08":
                return "31";
            case "09":
                return "31";
            case "10":
                return "30";
            case "11":
                return "31";
            case "12":
                return "31";
                default:
                    return "";
        }
    }

    public static String getMonth(int x){
        switch (x){
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "Jun";
            case 6:
                return "Jul";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";
            default:
                return "";
        }
    }
}
