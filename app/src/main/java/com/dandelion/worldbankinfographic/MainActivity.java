package com.dandelion.worldbankinfographic;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String downloadData = "";
    Spinner spinnerCountry;

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

        //Execute Asynctask to start JSON parsing of the URL stated above
        new DownloadData().execute(url);
    }

    /**
     * This class executes codes in the background
     */
    private class DownloadData extends AsyncTask<String, Void, String> {

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
