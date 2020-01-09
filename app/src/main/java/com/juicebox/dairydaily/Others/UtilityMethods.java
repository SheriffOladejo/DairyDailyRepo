package com.juicebox.dairydaily.Others;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class UtilityMethods {

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
}
