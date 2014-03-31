package edu.gatech.mytodos.model;

import java.util.List;

public class User {
	private int mUserid = -1; // primary key
	private String mFirstName = "";
	private String mLastName = "";
	private String mEmail = "";
	private String mPassword = "";
	
	public User(String firstName, String lastName, String email, String password) {
		super();
		mFirstName = firstName;
		mLastName = lastName;
		mEmail = email;
		mPassword = password;
	}

	public User() {
		// TODO Auto-generated constructor stub
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
}
