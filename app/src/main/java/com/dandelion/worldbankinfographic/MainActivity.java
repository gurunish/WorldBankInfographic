package com.dandelion.worldbankinfographic;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String downloadData = "";
    Spinner spinnerCountry;

    public float[] yData = {12, 18};
    public String[] xData = {"men", "women"};
//    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCountry = (Spinner)findViewById(R.id.countrySpinner);
        ArrayAdapter<CharSequence> adapterCountry = ArrayAdapter.createFromResource(this, R.array.Countries, android.R.layout.simple_spinner_item);
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapterCountry);

        //URL of API
        String url = new String("http://api.worldbank.org/countries/all/indicators/SL.UEM.TOTL.ZS?per_page=3000&date=2004:2013&format=json");
        new DownloadData().execute(url);
        addData();

        //        // Create a new ImageView
//        ImageView imageView = new ImageView(imageView);
//        // Set the background color to white
//        imageView.setBackgroundColor(Color.WHITE);
//        // Parse the SVG file from the resource
//        SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.graphic);
//        // Get a drawable from the parsed SVG and set it as the drawable for the ImageView
//        imageView.setImageDrawable(svg.createPictureDrawable());
//        // Set the ImageView as the content view for the Activity
//        setContentView(imageView);

    }

    public void addData() {

        PieChart chart = (PieChart) findViewById(R.id.pieChart);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColorTransparent(true);
        chart.setHoleRadius(7);
        chart.setTransparentCircleRadius(10);

//        private ArrayList<PieDataSet> getDataSet() {
//            ArrayList<PieDataSet> dataSets = null;


        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < yData.length; i++)
            yVals.add(new Entry(yData[i], i));

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);

        PieDataSet barDataSet = new PieDataSet(yVals, "");
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        barDataSet.setColors(colors);
        barDataSet.setSliceSpace(3);
        barDataSet.setSelectionShift(10);

        PieData pieData = new PieData(xVals, barDataSet);
        chart.setData(pieData);
        chart.setDescription("Unemployment");
        chart.animateXY(2000, 2000);
        //chart.invalidate();


        BarChart barChart = (BarChart) findViewById(R.id.barChart);

        BarData data = new BarData(getXAxisValues(), getDataSet());
        barChart.setData(data);
        barChart.setDescription("");
        barChart.animateXY(2000, 2000);
        barChart.invalidate();

    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(110.000f, 0); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(40.000f, 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(60.000f, 2); // Mar
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(30.000f, 3); // Apr
        valueSet1.add(v1e4);

//        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
//        BarEntry v2e1 = new BarEntry(150.000f, 0); // Jan
//        valueSet2.add(v2e1);
//        BarEntry v2e2 = new BarEntry(90.000f, 1); // Feb
//        valueSet2.add(v2e2);
//        BarEntry v2e3 = new BarEntry(120.000f, 2); // Mar
//        valueSet2.add(v2e3);
//        BarEntry v2e4 = new BarEntry(60.000f, 3); // Apr
//        valueSet2.add(v2e4);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Continent");
        barDataSet1.setColor(Color.rgb(75, 201, 252));
//        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Continent");
//        barDataSet2.setColors(ColorTemplate.VORDIPLOM_COLORS);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
//        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("2010");
        xAxis.add("2011");
        xAxis.add("2012");
        xAxis.add("2013");
        return xAxis;
    }

    /**
     * This class executes codes in the background
     */
    private class DownloadData extends AsyncTask<String, Integer, String> {
        private String readData(String urlName) throws IOException {
            StringBuffer buffer = new StringBuffer();
            URL url = new URL(urlName);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            BufferedReader in;
            in = new BufferedReader( new InputStreamReader(connection.getInputStream()));
            String inputLine = in.readLine();
            while (inputLine != null) {
                buffer.append(inputLine);
                inputLine = in.readLine();
            }
            in.close();
            connection.disconnect();
            return(buffer.toString());
        }

        protected String doInBackground(String... urls) {
            try {
                String data = readData(urls[0]);
                return data;
            } catch (IOException e) {
                Log.e("DownloadData", "Error when downloading from URL");
                return "Error occurred when reading data from URL";
            }
        }

        protected void onPostExecute(String result) {
            downloadData = result;
            Log.d("DownloadData", "Data has been downloaded.");
            Toast.makeText(getApplicationContext(), "Data has been successfully downloaded", Toast.LENGTH_LONG).show();
        }

    }

}
