package edu.gatech.mytodos.util;

import java.text.ParseException;
import java.util.Date;

import edu.gatech.mytodos.model.Task;

public class HelperUtil {
	public static Date convertStrToDate(String sDate) {
		Date date = null;
		if (sDate == null) {
			date = new Date();
			Task.DATE_FORMAT.format(date);
		} else {
			try {
				date = Task.DATE_FORMAT.parse(sDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return date;
	}

	public static String convertDateToStr(Date date) {
		if (date == null) {
			date = new Date();
			Task.DATE_FORMAT.format(date);
		}
		String sDate = null;
		sDate = Task.DATE_FORMAT.format(date);
		return sDate;
	}
}
