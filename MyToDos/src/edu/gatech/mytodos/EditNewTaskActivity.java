package edu.gatech.mytodos;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import edu.gatech.mytodos.model.MyTodosManager;
import edu.gatech.mytodos.model.Task;
import edu.gatech.mytodos.util.DatabaseHelper;

/**
 * Activity which lets user to create a new todo or edit an existing todo. After
 * it finishes, it will transit to {@link DisplayTodosActivity}. It comes with
 * auto save feature.
 * 
 */
public class EditNewTaskActivity extends Activity {

	public static final int NEW_TASK_ID = -1;
	
	// the task, if new, this is empty, if edit, it's filled onCreate
	private Task theTask = new Task();
	private MyTodosManager mMyTodosManager;
	private int mUserID;
	
	// the boxes!
	private EditText mSubjectEDT;
	private EditText mDueDateEDT;
	private EditText mDescEDT;
	private RadioGroup mPriorityRDO;
	
	@SuppressLint("SimpleDateFormat")
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
		mPriorityRDO = (RadioGroup)findViewById(R.id.priorityRDO);
		mDescEDT = (EditText)findViewById(R.id.todoDesc);
		mMyTodosManager = MyTodosManager.get(this);
		
		// if this is an existing todo
        if( theID != -1 )
        {
        	title.setText(getResources().getString(R.string.edit_todo_title));
        	// we got an ID, pull the info from the DB
        	theTask = mMyTodosManager.getTaskByID( theID );
        	// and put everything in it's place
        	mSubjectEDT.setText(theTask.getSubject());
        	mDueDateEDT.setText( new SimpleDateFormat("MM/dd/yyyy").format(theTask.getDueDate()));
        	// set the priority radio
			if (theTask.getPriority() == Task.TASK_PRIORITY_LOW)
				mPriorityRDO.check(R.id.lowPriority);
			else if (theTask.getPriority() == Task.TASK_PRIORITY_MEDIUM)
				mPriorityRDO.check(R.id.medPriority);
			else if (theTask.getPriority() == Task.TASK_PRIORITY_HIGH)
				mPriorityRDO.check(R.id.highPriority);
        	
        	mDescEDT.setText(theTask.getDescription());
        }
        // no ID, indicate 'New Todo' in the header bar thing
        else
        {
    		// set some defaults
        	// priority default is set on layout
        	mDueDateEDT.setText( new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        	
        	theTask.setTaskID(-1);
        	theTask.setUserid(mUserID);
        	title.setText(getResources().getString(R.string.create_new_todo_title));
        }
        
        // now that all the boxes are filled, set up some handlers to auto save stuff
        mSubjectEDT.setOnKeyListener( new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int arg1, KeyEvent arg2) {
				saveTask( false );
				return false;
			}
		} );
		
        mDueDateEDT.setOnKeyListener( new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int arg1, KeyEvent arg2) {
				saveTask( false );
				return false;
			}
		} );
        
        // this one is different because it's a radio button, we don't type on this, just click
        // so use a click handler
        mPriorityRDO.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				saveTask( false );
			}
		} );
        
        mDescEDT.setOnKeyListener( new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int arg1, KeyEvent arg2) {
				saveTask( false );
				return false;
			}
		} );
	}

	public void OnClick_saveTodoBTN(View view)
	{
		saveTask( true );
	}
	
	private void saveTask( boolean doFinish )
	{
		// get the values in the boxes
		theTask.setSubject(mSubjectEDT.getText().toString());
		theTask.setDueDate(mDueDateEDT.getText().toString());
		// set the priority based on the checked id
		int checked = mPriorityRDO.getCheckedRadioButtonId();
		if (checked == R.id.lowPriority)
			theTask.setPriority(Task.TASK_PRIORITY_LOW);
		else if (checked == R.id.medPriority)
			theTask.setPriority(Task.TASK_PRIORITY_MEDIUM);
		else if (checked == R.id.highPriority)
			theTask.setPriority(Task.TASK_PRIORITY_HIGH);
		theTask.setDescription(mDescEDT.getText().toString());
		theTask.setUserid(mUserID);
		
		
		// and now execute the SQL statement
		if( theTask.getTaskID() != -1 )
			mMyTodosManager.updateTask( theTask );
		else
			theTask.setTaskID( mMyTodosManager.newTask( theTask ) );
		
		// and leave, if requested
		if( doFinish )
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

	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = DatePickerFragment.newInstance(mDueDateEDT.getText().toString());
	    newFragment.show(getFragmentManager(), "datePicker");
	}

	public void setDate(int year, int month, int day) {
		mDueDateEDT.setText(month + "/"+ day + "/" + year) ;
	}
}
