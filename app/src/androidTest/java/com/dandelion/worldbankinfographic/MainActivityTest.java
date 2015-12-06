package com.dandelion.worldbankinfographic;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    int adapterCount = 50;
    int testPosition = 1;
    int initualPosition = 0;
    String initialSelection = "Albania";

    String guiSelection;
    String guiPosition;
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


    }
}