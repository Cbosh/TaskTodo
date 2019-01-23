package com.mrbreak.todo.util;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefUtil {

    private static String TOP_FRAGMENT = "TOP_FRAGMENT";

    public static int getCurrentFragment(Context context) {
        SharedPreferences pref = context.getSharedPreferences(TOP_FRAGMENT, MODE_PRIVATE);
        return pref.getInt("current_fragment", 0);
    }

    public static void saveCurrentFragment( Context context, int fragment) {
        SharedPreferences pref = context.getSharedPreferences(TOP_FRAGMENT, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("current_fragment", fragment);
        editor.apply();
    }

}
