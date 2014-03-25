package edu.gatech.mytodos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import edu.gatech.mytodos.model.MyTodosManager;
import edu.gatech.mytodos.model.User;
import edu.gatech.mytodos.util.DatabaseHelper;

/**
 * Activity which displays a view to let user create a new user account 
 *
 */
public class CreateNewAccountActivity extends Activity {
	private MyTodosManager mMyTodosManager;
	private User mUser;

	private Button mSaveButton;
	private Button mCancelButton;

	private String mFirstName = "";
	private String mLastName = "";
	private String mEmail = "";
	private String mPassword = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_account);
		// Show the Up button in the action bar.
		setupActionBar();

		init();

		mMyTodosManager = MyTodosManager.get(this);

		mSaveButton = (Button) findViewById(R.id.createNewUserSave);
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getInputs();
				// don't save or move on if no name
				if (mFirstName.isEmpty() || mLastName.isEmpty()) {
					alertbox(
							getResources().getString(
									R.string.alert_title_name_required),
							getResources().getString(
									R.string.alert_message_name_required));
				} else {
					mUser = mMyTodosManager.addNewUser(mFirstName, mLastName,
							mEmail, mPassword);
					displayTodosActivity(findViewById(R.layout.activity_create_new_account));
					finish();
				}
			}
		});

		mCancelButton = (Button) findViewById(R.id.createNewUserCancel);
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	// from
	// http://www.androidsnippets.com/display-an-alert-box
	protected void alertbox(String title, String mymessage) {
		new AlertDialog.Builder(this)
				.setMessage(mymessage)
				.setTitle(title)
				.setCancelable(true)
				.setNeutralButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
	}

	protected void getInputs() {
		// get inputs
		EditText firstNameText = (EditText) findViewById(R.id.createNewUserFirstName);
		if (firstNameText.getText() != null) {
			mFirstName = firstNameText.getText().toString();
		}
		EditText lastNameText = (EditText) findViewById(R.id.createNewUserLastName);
		if (lastNameText.getText() != null) {
			mLastName = lastNameText.getText().toString();
		}
		EditText emailText = (EditText) findViewById(R.id.createNewUserEmail);
		if (emailText.getText() != null) {
			mEmail = emailText.getText().toString();
		}
		EditText passwordText = (EditText) findViewById(R.id.createNewUserPassword);
		if (passwordText.getText() != null) {
			mPassword = passwordText.getText().toString();
		}
	}

	private void init() {
		Intent intent = getIntent();
		String email = intent.getStringExtra(DatabaseHelper.COLUMN_USER_EMAIL);
		String password = intent
				.getStringExtra(DatabaseHelper.COLUMN_USER_PASSWORD);

		if (email != null) {
			EditText emailText = (EditText) findViewById(R.id.createNewUserEmail);
			emailText.setText(email);
		}
		if (password != null) {
			EditText passwordText = (EditText) findViewById(R.id.createNewUserPassword);
			passwordText.setText(password);
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.create_new_account, menu); return true;
	 * }
	 */

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

	/**
	 * Display Todos list for the given user
	 * 
	 * @param view
	 */
	public void displayTodosActivity(View view) {
		// move to CreateNewAccountActivity view
		Intent intent = new Intent(this, DisplayTodosActivity.class);
		intent.putExtra(DatabaseHelper.COLUMN_USER_ID, mUser.getUserid());

		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMyTodosManager.close();
	}
}
