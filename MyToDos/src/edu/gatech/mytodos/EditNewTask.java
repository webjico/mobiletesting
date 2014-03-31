package edu.gatech.mytodos;

import edu.gatech.mytodos.model.MyTodosManager;
import edu.gatech.mytodos.model.Task;
import edu.gatech.mytodos.util.DatabaseHelper;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class EditNewTask extends Activity {

	public static final int NEW_TASK_ID = -1;
	
	// the task, if new, this is empty, if edit, it's filled onCreate
	private Task theTask = new Task();
	private MyTodosManager mMyTodosManager;
	private int mUserID;
	
	// the boxes!
	private EditText mSubjectEDT;
	private EditText mDueDateEDT;
	private EditText mPriorityEDT;
	private EditText mDescEDT;
	private CheckBox mCompletedCKB;
	
	// the buttons!
	private Button mSubmitBTN;
	private Button mCancelBTN;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_new_task);
		// Show the Up button in the action bar.
		setupActionBar();
		
		// check if we got an ID to use
    	TextView title = (TextView)findViewById(R.id.titlePlaceholder);
        int theID = getIntent().getExtras().getInt(DatabaseHelper.COLUMN_TASK_ID);
        mUserID = getIntent().getExtras().getInt(DatabaseHelper.COLUMN_USER_ID);
        
        // set the boxes inside here, after they exist
		mSubjectEDT = (EditText)findViewById(R.id.todoSubject);
		mDueDateEDT = (EditText)findViewById(R.id.todoDueDate);
		mPriorityEDT = (EditText)findViewById(R.id.todoPriority);
		mDescEDT = (EditText)findViewById(R.id.todoDesc);
		mCompletedCKB = (CheckBox)findViewById(R.id.completedCKB);
		mSubmitBTN = (Button)findViewById(R.id.editNewSaveTodoBTN);
		mCancelBTN = (Button)findViewById(R.id.editNewCancelBTN);
		mMyTodosManager = MyTodosManager.get(this);

        
        //@#@# check what happens when this isn't passed
        if( theID != -1 )
        {
        	title.setText("Edit Todo");
        	// we got an ID, pull the info from the DB
        	// TODO make that method
        	theTask = mMyTodosManager.getTaskByID( theID );
        	// and put everything in it's place
        	mSubjectEDT.setText(theTask.getSubject());
        	mDueDateEDT.setText(String.valueOf(theTask.getDueDate()));
        	mPriorityEDT.setText(theTask.getPriority());
        	mDescEDT.setText(theTask.getDescription());
        	mCompletedCKB.setChecked(theTask.isCompleted());
        }
        // no ID, indicate 'New Todo' in the header bar thing
        else
        {
        	theTask.setTaskID(-1);
        	theTask.setUserid(mUserID);
        	title.setText("New Todo");
        }
	}

	public void OnClick_saveTodoBTN(View view)
	{
		// get the values in the boxes
		theTask.setSubject(mSubjectEDT.getText().toString());
		theTask.setDueDate(mDueDateEDT.getText().toString());
		theTask.setPriority(mPriorityEDT.getText().toString());
		theTask.setDescription(mDescEDT.getText().toString());
		theTask.setCompleted(mCompletedCKB.isChecked());
		theTask.setUserid(mUserID);
		
		
		// and now execute the SQL statement
		if( theTask.getTaskID() != -1 )
			mMyTodosManager.updateTask( theTask );
		else
			mMyTodosManager.newTask( theTask );
		
		// and leave
		finish();
	}
	
	// just leave here, back to the parent activity
	public void OnClick_cancelBTN(View view)
	{
		finish();
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
		getMenuInflater().inflate(R.menu.edit_new_task, menu);
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
