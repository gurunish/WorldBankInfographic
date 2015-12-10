package com.dandelion.worldbankinfographic;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    MainActivity mainActivity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void testActivityExists() {
        mainActivity = getActivity();
        assertNotNull(mainActivity);
    }
}