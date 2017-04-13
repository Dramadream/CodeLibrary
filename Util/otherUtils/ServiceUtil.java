package com.m520it.mymobilsafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * @author Kiven
 * @time 2016-12-18  10:21
 * Email f842728368@163.com
 * @desc 服务相关的工具类
 */

public class ServiceUtil {
    private ServiceUtil() {
    }

    /**
     * 检测某个服务是否运行，这里不能用clz比较，要用className比较，会出问题
     *
     * @param context 上下文
     * @param clz     想要检测的服务的class对象
     */
    public static boolean isServiceRunning(Context context, Class clz) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(50);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            // 我们想要得到的是服务的状态，这里就是XXX.service.XXX
            if (clz.getName().equals(runningServiceInfo.service.getClassName())) {
                //System.out.println(runningServiceInfo.service.getClassName() + "正在运行");
                return true;
            }
        }
        return false;
    }
}
