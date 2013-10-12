package com.jnsapps.workshiftcalendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jnsapps.workshiftcalendar.db.DataBaseHelper;
import com.jnsapps.workshiftcalendar.db.WorkTableDbAdapter;
import com.jnsapps.workshiftcalendar.fragment.StatsDatePickerFragment;
import com.jnsapps.workshiftcalendar.model.Shift;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * @author Joaquín Navarro Salmerón
 * 
 */
public class StatisticsActivity extends SherlockFragmentActivity {

	protected final static String EXTRA_YEAR_ID = "STATISTICS_YEAR";

	private Calendar calendarStart = Calendar.getInstance();
	private Calendar calendarEnd = Calendar.getInstance();
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dfRow = new SimpleDateFormat("yyyy-MM");
	private DateFormat dfDateSelection = DateFormat.getDateInstance();

	private ArrayList<Hashtable<String, Integer>> monthStats = new ArrayList<Hashtable<String, Integer>>();
	private Hashtable<String, Integer> totalStats = new Hashtable<String, Integer>();
	private ArrayList<Long> monthOvertime = new ArrayList<Long>();
	private float totalShiftHours = 0;
	private long totalOvertime = 0;
	private Hashtable<String, String> mShiftAbbreviation;

	private TextView mStartDateTextView = null;
	private TextView mEndDateTextView = null;

