package com.m520it.mymobilsafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.m520it.mymobilsafe.R;
import com.m520it.mymobilsafe.beans.TaskInfoBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kiven
 * @time 2016-12-21  19:08
 * Email f842728368@163.com
 * @desc 进程的工具类
 */

public class TaskInfoUtil {

    private TaskInfoUtil() {
    }

    /**
     * 获得所有的运行的进程的数据，不能直接从RunningAppProcessInfo中拿到，
     * 要先根据processInfo获得对应应用的对象，再获得其他信息
     */
    public static List<TaskInfoBean> getTaskInfos(Context context) {
        List<TaskInfoBean> beans = new ArrayList<>();

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();

        TaskInfoBean bean = null;
        // 这里要获取每一个进程图标，名，包名，大小，是否是系统进程

        for (ActivityManager.RunningAppProcessInfo task : tasks) {
            bean = new TaskInfoBean();
            // 这里进程名也就是包名，获得packageInfo对象，再获得相应的信息
            String packageName = task.processName;
            bean.setPackageName(packageName);
            // 这里根据进程的pid来确定所占内存大小
            // 注意这里单位是kb，外面formatter要用的数据单位是byte，所以这里要乘以1024
            int taskSize = am.getProcessMemoryInfo(new int[]{task.pid})[0].getTotalPrivateDirty();
            bean.setMemSize(taskSize * 1024);
            try {
                // 这里可能会有异常，有了异常，就没有图标，没有名称，我们可以在下面的异常捕获时设置默认的图标和名称
                PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
                // 获取标签名，即我们常见的应用名
                bean.setIcon(packageInfo.applicationInfo.loadIcon(pm));
                bean.setName(packageInfo.applicationInfo.loadLabel(pm).toString());

                // 下面判断是用户进程还是系统进程
                int flags = packageInfo.applicationInfo.flags;
                if ((flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                    bean.setUserTask(false);
                } else {
                    bean.setUserTask(true);
                }

            } catch (PackageManager.NameNotFoundException e) {
                // 这里设置默认的图标和名称
                bean.setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
                bean.setName("未知进程");
                e.printStackTrace();
            }
            // 循环结束，加入数据
            beans.add(bean);
        }

        return beans;
    }


    /**
     * 获取当前的运行的进程总数
     */
    public static int getTasksTotalConut(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningAppProcesses().size();
    }

    /**
     * 获得可用的内存信息
     */
    public static long getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    /**
     * 获得总的内存信息
     */
    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static long getTotalMemory() {
        // 判断版本号
        if (Integer.parseInt(Build.VERSION.RELEASE) >= 16) {
            ActivityManager.MemoryInfo am = new ActivityManager.MemoryInfo();
            return am.totalMem;
        }
        // 如何在低版本上获取内存信息
        // 去 /proc/meminfo 中查找数据，以字符为单位查找
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream in = new FileInputStream(new File("/proc/meminfo"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String firstLine = reader.readLine();
            for (char c : firstLine.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 这里要注意数据也是一kb为单位的，要先转成byte
        return Long.parseLong(sb.toString()) * 1024;
    }
}
