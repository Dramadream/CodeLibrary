package com.m520it.mymobilsafe.utils;

import android.app.Activity;
import android.content.Intent;

/**
 * @author Kiven
 * @time 2016-12-14  9:39
 * Email f842728368@163.com
 * @desc 跳转界面的工具类
 */

public class IntentUtil {

    private IntentUtil() {
    }

    /**
     * 跳转到新的界面
     */
    public static void statiActivity(Activity activity, Class clz) {
        activity.startActivity(new Intent(activity, clz));
    }

    /**
     * 跳转到新的界面并关闭自身界面
     */
    public static void statiActivityAndFinish(Activity activity, Class clz) {
        activity.startActivity(new Intent(activity, clz));
        activity.finish();
    }

}
