package edu.gatech.mytodos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import edu.gatech.mytodos.model.MyTodosManager;
import edu.gatech.mytodos.model.Task;
import edu.gatech.mytodos.model.User;
import edu.gatech.mytodos.util.DatabaseHelper;
import edu.gatech.mytodos.util.DisplayTodosListAdapter;
import edu.gatech.mytodos.util.DisplayTodosListContext;
import edu.gatech.mytodos.util.HelperUtil;

/**
 * Activity which displays a list of TODOs
 *
 */
public class DisplayTodosActivity extends Activity {
	private static final String TAG = DisplayTodosActivity.class.getName();
	
	public static final int RESULT_SETTINGS = 1;  
	public static final int RESULT_NEW_TASK = 2; 
	public static final int RESULT_EDIT_TASK = 3; 
	private static final int DEFAULT_LIST_ITEM_POSITION = -1;
	
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
	
	private int mUserID = User.DEFAULT_USER_ID;
	private MyTodosManager mMyTodosManager;
	// to keep track which task gets clicked on
	private DisplayTodosListContext positionContext;
	private int currentItemPosition = DEFAULT_LIST_ITEM_POSITION;
	
	private Menu mDisplayTodosMenu = null;
	
	private boolean mShowCompletedTask = false; // default
	
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
				setActionBarButtonsVisible(true, currentItemPosition);
			}
		});
	}
	
	/**
	 * Enable or disable action bar buttons based on the todo clicked on.
	 * 
	 * @param enabled
	 * @param currentListItemPosition
	 */
	private void setActionBarButtonsVisible(boolean enabled,
			int currentListItemPosition) {
		// always show add, refresh
		// hide delete, edit, complete, undo by default

		// enable complete if the task is incomplete
		if (currentListItemPosition > DEFAULT_LIST_ITEM_POSITION) {
			if (!positionContext
					.isTaskCompleteAtPosition(currentListItemPosition)) {
				mDisplayTodosMenu.findItem(R.id.action_complete_task)
						.setVisible(true);
				mDisplayTodosMenu.findItem(R.id.action_incomplete_task)
						.setVisible(false);
			}
			// enable undo if the task is complete
			else {
				mDisplayTodosMenu.findItem(R.id.action_complete_task)
						.setVisible(false);
				mDisplayTodosMenu.findItem(R.id.action_incomplete_task)
						.setVisible(true);
			}
		} else {
			// also disable them if the view has just been loaded for the 1st
			// time
			mDisplayTodosMenu.findItem(R.id.action_complete_task).setVisible(
					enabled);
			mDisplayTodosMenu.findItem(R.id.action_incomplete_task).setVisible(
					enabled);
		}
		mDisplayTodosMenu.findItem(R.id.action_edit_task).setVisible(enabled);
		mDisplayTodosMenu.findItem(R.id.action_delete_task).setVisible(enabled);
	}
	
	private void init() {
		Intent intent = getIntent();
		mUserID = intent.getExtras().getInt(DatabaseHelper.COLUMN_USER_ID);

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
					mListHeaderTitleStrings,
					R.layout.activity_display_todos_list_header_row,
					LIST_HEADER_COLUMN_IDS, LIST_HEADER_LAYOUT_VIEW_IDS);
			mHeaderListView.setAdapter(mListHeaderTitleAdapter);
		} catch (Exception e) {
			Log.d(TAG,
					"Hit internal error while trying to create SimpleAdapter. Contact support.");
		}
	}
	
	private void createListContent() {
		// get preference on show or hide tasks
		loadSettings();
		// query data from db
		// get incomplete task by default
		// Always show incomplete task, no longer offer this option to user due to usability issue
		List<Task> allTaskList = mMyTodosManager.getAllTasksByUserIDAndStatus(mUserID, mShowCompletedTask, true);
		if (allTaskList == null || allTaskList.isEmpty()) {
			displayToast(R.string.create_new_account_confirmation);
			return;
		}
		
		positionContext = new DisplayTodosListContext();
		DisplayTodosListAdapter adapter = new DisplayTodosListAdapter(this,
				allTaskList, positionContext);
		mContentListView.setAdapter(adapter);
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
		setActionBarButtonsVisible(false, currentItemPosition);
 
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
			
		case R.id.action_settings:
			openSettings();
			break;
		
		case R.id.action_logout:
			HelperUtil.logout(this);
			break;
			
		case R.id.action_add_new_task:
			openNewTask();
			break;
			
		case R.id.action_edit_task:
			openEditTask(currentItemPosition);
			break;
			
		case R.id.action_delete_task:
			showDeleteTodoDialog();
			break;
			
		case R.id.action_complete_task:
			checkOffTask(currentItemPosition);
			break;
			
		case R.id.action_incomplete_task:
			undoCheckOffTask(currentItemPosition);
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
		Intent intent = new Intent(this, EditNewTaskActivity.class);
		intent.putExtra(DatabaseHelper.COLUMN_TASK_ID, EditNewTaskActivity.NEW_TASK_ID);
		intent.putExtra(DatabaseHelper.COLUMN_USER_ID, mUserID);
		// once EditNewTask finishes, come back then refresh todo list
		startActivityForResult(intent, RESULT_NEW_TASK); 
	}
	
	/**
	 * Open EditNewTask activity and edit an existing task given item position
	 * clicked
	 * 
	 * @param position
	 */
	private void openEditTask(int position) {
		Integer taskID = (Integer) positionContext.getTaskIDAtPosition(position);

		// move to DisplayTodosActivity view
		Intent intent = new Intent(this, EditNewTaskActivity.class);

		intent.putExtra(DatabaseHelper.COLUMN_TASK_ID, taskID);
		intent.putExtra(DatabaseHelper.COLUMN_USER_ID, mUserID);
		// once EditNewTask finishes, come back then refresh todo list
		startActivityForResult(intent, RESULT_EDIT_TASK); 
	}
	
	/**
	 * Prompt user to delete todo or not
	 */
	// Delete task
	// refresh and rebuild listItemPositionTaskIDMap after delete and check off
	public void showDeleteTodoDialog() {
	    DeleteTodoDialogFragment newFragment = new DeleteTodoDialogFragment();
	    newFragment.show(getFragmentManager(), "deleteTodoDialog");
	}
	
	public void confirmDelete(boolean delete) {
		if (delete) {
			Integer taskID = (Integer) positionContext
					.getTaskIDAtPosition(currentItemPosition);
			mMyTodosManager.deleteTask(taskID);
			displayToast(R.string.delete_todo_confirmation);
			refresh();
		}
	}

	private void checkOffTask(int position) {
		Integer taskID = (Integer) positionContext.getTaskIDAtPosition(position);
		mMyTodosManager.checkOffTask(taskID);
		displayToast(R.string.check_off_todo_confirmation);
		refresh();	
	}
	
	private void undoCheckOffTask(int position) {
		Integer taskID = (Integer) positionContext.getTaskIDAtPosition(position);
		mMyTodosManager.undoCheckOffTask(taskID);
		displayToast(R.string.undo_todo_confirmation);
		refresh();			
	}
	
	/**
	 * Refresh current activity
	 */
	private void refresh() {
		finish();
		Intent intent = new Intent(this, DisplayTodosActivity.class);
		intent.putExtra(DatabaseHelper.COLUMN_USER_ID, mUserID);
		startActivity(intent);
	}
	

	private void openSettings() {
		Intent intent = new Intent(this, DisplayTodosSettingsActivity.class);
		intent.putExtra(DatabaseHelper.COLUMN_USER_ID, mUserID);
		startActivityForResult(intent, RESULT_SETTINGS); 	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RESULT_SETTINGS:
			// do not call it here, calling refresh will overwrite the settings
			// loadSettings();
			refresh();
			break;
		case RESULT_NEW_TASK:
			refresh();
			break;
		case RESULT_EDIT_TASK:
			refresh();
			break;
		default:
			break;
		}
	}

	private void loadSettings() {
		mShowCompletedTask = mMyTodosManager
				.getUserSettingsShowCompleteTaskWithID(mUserID);
	}	
	
	private void displayToast(int msgID) {
		Toast.makeText(getApplicationContext(),
				getResources().getString(msgID), Toast.LENGTH_LONG).show();
	}
}
