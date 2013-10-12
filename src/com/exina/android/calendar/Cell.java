/*
 * Copyright (C) 2011 Chris Gao <chris@exina.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.exina.android.calendar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;

public class Cell {
	protected Rect mBound = null;
	protected int mDayOfMonth = 1; // from 1 to 31
	protected Paint mPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG
			| Paint.ANTI_ALIAS_FLAG);
	protected Paint mWorkShiftPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG
			| Paint.ANTI_ALIAS_FLAG);
	protected Paint mPreviousWorkShiftPaint = new Paint(
			Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
	protected Paint mOvertimePaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG
			| Paint.ANTI_ALIAS_FLAG);
	protected Paint mDailyNotesPaint = new Paint();
	int dx, dy, wsx, wsy;
	String mWorkShift, mPreviousWorkShift, mOvertime;
	private boolean existsDailyNote = false;

	public static final int DEFAULT_TODAY_COLOR = MonthYearTitle.DEFAULT_BACKGROUND_COLOR;
	public static final int DEFAULT_OVERTIME_COLOR = MonthYearTitle.DEFAULT_BACKGROUND_COLOR;
	public static final int DEFAULT_DAILY_NOTES_COLOR = MonthYearTitle.DEFAULT_BACKGROUND_COLOR;

	private boolean today = false;
	private float today_stoke = 3f;
	private int today_color = DEFAULT_TODAY_COLOR;

	public Cell(int dayOfMon, Rect rect, float textSize, float shiftSize,
			float prevShiftSize, boolean bold) {
		mDayOfMonth = dayOfMon;
		mBound = rect;
		mPaint.setTextSize(textSize);
		mPaint.setColor(Color.GRAY);
		if (bold)
			mPaint.setFakeBoldText(true);

		dx = (int) mPaint.measureText(String.valueOf(mDayOfMonth)) / 2;
		dy = (int) (-mPaint.ascent() + mPaint.descent()) / 2;

		// Work shift text format
		mWorkShiftPaint.setTextSize(shiftSize);
		mWorkShiftPaint.setColor(Color.BLACK);
		mWorkShift = "";
		// Previous work shift text format
		mPreviousWorkShiftPaint.setTextSize(prevShiftSize);
		mPreviousWorkShiftPaint.setColor(Color.DKGRAY);
		mPreviousWorkShift = "";
		// Overtime text format
		mOvertime = "";
		mOvertimePaint.setTextSize(prevShiftSize);
		mOvertimePaint.setColor(Color.RED);

	}

	public Cell(int dayOfMon, Rect rect, float textSize, float shiftSize,
			float prevShiftSize) {
		this(dayOfMon, rect, textSize, shiftSize, prevShiftSize, false);
	}

	protected void draw(Canvas canvas) {
		// Draw background
		Paint testborder = new Paint();
		testborder.setStyle(Style.FILL);
		testborder.setColor(Color.WHITE);
		canvas.drawRect(mBound, testborder);
		// Draw note
		if (existsDailyNote) {
			Path path = new Path();
			path.setFillType(Path.FillType.EVEN_ODD);
			path.moveTo(mBound.left, mBound.top);
			path.lineTo(mBound.left + (int) (mBound.width() * 0.15), mBound.top);
			path.lineTo(mBound.left, mBound.top + (int) (mBound.height() * 0.2));
			path.lineTo(mBound.left, mBound.top);
			path.close();
			mDailyNotesPaint.setStyle(Style.FILL);
			canvas.drawPath(path, mDailyNotesPaint);
		}
		// Draw border
		testborder.setStyle(Style.STROKE);
		if (today) {
			testborder.setColor(today_color);
			testborder.setStrokeWidth(today_stoke);
		} else {
			testborder.setColor(Color.GRAY);
		}

		canvas.drawRect(mBound, testborder);
		// Modify day text position (right-top corner)
		canvas.drawText(String.valueOf(mDayOfMonth), mBound.right - dx * 2 - 3,
				mBound.top + dy * 2, mPaint);
		if (mOvertime.length() > 0) {
			// Add overtime text
			Rect bounds = new Rect();
			mOvertimePaint.getTextBounds(mOvertime, 0, mOvertime.length(),
					bounds);
			canvas.drawText(mOvertime, mBound.left + bounds.height() / 4,
					mBound.bottom - bounds.height() / 4, mOvertimePaint);
		}
		// Add work shift text
		Rect bounds = new Rect();
		mWorkShiftPaint.getTextBounds(mWorkShift, 0, mWorkShift.length(),
				bounds);
		canvas.drawText(mWorkShift, mBound.centerX() - bounds.width() / 2,
				mBound.centerY() + bounds.height() / 2, mWorkShiftPaint);
		// Add previous work shift text
		mPreviousWorkShiftPaint.getTextBounds(mPreviousWorkShift, 0,
				mPreviousWorkShift.length(), bounds);
		canvas.drawText(mPreviousWorkShift, mBound.right - bounds.width()
				- bounds.height() / 4, mBound.bottom - bounds.height() / 4,
				mPreviousWorkShiftPaint);
	}

	public int getDayOfMonth() {
		return mDayOfMonth;
	}

	public boolean hitTest(int x, int y) {
		return mBound.contains(x, y);
	}

	public Rect getBound() {
		return mBound;
	}

	public String toString() {
		return String.valueOf(mDayOfMonth) + "(" + mBound.toString() + ")";
	}

	public void setWorkShift(String shift, int color) {
		mWorkShift = shift;
		mWorkShiftPaint.setColor(color);
	}

	public void setPreviousWorkShift(String pshift) {
		mPreviousWorkShift = pshift;
	}

	public void setOvertime(String overtime, int color) {
		mOvertime = overtime;
		mOvertimePaint.setColor(color);
	}

	public void setToday(boolean today, int color, float stroke) {
		this.today = today;
		this.today_color = color;
		this.today_stoke = stroke;
	}

	public String getShift() {
		return mWorkShift;
	}

	public String getOvertime() {
		return mOvertime;
	}

	public void setDailyNote(boolean exists, int color) {
		existsDailyNote = exists;
		mDailyNotesPaint.setColor(color);
	}

	public boolean existsDailyNote() {
		return existsDailyNote;
	}

}
