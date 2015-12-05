package com.dandelion.worldbankinfographic;

import android.test.ActivityInstrumentationTestCase2;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void testActivityExists() {
        MainActivity mainActivity = getActivity();
        assertNotNull(mainActivity);
    }
}