package com.jnsapps.workshiftcalendar.fragment;

import java.util.Calendar;

import com.jnsapps.workshiftcalendar.ShiftPatternActivity;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import android.app.Dialog;

/**
 * @author Joaquín Navarro Salmerón
 *
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

	private int type;
	
	public DatePickerFragment(){}
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		//Get type
		Bundle args = getArguments();
		type = args.getInt("type");
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, monthOfYear);
		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		ShiftPatternActivity activity = (ShiftPatternActivity) getActivity();
		if(type == 0){
			activity.setStartDate(c);
		}else{
			activity.setEndDate(c);
		}
		
	}

}
