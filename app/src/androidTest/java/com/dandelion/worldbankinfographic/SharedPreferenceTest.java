package com.dandelion.worldbankinfographic;

import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;

public class SharedPreferenceTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public SharedPreferenceTest() {
        super(MainActivity.class);
    }

    String DATA = "DATA_CACHE";
    String testString = "testData";
    String testKey = "Albania";

    SharedPreferences testPref;
    SharedPreferences.Editor editor;

    public void testSaveAndRead(){
        MainActivity mainActivity = getActivity();
        testPref = mainActivity.getSharedPreferences(DATA,0);
        editor = testPref.edit();
        editor.putString(testKey, testString);
        editor.commit();

        String retrieve = testPref.getString(testKey,"");
        assertEquals(retrieve, testString);
        editor.clear();
        editor.commit();
    }
}