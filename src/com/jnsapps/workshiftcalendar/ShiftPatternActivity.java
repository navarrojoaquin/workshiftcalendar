package com.jnsapps.workshiftcalendar;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jnsapps.workshiftcalendar.db.WorkTableDbAdapter;
import com.jnsapps.workshiftcalendar.fragment.DatePickerFragment;
import com.jnsapps.workshiftcalendar.fragment.ShiftPatternListFragment;
import com.jnsapps.workshiftcalendar.model.Shift;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Joaquín Navarro Salmerón
 * 
 */
public class ShiftPatternActivity extends SherlockFragmentActivity {

	private TextView mStartDateTextView = null;
	private TextView mEndDateTextView = null;
	private DateFormat df = DateFormat.getDateInstance();
	private Calendar mStartDate = null;
	private Calendar mEndDate = null;

	private WorkTableDbAdapter mDatabase;

	public static final int PATTERN_SET = 20;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shift_pattern);

		mDatabase = new WorkTableDbAdapter(this);

		mStartDateTextView = (TextView) findViewById(R.id.start_date_textview);
		mEndDateTextView = (TextView) findViewById(R.id.end_date_textview);
		// 'Save' button
		Button saveButton = (Button) findViewById(R.id.pattern_save_button);
		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setPattern();
			}
		});
	}

	public void showStartDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		Bundle args = new Bundle();
		args.putInt("type", 0);
		newFragment.setArguments(args);
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	public void showEndDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		Bundle args = new Bundle();
		args.putInt("type", 1);
		newFragment.setArguments(args);
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	public void setStartDate(Calendar c) {
		mStartDate = c;
		mStartDateTextView.setText(df.format(c.getTime()));
	}

	public void setEndDate(Calendar c) {
		mEndDate = c;
		mEndDateTextView.setText(df.format(c.getTime()));
	}

	private void setPattern() {
		// Validations
		if (mStartDate == null || mEndDate == null) {
			Toast.makeText(this, R.string.pattern_validation_complete_date,
					Toast.LENGTH_LONG).show();
			return;
		}
		if (mStartDate.compareTo(mEndDate) > 0) {
			Toast.makeText(this, R.string.pattern_validation_date_order,
					Toast.LENGTH_LONG).show();
			return;
		}
		ShiftPatternListFragment fragment = (ShiftPatternListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.shift_pattern_list_fragment);
		ArrayList<Shift> shiftPattern = fragment.getPattern();
		if (shiftPattern.size() == 0) {
			Toast.makeText(this, R.string.pattern_validation_create_pattern,
					Toast.LENGTH_LONG).show();
			return;
		}
		// Begin
		new SetShiftPattern().execute(shiftPattern);
		setResult(ShiftPatternActivity.PATTERN_SET);
	}

	private class SetShiftPattern extends
			AsyncTask<ArrayList<Shift>, Void, Boolean> {

		protected void onPreExecute() {
			Toast.makeText(ShiftPatternActivity.this,
					R.string.pattern_set_progress, Toast.LENGTH_SHORT).show();
		}

		protected Boolean doInBackground(ArrayList<Shift>... patterns) {
			try {
				mDatabase = mDatabase.open();
				mDatabase.setPattern(patterns[0], mStartDate, mEndDate);
				mDatabase.close();
			} catch (Exception e) {
				mDatabase.close();
				return false;
			}
			return true;
		}

		protected void onPostExecute(Boolean result) {
			try {
				if (result.booleanValue()) {
					Toast.makeText(ShiftPatternActivity.this,
							R.string.pattern_set_complete, Toast.LENGTH_SHORT)
							.show();

				} else {
					Toast.makeText(ShiftPatternActivity.this,
							R.string.pattern_set_error, Toast.LENGTH_SHORT)
							.show();
				}
			} catch (Exception e) {
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("startDate", mStartDate);
		outState.putSerializable("endDate", mEndDate);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Calendar startDate = (Calendar) savedInstanceState
				.getSerializable("startDate");
		Calendar endDate = (Calendar) savedInstanceState
				.getSerializable("endDate");
		if (startDate != null) {
			setStartDate(startDate);
		}
		if (endDate != null) {
			setEndDate(endDate);
		}
	}

}
