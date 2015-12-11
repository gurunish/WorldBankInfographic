package com.dandelion.worldbankinfographic;

import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.graphics.Color;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class MainActivity extends AppCompatActivity {

    public float[] yData = {12, 18};
    public String[] xData = {"men", "women"};
    private double[] unemploymentRate;
    private static final String TAG_VALUE = "value";
    Country[] countries = new Country[50];
    String downloadData = "";
    Spinner spinnerCountry;
    String url = "http://api.worldbank.org/";
    private static final String dataCache = "DATA_CACHE";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private int retrieveIndex;

    private static String[] countryID = {"ALB","AND","ARM","AUT","AZE","BLR","BEL","BIH","BGR",
            "HRV","CYP","CZE","DNK","EST","FRO","FIN","FRA","GEO","DEU","GIB","GRC","HUN","ISL","IRL","ISR",
            "ITA","KAZ","LVA","LIT","LTU","LUX","MKD","MLT","MDA","MCO","NLD","NOR","POL","PRT","ROM","RUS",
            "SMR","SVK","SVN","ESP","SWE","CHE","TUR","UKR","GBR"};

    private static String[] countryNames = {"Albania","Andorra","Armenia","Austria","Azerbaijan",
            "Belarus","Belgium","Bosnia & Herzegovina","Bulgaria","Croatia","Cyprus","Czech Republic",
            "Denmark","Estonia","Faroe Islands","Finland","France","Georgia","Germany","Gibraltar","Greece",
            "Hungary","Iceland","Ireland","Israel","Italy","Kazakhstan","Latvia","Liechtenstein","Lithuania",
            "Luxembourg","Macedonia","Malta","Moldova","Monaco","Netherlands","Norway","Poland","Portugal",
            "Romania","Russia","San Marino","Slovakia","Slovenia","Spain","Sweden","Switzerland","Turkey",
            "Ukraine","United Kingdom"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences(dataCache, 0);
        editor = sharedPref.edit();

        //Retrives data from localStorage and creates Country objects of the 50 countries
        //retrieveLocalData();

        retrieveIndex = 0;
        //Execute Asynctask to start JSON parsing of the 50 URLs of EU countries we chose

        for (int i = 0; i < 50; i++){
            url = "http://api.worldbank.org/countries/" + countryID[i] + "/indicators/SL.UEM.TOTL.ZS?per_page=3000&date=2004:2013&format=json";
            new DownloadData().execute(url);
        }

        spinnerCountry = (Spinner)findViewById(R.id.countrySpinner);
        ArrayAdapter<CharSequence> adapterCountry = ArrayAdapter.createFromResource(this, R.array.Countries, android.R.layout.simple_spinner_item);
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapterCountry);
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            }
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing, just another required interface callback
            }
        });
        addData();
    }

    private class DownloadData extends AsyncTask<String,Double,JSONArray> {
        int indexCountry;

        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray updateMethod = null;
            unemploymentRate = new double[10];
            String urlString = params[0];
            int responseCode = 0;

            //checks if URL is active, if not then it will pull from SharedPrefs
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(urlString).openConnection();
                connection.setRequestMethod("HEAD");
                connection.setFollowRedirects(false);
                responseCode = connection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (responseCode != 200) {
                retrieveLocalData(retrieveIndex);
                retrieveIndex++;
            }
            else {
                String countryCode = Character.toString(urlString.charAt(35)) + Character.toString(urlString.charAt(36)) + Character.toString(urlString.charAt(37));
                for (int i = 0; i < 50; i++){
                    if (countryCode.equals(countryID[i])){
                        indexCountry = i;
                        Log.d("COUNTRY CODE", countryCode + " was extracted and the index for this country is: " + String.valueOf(i));
                    }
                }

                countries[indexCountry] = new Country(countryNames[indexCountry], unemploymentRate);
                Log.d("OBJECT CREATED", "Country object created for " + countries[indexCountry].getName());
                if (params.length != 1){
                    return null;
                }
                try{
                    URL url = new URL(params[0]);
                    InputStream is = url.openStream();
                    DataInputStream dataInputStream = new DataInputStream(is);
                    byte[] buffer = new byte[1024];
                    int bufferLength;
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    while((bufferLength = dataInputStream.read(buffer))>0){
                        output.write(buffer,0,bufferLength);
                    }
                    downloadData = output.toString();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    updateMethod = new JSONArray(downloadData);
                    updateArrays(updateMethod, indexCountry);
                    return updateMethod;
                }
                catch(Exception e){
                    e.printStackTrace();
                    Log.d("Catch Exception", "Error");
                }
            }
            return null;
        }
    }

    public void updateArrays(JSONArray downloadData, int indexCountry){
        if (downloadData!=null) {
            try {
                JSONArray countryList = downloadData.getJSONArray(1);
                JSONObject country;
                for (int i = 0; i < countryList.length(); i++) {
                    country = countryList.getJSONObject(i);
                    String unemploymentString = country.getString(TAG_VALUE);
                    double unemploymentValue = Double.parseDouble(unemploymentString);
                    unemploymentRate[i] = unemploymentValue;
                    Log.d("ARRAY OUTPUT", "unemploymentValue: " + unemploymentValue + " was successfully added to the unemploymentRate array at index: " + String.valueOf(i));
                }
                countries[indexCountry].updateValues(unemploymentRate);
                Log.d("OBJECT UPDATED", "Update array for " + countryNames[indexCountry]);
                saveData(indexCountry);
            }
            catch (Exception e) {
                Log.d("Catch Exception", "Error");
            }
        }
    }

    //Saves data to local storage
    public void saveData(int indexCountry){
        editor.putString(countryNames[indexCountry], countries[indexCountry].getStringValues());
        editor.commit();
        Log.d("saveData", countryNames[indexCountry] + "|" + countries[indexCountry].getStringValues());
    }

    public void retrieveLocalData(int index){
        String tempValues = sharedPref.getString(countryNames[index],"");
        if(tempValues.length() >0){
            Log.d("retrieveLocalData", countryNames[index] + " | " +tempValues);
            String[] splitString = tempValues.split(",");
            double[] doubleString = new double[splitString.length];
            for(int i = 0 ; i < doubleString.length; i++){
                doubleString[i] = Double.parseDouble(splitString[i]);
            }
            countries[index] = new Country(countryNames[index], doubleString);
            Log.d("retrieveLocalData", countryNames[index] + " class was retrieved and created");
        }
    }

    public void addData() {
        PieChart chart = (PieChart) findViewById(R.id.pieChart);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColorTransparent(true);
        chart.setHoleRadius(7);
        chart.setTransparentCircleRadius(10);

        // private ArrayList<PieDataSet> getDataSet() {
        // ArrayList<PieDataSet> dataSets = null;

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

        // ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        // BarEntry v2e1 = new BarEntry(150.000f, 0); // Jan
        // valueSet2.add(v2e1);
        // BarEntry v2e2 = new BarEntry(90.000f, 1); // Feb
        // valueSet2.add(v2e2);
        // BarEntry v2e3 = new BarEntry(120.000f, 2); // Mar
        // valueSet2.add(v2e3);
        // BarEntry v2e4 = new BarEntry(60.000f, 3); // Apr
        // valueSet2.add(v2e4);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Continent");
        barDataSet1.setColor(Color.rgb(75, 201, 252));
        // BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Continent");
        // barDataSet2.setColors(ColorTemplate.VORDIPLOM_COLORS);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        // dataSets.add(barDataSet2);
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
}
