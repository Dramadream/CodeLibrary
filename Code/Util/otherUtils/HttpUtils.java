package com.m520it.google_play.util;

import java.util.Map;

/**
 * @author Kiven
 * @time 2017-1-10  19:34
 * Email f842728368@163.com
 * @desc Http参数的拼接类，负责拼接网络访问接口参数
 */
public class HttpUtils {
    /**
     * 传递get参数对应的map集合,返回拼接之后的字符串信息
     *
     * @param map
     * @return
     */
    public static String getUrlParamsByMap(Map<String, Object> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
