package com.map.gpslibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Preference工具类，方便Preference的读写和存储
 * 使用app包名作为存储的preference name
 */
public class PrefHelper {
    /**
     * For Boolean
     */
    public static boolean getBoolean(Context c, String preName, boolean defValue) {
        final SharedPreferences prefs = c.getSharedPreferences(getSharePrefName(c), Context.MODE_PRIVATE);
        if (prefs.contains(preName)) {
            return prefs.getBoolean(preName, defValue);
        }
        return defValue;
    }

    public static void setBoolean(Context context, String str, boolean value) {
        SharedPreferences.Editor ed = getEditor(context);
        ed.putBoolean(str, value);
        ed.commit();
    }

    /**
     * For String
     */
    public static void setString(Context c, String preName, String value) {
        SharedPreferences.Editor ed = getEditor(c);
        ed.putString(preName, value);
        ed.commit();
    }

    public static String getString(Context c, String preName, String defString) {
        final SharedPreferences prefs = c.getSharedPreferences(getSharePrefName(c), Context.MODE_PRIVATE);
        if (prefs.contains(preName)) {
            return prefs.getString(preName, defString);
        }
        return defString;
    }

    /**
     * For Integer
     */
    public static int getInt(Context c, String preName, int defValue) {
        final SharedPreferences prefs = c.getSharedPreferences(getSharePrefName(c), Context.MODE_PRIVATE);
        if (prefs.contains(preName)) {
            return prefs.getInt(preName, defValue);
        }
        return defValue;
    }

    public static void setInt(Context context, String preName, int value) {
        SharedPreferences.Editor ed = getEditor(context);
        ed.putInt(preName, value);
        ed.commit();
    }

    /**
     * For Long
     */
    public static void setLong(Context c, String preName, long value) {
        SharedPreferences.Editor ed = getEditor(c);
        ed.putLong(preName, value);
        ed.commit();
    }

    public static long getLong(Context c, String preName, long defValue) {
        final SharedPreferences prefs = c.getSharedPreferences(getSharePrefName(c), Context.MODE_PRIVATE);
        if (prefs.contains(preName)) {
            return prefs.getLong(preName, defValue);
        }
        return defValue;
    }

    /**
     * For Float
     */
    public static void setFloat(Context c, String preName, float value) {
        SharedPreferences.Editor ed = getEditor(c);
        ed.putFloat(preName, value);
        ed.commit();
    }

    public static float getFloat(Context c, String preName, float defValue) {
        final SharedPreferences prefs = c.getSharedPreferences(getSharePrefName(c), Context.MODE_PRIVATE);
        if (prefs.contains(preName)) {
            return prefs.getFloat(preName, defValue);
        }
        return defValue;
    }



    private static SharedPreferences.Editor getEditor(Context c) {
        SharedPreferences prefs = c.getSharedPreferences(getSharePrefName(c), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        return editor;
    }

    private static String getSharePrefName(Context c) {
        return c.getPackageName();
    }

}
