package edu.gatech.mytodos.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Task {
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"MM/dd/yyyy", Locale.ENGLISH);
	
	private int mTaskID = -1; // primary key
	private String mSubject = "";
	private Date mDueDate = new Date();
	private String mPriority = ""; // high, medium, low
	private String mDescription = "";
	private boolean isCompleted = false; // checked off or not
	private boolean isHidden = false; 

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

	public void setDueDate( Date dDate )
	{
		mDueDate = dDate;
	}
	
	public void setDueDate( String sDate )
	{
		//@#@# TODO figure out how to get a date from a string here
		Date date = null;
		if (sDate == null) {
			date = new Date();
			DATE_FORMAT.format(date);
		} else {
			try {
				date = DATE_FORMAT.parse(sDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.mDueDate = date;
	}
	
	public Date getDueDate()
	{
		return mDueDate;
	}
	
	public String getPriority() {
		return mPriority;
	}

	public void setPriority(String priority) {
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

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public int getUserid() {
		return mUserid;
	}

	public void setUserid(int userid) {
		mUserid = userid;
	}
}
