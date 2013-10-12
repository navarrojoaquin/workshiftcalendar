package com.jnsapps.workshiftcalendar;

import java.util.Calendar;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockActivity;
import com.jnsapps.workshiftcalendar.db.WorkTableDbAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Joaquín Navarro Salmerón
 * 
 */
public class NoteActivity extends SherlockActivity {

	protected final static String EXTRA_MONTH_ID = "NOTE_MONTH";
	protected final static String EXTRA_YEAR_ID = "NOTE_YEAR";

	private WorkTableDbAdapter mDatabase;

	private int month, year;
	private Calendar calendar = Calendar.getInstance();

	private EditText mEditNote;
	private TextView mNoteDateText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_edit);

		month = (savedInstanceState == null) ? -1 : savedInstanceState
				.getInt(EXTRA_MONTH_ID);
		year = (savedInstanceState == null) ? -1 : savedInstanceState
				.getInt(EXTRA_YEAR_ID);
		if (month == -1 || year == -1) {
			Bundle extras = getIntent().getExtras();
			month = (extras == null) ? -1 : extras.getInt(EXTRA_MONTH_ID);
			year = (extras == null) ? -1 : extras.getInt(EXTRA_YEAR_ID);
			if (month == -1 || year == -1) {
				finish();
			}
		}

		mEditNote = (EditText) findViewById(R.id.note_body);
		mNoteDateText = (TextView) findViewById(R.id.note_date);

		// Database
		mDatabase = new WorkTableDbAdapter(this);

		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		populateNote();

		// Button listener registration
		Button save = (Button) findViewById(R.id.save_button);
		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				saveNote();
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putInt(EXTRA_MONTH_ID, month);
		savedInstanceState.putInt(EXTRA_YEAR_ID, year);
		super.onSaveInstanceState(savedInstanceState);
	}

	private void populateNote() {
		String monthString = String
				.format(Locale.getDefault(), "%tB", calendar);
		mNoteDateText.setText(" " + monthString + " "
				+ calendar.get(Calendar.YEAR));
		mDatabase = mDatabase.open();
		String note = mDatabase.getNote(calendar);
		if (note != null) {
			mEditNote.setText(note);
		}
		mDatabase.close();
	}

	private void saveNote() {
		mDatabase = mDatabase.open();
		mDatabase.setNote(calendar, mEditNote.getText().toString());
		Toast.makeText(this, getString(R.string.saved_note), Toast.LENGTH_SHORT)
				.show();
		mDatabase.close();

	}

}
