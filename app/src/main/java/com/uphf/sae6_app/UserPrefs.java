package com.uphf.sae6_app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper utilitaire pour accéder au nom d'utilisateur dans SharedPreferences.
 */
public class UserPrefs {

    public static final String PREFS_NAME = "prefs_user"; // doit rester identique au reste de l'app
    public static final String KEY_USER_NAME = "user_name";

    public static boolean hasUserName(Context ctx) {
        if (ctx == null) return false;
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String s = prefs.getString(KEY_USER_NAME, null);
        return s != null && !s.trim().isEmpty();
    }

    public static String getUserName(Context ctx) {
        if (ctx == null) return null;
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String s = prefs.getString(KEY_USER_NAME, null);
        return s == null ? null : s.trim();
    }

    public static void setUserName(Context ctx, String name) {
        if (ctx == null || name == null) return;
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USER_NAME, name.trim()).apply();
    }

}

