package com.m520it.mymobilsafe.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Kiven
 * @time 2016-12-18  21:32
 * Email f842728368@163.com
 * @desc DB的工具类，复制DB，从号码归属地数据库中查询信息
 */

public class DBUtil {

    private DBUtil() {
    }

    /**
     * 把数据库从assets中拷贝到文件目录中，数据库名不变
     *
     * @param DBName 要拷贝的数据库的名字
     */
    public static void copyDB(final Context context, final String DBName) {
        new Thread() {
            public void run() {
                // 先找到源和目标
                File des = new File(context.getFilesDir(), DBName);
                if (des.exists() && des.length() > 0) {
                    // 已经拷贝过了，不需要重新复制
                    return;
                }
                InputStream in;
                FileOutputStream out;
                try {

                    in = context.getAssets().open(DBName);

                    out = new FileOutputStream(des);
                    int len = -1;
                    byte[] buff = new byte[1024];
                    while ((len = in.read(buff)) != -1) {
                        out.write(buff, 0, len);
                    }
                    in.close();
                    out.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }




}
