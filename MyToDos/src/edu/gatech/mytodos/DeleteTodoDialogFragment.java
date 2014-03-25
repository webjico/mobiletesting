package edu.gatech.mytodos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Fragment that displays a confirmation dialog when user tries to delete a
 * todo. It's attached to {@link DisplayTodosActivity}.
 * 
 */
public class DeleteTodoDialogFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final DisplayTodosActivity displayTodo = (DisplayTodosActivity) getActivity();

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.delete_todo_dialog_message)
				.setPositiveButton(R.string.delete_todo_dialog_no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								displayTodo.confirmDelete(false);
							}
						})
				.setNegativeButton(R.string.delete_todo_dialog_yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								displayTodo.confirmDelete(true);
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}
}