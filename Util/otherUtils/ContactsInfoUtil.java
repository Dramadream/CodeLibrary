package com.m520it.mymobilsafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kiven
 * @time 2016-12-14  19:43
 * Email f842728368@163.com
 * @desc 联系人数据的工具类
 */

public class ContactsInfoUtil {

    // 这里只是最简单的两个信息，姓名和电话号码
    public static class ContactInfo {
        public String name;
        public String number;

        @Override
        public String toString() {
            return "ContactInfo{" +
                    "name='" + name + '\'' +
                    ", number='" + number + '\'' +
                    '}';
        }
    }

    // 通过内容提供者来查询信息
    public static List<ContactInfo> getInfo(Context context) {

        ContentResolver resolver = context.getContentResolver();
        Uri rawContactsUri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        List<ContactInfo> contactsInfo = new ArrayList<>();
        //先查询id，然后通过id来查询信息
        Cursor cursor = resolver.query(rawContactsUri, new String[]{"contact_id"}, null, null, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            // System.out.println(id);

            // 再根据id和mimeType查找联系人数据,这里每次查找出一个新的联系人id就会查找他的其他信息
            ContactInfo contact = new ContactInfo();

            Cursor dataCursor = resolver.query(
                    dataUri,
                    new String[]{"data1", "mimetype"},
                    "raw_contact_id = ?",
                    new String[]{id}, null);


            while (dataCursor.moveToNext()) {
                // 获得数据---> data1和mimeType
                String data = dataCursor.getString(0);
                String mimeType = dataCursor.getString(1);
                if ("vnd.android.cursor.item/name".equals(mimeType)) {
                    contact.name = data;
                } else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) {
                    contact.number = data;
                }
            }
            contactsInfo.add(contact);
        }
        //System.out.println(contactsInfo);
        return contactsInfo;
    }
}
