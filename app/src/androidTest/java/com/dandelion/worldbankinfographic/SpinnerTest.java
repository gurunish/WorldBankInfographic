package com.dandelion.worldbankinfographic;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by Nish on 05/12/2015.
 */
public class SpinnerTest extends ActivityInstrumentationTestCase2<MainActivity> {
    MainActivity mainActivity;
    int selectionPosition;


    public SpinnerTest() {
        super(MainActivity.class);
        mainActivity = getActivity();
    }

    public void testSpinnerIntialisation() {
        final Spinner spinner = (Spinner)mainActivity.findViewById(R.id.countrySpinner);
        ArrayAdapter<CharSequence> adapterCountry = ArrayAdapter.createFromResource(this.getActivity().getApplicationContext(), R.array.Countries, android.R.layout.simple_spinner_item);
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterCountry);

        String[] countries = {"Albania","Andorra","Armenia","Austria","Azerbaijan",
                "Belarus","Belgium","Bosnia & Herzegovina","Bulgaria","Croatia","Cyprus","Czech Republic",
                "Denmark","Estonia","Faroe Islands","Finland","France","Georgia","Germany","Gibraltar","Greece",
                "Hungary","Iceland","Ireland","Israel","Italy","Kazakhstan","Latvia","Liechtenstein","Lithuania",
                "Luxembourg","Macedonia","Malta","Moldova","Monaco","Netherlands","Norway","Poland","Portugal",
                "Romania","Russia","San Marino","Slovakia","Slovenia","Spain","Sweden","Switzerland","Turkey",
                "Ukraine","United Kingdom"};

        selectionPosition = 0;
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                spinner.requestFocus();
                spinner.setSelection(selectionPosition);
            }
        });
        getInstrumentation().waitForIdleSync();

        String spinnerSelection = (String)spinner.getItemAtPosition(selectionPosition);
        assertEquals(countries[selectionPosition], spinnerSelection);
    }
}
