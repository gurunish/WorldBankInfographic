package com.dandelion.worldbankinfographic;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import android.test.suitebuilder.annotation.SmallTest;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    int adapterCount = 50;
    int testPosition = 1;

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
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testCountries() {
        ImageView img_Spares = (ImageView) getActivity().findViewById(R.id.img_Spares);
        assertNotNull(img_Spares);
        ImageView img_Iceland = (ImageView) getActivity().findViewById(R.id.img_Iceland);
        assertNotNull(img_Iceland);
        ImageView img_UK = (ImageView) getActivity().findViewById(R.id.img_UK);
        assertNotNull(img_UK);
        ImageView img_France = (ImageView) getActivity().findViewById(R.id.img_France);
        assertNotNull(img_France);
        ImageView img_Spain = (ImageView) getActivity().findViewById(R.id.img_Spain);
        assertNotNull(img_Spain);
        ImageView img_Finland = (ImageView) getActivity().findViewById(R.id.img_Finland);
        assertNotNull(img_Finland);
        ImageView img_Sweden = (ImageView) getActivity().findViewById(R.id.img_Sweden);
        assertNotNull(img_Sweden);
        ImageView img_Norway = (ImageView) getActivity().findViewById(R.id.img_Norway);
        assertNotNull(img_Norway);
        ImageView img_Germany = (ImageView) getActivity().findViewById(R.id.img_Germany);
        assertNotNull(img_Germany);
        ImageView img_Italy = (ImageView) getActivity().findViewById(R.id.img_Italy);
        assertNotNull(img_Italy);
        ImageView img_Poland = (ImageView) getActivity().findViewById(R.id.img_Poland);
        assertNotNull(img_Poland);
        ImageView img_Russia = (ImageView) getActivity().findViewById(R.id.img_Russia);
        assertNotNull(img_Russia);
        ImageView img_Ukraine = (ImageView) getActivity().findViewById(R.id.img_Ukraine);
        assertNotNull(img_Ukraine);
        ImageView img_Denmark = (ImageView) getActivity().findViewById(R.id.img_Denmark);
        assertNotNull(img_Denmark);
        ImageView img_Turkey = (ImageView) getActivity().findViewById(R.id.img_Turkey);
        assertNotNull(img_Turkey);
        ImageView img_Belarus = (ImageView) getActivity().findViewById(R.id.img_Belarus);
        assertNotNull(img_Belarus);
        ImageView img_Austria = (ImageView) getActivity().findViewById(R.id.img_Austria);
        assertNotNull(img_Austria);
    }


    @SmallTest
    public void testPieChart() {
        PieChart pc = (PieChart) getActivity().findViewById(R.id.pieChart);
        assertNotNull(pc);
    }

    @SmallTest
    public void testLineChart() {
        LineChart lc = (LineChart) getActivity().findViewById(R.id.lineChart);
        assertNotNull(lc);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}