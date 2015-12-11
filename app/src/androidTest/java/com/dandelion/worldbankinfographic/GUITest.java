package com.dandelion.worldbankinfographic;

import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;

public class GUITest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity activity;

    public GUITest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        activity = getActivity();
        android.app.Instrumentation inst = getInstrumentation();

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
        ImageView img_Greece = (ImageView) getActivity().findViewById(R.id.img_Greece);
        assertNotNull(img_Greece);
        ImageView img_Latvia = (ImageView) getActivity().findViewById(R.id.img_Latvia);
        assertNotNull(img_Latvia);
        ImageView img_Lithuania = (ImageView) getActivity().findViewById(R.id.img_Lithuania);
        assertNotNull(img_Lithuania);
        ImageView img_Luxembourg = (ImageView) getActivity().findViewById(R.id.img_Luxembourg);
        assertNotNull(img_Luxembourg);
        ImageView img_Moldova = (ImageView) getActivity().findViewById(R.id.img_Moldova);
        assertNotNull(img_Moldova);
        ImageView img_Netherlands = (ImageView) getActivity().findViewById(R.id.img_Netherlands);
        assertNotNull(img_Netherlands);
        ImageView img_Portugal = (ImageView) getActivity().findViewById(R.id.img_Portugal);
        assertNotNull(img_Portugal);
        ImageView img_Romania = (ImageView) getActivity().findViewById(R.id.img_Romania);
        assertNotNull(img_Romania);
        ImageView img_Estonia = (ImageView) getActivity().findViewById(R.id.img_Estonia);
        assertNotNull(img_Estonia);
        ImageView img_Czech = (ImageView) getActivity().findViewById(R.id.img_Czech);
        assertNotNull(img_Czech);
        ImageView img_Bulgaria = (ImageView) getActivity().findViewById(R.id.img_Bulgaria);
        assertNotNull(img_Bulgaria);
        ImageView img_Belgium = (ImageView) getActivity().findViewById(R.id.img_Belgium);
        assertNotNull(img_Belgium);
    }

    public void testImgKey() {
        ImageView img_Key = (ImageView) getActivity().findViewById(R.id.img_Key);
        assertNotNull(img_Key);
    }

    public void testTextView(){
        TextView textHistory = (TextView) getActivity().findViewById(R.id.textHistory);
        assertNotNull(textHistory);
        ViewAsserts.assertOnScreen(activity.getWindow().getDecorView(), textHistory);

        TextView textSidebar = (TextView) getActivity().findViewById(R.id.textSidebarCountry);
        assertNotNull(textSidebar);
        ViewAsserts.assertOnScreen(activity.getWindow().getDecorView(), textSidebar);

        TextView textSideLow = (TextView) getActivity().findViewById(R.id.textSideLow);
        assertNotNull(textSideLow);
        ViewAsserts.assertOnScreen(activity.getWindow().getDecorView(), textSideLow);

        TextView textSidebarTitle = (TextView) getActivity().findViewById(R.id.textSidebarTitle);
        assertNotNull(textSidebarTitle);
        ViewAsserts.assertOnScreen(activity.getWindow().getDecorView(), textSidebarTitle);

    }


    @SmallTest
    public void testLineChart() {
        LineChart lc = (LineChart) getActivity().findViewById(R.id.lineChart);
        assertNotNull(lc);
    }



    @SmallTest
    public void testPieChart() {
        PieChart pc = (PieChart) getActivity().findViewById(R.id.pieChart);
        assertNotNull(pc);
    }


    @SmallTest
    public void testOnTouch() {

        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_DOWN, 100, 100, 0);
        getInstrumentation().sendPointerSync(event);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
