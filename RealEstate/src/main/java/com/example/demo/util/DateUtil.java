package com.example.demo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	//현재 날짜 조회
	public static String getCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date timeInDate = new Date();
		String timeInFormat = sdf.format(timeInDate);
		return timeInFormat;
	}
}
