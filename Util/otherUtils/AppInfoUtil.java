package com.m520it.mymobilsafe.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.m520it.mymobilsafe.beans.AppInfoBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kiven
 * @time 2016-12-20  20:25
 * Email f842728368@163.com
 * @desc App信息的工具类，用来获取系统中的应用信息
 */

public class AppInfoUtil {

    private AppInfoUtil() {
    }

    /**
     * 获取所有的app信息，所有的信息都放在 PackageInfo 这个对象中
     * 系统的应用在system/app下
     * 用户的应用在data/app下
     *
     * @return
     */
    public static List<AppInfoBean> getAllAppInfos(Context context) {


        List<AppInfoBean> beans = new ArrayList<>();


        // 获取所有app的信息，要使用package管理器
        PackageManager pm = context.getPackageManager();

        // 注意这里的flags都是什么意思
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);

        AppInfoBean bean = null;
        for (PackageInfo info : installedPackages) {
            bean = new AppInfoBean();

            bean.setPackageName(info.packageName);
            bean.setAppname(info.applicationInfo.loadLabel(pm).toString());
            bean.setIcon(info.applicationInfo.loadIcon(pm));
            bean.setSize(new File(info.applicationInfo.sourceDir).length());// 获得应用的根路径，再计算文件大小

            // 接下来就是获得是否在内部存储中安装和是否是系统应用
            int flags = info.applicationInfo.flags;

            // 程序的flag和系统对应的flag相与，结果是0，就表明不是这种类型
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
                bean.setInRom(true);// 在内部存储中
            } else {
                bean.setInRom(false);// 在SD卡中
            }
            if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                bean.setSystem(false);// 用户程序
            } else {
                bean.setSystem(true);//系统程序
            }
            beans.add(bean);
        }
        return beans;
    }
}
