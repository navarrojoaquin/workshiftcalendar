package com.exina.android.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import com.jnsapps.workshiftcalendar.Helper;
import com.jnsapps.workshiftcalendar.R;
import com.jnsapps.workshiftcalendar.db.DataBaseHelper;
import com.jnsapps.workshiftcalendar.db.WorkTableDbAdapter;
import com.jnsapps.workshiftcalendar.model.Shift;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.preference.PreferenceManager;

/**
 * @author Joaquín Navarro Salmerón
 * 
 */
public class Statistics {
	protected Rect mBound = null;
	protected Paint mPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG
			| Paint.ANTI_ALIAS_FLAG);
	// Database
	private WorkTableDbAdapter mDatabase;

	private Context mContext;
	private CalendarView mCalendarView;

	private Hashtable<String, Integer> mMonthlyStats;
	private long mOvertimeMinutes = 0;
	private float mTotalHours;

	private float lineHeight;
	private int totalHeight = 0;

	public Statistics(Context context, CalendarView cv) {
		mContext = context;
		mCalendarView = cv;
		float textsize = context.getResources().getDimensionPixelSize(
				R.dimen.statistics_text_size);
		mPaint.setTextSize(textsize);
		mPaint.setColor(Color.LTGRAY);
		lineHeight = (-mPaint.ascent() + mPaint.descent()) / 2f * 2f;
		getStatistics(mCalendarView);
		getPredictedHeight();
	}

	protected void setBounds(Rect rect) {
		mBound = rect;
	}

	protected int getPredictedHeight() {
		mDatabase = mDatabase.open();
		Cursor shiftsNameCursor = mDatabase.fetchShiftNames();

		int totalShiftNames = shiftsNameCursor.getCount()
				+ mMonthlyStats.size();
		shiftsNameCursor.moveToFirst();
		while (!shiftsNameCursor.isAfterLast()) {
			String shiftName = shiftsNameCursor.getString(0);
			if (mMonthlyStats.containsKey(shiftName))
				totalShiftNames--;
			shiftsNameCursor.moveToNext();
		}
		shiftsNameCursor.close();
		mDatabase.close();
		// +1 (margin)
		int shiftsColumnHeigth = (int) (lineHeight * (totalShiftNames + 1));
		// +7 (6 lines + 1 margin)
		int hoursColumnHeight = (int) (lineHeight * 7);
		totalHeight = Math.max(shiftsColumnHeigth, hoursColumnHeight);
		return totalHeight;
	}

	protected void draw(Canvas canvas) {
		drawStatistics(canvas);
	}

	protected void getStatistics(CalendarView cv) {
		// Database
		mDatabase = new WorkTableDbAdapter(mContext);
		mDatabase = mDatabase.open();
		// Get month data
		Calendar startDate = Calendar.getInstance();
		startDate.set(Calendar.YEAR, cv.getYear());
		startDate.set(Calendar.MONTH, cv.getMonth());
		startDate.set(Calendar.DAY_OF_MONTH, 1);

		Calendar endDate = Calendar.getInstance();
		endDate.set(Calendar.YEAR, cv.getYear());
		endDate.set(Calendar.MONTH, cv.getMonth());
		endDate.set(Calendar.DAY_OF_MONTH, cv.mHelper.getNumberOfDaysInMonth());
		// Iterate result
		Cursor shiftCursor = mDatabase.fetchShifts(startDate, endDate);
		shiftCursor.moveToFirst();
		int shiftFinalIndex = shiftCursor
				.getColumnIndex(DataBaseHelper.KEY_NAME);
		int shiftHoursIndex = shiftCursor
				.getColumnIndex(DataBaseHelper.KEY_HOURS);
		mMonthlyStats = new Hashtable<String, Integer>();
		mTotalHours = 0;
		while (!shiftCursor.isAfterLast()) {
			String shift = shiftCursor.getString(shiftFinalIndex);
			Integer shiftNumber = mMonthlyStats.get(shift);
			if (shiftNumber != null) {
				mMonthlyStats.put(shift, shiftNumber + 1);
			} else {
				mMonthlyStats.put(shift, 1);
			}
			float hours = shiftCursor.getFloat(shiftHoursIndex);
			mTotalHours += hours;
			shiftCursor.moveToNext();
		}
		shiftCursor.close();

		// Include shift even if there aren't any shift set in the current month
		for (Shift shift : getShiftList()) {
			if (mMonthlyStats.get(shift.getName()) == null) {
				mMonthlyStats.put(shift.getName(), 0);
			}
		}

		// Include overtime
		Cursor overtimeCursor = mDatabase.fetchOvertime(startDate, endDate);
		overtimeCursor.moveToFirst();
		int overtimeHoursIndex = overtimeCursor
				.getColumnIndex(DataBaseHelper.KEY_MINUTES);
		long overtimeMinutesCount = 0;
		while (!overtimeCursor.isAfterLast()) {
			overtimeMinutesCount += overtimeCursor.getFloat(overtimeHoursIndex);
			overtimeCursor.moveToNext();
		}
		mOvertimeMinutes = overtimeMinutesCount;
		overtimeCursor.close();
		// End
		mDatabase.close();
	}

	private void drawStatistics(Canvas canvas) {

		Enumeration<String> shiftNames = mMonthlyStats.keys();
		float heightPointer = mBound.top + lineHeight;
		while (shiftNames.hasMoreElements()) {
			String shiftName = (String) shiftNames.nextElement();
			canvas.drawText(shiftName, mBound.left + 10, heightPointer, mPaint);
			String shiftNumber = mMonthlyStats.get(shiftName).toString();
			canvas.drawText(shiftNumber, mBound.left + mBound.width() / 2.2f,
					heightPointer, mPaint);
			heightPointer += lineHeight;
		}
		String totalHoursLabel = mContext.getString(R.string.horas_totales);
		float linesHeight = 6 * lineHeight;
		heightPointer = mBound.top + (totalHeight - linesHeight) / 2;
		float widthOrigin = mBound.left + mBound.width() / 1.6f;
		float remainingWidth = mBound.width() - widthOrigin;
		float widthPointer = widthOrigin
				+ (remainingWidth - mPaint.measureText(totalHoursLabel)) / 2;
		canvas.drawText(totalHoursLabel, widthPointer, heightPointer, mPaint);
		String totalHoursString = Helper.formatInterval(mTotalHours);
		heightPointer += lineHeight;
		widthPointer = widthOrigin
				+ (remainingWidth - mPaint.measureText(totalHoursString)) / 2;
		canvas.drawText(totalHoursString, widthPointer, heightPointer, mPaint);
		String overtimeHoursLabel = mContext.getString(R.string.overtime_set);
		heightPointer += lineHeight;
		widthPointer = widthOrigin
				+ (remainingWidth - mPaint.measureText(overtimeHoursLabel)) / 2;
		canvas.drawText(overtimeHoursLabel, widthPointer, heightPointer, mPaint);
		String overtimeHours = Helper.formatInterval(mOvertimeMinutes);
		heightPointer += lineHeight;
		widthPointer = widthOrigin
				+ (remainingWidth - mPaint.measureText(overtimeHours)) / 2;
		canvas.drawText(overtimeHours, widthPointer, heightPointer, mPaint);

		String totalAndOvertimeHoursLabel = mContext
				.getString(R.string.total_hours);
		heightPointer += lineHeight;
		widthPointer = widthOrigin
				+ (remainingWidth - mPaint
						.measureText(totalAndOvertimeHoursLabel)) / 2;
		canvas.drawText(totalAndOvertimeHoursLabel, widthPointer,
				heightPointer, mPaint);
		String totalAndOvertimeHours = Helper.formatInterval(mTotalHours
				+ mOvertimeMinutes / 60f);
		heightPointer += lineHeight;
		widthPointer = widthOrigin
				+ (remainingWidth - mPaint.measureText(totalAndOvertimeHours))
				/ 2;
		canvas.drawText(totalAndOvertimeHours, widthPointer, heightPointer,
				mPaint);
	}

	private ArrayList<Shift> getShiftList() {
		// Create default shifts if preferences are empty
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		String mCustomShifts = prefs.getString("shifts", "");
		return Shift.parse(mCustomShifts);
	}

}
