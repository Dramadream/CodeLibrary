package com.m520it.mymobilsafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.m520it.mymobilsafe.constant.Constants;

/**
 * @author Kiven
 * @time 2016-12-11  20:21
 * Email f842728368@163.com
 * @desc SP的工具类，负责读取和写入数据
 */

public class SPUtils {

    private SPUtils() {
    }

    /**
     * 保存boolean
     */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SPFILENAME, Context.MODE_PRIVATE);
        // 获得sp文件后，根据事务的特点保存数据
        sp.edit().putBoolean(key, value).commit();
    }


    /**
     * 得到boolean，默认是false
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SPFILENAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    /**
     * 得到boolean，默认值由调用者来确定
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SPFILENAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }


    /**
     * 保存String
     */
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SPFILENAME, Context.MODE_PRIVATE);
        // 获得sp文件后，根据事务的特点保存数据
        sp.edit().putString(key, value).commit();
    }

    /**
     * gutString，默认值是空字符串
     */
    public static String getString(Context context, String key) {
        return getString(context, key, "");
    }

    /**
     * gutString，默认值由调用者决定
     */
    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SPFILENAME, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }


    /**
     * 保存int
     */
    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SPFILENAME, Context.MODE_PRIVATE);
        // 获得sp文件后，根据事务的特点保存数据
        sp.edit().putInt(key, value).commit();
    }

    /**
     * gutInt，默认值是0
     */
    public static int getInt(Context context, String key) {

        return getInt(context, key, 0);
    }

    /**
     * gutInt，默认值由调用者决定
     */
    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SPFILENAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }



}
