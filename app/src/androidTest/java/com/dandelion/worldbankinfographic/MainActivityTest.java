package com.dandelion.worldbankinfographic;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    int adapterCount = 50;
    int testPosition = 1;
    String testSelection = "Andorra";

    String guiSelection;
    int guiPosition;
    Spinner guiSpinner;
    SpinnerAdapter guiSpinnerAdapter;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void testActivityExists() {
        MainActivity mainActivity = getActivity();
        assertNotNull(mainActivity);
    }

    public void testSpinnerSetUp() {
        MainActivity mainActivity = getActivity();
        guiSpinner = mainActivity.spinnerCountry;
        guiSpinnerAdapter = guiSpinner.getAdapter();

        assertTrue(guiSpinner.getOnItemSelectedListener() != null);
        assertTrue(guiSpinnerAdapter != null);
        assertEquals(guiSpinnerAdapter.getCount(), adapterCount);
    }

    public void testSpinnerSelection(){
        MainActivity mainActivity = getActivity();
        guiSpinner = mainActivity.spinnerCountry;

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
        //TODO: Test guiSelection == "Andorra"
    }
}