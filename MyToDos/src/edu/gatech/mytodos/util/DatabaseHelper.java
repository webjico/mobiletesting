package edu.gatech.mytodos.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import edu.gatech.mytodos.model.Task;
import edu.gatech.mytodos.model.User;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "mytodos.sqlite";
	private static final int VERSION = 1;
	// user table
	public static final String TABLE_USER = "user";
	public static final String COLUMN_USER_ID = "_userid";
	public static final String COLUMN_USER_FIRST_NAME = "first_name";
	public static final String COLUMN_USER_LAST_NAME = "last_name";
	public static final String COLUMN_USER_EMAIL = "email";
	public static final String COLUMN_USER_PASSWORD = "password";
	// task table
	public static final String TABLE_TASK = "task";
	public static final String COLUMN_TASK_ID = "_taskID";
	public static final String COLUMN_TASK_USERIDFK = "userIDFK";
	public static final String COLUMN_TASK_SUBJECT = "subject";
	public static final String COLUMN_TASK_DUEDATE = "dateDue";
	public static final String COLUMN_TASK_PRIORITY = "priority";
	public static final String COLUMN_TASK_DESCRIPTION = "description";
	public static final String COLUMN_TASK_COMPLETED = "completed";

	private static final String CREATE_USER_TABLE = "CREATE TABLE "
			+ TABLE_USER + " (" + COLUMN_USER_ID
			+ " integer primary key autoincrement, " + COLUMN_USER_FIRST_NAME
			+ " text, " + COLUMN_USER_LAST_NAME + " text, " + COLUMN_USER_EMAIL
			+ " text, " + COLUMN_USER_PASSWORD + " text);";
	
	//@#@# TODO check on SQLite datetime type
	private static final String CREATE_TASK_TABLE = "CREATE TABLE "
			+ TABLE_TASK + " (" + COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_TASK_USERIDFK + " INTEGER, "
			+ COLUMN_TASK_SUBJECT + " TEXT, "
			+ COLUMN_TASK_DUEDATE + " DATE, "
			+ COLUMN_TASK_PRIORITY + " INTEGER, "
			+ COLUMN_TASK_DESCRIPTION + " TEXT, "
			+ COLUMN_TASK_COMPLETED + " TEXT );"; // TRUE or FALSE

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create the "user" table
		db.execSQL(CREATE_USER_TABLE);
		// create "task" table
		db.execSQL( CREATE_TASK_TABLE );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Implement schema changes and data massage here when upgrading
	}

	/**
	 * Insert a user row
	 * 
	 * @param user
	 * @return
	 */
	public int insertUser(User user) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_USER_FIRST_NAME, user.getFirstName());
		cv.put(COLUMN_USER_LAST_NAME, user.getLastName());
		cv.put(COLUMN_USER_EMAIL, user.getEmail());
		cv.put(COLUMN_USER_PASSWORD, user.getPassword());

		Long result = getWritableDatabase().insert(TABLE_USER, null, cv);

		return result.intValue();
	}


	public UserCursor queryUser(long id) {
		Cursor wrapped = getReadableDatabase().query(TABLE_USER, null, // all
																		// columns
				COLUMN_USER_ID + " = ?", // pass in USER_ID
				new String[] { String.valueOf(id) }, // with this value
				null, // group by
				null, // having
				null, // order by
				"1"); // limit 1 row
		return new UserCursor(wrapped);
	}

	public UserCursor queryUserWithEmail(String email) {
		Cursor wrapped = getReadableDatabase().query(TABLE_USER,
				new String[] { COLUMN_USER_ID }, COLUMN_USER_EMAIL + " = ?", // pass
																				// in
																				// COLUMN_USER_EMAIL
				new String[] { email }, // with this value
				null, // group by
				null, // having
				null, // order by
				"1"); // limit 1 row
		return new UserCursor(wrapped);
	}

	public UserCursor queryUserWithLogin(String email, String password) {
		Cursor wrapped = getReadableDatabase().query(TABLE_USER,
				null, // all columns
				COLUMN_USER_EMAIL + " = ? " + " AND " + COLUMN_USER_PASSWORD
						+ " = ? ", // pass in COLUMN_USER_EMAIL
				new String[] { email, password }, // with this value
				null, // group by
				null, // having
				null, // order by 
				"1"); // limit 1 row
		return new UserCursor(wrapped);
	}

	/**
	 * A convenience class to wrap a cursor that returns rows from the "user"
	 * table. The {@link getUser()} method will give you a Run instance
	 * representing the current row.
	 */
	public static class UserCursor extends CursorWrapper {

		public UserCursor(Cursor c) {
			super(c);
		}

		/**
		 * Returns a User object configured for the current row, or null if the
		 * current row is invalid.
		 */
		public User getUser() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			User user = new User();
			user.setUserid(getInt(getColumnIndex(COLUMN_USER_ID)));
			user.setFirstName(getString(getColumnIndex(COLUMN_USER_FIRST_NAME)));
			user.setLastName(getString(getColumnIndex(COLUMN_USER_LAST_NAME)));
			user.setEmail(getString(getColumnIndex(COLUMN_USER_EMAIL)));
			user.setPassword(getString(getColumnIndex(COLUMN_USER_PASSWORD)));

			return user;
		}

		/**
		 * Returns COLUMN_USER_ID if email is found in DB, or null if the email
		 * is invalid.
		 */
		public int getUserIDWithEmail() {
			if (isBeforeFirst() || isAfterLast())
				return -1;
			int userID = getInt(getColumnIndex(COLUMN_USER_ID));
			return userID;
		}
	}
	
	public static class TaskCursor extends CursorWrapper {

		public TaskCursor(Cursor c) {
			super(c);
		}

		/**
		 * Returns a User object configured for the current row, or null if the
		 * current row is invalid.
		 */
		public Task getTask() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			Task task = new Task();
			task.setTaskID(getInt(getColumnIndex(COLUMN_TASK_ID)));
			task.setDueDate(HelperUtil.convertStrToDate(getString(getColumnIndex(COLUMN_TASK_DUEDATE)))); // date type
			task.setPriority(getString(getColumnIndex(COLUMN_TASK_PRIORITY)));
			task.setSubject(getString(getColumnIndex(COLUMN_TASK_SUBJECT)));
			task.setDescription(getString(getColumnIndex(COLUMN_TASK_DESCRIPTION)));
			
			String completed = getString(getColumnIndex(COLUMN_TASK_COMPLETED)); 
			if ("TRUE".equalsIgnoreCase(completed)) {
				task.setCompleted(true);
			} else if ("FALSE".equalsIgnoreCase(completed)) {
				task.setCompleted(false);
			}
			
			task.setUserid(getInt(getColumnIndex(COLUMN_TASK_USERIDFK))); 

			return task;
		}
		
		public Task getTask4Display() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			Task task = new Task();
			task.setTaskID(getInt(getColumnIndex(COLUMN_TASK_ID)));
			task.setDueDate(HelperUtil.convertStrToDate(getString(getColumnIndex(COLUMN_TASK_DUEDATE)))); // date type
			task.setPriority(getString(getColumnIndex(COLUMN_TASK_PRIORITY))); 
			task.setSubject(getString(getColumnIndex(COLUMN_TASK_SUBJECT)));
			//task.setDescription(getString(getColumnIndex(COLUMN_TASK_DESCRIPTION)));
			
			String completed = getString(getColumnIndex(COLUMN_TASK_COMPLETED)); 
			if ("TRUE".equalsIgnoreCase(completed)) {
				task.setCompleted(true);
			} else if ("FALSE".equalsIgnoreCase(completed)) {
				task.setCompleted(false);
			}
			
			//task.setTaskID(getInt(getColumnIndex(COLUMN_TASK_USERIDFK))); 

			return task;
		}
	}
	
