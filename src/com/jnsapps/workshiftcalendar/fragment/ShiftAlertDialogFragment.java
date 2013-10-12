package com.jnsapps.workshiftcalendar.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * @author Joaquín Navarro Salmerón
 *
 */
public class ShiftAlertDialogFragment extends DialogFragment {
	
	public ShiftAlertDialogFragment(){}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        CharSequence[] items = getArguments().getCharSequenceArray("items");        

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ((ShiftPatternListFragment)getTargetFragment()).addToPattern(whichButton);
                    }
                })
                .create();
    }
    
    
}
