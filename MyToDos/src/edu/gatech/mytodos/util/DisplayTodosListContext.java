package edu.gatech.mytodos.util;

import android.util.SparseIntArray;

/**
 * Utility class which carries various info used {@link DisplayTodosListAdapter}
 * .
 * 
 */
public class DisplayTodosListContext {
	private static final int TASK_STATUS_INCOMPLETE = 0;
	private static final int TASK_STATUS_COMPLETE = 1;

	// to keep track which task gets clicked on
	// key - clicked list item position; value - task ID
	private SparseIntArray mListItemPositionTaskIDMap = null;
	// to keep track of the status of the task, to show complete or incomplete
	// task button accordingly
	// key - position; value - task status
	private SparseIntArray mListItemPositionStatusMap = null;

	public DisplayTodosListContext() {
		mListItemPositionTaskIDMap = new SparseIntArray();
		mListItemPositionStatusMap = new SparseIntArray();
	}

	public SparseIntArray getListItemPositionTaskIDMap() {
		return mListItemPositionTaskIDMap;
	}

	public int getTaskIDAtPosition(int position) {
		return mListItemPositionTaskIDMap.get(position);
	}

	public void addToListItemPositionTaskIDMap(int position, int taskID) {
		mListItemPositionTaskIDMap.put(position, taskID);
	}

	public SparseIntArray getListItemPositionStatusMap() {
		return mListItemPositionStatusMap;
	}

	public boolean isTaskCompleteAtPosition(int position) {
		int taskStatus = mListItemPositionStatusMap.get(position);
		if (taskStatus == TASK_STATUS_COMPLETE) {
			return true;
		} else if (taskStatus == TASK_STATUS_INCOMPLETE) {
			return false;
		} else {
			return false; // default is incomplete
		}
	}

	public void addToListItemPositionStatusMap(int position, boolean complete) {
		if (complete) {
			mListItemPositionStatusMap.put(position, TASK_STATUS_COMPLETE);
		} else {
			mListItemPositionStatusMap.put(position, TASK_STATUS_INCOMPLETE);
		}
	}
}
