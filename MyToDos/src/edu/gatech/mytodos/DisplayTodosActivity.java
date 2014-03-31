package edu.gatech.mytodos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import edu.gatech.mytodos.model.MyTodosManager;
import edu.gatech.mytodos.model.Task;
import edu.gatech.mytodos.model.User;
import edu.gatech.mytodos.util.DatabaseHelper;
import edu.gatech.mytodos.util.HelperUtil;

public class DisplayTodosActivity extends Activity {
	private static final String LIST_HEADER_COLUMN_ID_DUE_DATE = "LIST_HEADER_COLUMN_ID_DUE_DATE";
	private static final String LIST_HEADER_COLUMN_ID_PRIORITY = "LIST_HEADER_COLUMN_ID_PRIORITY";
	private static final String LIST_HEADER_COLUMN_ID_SUBJECT = "LIST_HEADER_COLUMN_ID_SUBJECT";
	private static final String[] LIST_HEADER_COLUMN_IDS = {
			LIST_HEADER_COLUMN_ID_DUE_DATE, LIST_HEADER_COLUMN_ID_PRIORITY,
			LIST_HEADER_COLUMN_ID_SUBJECT };
	private static final int[] LIST_HEADER_LAYOUT_VIEW_IDS = {
			R.id.listHeaderDueDate, R.id.listHeaderPriority,
			R.id.listHeaderSubject };

	private ListView mHeaderListView;
	private ListView mContentListView;
	
	private int mUserID = -1;
	private boolean mCompleted = false; // default
	private MyTodosManager mMyTodosManager;
	// to keep track which task gets clicked on
	// index - clicked list item position; value - task ID
	private LinkedList<Integer> listItemPositionTaskIDList = null;
	private int currentItemPosition = -1;
	
