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
    private double[] unemploymentRate;
    private static final String TAG_VALUE = "value";
    Country[] countries = new Country[50];
    String downloadData = "";
    TextView testViewOutput;
    Spinner spinnerCountry;
    String url = "";

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

        //Execute Asynctask to start JSON parsing of the 50 URLs of EU countries we chose
        for (int i = 0; i < 50; i++){
            url = "http://api.worldbank.org/countries/" + countryID[i] + "/indicators/SL.UEM.TOTL.ZS?per_page=3000&date=2004:2013&format=json";
            new DownloadData().execute(url);
        }

        testViewOutput = (TextView)findViewById(R.id.testView);
        spinnerCountry = (Spinner)findViewById(R.id.countrySpinner);
        ArrayAdapter<CharSequence> adapterCountry = ArrayAdapter.createFromResource(this, R.array.Countries, android.R.layout.simple_spinner_item);
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapterCountry);
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Toast.makeText(getApplicationContext(), countries[pos].getName() + " was selected from the spinner.", Toast.LENGTH_SHORT).show();
                testViewOutput.setText(countries[pos].valuesToString());
            }
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing, just another required interface callback
            }
        });
    }

    private class DownloadData extends AsyncTask<String,Double,JSONArray> {
        int indexCountry;

        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray updateMethod = null;
            unemploymentRate = new double[10];

            String urlString = params[0];
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
                Toast.makeText(getApplicationContext(), "Error downloading data, check internet connection.", Toast.LENGTH_LONG).show();
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
            }
            catch (Exception e) {
                Log.d("Catch Exception", "Error");
            }
        }
    }

}
