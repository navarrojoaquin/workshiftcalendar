package com.jnsapps.workshiftcalendar.fragment;

import java.util.ArrayList;
import java.util.Hashtable;

import com.jnsapps.workshiftcalendar.R;
import com.jnsapps.workshiftcalendar.model.Shift;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @author Joaquín Navarro Salmerón
 * 
 */
public class ShiftPatternListFragment extends ListFragment {
	
	private ArrayList<Shift> mShiftPattern = new ArrayList<Shift>();
	private CharSequence[] items;
	private String mCustomShifts;
	private ArrayList<Shift> mShiftList;
	private Hashtable<String, Shift> mShiftTable;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.shift_pattern_list, container);
	}

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //'Add' button
		ImageButton addShiftButton = (ImageButton) getView().findViewById(R.id.pattern_add_shift_button);
		addShiftButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showAddShiftDialog();
			}
		});
		
		refreshShiftsPreferences();
		if(savedInstanceState != null){
			ArrayList<Shift> savedPattern = (ArrayList<Shift>)savedInstanceState.getSerializable("pattern");
			if(savedPattern!=null){
				mShiftPattern = savedPattern;
			}
		}
		setListAdapter(new ShiftAdapter(getActivity(), R.layout.shift_pattern_list_item, mShiftPattern));
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("pattern", mShiftPattern);
	}

	private void refreshShiftsPreferences(){
		//Create default shifts if preferences are empty
		SharedPreferences prefs = PreferenceManager
		.getDefaultSharedPreferences(getActivity());
		mCustomShifts = prefs.getString("shifts", "");
		if(mCustomShifts.equals("")){
			SharedPreferences.Editor editor1 = prefs.edit();
			String defaultShifts = getString(R.string.default_shifts);
			editor1.putString("shifts", defaultShifts);
			editor1.commit();
			mCustomShifts = defaultShifts;
		}
		mShiftList = Shift.parse(mCustomShifts);
		mShiftTable = new Hashtable<String, Shift>();
		items = new CharSequence[mShiftList.size()];
		int i = 0;
		for (Shift s: mShiftList) {
			items[i] = s.getAbbreviation() + " (" + s.getName() + ")";
			i++;
			mShiftTable.put(s.getAbbreviation(), s);
		}		
	}
	
	public void showAddShiftDialog() {
		DialogFragment newFragment = new ShiftAlertDialogFragment();
		newFragment.setTargetFragment(this, 0);
	    Bundle args = new Bundle();
	    args.putInt("title", R.string.pattern_add_shift);
        args.putCharSequenceArray("items", items);
        
        newFragment.setArguments(args);
        FragmentManager fm = getFragmentManager();
		newFragment.show(fm, "dialog");
	}
	
	public void addToPattern(int which) {
		String abbreviation = items[which].toString().split(" \\(")[0];
		Shift s = mShiftTable.get(abbreviation);
		mShiftPattern.add(s);
		ShiftAdapter adapter = (ShiftAdapter) ShiftPatternListFragment.this.getListAdapter();
		adapter.notifyDataSetChanged();
	}
	
	public ArrayList<Shift> getPattern(){
		return mShiftPattern;
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
				LayoutInflater vi = getLayoutInflater(null);
				v = vi.inflate(R.layout.shift_pattern_list_item, null);
			}
			Shift s = items.get(position);
			if (s != null) {
				TextView nt = (TextView) v
						.findViewById(R.id.shift_item_name_text);
				TextView at = (TextView) v
						.findViewById(R.id.shift_item_abbreviation_text);
				ImageButton button = (ImageButton) v.findViewById(R.id.pattern_remove_shift_button);

				nt.setText(s.getName());
				at.setText(s.getAbbreviation());
				at.setTextColor(s.getColor());
				
				button.setOnClickListener(new OnItemClickListener(position));

			}
			return v;
		}
		
		private class OnItemClickListener implements OnClickListener{       
		    private int mPosition;
		    OnItemClickListener(int position){
		        mPosition = position;
		    }
		    @Override
		    public void onClick(View arg0) {
		        items.remove(mPosition);
		        ShiftAdapter.this.notifyDataSetChanged();
		    }       
		}
	}
	
}
