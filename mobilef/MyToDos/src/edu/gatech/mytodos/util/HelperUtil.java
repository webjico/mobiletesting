package edu.gatech.mytodos.util;

import java.text.ParseException;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import edu.gatech.mytodos.MainLoginActivity;
import edu.gatech.mytodos.model.Task;

/**
 * Utility class to share commonly used implementation methods
 * 
 */
public class HelperUtil {
	private static final String TAG = HelperUtil.class.getName();

	public static Date convertStrToDate(String sDate) {
		Date date = null;
		if (sDate == null) {
			date = new Date();
			Task.DATE_FORMAT.format(date);
		} else {
			try {
				date = Task.DATE_FORMAT.parse(sDate);
			} catch (ParseException e) {
				Log.d(TAG,
						"Hit internal error while trying to convert due date. Contact support.");
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

	/**
	 * Perform log out action and return to the MainLoginActivity view.
	 * 
	 * @param activity
	 */
	public static void logout(Activity activity) {
		Intent intent = new Intent(activity.getApplicationContext(),
				MainLoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(intent);
		activity.finish();
	}
}