	private Menu mDisplayTodosMenu = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_todos);
		// Show the Up button in the action bar.
		setupActionBar();
		
		mMyTodosManager = MyTodosManager.get(this);
		
		mHeaderListView = (ListView) findViewById(R.id.listViewHeader);
		createContentListView();
		
		init();
		createListHeader();
		createListContent();
	}
	
	private void createContentListView() {
		mContentListView = (ListView) findViewById(R.id.listViewContent);
		mContentListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				currentItemPosition = position;
				/*Toast.makeText(getApplicationContext(),
						"Click ListItem Number " + position, Toast.LENGTH_LONG)
						.show();*/
				// Object itemSelected = mContentListView.getItemAtPosition(position);
				// TODO: temp fix
				// openEditTask(position);
				setActionBarButtonsVisible(true);
			}
		});
	}
	
	private void setActionBarButtonsVisible(boolean enabled) {
		// always show add, refresh
		// hide delete, edit, complete
		mDisplayTodosMenu.findItem(R.id.action_complete_task).setVisible(enabled);
		mDisplayTodosMenu.findItem(R.id.action_edit_task).setVisible(enabled);
		mDisplayTodosMenu.findItem(R.id.action_delete_task).setVisible(enabled);		
	}
	
	private void init() {
		Intent intent = getIntent();
		mUserID = intent.getExtras().getInt(DatabaseHelper.COLUMN_USER_ID);

		// TODO: simplify, do not need all user columns
		User user = mMyTodosManager.getUserByID(mUserID);
		TextView welcomeMsg = (TextView) findViewById(R.id.welcomeBack);
		String msg = getResources().getString(R.string.welcome_back) + ", "
				+ user.getFirstName();
		welcomeMsg.setText(msg);
	}
	
	private void createListHeader() {
		// create column header key-value pair
		final HashMap<String, String> mListHeaderTitleMap = new HashMap<String, String>();
		mListHeaderTitleMap.put(LIST_HEADER_COLUMN_ID_DUE_DATE, getResources()
				.getString(R.string.list_header_due_date));
		mListHeaderTitleMap.put(LIST_HEADER_COLUMN_ID_PRIORITY, getResources()
				.getString(R.string.list_header_priority));
		mListHeaderTitleMap.put(LIST_HEADER_COLUMN_ID_SUBJECT, getResources()
				.getString(R.string.list_header_subject));

		ArrayList<HashMap<String, String>> mListHeaderTitleStrings = new ArrayList<HashMap<String, String>>();
		// since this is the header, there is only 1 row
		mListHeaderTitleStrings.add(mListHeaderTitleMap);

		try {
			SimpleAdapter mListHeaderTitleAdapter = new SimpleAdapter(this,
					mListHeaderTitleStrings, R.layout.activity_display_todos_list_row,
					LIST_HEADER_COLUMN_IDS, LIST_HEADER_LAYOUT_VIEW_IDS);
			mHeaderListView.setAdapter(mListHeaderTitleAdapter);
		} catch (Exception e) {
			// TODO
		}
	}
	
	private void createListContent() {
		// query data from db
		// get incomplete task by default
		List<Task> allTaskList = mMyTodosManager.getAllTasksByUserIDAndStatus(mUserID, mCompleted);
		if (allTaskList == null || allTaskList.isEmpty()) {
			return;
		}
		
		// create column content key-value pair
		HashMap<String, String> mListContentTitleMap = null;		
		ArrayList<HashMap<String, String>> mListContentTitleStrings = new ArrayList<HashMap<String, String>>();
		listItemPositionTaskIDList = new LinkedList<Integer>();
		
		for (int i = 0; i < allTaskList.size(); i++) {
			Task task = allTaskList.get(i);
			mListContentTitleMap = new HashMap<String, String>();
			String dueDateStr = HelperUtil.convertDateToStr(task.getDueDate());
			mListContentTitleMap
					.put(LIST_HEADER_COLUMN_ID_DUE_DATE, dueDateStr);
			mListContentTitleMap.put(LIST_HEADER_COLUMN_ID_PRIORITY,
					task.getPriority());
			mListContentTitleMap.put(LIST_HEADER_COLUMN_ID_SUBJECT,
					task.getSubject());

			mListContentTitleStrings.add(mListContentTitleMap);
			listItemPositionTaskIDList.add(Integer.valueOf(task.getTaskID()));
		}

		try {
			SimpleAdapter mListContentTitleAdapter = new SimpleAdapter(this,
					mListContentTitleStrings, R.layout.activity_display_todos_list_row,
					LIST_HEADER_COLUMN_IDS, LIST_HEADER_LAYOUT_VIEW_IDS);
			mContentListView.setAdapter(mListContentTitleAdapter);
		} catch (Exception e) {
			// TODO
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.mDisplayTodosMenu = menu;
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_todos, menu);
		getMenuInflater().inflate(R.menu.activity_display_todos_actions, menu);
		setActionBarButtonsVisible(false);
 
        return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
			
		case R.id.action_add_new_task:
			openNewTask();
			break;
			
		case R.id.action_edit_task:
			openEditTask(currentItemPosition);
			break;
			
		case R.id.action_delete_task:
			deleteTask(currentItemPosition);
			break;
			
		case R.id.action_complete_task:
			checkOffTask(currentItemPosition);
			break;
			
		case R.id.action_refresh:
			refresh();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Open EditNewTask activity and create a new task
	 */
	private void openNewTask() {
		// move to DisplayTodosActivity view
		Intent intent = new Intent(this, EditNewTask.class);
		intent.putExtra(DatabaseHelper.COLUMN_TASK_ID, EditNewTask.NEW_TASK_ID);
		intent.putExtra(DatabaseHelper.COLUMN_USER_ID, mUserID);

		startActivity(intent);
	}
	
	/**
	 * Open EditNewTask activity and edit an existing task given item position
	 * clicked
	 * 
	 * @param position
	 */
	private void openEditTask(int position) {
		Integer taskID = (Integer) listItemPositionTaskIDList.get(Integer
				.valueOf(position));

		// move to DisplayTodosActivity view
		Intent intent = new Intent(this, EditNewTask.class);

		intent.putExtra(DatabaseHelper.COLUMN_TASK_ID, taskID);
		intent.putExtra(DatabaseHelper.COLUMN_USER_ID, mUserID);

		startActivity(intent);
	}
	
	// Delete task
	// TODO: 
	// add popup dialog to confirm
	// refresh and rebuild listItemPositionTaskIDMap after delete and check off, use linkedlist?
	private void deleteTask(int position) {
		Integer taskID = (Integer) listItemPositionTaskIDList.get(Integer
				.valueOf(position));
		mMyTodosManager.deleteTask(taskID);
		refresh();
	}	

	private void checkOffTask(int position) {
		Integer taskID = (Integer) listItemPositionTaskIDList.get(Integer
				.valueOf(position));
		mMyTodosManager.checkOffTask(taskID);
		refresh();	
	}
	
	/**
	 * Refresh current activity
	 */
	// TODO: add progress thread
	private void refresh() {
		finish();
		Intent intent = new Intent(this, DisplayTodosActivity.class);
		intent.putExtra(DatabaseHelper.COLUMN_USER_ID, mUserID);
		startActivity(intent);
	}
}
