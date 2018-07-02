package com.m520it.mymobilsafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Kiven
 * @time 2016-12-20  18:27
 * Email f842728368@163.com
 * @desc 短信备份的工具类
 */
public class SmsBackupUtil {

    private SmsBackupUtil() {
    }

    public interface SmsBackUpCallBack {
        /**
         * 在备份开始之前调用
         *
         * @param max 进度条的最大值，备份的总数目
         */
        void befroeBackup(int max);

        /**
         * 备份中调用
         *
         * @param process 备份的进度，
         */
        void onBackup(int process);
    }


    /**
     * 备份短信到文件目录中去，写完主要流程之后调整，要子线程中运行，并把异常捕获起来
     */
    public static boolean smsBackup(final Context context, SmsBackUpCallBack callback) {

        boolean isSuccess = true;
        try {
            // 1.获得内容解析着，解析数据
            // 2.将解析到的数据存放到XML文件中，这里使用xml序列化器来做
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms");

            // 获得序列化器
            XmlSerializer serializer = Xml.newSerializer();
            // 设置备份的文件，这里可以设置文件夹和文件名
            File file = new File(context.getFilesDir(), "smsBackup.xml");
            // 将文件转换成流，并绑定到序列化器

            // 设置文件头信息， 编码类型，是否是独立的文件
            FileOutputStream fos = new FileOutputStream(file);
            // BufferedOutputStream bos = new BufferedOutputStream(fos, 1024 * 1024);
            serializer.setOutput(fos, "utf-8");
            serializer.startDocument("utf-8", true);

            // 开始写入信息
            // 1,写根节点
            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "body"}, null, null, null);
            serializer.startTag(null, "smsInfo");

            // 这里处理短信的备份的进度条，ProgressDialog可以直接在子线程修改UI，源码中已经有handler处理了
            // pd.setMax(cursor.getCount());
            callback.befroeBackup(cursor.getCount());
            int process = 0;

            while (cursor.moveToNext()) {

                process++;
                //pd.setProgress(process);
                callback.onBackup(process);

                // 游标移动到下一行，就创建一个新的sms节点
                serializer.startTag(null, "sms");

                // 每查询一条新的数据，就添加一个新的tag，并设置content，再结束tag
                serializer.startTag(null, "address");
                String address = cursor.getString(0);
                serializer.text(address);
                serializer.endTag(null, "address");

                serializer.startTag(null, "date");
                String date = cursor.getString(1);
                serializer.text(date);
                serializer.endTag(null, "date");

                serializer.startTag(null, "body");
                String body = cursor.getString(2);
                serializer.text(body);
                serializer.endTag(null, "body");

                serializer.endTag(null, "sms");

                //System.out.println("-------------------------------------------------------------------------");
                //System.out.println("address:" + address + "date:" + date + "body:" + body);

                SystemClock.sleep(50);
                // 改变进度条的进度


            }

            // 不要忘记根节点
            serializer.endTag(null, "smsInfo");
            // 写完之后关闭流。。。这里流没有对象引用
            // bos.close();

        } catch (IOException e) {
            isSuccess = false;
            e.printStackTrace();
        }

        return isSuccess;
    }


}
