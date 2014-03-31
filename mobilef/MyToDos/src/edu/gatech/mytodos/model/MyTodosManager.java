package edu.gatech.mytodos.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import edu.gatech.mytodos.util.DatabaseHelper;
import edu.gatech.mytodos.util.DatabaseHelper.TaskCursor;
import edu.gatech.mytodos.util.DatabaseHelper.UserCursor;

/**
 * Manage access to data layer.
 * 
 */
public class MyTodosManager {
	private static MyTodosManager sMyTodosManager;
	private Context mAppContext;
	private DatabaseHelper mDBHelper;

	private MyTodosManager(Context appContext) {
		mAppContext = appContext;
		mDBHelper = new DatabaseHelper(mAppContext);
	}

	public static MyTodosManager get(Context c) {
		if (sMyTodosManager == null) {
			// we use the application context to avoid leaking activities
			sMyTodosManager = new MyTodosManager(c.getApplicationContext());
		}
		return sMyTodosManager;
	}

	// ///////////////////////////////////////////////////////////////
	// USER
	// ///////////////////////////////////////////////////////////////

	public User addNewUser(String firstName, String lastName, String email,
			String password) {
		// Insert a user into the db
		User user = new User(firstName, lastName, email, password);
		user.setUserid(mDBHelper.insertUser(user));

		return user;
	}

	public User getUserByID(int id) {
		User user = null;
		UserCursor cursor = mDBHelper.queryUser(id);
		cursor.moveToFirst();
		// expects 1 row only
		if (!cursor.isAfterLast()) {
			user = cursor.getUser();
		}
		cursor.close();
		return user;
	}

	public int validateUserEmail(String email) {
		int userID = -1;
		UserCursor cursor = mDBHelper.queryUserWithEmail(email);
		cursor.moveToFirst();
		// expects 1 row only
		if (!cursor.isAfterLast()) {
			userID = cursor.getUserIDWithEmail();
		}
		cursor.close();
		return userID;
	}

	public User validateUserLogin(String email, String password) {
		User user = null;
		UserCursor cursor = mDBHelper.queryUserWithLogin(email, password);
		cursor.moveToFirst();
		// expects 1 row only
		if (!cursor.isAfterLast()) {
			user = cursor.getUser();
		}
		cursor.close();
		return user;
	}

	public User getUserShowCompleteTaskByID(int id) {
		User user = null;
		UserCursor cursor = mDBHelper.queryUser(id);
		cursor.moveToFirst();
		// expects 1 row only
		if (!cursor.isAfterLast()) {
			user = cursor.getUser();
		}
		cursor.close();
		return user;
	}

	public boolean getUserSettingsShowCompleteTaskWithID(int id) {
		boolean show = User.DEFAULT_SHOW_COMPLETE_TASK;
		UserCursor cursor = mDBHelper.queryUserShowCompleteTaskWithID(id);
		cursor.moveToFirst();
		// expects 1 row only
		if (!cursor.isAfterLast()) {
			show = cursor.getUserShowCompleteTaskWithID();
		}
		cursor.close();
		return show;
	}

	public void updateUserSettingsShowCompleteTaskWithID(int userID,
			boolean show) {
		mDBHelper.updateUserSettingShowCompleteTask(userID, show);
	}

	// ///////////////////////////////////////////////////////////////
	// TASK
	// ///////////////////////////////////////////////////////////////
	public List<Task> getAllTasks() {
		Task task = null;
		List<Task> taskList = null;
		TaskCursor cursor = mDBHelper.queryAllTask();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			if (taskList == null) {
				taskList = new ArrayList<Task>();
			}
			task = cursor.getTask4Display();
			taskList.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		return taskList;
	}

	public List<Task> getAllTasksByUserID(int userID) {
		Task task = null;
		List<Task> taskList = null;
		TaskCursor cursor = mDBHelper.queryAllTaskByUserID(userID);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			if (taskList == null) {
				taskList = new ArrayList<Task>();
			}
			task = cursor.getTask4Display();
			taskList.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		return taskList;
	}

	public List<Task> getAllTasksByUserIDAndStatus(int userID,
			boolean showCompleted, boolean showIncompleted) {
		Task task = null;
		List<Task> taskList = null;
		TaskCursor cursor = null;

		if (showCompleted == false && showIncompleted == false) {
			// nothing to show
			return null;
		} else if (showCompleted == true && showIncompleted == true) {
			// show all
			cursor = mDBHelper.queryAllTaskByUserID(userID);
		} else if ((showCompleted == true && showIncompleted == false)
				|| (showCompleted == false && showIncompleted == true)) {
			// either one is true
			cursor = mDBHelper.queryAllTaskByUserIDAndStatus(userID,
					showCompleted);
		}

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			if (taskList == null) {
				taskList = new ArrayList<Task>();
			}
			task = cursor.getTask4Display();
			taskList.add(task);
			cursor.moveToNext();
		}
		cursor.close();
		return taskList;
	}

	public Task getTaskByID(int taskID) {
		Task task = null;
		TaskCursor cursor = mDBHelper.queryTaskByID(taskID);
		cursor.moveToFirst();
		// expects 1 row only
		if (!cursor.isAfterLast()) {
			task = cursor.getTask();
		}
		cursor.close();
		return task;
	}

	// a passthrough for a new task, already initialized in EditNewTask
	public int newTask(Task theTask) {
		return mDBHelper.insertTask(theTask);
	}

	// another passthrough to update a task
	public int updateTask(Task theTask) {
		return mDBHelper.updateTask(theTask);
	}

	public void checkOffTask(int taskID) {
		mDBHelper.updateTaskUponCompletion(taskID, true);
	}

	public void undoCheckOffTask(int taskID) {
		mDBHelper.updateTaskUponCompletion(taskID, false);
	}

	public void deleteTask(int taskID) {
		mDBHelper.deleteTask(taskID);
	}

	public void close() {
		mDBHelper.close();
	}
}
