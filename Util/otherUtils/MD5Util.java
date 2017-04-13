package com.m520it.mymobilsafe.utils;

import android.content.pm.PackageInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Kiven
 * @time 2016-12-17  15:08
 * Email f842728368@163.com
 * @desc MD5加密的工具类
 */

public class MD5Util {
    private MD5Util() {
    }

    /**
     * 加密字符串
     */
    public static String msg2MD5(String src) {

        // 创建转换的对象，数字摘要器
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // 转换完成，获得byte数组，然后要每个字节都转成字符串
        byte[] resByte = digest.digest(src.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : resByte) {
            // 字节--->int--->String
            String hex = Integer.toHexString(b & 0xff);     // 注意加盐
            // 字节转成的字符串可能有1位，也可能有两位，要做不同的处理
            sb.append(hex);
            if (hex.length() == 1) {
                sb.append("0");
            }

        }
        return sb.toString();
    }


    /**
     * 根据应用的apk，生成MD5
     * 得到apk位置---->获得流---->更新MD5---->转成MD5字符串
     */
    public static String app2MD5(PackageInfo packageInfo) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream is = new FileInputStream(new File(packageInfo.applicationInfo.sourceDir));
            int len = -1;
            byte[] buff = new byte[1024];
            while ((len = is.read(buff)) != -1) {
                // 这里是要根据文件中的每个字节来更新MD5的值
                digest.update(buff, 0, len);
            }
            // 这里已经生成了MD5的值，接下来转成字符串，并做加盐处理
            byte[] bytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                // 字节--->int--->String
                String hex = Integer.toHexString(b & 0xff);     // 注意加盐
                // 字节转成的字符串可能有1位，也可能有两位，要做不同的处理
                sb.append(hex);
                if (hex.length() == 1) {
                    sb.append("0");
                }
                return sb.toString();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
