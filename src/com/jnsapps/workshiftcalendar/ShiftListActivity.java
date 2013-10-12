package com.jnsapps.workshiftcalendar;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jnsapps.workshiftcalendar.model.Shift;

import afzkl.development.mColorPicker.ColorPickerDialog;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Joaquín Navarro Salmerón
 * 
 */
public class ShiftListActivity extends SherlockListActivity {

	private static final int DIALOG_NEW_SHIFT = 0;
	private AlertDialog dialog;
	private TextView mEmptyListText;
	private TextView mSampleTextView;

	private int mSelectedColor = 0xff151515;

	private Shift editedShift = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shift_list);
		// Empty text
		mEmptyListText = (TextView) findViewById(R.id.empty_shift_list_text);
		// Load shift list
		loadList();
		registerForContextMenu(getListView());
	}

	private void loadList() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		String mCustomShifts = prefs.getString("shifts", "");
		if (mCustomShifts.equals("")) {
			mEmptyListText.setVisibility(View.VISIBLE);
		} else {
			setListAdapter(new ShiftAdapter(this, R.layout.shift_list_item,
					Shift.parse(mCustomShifts)));
		}
	}

	private class ShiftAdapter extends ArrayAdapter<Shift> {

		private ArrayList<Shift> items;

		public ShiftAdapter(Context context, int textViewResourceId,
				ArrayList<Shift> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.shift_list_item, null);
			}
			Shift s = items.get(position);
			if (s != null) {
				TextView nt = (TextView) v
						.findViewById(R.id.shift_item_name_text);
				TextView at = (TextView) v
						.findViewById(R.id.shift_item_abbreviation_text);
				TextView ht = (TextView) v
						.findViewById(R.id.shift_item_hours_text);

				nt.setText(s.getName());
				at.setText(s.getAbbreviation());
				at.setTextColor(s.getColor());
				ht.setText(Helper.formatInterval(s.getHours()));

			}
			return v;
		}
	}

	// Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_shift_list, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_shift_menu_item:
			showDialog(DIALOG_NEW_SHIFT);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_NEW_SHIFT:
			AlertDialog.Builder builder;
			LayoutInflater inflater = LayoutInflater.from(this);
			View layout = inflater.inflate(R.layout.dialog_new_shift, null);
			Button colorPickButton = (Button) layout
					.findViewById(R.id.color_pick_button);
			mSampleTextView = (TextView) layout
					.findViewById(R.id.color_sample_text_view);
			colorPickButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					openColorPick();
				}
			});
			builder = new AlertDialog.Builder(this);
			builder.setView(layout);
			builder.setTitle(getString(R.string.add_shift_label));
			builder.setPositiveButton(getString(R.string.save_button_text),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface,
								int id) {
							if (editedShift == null) {
								saveShift(dialog);
							} else {
								editShift(dialog);
							}

						}
					});
			builder.setNegativeButton(getString(R.string.cancel_button_text),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface,
								int id) {
							dialoginterface.cancel();
						}
					});
			dialog = builder.create();
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					editedShift = null;
				}
			});
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case DIALOG_NEW_SHIFT:
			// Populate fields if editing
			EditText nameEditText = (EditText) dialog
					.findViewById(R.id.name_edit_text);
			EditText abbreviationEditText = (EditText) dialog
					.findViewById(R.id.abbreviation_edit_text);
			EditText hoursEditText = (EditText) dialog
					.findViewById(R.id.hours_edit_text);
			EditText minutesEditText = (EditText) dialog
					.findViewById(R.id.minutes_edit_text);
			if (editedShift != null) {
				nameEditText.setText(editedShift.getName());
				abbreviationEditText.setText(editedShift.getAbbreviation());
				hoursEditText.setText(""
						+ Helper.getCompleteHours(editedShift.getHours()));
				minutesEditText.setText(""
						+ Helper.getMinutesLeft(editedShift.getHours()));
				mSelectedColor = editedShift.getColor();
			} else {
				nameEditText.setText("");
				abbreviationEditText.setText("");
				hoursEditText.setText("");
				mSelectedColor = 0xff151515;
				nameEditText.requestFocus();
			}
			mSampleTextView.setTextColor(mSelectedColor);
			// End populate
			break;
		}
	}

	private void openColorPick() {
		final ColorPickerDialog d = new ColorPickerDialog(this, mSelectedColor);
		d.setAlphaSliderVisible(false);
		d.setButton(getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mSampleTextView.setTextColor(d.getColor());
						mSelectedColor = d.getColor();
					}
				});

		d.setButton2(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		d.show();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		android.view.MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.shift_list_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			return false;
		}
		long id = getListAdapter().getItemId(info.position);
		ShiftAdapter adapter = (ShiftAdapter) getListAdapter();
		Shift s = adapter.getItem((int) id);
		removeShift(s);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		ShiftAdapter adapter = (ShiftAdapter) getListAdapter();
		Shift s = adapter.getItem(position);
		editedShift = s;
		showDialog(DIALOG_NEW_SHIFT);
	}

	private void saveShift(Dialog d) {
		EditText nameEditText = (EditText) d.findViewById(R.id.name_edit_text);
		EditText abbreviationEditText = (EditText) d
				.findViewById(R.id.abbreviation_edit_text);
		EditText hoursEditText = (EditText) d
				.findViewById(R.id.hours_edit_text);
		EditText minutesEditText = (EditText) dialog
				.findViewById(R.id.minutes_edit_text);
		String name = nameEditText.getText().toString();
		String abbreviation = abbreviationEditText.getText().toString();
		String hours = hoursEditText.getText().toString();
		String minutes = minutesEditText.getText().toString();
		if (name.equals("") || abbreviation.equals("")
				|| (hours.equals("") && minutes.equals(""))) {
			Toast.makeText(getApplicationContext(),
					R.string.error_incomplete_form, Toast.LENGTH_SHORT).show();
		} else if (name.contains(":") || name.contains(";")
				|| abbreviation.contains(":") || abbreviation.contains(";")) {
			Toast.makeText(getApplicationContext(),
					R.string.error_no_valid_characters, Toast.LENGTH_SHORT)
					.show();

		} else {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext());
			String customShifts = prefs.getString("shifts", "");
			// Avoid duplicates
			ArrayList<Shift> previousShifts = Shift.parse(customShifts);
			for (Shift shift : previousShifts) {
				if (shift.getName().equals(name)
						|| shift.getAbbreviation().equals(abbreviation)) {
					Toast.makeText(getApplicationContext(),
							R.string.shift_duplicated, Toast.LENGTH_SHORT)
							.show();
					return;
				}
			}
			// Avoid duplicates end
			SharedPreferences.Editor editor1 = prefs.edit();
			hours = hours.equals("") ? "0" : hours;
			minutes = minutes.equals("") ? "0" : minutes;
			float totalHours = Float.valueOf(hours) + Float.valueOf(minutes)
					/ 60f;
			String newShifts = customShifts + name + ":" + abbreviation + ":"
					+ mSelectedColor + ":"
					+ Float.valueOf(totalHours).toString() + ";";
			editor1.putString("shifts", newShifts);
			editor1.commit();
			loadList();
			Toast.makeText(getApplicationContext(), R.string.shift_saved,
					Toast.LENGTH_SHORT).show();
		}

	}

	private void editShift(Dialog d) {
		String oldShift = editedShift.toString();

		EditText nameEditText = (EditText) d.findViewById(R.id.name_edit_text);
		EditText abbreviationEditText = (EditText) d
				.findViewById(R.id.abbreviation_edit_text);
		EditText hoursEditText = (EditText) d
				.findViewById(R.id.hours_edit_text);
		EditText minutesEditText = (EditText) dialog
				.findViewById(R.id.minutes_edit_text);
		String name = nameEditText.getText().toString();
		String abbreviation = abbreviationEditText.getText().toString();
		String hours = hoursEditText.getText().toString();
		String minutes = minutesEditText.getText().toString();
		if (name.equals("") || abbreviation.equals("")
				|| (hours.equals("") && minutes.equals(""))) {
			Toast.makeText(getApplicationContext(),
					R.string.error_incomplete_form, Toast.LENGTH_SHORT).show();
		} else if (name.contains(":") || name.contains(";")
				|| abbreviation.contains(":") || abbreviation.contains(";")) {
			Toast.makeText(getApplicationContext(),
					R.string.error_no_valid_characters, Toast.LENGTH_SHORT)
					.show();

		} else {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext());
			String customShifts = prefs.getString("shifts", "");
			// Avoid duplicates end
			Shift newShiftObj = new Shift();
			newShiftObj.setName(name);
			newShiftObj.setAbbreviation(abbreviation);
			// Compute hours
			hours = hours.equals("") ? "0" : hours;
			minutes = minutes.equals("") ? "0" : minutes;
			float totalHours = Float.valueOf(hours) + Float.valueOf(minutes)
					/ 60f;
			// Set hours
			newShiftObj.setHours(Float.valueOf(totalHours));
			newShiftObj.setColor(mSelectedColor);
			SharedPreferences.Editor editor1 = prefs.edit();
			String newShift = newShiftObj.toString();
			String newShifts = customShifts.replace(oldShift, newShift);
			editor1.putString("shifts", newShifts);
			editor1.commit();
			loadList();
			Toast.makeText(getApplicationContext(), R.string.shift_saved,
					Toast.LENGTH_SHORT).show();
			editedShift = null;
		}
	}

	private void removeShift(Shift s) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		String customShifts = prefs.getString("shifts", "");
		SharedPreferences.Editor editor1 = prefs.edit();
		String newShifts = customShifts.replaceAll(s.toString(), "");
		editor1.putString("shifts", newShifts);
		editor1.commit();
		loadList();
		Toast.makeText(getApplicationContext(), R.string.shift_deleted,
				Toast.LENGTH_SHORT).show();
	}

}