/*	public static final String TABLE_TASK = "task";
	public static final String COLUMN_TASK_ID = "_taskID";
	public static final String COLUMN_TASK_USERIDFK = "userIDFK";
	public static final String COLUMN_TASK_SUBJECT = "subject";
	public static final String COLUMN_TASK_DUEDATE = "dateDue";
	public static final String COLUMN_TASK_PRIORITY = "priority";
	public static final String COLUMN_TASK_DESCRIPTION = "description";
	public static final String COLUMN_TASK_COMPLETED = "completed";*/
	
	private String getTaskStatusInStr(boolean completed) {
		if (completed) {
			return "TRUE";
		} else {
			return "FALSE";
		}		
	}
	
	public TaskCursor queryAllTask() {
		Cursor wrapped = getReadableDatabase().query(TABLE_TASK, 
				new String[] { COLUMN_TASK_COMPLETED, COLUMN_TASK_DUEDATE, COLUMN_TASK_PRIORITY, COLUMN_TASK_SUBJECT, COLUMN_TASK_ID}, // columns
				null, // all tasks
				null, // with this value
				null, // group by
				null, // having
				null, // order by
				null); // no limit
		return new TaskCursor(wrapped);
	}
	
	public TaskCursor queryAllTaskByUserID(int userID) {
		Cursor wrapped = getReadableDatabase().query(TABLE_TASK, 
				new String[] { COLUMN_TASK_COMPLETED, COLUMN_TASK_DUEDATE, COLUMN_TASK_PRIORITY, COLUMN_TASK_SUBJECT, COLUMN_TASK_ID}, // columns
				COLUMN_TASK_USERIDFK + " = ? ", // all tasks
				new String[] { String.valueOf(userID) }, // with this value
				COLUMN_TASK_COMPLETED + " , " + COLUMN_TASK_DUEDATE + " , " + COLUMN_TASK_PRIORITY + " , " + COLUMN_TASK_SUBJECT + " , " + COLUMN_TASK_ID, // group by
				null, // having
				COLUMN_TASK_COMPLETED + " ASC , " + COLUMN_TASK_DUEDATE + " ASC , " + COLUMN_TASK_PRIORITY + " , " + COLUMN_TASK_SUBJECT + " , " + COLUMN_TASK_ID, // order by
				null); // no limit
		return new TaskCursor(wrapped);
	}
	
	public TaskCursor queryAllTaskByUserIDAndStatus(int userID, boolean completed) {
		Cursor wrapped = getReadableDatabase().query(TABLE_TASK, 
				new String[] { COLUMN_TASK_COMPLETED, COLUMN_TASK_DUEDATE, COLUMN_TASK_PRIORITY, COLUMN_TASK_SUBJECT, COLUMN_TASK_ID}, // columns
				COLUMN_TASK_USERIDFK + " = ? " + " AND " + COLUMN_TASK_COMPLETED + " = ? ", // all tasks
				new String[] { String.valueOf(userID), getTaskStatusInStr(completed) }, // with this value
				COLUMN_TASK_COMPLETED + " , " + COLUMN_TASK_DUEDATE + " , " + COLUMN_TASK_PRIORITY + " , " + COLUMN_TASK_SUBJECT + " , " + COLUMN_TASK_ID, // group by
				null, // having
				COLUMN_TASK_COMPLETED + " ASC , " + COLUMN_TASK_DUEDATE + " ASC , " + COLUMN_TASK_PRIORITY + " , " + COLUMN_TASK_SUBJECT + " , " + COLUMN_TASK_ID, // order by
				null); // no limit
		return new TaskCursor(wrapped);
	}
	
	public TaskCursor queryTaskByID(int taskID) {
		Cursor wrapped = getReadableDatabase().query(TABLE_TASK,
				null, // all columns
				COLUMN_TASK_ID + " = ? ", // pass in COLUMN_TASK_ID
				new String[] { String.valueOf(taskID) }, // with this value
				null, // group by
				null, // having
				null, // order by
				"1"); // limit 1 row
		return new TaskCursor(wrapped);
	}
	
	
	// insert a passed task into the tasks table
	public int insertTask( Task theTask )
	{
		ContentValues cv = new ContentValues();
		cv.put( COLUMN_TASK_SUBJECT, theTask.getSubject() );
		cv.put( COLUMN_TASK_DUEDATE, HelperUtil.convertDateToStr(theTask.getDueDate()));
		cv.put( COLUMN_TASK_PRIORITY, theTask.getPriority() );
		cv.put( COLUMN_TASK_DESCRIPTION, theTask.getDescription() );
		cv.put(COLUMN_TASK_COMPLETED, getTaskStatusInStr(theTask.isCompleted()));
		cv.put( COLUMN_TASK_USERIDFK, theTask.getUserid()) ;

		Long result = getWritableDatabase().insert( TABLE_TASK, null, cv );
		return result.intValue();
	}
	
	// update a task, the Task instance will have the ID we need
	public int updateTask( Task theTask )
	{
		ContentValues cv = new ContentValues();
		cv.put( COLUMN_TASK_SUBJECT, theTask.getSubject() );
		cv.put( COLUMN_TASK_DUEDATE, HelperUtil.convertDateToStr(theTask.getDueDate()));
		cv.put( COLUMN_TASK_PRIORITY, theTask.getPriority() );
		cv.put( COLUMN_TASK_DESCRIPTION, theTask.getDescription() );
		cv.put(COLUMN_TASK_COMPLETED, getTaskStatusInStr(theTask.isCompleted()));
		cv.put( COLUMN_TASK_USERIDFK, theTask.getUserid() );
		
		// TODO make sure this is how this works
		String whereClause = COLUMN_TASK_ID + " = ?";
		String[] whereArgs = { Integer.toString( theTask.getTaskID() ) };
		
		int result = getWritableDatabase().update( TABLE_TASK, cv, whereClause, whereArgs );
		return result;
	}
	
	/**
	 * Update a given task upon completion
	 * 
	 * @param taskID
	 * @return no. of rows affected
	 */
	public int updateTaskUponCompletion(int taskID, boolean completed) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TASK_COMPLETED, getTaskStatusInStr(completed));

		String whereClause = COLUMN_TASK_ID + " = ?";
		String[] whereArgs = new String[] { String.valueOf(taskID) };

		int result = getWritableDatabase().update(TABLE_TASK, cv, whereClause,
				whereArgs);
		return result;
	}
	
	/**
	 * Delete a given task.
	 * 
	 * @param taskID
	 * @return no. of rows affected
	 */
	public int deleteTask(int taskID) {
		String whereClause = COLUMN_TASK_ID + " = ?";
		String[] whereArgs = new String[] { String.valueOf(taskID) };

		int result = getWritableDatabase().delete(TABLE_TASK, whereClause,
				whereArgs);
		return result;
	}

	public void close() {
		this.close();
	}
}
