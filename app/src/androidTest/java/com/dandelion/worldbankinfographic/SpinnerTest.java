package com.dandelion.worldbankinfographic;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class SpinnerTest extends ActivityInstrumentationTestCase2<MainActivity> {
    int adapterCount = 50;
    int testPosition = 1;

    int guiPosition;
    Spinner guiSpinner;
    SpinnerAdapter guiSpinnerAdapter;

    public SpinnerTest() {
        super(MainActivity.class);
    }

    public void testSpinnerSetUp() {
        MainActivity mainActivity = getActivity();
        assertNotNull(mainActivity);

        guiSpinner = (Spinner)mainActivity.findViewById(R.id.countrySpinner);
        guiSpinnerAdapter = guiSpinner.getAdapter();
        assertNotNull(guiSpinner);
        assertNotNull(guiSpinnerAdapter);
        assertEquals(guiSpinnerAdapter.getCount(), adapterCount);
    }

    public void testSpinnerSelection(){
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                guiSpinner.requestFocus();
                guiSpinner.setSelection(0);
            }
        });

        this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
        this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
        this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
        guiPosition = guiSpinner.getSelectedItemPosition();

        assertEquals(testPosition, guiPosition);
    }
}