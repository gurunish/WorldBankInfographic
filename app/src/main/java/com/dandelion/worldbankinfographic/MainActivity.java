package com.dandelion.worldbankinfographic;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String downloadData = "";
    Spinner spinnerCountry;
    TextView testViewOutput;
    Country[] countries = new Country[50];

    //JSON tags
    private static final String TAG_PAGE = "page";
    private static final String TAG_PAGES = "pages";
    private static final String TAG_PER_PAGE = "per_page";
    private static final String TAG_TOTAL = "total";
    private static final String TAG_INDICATOR = "indicator";
    private static final String TAG_INDICATOR_ID = "id";
    private static final String TAG_INDICATOR_VALUE = "value";
    private static final String TAG_COUNTRY = "country";
    private static final String TAG_COUNTRY_ID = "id";
    private static final String TAG_COUNTRY_VALUE = "value";
    private static final String TAG_VALUE = "value";
    private static final String TAG_DECIMAL = "decimal";
    private static final String TAG_DATE = "date";
    private double[] unemploymentRate = new double[10];


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

    //private static String url = "http://api.worldbank.org/countries/all/indicators/SL.UEM.TOTL.ZS?per_page=3000&date=2004:2013&format=json";
    private static String url = "http://api.worldbank.org/countries/GBR/indicators/SL.UEM.TOTL.ZS?per_page=3000&date=2013&format=json";
    //JSONArray page = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCountry = (Spinner)findViewById(R.id.countrySpinner);
        ArrayAdapter<CharSequence> adapterCountry = ArrayAdapter.createFromResource(this, R.array.Countries, android.R.layout.simple_spinner_item);
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapterCountry);

        testViewOutput = (TextView)findViewById(R.id.testView);

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Toast.makeText(getApplicationContext(), countries[pos].getName() + " was selected from the spinner.", Toast.LENGTH_SHORT).show();
                testViewOutput.setText(countries[pos].valuesToString());
            }
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing, just another required interface callback
            }
        });

        //Execute Asynctask to start JSON parsing of the 50 URLs of EU countries we chose
        for (int i = 0; i < countryID.length; i++){
            url = "http://api.worldbank.org/countries/" + countryID[i] + "/indicators/SL.UEM.TOTL.ZS?per_page=3000&date=2004:2013&format=json";
            new DownloadData().execute(url);
            countries[i] = new Country(countryNames[i], unemploymentRate);
        }
    }

    /**
     * This class executes codes in the background
     */
    private class DownloadData extends AsyncTask<String,Double,JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            if(params.length != 1){
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
                Toast.makeText(getApplicationContext(), "Error downloading data", Toast.LENGTH_LONG).show();
            }
            try {
                return new JSONArray(downloadData);
            }
            catch(Exception e){
            }
            return null;
        }

        @Override
        public void onPostExecute(JSONArray downloadData) {
            if(downloadData!=null) {
                try {
                    JSONArray countryList = downloadData.getJSONArray(1);
                    JSONObject country;
                    for (int i = 0; i < countryList.length(); i++) {

                        country = countryList.getJSONObject(i);
                        String unemploymentString = country.getString(TAG_VALUE);
                        double unemploymentValue = Double.parseDouble(unemploymentString);
                        unemploymentRate[i] = unemploymentValue;
                    }
                }
                catch (Exception e) {
                }
            }
        }
    }
}
