package com.m520it.homework.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.m520it.homework.bean.HomeBean;
import com.m520it.homework.bean.ResultApi;

/**
 * Created by Kiven on 2016-11-24.
 */

public class XMLUtil {
	private XMLUtil() {
	}

	public static ResultApi xml2Bean(InputStream is) {
		ResultApi res = new ResultApi();
		if (is == null) {
			L.e("XMLUtil.xml2Bean InputStream  is null");
			return null;
		}
		try {
			XmlPullParser pull = Xml.newPullParser();
			pull.setInput(is, "utf-8");
			HomeBean bean = null;
			List<HomeBean> data = null;
			int eventType = pull.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("state".equals(pull.getName())) {
						res.setState(Integer.parseInt(pull.nextText()));
					} else if ("msg".equals(pull.getName())) {
						res.setMsg(pull.nextText());
					} else if ("date".equals(pull.getName())) {
						data = new ArrayList<HomeBean>();
						res.setDate(data);
					} else if ("HomeBean".equals(pull.getName())) {
						bean = new HomeBean();
					} else if ("imageUrl".equals(pull.getName())) {
						bean.setImageUrl(pull.nextText());
					} else if ("iconHeart".equals(pull.getName())) {
						bean.setIconHeart(pull.nextText());
					} else if ("description".equals(pull.getName())) {
						bean.setDescription(pull.nextText());
					} else if ("counts".equals(pull.getName())) {
						bean.setCounts(Integer.parseInt(pull.nextText()));
					} else if ("time".equals(pull.getName())) {
						bean.setTime(pull.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("HomeBean".equals(pull.getName())) {
						data.add(bean);
					} else if ("ResultApi".equals(pull.getName())) {
						return res;
					}
					break;

				}

				eventType = pull.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}
}
