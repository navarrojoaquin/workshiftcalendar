package com.jnsapps.workshiftcalendar;

import java.util.Calendar;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.exina.android.calendar.Cell;
import com.exina.android.calendar.MonthYearTitle;
import com.exina.android.calendar.WeekTitle;

import afzkl.development.mColorPicker.ColorPickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * @author Joaquín Navarro Salmerón
 * 
 */
public class Preferences extends SherlockPreferenceActivity {

	public static final int PREFERENCES_CHANGED = 10;
	private int initialColor = Color.BLACK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		// Click events
		Preference monthBackgroundColor = (Preference) findPreference("monthBackgroundColor");
		monthBackgroundColor
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						final SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(Preferences.this);
						final String key = preference.getKey();
						initialColor = prefs.getInt(key,
								MonthYearTitle.DEFAULT_BACKGROUND_COLOR);
						final ColorPickerDialog d = new ColorPickerDialog(
								Preferences.this, initialColor);
						d.setAlphaSliderVisible(false);
						d.setButton2(getString(R.string.cancel),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});
						d.setButton(getString(R.string.ok),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										SharedPreferences.Editor editor = prefs
												.edit();
										editor.putInt(key, d.getColor());
										editor.commit();
										if (initialColor != d.getColor()) {
											setResult(Preferences.PREFERENCES_CHANGED);
										}
									}
								});
						d.show();
						return true;
					}
				});
		Preference monthTextColor = (Preference) findPreference("monthTextColor");
		monthTextColor
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						final SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(Preferences.this);
						final String key = preference.getKey();
						initialColor = prefs.getInt(key,
								MonthYearTitle.DEFAULT_TEXT_COLOR);
						final ColorPickerDialog d = new ColorPickerDialog(
								Preferences.this, initialColor);
						d.setAlphaSliderVisible(false);
						d.setButton2(getString(R.string.cancel),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});
						d.setButton(getString(R.string.ok),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										SharedPreferences.Editor editor = prefs
												.edit();
										editor.putInt(key, d.getColor());
										editor.commit();
										if (initialColor != d.getColor()) {
											setResult(Preferences.PREFERENCES_CHANGED);
										}
									}
								});
						d.show();
						return true;
					}
				});
		Preference weekBackgroundColor = (Preference) findPreference("weekBackgroundColor");
		weekBackgroundColor
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						final SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(Preferences.this);
						final String key = preference.getKey();
						initialColor = prefs.getInt(key,
								WeekTitle.DEFAULT_BACKGROUND_COLOR);
						final ColorPickerDialog d = new ColorPickerDialog(
								Preferences.this, initialColor);
						d.setAlphaSliderVisible(false);
						d.setButton2(getString(R.string.cancel),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});
						d.setButton(getString(R.string.ok),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										SharedPreferences.Editor editor = prefs
												.edit();
										editor.putInt(key, d.getColor());
										editor.commit();
										if (initialColor != d.getColor()) {
											setResult(Preferences.PREFERENCES_CHANGED);
										}
									}
								});
						d.show();
						return true;
					}
				});
		Preference weekTextColor = (Preference) findPreference("weekTextColor");
		weekTextColor
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						final SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(Preferences.this);
						final String key = preference.getKey();
						initialColor = prefs.getInt(key,
								WeekTitle.DEFAULT_TEXT_COLOR);
						final ColorPickerDialog d = new ColorPickerDialog(
								Preferences.this, initialColor);
						d.setAlphaSliderVisible(false);
						d.setButton2(getString(R.string.cancel),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});
						d.setButton(getString(R.string.ok),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										SharedPreferences.Editor editor = prefs
												.edit();
										editor.putInt(key, d.getColor());
										editor.commit();
										if (initialColor != d.getColor()) {
											setResult(Preferences.PREFERENCES_CHANGED);
										}
									}
								});
						d.show();
						return true;
					}
				});

		Preference todayColor = (Preference) findPreference("todayColor");
		todayColor
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						final SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(Preferences.this);
						final String key = preference.getKey();
						initialColor = prefs.getInt(key,
								Cell.DEFAULT_TODAY_COLOR);
						final ColorPickerDialog d = new ColorPickerDialog(
								Preferences.this, initialColor);
						d.setAlphaSliderVisible(false);
						d.setButton2(getString(R.string.cancel),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});
						d.setButton(getString(R.string.ok),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										SharedPreferences.Editor editor = prefs
												.edit();
										editor.putInt(key, d.getColor());
										editor.commit();
										if (initialColor != d.getColor()) {
											setResult(Preferences.PREFERENCES_CHANGED);
										}
									}
								});
						d.show();
						return true;
					}
				});

		Preference overtimeColor = (Preference) findPreference("overtimeColor");
		overtimeColor
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						final SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(Preferences.this);
						final String key = preference.getKey();
						initialColor = prefs.getInt(key,
								Cell.DEFAULT_OVERTIME_COLOR);
						final ColorPickerDialog d = new ColorPickerDialog(
								Preferences.this, initialColor);
						d.setAlphaSliderVisible(false);
						d.setButton2(getString(R.string.cancel),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});
						d.setButton(getString(R.string.ok),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										SharedPreferences.Editor editor = prefs
												.edit();
										editor.putInt(key, d.getColor());
										editor.commit();
										if (initialColor != d.getColor()) {
											setResult(Preferences.PREFERENCES_CHANGED);
										}
									}
								});
						d.show();
						return true;
					}
				});

		Preference dailyNotesColor = (Preference) findPreference("dailyNotesColor");
		dailyNotesColor
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						final SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(Preferences.this);
						final String key = preference.getKey();
						initialColor = prefs.getInt(key,
								Cell.DEFAULT_DAILY_NOTES_COLOR);
						final ColorPickerDialog d = new ColorPickerDialog(
								Preferences.this, initialColor);
						d.setAlphaSliderVisible(false);
						d.setButton2(getString(R.string.cancel),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});
						d.setButton(getString(R.string.ok),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										SharedPreferences.Editor editor = prefs
												.edit();
										editor.putInt(key, d.getColor());
										editor.commit();
										if (initialColor != d.getColor()) {
											setResult(Preferences.PREFERENCES_CHANGED);
										}
									}
								});
						d.show();
						return true;
					}
				});

		Preference reset = (Preference) findPreference("reset");
		reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(Preferences.this);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putInt("monthBackgroundColor",
						MonthYearTitle.DEFAULT_BACKGROUND_COLOR);
				editor.putInt("monthTextColor",
						MonthYearTitle.DEFAULT_TEXT_COLOR);
				editor.putInt("weekBackgroundColor",
						WeekTitle.DEFAULT_BACKGROUND_COLOR);
				editor.putInt("weekTextColor", WeekTitle.DEFAULT_TEXT_COLOR);
				editor.putInt("todayColor", Cell.DEFAULT_TODAY_COLOR);
				editor.putInt("overtimeColor", Cell.DEFAULT_OVERTIME_COLOR);
				editor.putInt("dailyNotesColor", Cell.DEFAULT_DAILY_NOTES_COLOR);
				editor.putString("firstDayOfWeek", Integer.toString(Calendar
						.getInstance().getFirstDayOfWeek()));
				editor.commit();
				Toast.makeText(Preferences.this, R.string.prefs_reset_done,
						Toast.LENGTH_SHORT).show();
				setResult(Preferences.PREFERENCES_CHANGED);
				return true;
			}
		});

		ListPreference list = (ListPreference) findPreference("firstDayOfWeek");
		list.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				setResult(Preferences.PREFERENCES_CHANGED);
				return true;
			}
		});
	}

}
