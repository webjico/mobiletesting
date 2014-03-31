package edu.gatech.mytodos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import edu.gatech.mytodos.model.MyTodosManager;
import edu.gatech.mytodos.model.User;
import edu.gatech.mytodos.util.DatabaseHelper;

/**
 * Activity which display a Settings view for {@link DisplayTodosActivity}.
 * Please note that this Setting is stored at the user level, i.e. it's not a
 * shared preference for the entire App among all users. It comes with auto
 * save. After it finishes, it will transit to {@link DisplayTodosActivity}
 * 
 */
public class DisplayTodosSettingsActivity extends Activity {
	private int mUserID = User.DEFAULT_USER_ID;
	private CheckBox hideCompeteTaskCheckBox = null;
	boolean showCompleteTask = User.DEFAULT_SHOW_COMPLETE_TASK;
	private MyTodosManager mMyTodosManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_todos_settings);
		// Show the Up button in the action bar.
		setupActionBar();

		mMyTodosManager = MyTodosManager.get(this);

		hideCompeteTaskCheckBox = (CheckBox) findViewById(R.id.settingHideCompleteTask);

		init();

		if (showCompleteTask) {
			hideCompeteTaskCheckBox.setChecked(false); // do not hide
		} else {
			hideCompeteTaskCheckBox.setChecked(true); // hide
		}

		hideCompeteTaskCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (hideCompeteTaskCheckBox.isChecked()) {
					showCompleteTask = false;
				} else {
					showCompleteTask = true;
				}
				mMyTodosManager.updateUserSettingsShowCompleteTaskWithID(
						mUserID, showCompleteTask);
			}
		});
	}
	
	private void init() {
		Intent intent = getIntent();
		mUserID = intent.getExtras().getInt(DatabaseHelper.COLUMN_USER_ID);

		showCompleteTask = mMyTodosManager
				.getUserSettingsShowCompleteTaskWithID(mUserID);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_todos_settings, menu);
		return true;
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
		}
		return super.onOptionsItemSelected(item);
	}
}