	private WorkTableDbAdapter mDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics);

		// Database
		mDatabase = new WorkTableDbAdapter(this);

		mStartDateTextView = (TextView) findViewById(R.id.stats_start_date_textview);
		mEndDateTextView = (TextView) findViewById(R.id.stats_end_date_textview);

		if (savedInstanceState == null) {
			calendarStart.set(Calendar.DAY_OF_MONTH,
					calendarStart.getActualMinimum(Calendar.DAY_OF_MONTH));
			calendarStart.set(Calendar.MONTH,
					calendarStart.getActualMinimum(Calendar.MONTH));
			calendarEnd.set(Calendar.DAY_OF_MONTH,
					calendarStart.getActualMaximum(Calendar.DAY_OF_MONTH));
			calendarEnd.set(Calendar.MONTH,
					calendarStart.getActualMaximum(Calendar.MONTH));

			mStartDateTextView.setText(dfDateSelection.format(calendarStart
					.getTime()));
			mEndDateTextView.setText(dfDateSelection.format(calendarEnd
					.getTime()));

			refreshStats();
		}

	}

	public void refreshStats() {
		monthStats.clear();
		totalStats.clear();
		monthOvertime.clear();
		totalShiftHours = 0;
		totalOvertime = 0;
		getStatistics();
		populateTotalStats();
		populateMonthStats();
	}

	public void showStartDatePickerDialog(View v) {
		DialogFragment newFragment = new StatsDatePickerFragment();
		Bundle args = new Bundle();
		args.putInt("type", 0);
		newFragment.setArguments(args);
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	public void showEndDatePickerDialog(View v) {
		DialogFragment newFragment = new StatsDatePickerFragment();
		Bundle args = new Bundle();
		args.putInt("type", 1);
		newFragment.setArguments(args);
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	public void setStartDate(Calendar c) {
		calendarStart = c;
		mStartDateTextView.setText(dfDateSelection.format(c.getTime()));
		refreshStats();
	}

	public void setEndDate(Calendar c) {
		calendarEnd = c;
		mEndDateTextView.setText(dfDateSelection.format(c.getTime()));
		refreshStats();
	}

	public void getStatistics() {
		Log.i("Statistics", "GET STATS");
		ArrayList<Shift> defaultShiftList = getDefaultShiftList();
		mShiftAbbreviation = new Hashtable<String, String>();
		Calendar date = (Calendar) calendarStart.clone();
		Calendar endDate;
		Hashtable<String, Integer> currentMonthStats;
		// Database
		mDatabase = mDatabase.open();
		while (calendarEnd.compareTo(date) != -1) {

			endDate = (Calendar) date.clone();
			if (calendarEnd.get(Calendar.YEAR) == endDate.get(Calendar.YEAR)
					&& calendarEnd.get(Calendar.MONTH) == endDate
							.get(Calendar.MONTH)) {
				endDate = (Calendar) calendarEnd.clone();
			} else {
				endDate.set(Calendar.DAY_OF_MONTH,
						endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
			}
			currentMonthStats = new Hashtable<String, Integer>();
			Log.i("Statistics", "Entra while: " + df.format(date.getTime())
					+ ": " + df.format(endDate.getTime()));

			// Iterate result
			Cursor shiftCursor = mDatabase.fetchShifts(date, endDate);
			Log.i("Statistics", "Cursor size=" + shiftCursor.getCount());
			shiftCursor.moveToFirst();
			int shiftAbbrIndex = shiftCursor
					.getColumnIndex(DataBaseHelper.KEY_SHIFT_FINAL);
			int shiftNameIndex = shiftCursor
					.getColumnIndex(DataBaseHelper.KEY_NAME);
			int shiftHoursIndex = shiftCursor
					.getColumnIndex(DataBaseHelper.KEY_HOURS);

			while (!shiftCursor.isAfterLast()) {
				String shift = shiftCursor.getString(shiftAbbrIndex);
				// Month stats
				Integer shiftNumber = currentMonthStats.get(shift);
				if (shiftNumber != null) {
					currentMonthStats.put(shift, shiftNumber + 1);
				} else {
					currentMonthStats.put(shift, 1);
				}
				// Total stats
				Integer shiftNumberTotal = totalStats.get(shift);
				if (shiftNumberTotal != null) {
					totalStats.put(shift, shiftNumberTotal + 1);
				} else {
					totalStats.put(shift, 1);
				}
				float hours = shiftCursor.getFloat(shiftHoursIndex);
				Log.i("Statistics", "hours=" + hours);
				totalShiftHours += hours;
				// Abbreviation
				mShiftAbbreviation.put(shift,
						shiftCursor.getString(shiftNameIndex));
				shiftCursor.moveToNext();
			}
			monthStats.add(currentMonthStats);
			shiftCursor.close();
			// Include shift even if there aren't any shift set in the current
			// month

			for (Shift shift : defaultShiftList) {
				// Month stats
				if (currentMonthStats.get(shift.getAbbreviation()) == null) {
					currentMonthStats.put(shift.getAbbreviation(), 0);
				}
				// Total stats
				if (totalStats.get(shift.getAbbreviation()) == null) {
					totalStats.put(shift.getAbbreviation(), 0);
				}
				// Abbreviation
				mShiftAbbreviation
						.put(shift.getAbbreviation(), shift.getName());
			}

			// Overtime
			Cursor overtimeCursor = mDatabase.fetchOvertime(date, endDate);
			overtimeCursor.moveToFirst();
			int overtimeHoursIndex = overtimeCursor
					.getColumnIndex(DataBaseHelper.KEY_MINUTES);
			long overtimeAux = 0;
			while (!overtimeCursor.isAfterLast()) {
				long overtime = overtimeCursor.getLong(overtimeHoursIndex);
				overtimeAux += overtime;
				overtimeCursor.moveToNext();
			}
			// Month
			monthOvertime.add(overtimeAux);
			// Year
			totalOvertime += overtimeAux;
			overtimeCursor.close();

			// End
			date = (Calendar) endDate.clone();
			date.add(Calendar.DAY_OF_MONTH, 1);

		}
		// End
		mDatabase.close();
	}

	public void populateTotalStats() {
		// Total hours
		TextView tv = (TextView) findViewById(R.id.num_total);
		tv.setText(Helper.formatInterval(totalShiftHours));
		// Total overtime
		TextView overtimeTV = (TextView) findViewById(R.id.statistics_overtime);
		overtimeTV.setText(Helper.formatInterval(totalOvertime));
		// Total hours + overtime
		TextView totalTV = (TextView) findViewById(R.id.statistics_total_and_overtime);
		totalTV.setText(Helper
				.formatInterval((totalOvertime / 60f + totalShiftHours)));
		// Shift numbers
		LinearLayout shiftLabelsContainer = (LinearLayout) findViewById(R.id.stats_shift_label_container);
		shiftLabelsContainer.removeAllViews();
		LinearLayout shiftNumContainer = (LinearLayout) findViewById(R.id.stats_shift_num_container);
		shiftNumContainer.removeAllViews();
		Enumeration<String> shiftAbbreviations = totalStats.keys();
		while (shiftAbbreviations.hasMoreElements()) {
			String shiftAbbr = shiftAbbreviations.nextElement();
			String shiftName = mShiftAbbreviation.get(shiftAbbr);
			String shiftNumber = totalStats.get(shiftAbbr).toString();
			TextView tvShiftName = new TextView(this);
			TextView tvShiftNumber = new TextView(this);
			tvShiftName.setText(shiftName);
			tvShiftNumber.setText(shiftNumber);
			shiftLabelsContainer.addView(tvShiftName);
			shiftNumContainer.addView(tvShiftNumber);
		}
	}

	public void populateMonthStats() {
		// Details table
		TableLayout table = (TableLayout) findViewById(R.id.tableshiftcustom);
		table.removeAllViews();
		// First row (abbreviations)
		TableRow firstRow = new TableRow(this);
		Enumeration<String> shiftAbbEnumeration = mShiftAbbreviation.keys();
		firstRow.addView(new TextView(this));
		for (String abbr : Collections.list(shiftAbbEnumeration)) {
			TextView shiftTV = new TextView(this);
			shiftTV.setText(abbr);
			firstRow.addView(shiftTV);
		}
		table.addView(firstRow);
		// Overtime abbreviation
		if (monthStats.size() != 0) {
			TextView overtimeAbbreviation = new TextView(this);
			overtimeAbbreviation
					.setText(getString(R.string.overtime_abbreviation));
			firstRow.addView(overtimeAbbreviation);
		}

		// Remaining rows (shift numbers)
		Calendar date = (Calendar) calendarStart.clone();
		int overtimeListIndex = 0;

		for (Hashtable<String, Integer> monthShifts : monthStats) {
			TableRow row = new TableRow(this);
			// Date
			TextView dateTV = new TextView(this);
			row.addView(dateTV);
			dateTV.setText(dfRow.format(date.getTime()));
			// Shifts
			Enumeration<String> shiftAbbreviations = totalStats.keys();
			while (shiftAbbreviations.hasMoreElements()) {
				String abbr = shiftAbbreviations.nextElement();
				TextView shiftTV = new TextView(this);
				String shiftNum = monthShifts.get(abbr) == null ? "0"
						: monthShifts.get(abbr).toString();
				shiftTV.setText(shiftNum);
				row.addView(shiftTV);
			}

			// Overtime
			TextView overtimeHoursTV = new TextView(this);
			overtimeHoursTV.setText(Helper.formatInterval(monthOvertime
					.get(overtimeListIndex++)));
			row.addView(overtimeHoursTV);
			// End for
			table.addView(row);
			date.add(Calendar.MONTH, 1);
		}

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putSerializable("calendarStart", calendarStart);
		savedInstanceState.putSerializable("calendarEnd", calendarEnd);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Calendar startDate = (Calendar) savedInstanceState
				.getSerializable("calendarStart");
		Calendar endDate = (Calendar) savedInstanceState
				.getSerializable("calendarEnd");
		if (startDate != null) {
			calendarStart = startDate;
			mStartDateTextView.setText(dfDateSelection.format(calendarStart
					.getTime()));

		}
		if (endDate != null) {
			calendarEnd = endDate;
			mEndDateTextView.setText(dfDateSelection.format(calendarEnd
					.getTime()));
		}
		refreshStats();
	}

	private ArrayList<Shift> getDefaultShiftList() {
		// Create default shifts if preferences are empty
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String mCustomShifts = prefs.getString("shifts", "");
		return Shift.parse(mCustomShifts);
	}

}
