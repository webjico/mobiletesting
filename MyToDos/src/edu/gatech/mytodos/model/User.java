package edu.gatech.mytodos.model;

/**
 * Date modeling class representing a user
 * 
 */
public class User {
	/**
	 * Default user ID is -1
	 */
	public static final int DEFAULT_USER_ID = -1;
	/**
	 * Default is to hide complete task
	 */
	public static final boolean DEFAULT_SHOW_COMPLETE_TASK = false;
	
	private int mUserid = -1; // primary key
	private String mFirstName = "";
	private String mLastName = "";
	private String mEmail = "";
	private String mPassword = "";
	private boolean mShowCompleteTask = DEFAULT_SHOW_COMPLETE_TASK; // default - hide complete task
	

	public User() {
	}

	public User(String firstName, String lastName, String email, String password) {
		mFirstName = firstName;
		mLastName = lastName;
		mEmail = email;
		mPassword = password;
		mShowCompleteTask = User.DEFAULT_SHOW_COMPLETE_TASK;
	}
	
	public User(String firstName, String lastName, String email, String password, boolean showCompleteTask) {
		super();
		mFirstName = firstName;
		mLastName = lastName;
		mEmail = email;
		mPassword = password;
		mShowCompleteTask = showCompleteTask;
	}
	
	public int getUserid() {
		return mUserid;
	}

	public void setUserid(int userid) {
		this.mUserid = userid;
	}

	public String getFirstName() {
		return mFirstName;
	}

	public void setFirstName(String firstName) {
		mFirstName = firstName;
	}

	public String getLastName() {
		return mLastName;
	}

	public void setLastName(String lastName) {
		mLastName = lastName;
	}

	public String getEmail() {
		return mEmail;
	}

	public void setEmail(String email) {
		mEmail = email;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String password) {
		mPassword = password;
	}

	public boolean isShowCompleteTask() {
		return mShowCompleteTask;
	}

	public void setShowCompleteTask(boolean showCompleteTask) {
		this.mShowCompleteTask = showCompleteTask;
	}
}
