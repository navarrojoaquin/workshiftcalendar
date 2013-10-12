package com.exina.android.calendar;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

/**
 * @author Joaquín Navarro Salmerón
 * 
 */
public class WeekTitle {

	private Rect mBound;
	private Paint mPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG
			| Paint.ANTI_ALIAS_FLAG);
	private Paint mPaintBackground = new Paint();

	private String[] completeStringDays = new DateFormatSymbols().getWeekdays();
	private Calendar calendarAux = Calendar.getInstance();
	private String[] days = new String[7];
	private String mReferenceText = "";

	public static int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#5858FA");
	public static int DEFAULT_TEXT_COLOR = Color.WHITE;

	public WeekTitle(Rect bound, float textSize, int backgroundColor,
			int textColor, int firstDayOfWeek) {
		mBound = bound;
		mPaint.setTextSize(textSize);
		mPaint.setColor(textColor);
		mPaintBackground.setStyle(Style.FILL);
		mPaintBackground.setColor(backgroundColor);
		// Set days array and mReferenceText
		calendarAux.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
		for (int i = 0; i < days.length; i++) {
			int index = calendarAux.get(Calendar.DAY_OF_WEEK);
			String day = completeStringDays[index];
			String dayAbbreviation;
			if (day.length() > 3) {
				dayAbbreviation = day.substring(0, 3) + ".";
			} else {
				dayAbbreviation = day;
			}
			days[i] = dayAbbreviation;
			mReferenceText += dayAbbreviation;
			if (i < 6) {
				mReferenceText += " ";
			}
			calendarAux.add(Calendar.DAY_OF_WEEK, 1);
		}

	}

	protected void draw(Canvas canvas) {
		// Background
		canvas.drawRect(mBound, mPaintBackground);
		// Text
		float separation = mBound.width() / 7f;
		float offset = separation / 2;
		Rect textBound = new Rect();
		mPaint.getTextBounds(mReferenceText, 0, mReferenceText.length(),
				textBound);
		float textSize;
		for (int i = 0; i < days.length; i++) {
			textSize = mPaint.measureText(days[i]);
			canvas.drawText(days[i], (offset + separation * i) - textSize / 2,
					mBound.centerY() + textBound.height() / 2f, mPaint);
		}
	}

}
