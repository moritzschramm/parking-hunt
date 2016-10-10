package com.moritzschramm.parkinghunt;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Moritz on 27.08.2016.
 */
public class Cookie {

    private static final String FILE_KEY = "cookie_storage";

    public static String get(String key, Context context) {

        SharedPreferences sharedPref = context.getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }
    public static void set(String key, String value, Context context) {

        SharedPreferences sharedPref = context.getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static boolean isSet(String key, Context context) {

        SharedPreferences sharedPref = context.getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
        return !sharedPref.getString(key, "").equals("");
    }
}
