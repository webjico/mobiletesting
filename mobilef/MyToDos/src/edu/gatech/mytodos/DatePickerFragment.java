package edu.gatech.mytodos;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

/**
 * Fragment that displays a date picker for the due date field on the edit/new
 * todo view. It's attached to {@link EditNewTaskActivity}.
 * 
 */
public class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	private int year, month, day;

	public static DatePickerFragment newInstance(String dateString) {
		DatePickerFragment ret = new DatePickerFragment();
		String[] parts = dateString.split("/");

		Bundle b = new Bundle();
		final Calendar c = Calendar.getInstance();

		b.putInt("month", (parts.length == 3) ? Integer.parseInt(parts[0]) - 1
				: c.get(Calendar.MONTH));
		b.putInt(
				"day",
				(parts.length == 3) ? Integer.parseInt(parts[1]) : c
						.get(Calendar.DAY_OF_MONTH));
		b.putInt(
				"year",
				(parts.length == 3) ? Integer.parseInt(parts[2]) : c
						.get(Calendar.YEAR));
		ret.setArguments(b);
		return ret;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// if values not set
		super.onCreate(savedInstanceState);
		year = getArguments().getInt("year");
		month = getArguments().getInt("month");
		day = getArguments().getInt("day");

		if (year == 0) {
			// Use the current date as the default date in the picker
		}
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		EditNewTaskActivity ent = (EditNewTaskActivity) getActivity();
		ent.setDate(year, month + 1, day); // month is 0 based
	}
}
