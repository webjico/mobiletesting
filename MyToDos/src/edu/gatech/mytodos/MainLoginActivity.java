package edu.gatech.mytodos;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import edu.gatech.mytodos.model.MyTodosManager;
import edu.gatech.mytodos.model.User;
import edu.gatech.mytodos.util.DatabaseHelper;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well. This is the main activity in the App. It could transit to these
 * other activities: <br>
 * - {@link CreateNewAccountActivity} if this is a new user who tries to log in <br>
 * - {@link DisplayTodosActivity} if this is an existing user who tries to log
 * in
 */
public class MainLoginActivity extends Activity {
	private MyTodosManager mMyTodosManager;

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	// flags for login result
	private boolean emailNotFound = false;
	private boolean wrongPassword = false;

	private User loginFound = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_login);

		mMyTodosManager = MyTodosManager.get(this);
		// leave this commented, remove db is for development purpose ONLY
		// getApplicationContext().deleteDatabase("mytodos.sqlite");

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
			// mAuthTask.onPostExecute(emailNotFound);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			if (!validateEmail()) {
				// email not found, need to register
				emailNotFound = true;
				return false;
			} else {
				// found email
				User user = validatePassword();
				// but wrong password
				if (user == null) {
					wrongPassword = true;
					return false;
				} else {
					// found correct login
					emailNotFound = false;
					wrongPassword = false;
					loginFound = user;
					return true;
				}
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (emailNotFound) {
				createNewAccountActivity();
			} else {
				if (wrongPassword) {
					mPasswordView
							.setError(getString(R.string.error_incorrect_password));
					mPasswordView.requestFocus();
				} else {
					if (loginFound != null) {
						// finish();
						// display user TODOs
						displayTodosActivity(loginFound.getUserid());
					}
				}
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	private boolean validateEmail() {
		boolean foundEmail = false;
		int userIDFound = mMyTodosManager.validateUserEmail(mEmail.trim());
		if (userIDFound < 0) {
			foundEmail = false;
		} else {
			foundEmail = true;
		}
		return foundEmail;
	}

	private User validatePassword() {
		User userFound = null;
		// found user
		// Account exists, return true if the password matches
		userFound = mMyTodosManager.validateUserLogin(mEmail.trim(),
				mPassword.trim());

		return userFound;
	}

	/**
	 * Register a new user
	 * 
	 */
	public void createNewAccountActivity() {
		// go to CreateNewAccountActivity view
		Intent intent = new Intent(this, CreateNewAccountActivity.class);
		intent.putExtra(DatabaseHelper.COLUMN_USER_EMAIL, mEmail);
		intent.putExtra(DatabaseHelper.COLUMN_USER_PASSWORD, mPassword);

		startActivity(intent);
	}

	/**
	 * Display existing user's Todos
	 * 
	 */
	public void displayTodosActivity(int userID) {
		// go to DisplayTodosActivity view
		Intent intent = new Intent(this, DisplayTodosActivity.class);
		intent.putExtra(DatabaseHelper.COLUMN_USER_ID, userID);

		startActivity(intent);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mMyTodosManager.close();
	}
}
