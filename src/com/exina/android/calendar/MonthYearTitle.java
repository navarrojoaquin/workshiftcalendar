package com.exina.android.calendar;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;

/**
 * @author Joaqu�n Navarro Salmer�n
 * 
 */
public class MonthYearTitle {

	private Rect mBound;
	private Paint mPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG
			| Paint.ANTI_ALIAS_FLAG);
	private Paint mPaintBackground = new Paint();
	private String title;

	public static int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#0404B4");
	public static int DEFAULT_TEXT_COLOR = Color.WHITE;

	public MonthYearTitle(Rect bound, float textSize, int backgroundColor,
			int textColor) {
		mBound = bound;
		mPaint.setTextSize(textSize);
		mPaint.setColor(textColor);

		mPaintBackground.setStyle(Style.FILL);
		mPaintBackground.setColor(backgroundColor);
	}

	protected void draw(Canvas canvas) {
		// Background
		canvas.drawRect(mBound, mPaintBackground);
		// Text
		Rect textBound = new Rect();
		mPaint.getTextBounds(title, 0, title.length(), textBound);
		canvas.drawText(title, mBound.centerX() - textBound.width() / 2f,
				mBound.centerY() + textBound.height() / 2f, mPaint);
	}

	protected void setTitle(int month, int year) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		String monthString = getMonthForInt(month);
		this.title = monthString + " " + year;
	}

	private String getMonthForInt(int m) {
		String month = "invalid";
		DateFormatSymbols dfs = new DateFormatSymbols2();
		String[] months = dfs.getMonths();
		if (m >= 0 && m <= 11) {
			month = months[m];
		}
		return month;
	}

	private class DateFormatSymbols2 extends DateFormatSymbols {

		private static final long serialVersionUID = 1L;

		@Override
		public String[] getMonths() {
			if (Locale.getDefault().getISO3Language().equals("pol")) {
				String[] polishMonths = new String[12];
				polishMonths[0] = "Styczeń";
				polishMonths[1] = "Luty";
				polishMonths[2] = "Marzec";
				polishMonths[3] = "Kwiecień";
				polishMonths[4] = "Maj";
				polishMonths[5] = "Czerwiec";
				polishMonths[6] = "Lipiec";
				polishMonths[7] = "Sierpień";
				polishMonths[8] = "Wrzesień";
				polishMonths[9] = "Październik";
				polishMonths[10] = "Listopad";
				polishMonths[11] = "Grudzień";
				return polishMonths;
			}
			return super.getMonths();
		}

	}

}
