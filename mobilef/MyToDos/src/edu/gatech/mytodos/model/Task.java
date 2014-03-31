package edu.gatech.mytodos.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

/**
 * Date modeling class representing a todo
 * 
 */
public class Task {
	private static final String TAG = Task.class.getName();

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"MM/dd/yyyy", Locale.ENGLISH);

	public static final int TASK_PRIORITY_HIGH = 1;
	public static final int TASK_PRIORITY_MEDIUM = 2;
	public static final int TASK_PRIORITY_LOW = 3;

	private int mTaskID = -1; // primary key
	private String mSubject = "";
	private Date mDueDate = new Date();
	private int mPriority = TASK_PRIORITY_MEDIUM; // default
	private String mDescription = "";
	private boolean isCompleted = false; // checked off or not

	private int mUserid = -1; // foreign key referencing User

	public int getTaskID() {
		return mTaskID;
	}

	public void setTaskID(int taskID) {
		mTaskID = taskID;
	}

	public String getSubject() {
		return mSubject;
	}

	public void setSubject(String subject) {
		mSubject = subject;
	}

	public void setDueDate(Date dDate) {
		mDueDate = dDate;
	}

	public void setDueDate(String sDate) {
		Date date = null;
		if (sDate == null) {
			date = new Date();
			DATE_FORMAT.format(date);
		} else {
			try {
				date = DATE_FORMAT.parse(sDate);
			} catch (ParseException e) {
				Log.d(TAG,
						"Hit internal error while trying to set due date. Contact support.");
			}
		}
		this.mDueDate = date;
	}

	public Date getDueDate() {
		return mDueDate;
	}

	public int getPriority() {
		return mPriority;
	}

	public void setPriority(int priority) {
		mPriority = priority;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public int getUserid() {
		return mUserid;
	}

	public void setUserid(int userid) {
		mUserid = userid;
	}
}
