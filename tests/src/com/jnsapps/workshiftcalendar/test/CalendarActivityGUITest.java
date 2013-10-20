package com.jnsapps.workshiftcalendar.test;

import java.util.Calendar;

import com.exina.android.calendar.CalendarView;
import com.exina.android.calendar.CustomScrollView;
import com.jayway.android.robotium.solo.Solo;
import com.jnsapps.workshiftcalendar.CalendarActivity;
import com.jnsapps.workshiftcalendar.R;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;

public class CalendarActivityGUITest extends ActivityInstrumentationTestCase2<CalendarActivity> {

	private Solo solo;
	
	private static int SCROLL_SLEEP_TIME = 2000;
	
	public CalendarActivityGUITest() {
		super(CalendarActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}
	
	public void testActivityLaunched() {
		// check that we have the right activity
		solo.assertCurrentActivity("Wrong activity", CalendarActivity.class);
	}
	
	public void testNewVersionDialog() {
		// check that the dialog is open
		assertTrue(solo.waitForDialogToOpen(2000));
		// check that the dialog is closed
		solo.clickOnButton(solo.getString(R.string.help_dialog_close));
		assertTrue(solo.waitForDialogToClose(2000));
		// reopen activity (the dialog should be opened)
		solo.finishOpenedActivities();
		Intent intent = new Intent(getInstrumentation().getTargetContext(), CalendarActivity.class); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		getInstrumentation().getTargetContext().startActivity(intent); 
		assertTrue(solo.waitForDialogToOpen(2000));
		// click don't show again
		solo.clickOnButton(solo.getString(R.string.help_dialog_close_forever));
		assertTrue(solo.waitForDialogToClose(2000));
		// reopen activity (the dialog shouldn't be opened)
		solo.finishOpenedActivities();
		getInstrumentation().getTargetContext().startActivity(intent); 
		assertFalse(solo.waitForDialogToOpen(2000));
	}
	
	public void testViewPager() {
		// ViewPager exists
		ViewPager pager = (ViewPager) solo.getView(R.id.awesomepager);
		assertNotNull(pager);
		// Current date
		Calendar cal = Calendar.getInstance();
		// Current month is shown
		CustomScrollView container = (CustomScrollView) pager.getChildAt(1);
		CalendarView currentCalendarView = (CalendarView) container.findViewById(R.id.calendarview);
		assertMonth(cal, currentCalendarView.getYear(), currentCalendarView.getMonth());
		// swipe left
		solo.scrollToSide(Solo.LEFT);
		solo.sleep(SCROLL_SLEEP_TIME);
		container = (CustomScrollView) pager.getChildAt(1);
		currentCalendarView = (CalendarView) container.findViewById(R.id.calendarview);
		cal.add(Calendar.MONTH, -1);
		assertMonth(cal, currentCalendarView.getYear(), currentCalendarView.getMonth());
		// swipe right
		solo.scrollToSide(Solo.RIGHT);
		solo.sleep(SCROLL_SLEEP_TIME);
		container = (CustomScrollView) pager.getChildAt(1);
		currentCalendarView = (CalendarView) container.findViewById(R.id.calendarview);
		cal.add(Calendar.MONTH, 1);
		assertMonth(cal, currentCalendarView.getYear(), currentCalendarView.getMonth());
		// swipe right again
		solo.scrollToSide(Solo.RIGHT);
		solo.sleep(SCROLL_SLEEP_TIME);
		container = (CustomScrollView) pager.getChildAt(1);
		currentCalendarView = (CalendarView) container.findViewById(R.id.calendarview);
		cal.add(Calendar.MONTH, 1);
		assertMonth(cal, currentCalendarView.getYear(), currentCalendarView.getMonth());
	}
	
	private void assertMonth(Calendar expected, int actualYear, int actualMonth) {
		assertEquals(expected.get(Calendar.YEAR), actualYear);
		assertEquals(expected.get(Calendar.MONTH), actualMonth);
	}

}
