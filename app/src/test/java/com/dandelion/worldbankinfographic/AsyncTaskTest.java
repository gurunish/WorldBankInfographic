package com.dandelion.worldbankinfographic;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class AsyncTaskTest {
    final CountDownLatch signal = new CountDownLatch(1);
    String downloadData = "";
    Country[] countries = new Country[50];
    double[] unemploymentRate;
    double[] valuesF;
    double[] valuesM;
    private static final String TAG_VALUE = "value";
    int retrieveIndex = 0;

    String testDownloadData = "Temp string";

    private static String[] countryID = {"ALB", "AND", "ARM", "AUT", "AZE", "BLR", "BEL", "BIH", "BGR",
            "HRV", "CYP", "CZE", "DNK", "EST", "FRO", "FIN", "FRA", "GEO", "DEU", "GIB", "GRC", "HUN", "ISL", "IRL", "ISR",
            "ITA", "KAZ", "LVA", "LIT", "LTU", "LUX", "MKD", "MLT", "MDA", "MCO", "NLD", "NOR", "POL", "PRT", "ROM", "RUS",
            "SMR", "SVK", "SVN", "ESP", "SWE", "CHE", "TUR", "UKR", "GBR"};

    private static String[] countryNames = {"Albania", "Andorra", "Armenia", "Austria", "Azerbaijan",
            "Belarus", "Belgium", "Bosnia & Herzegovina", "Bulgaria", "Croatia", "Cyprus", "Czech Republic",
            "Denmark", "Estonia", "Faroe Islands", "Finland", "France", "Georgia", "Germany", "Gibraltar", "Greece",
            "Hungary", "Iceland", "Ireland", "Israel", "Italy", "Kazakhstan", "Latvia", "Liechtenstein", "Lithuania",
            "Luxembourg", "Macedonia", "Malta", "Moldova", "Monaco", "Netherlands", "Norway", "Poland", "Portugal",
            "Romania", "Russia", "San Marino", "Slovakia", "Slovenia", "Spain", "Sweden", "Switzerland", "Turkey",
            "Ukraine", "United Kingdom"};

    @Test
    public void testUpdateArrays() {
        if (testDownloadData != null) {
            try {
                int indexCountry = 0;
                double unemploymentValue;
                JSONArray testJSONArray = new JSONArray(testDownloadData);
                JSONArray countryList = testJSONArray.getJSONArray(1);
                JSONObject country;

                country = countryList.getJSONObject(indexCountry);
                String unemploymentString = country.getString(TAG_VALUE);
                unemploymentValue = Double.parseDouble(unemploymentString);
                unemploymentRate[indexCountry] = unemploymentValue;
                countries[indexCountry].updateValues(unemploymentRate);

                assertEquals(countries[indexCountry].getValues(), unemploymentValue);
            } catch (Exception e) {
            }
        }
    }

    final AsyncTask<String, Double, JSONArray> backgroundTask = new AsyncTask<String, Double, JSONArray>() {
        URLStub urlStub = new URLStub();
        int indexCountry;
        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray updateMethod = null;
            unemploymentRate = new double[10];
            String urlString = "http://api.worldbank.org/countries/ALB/indicators/SL.UEM.TOTL.ZS?per_page=3000&date=2004:2013&format=json";
            int responseCode = 0;

            HttpURLConnection connection = null;
            responseCode = urlStub.getResponseCode();

            if (responseCode != 200) {
                urlStub.retrieveLocalData(retrieveIndex);
                retrieveIndex++;
            }
            else {
                String countryCode = Character.toString(urlString.charAt(35)) + Character.toString(urlString.charAt(36)) + Character.toString(urlString.charAt(37));
                for (int i = 0; i < 50; i++){
                    if (countryCode.equals(countryID[i])){
                        indexCountry = i;
                    }
                }
                indexCountry = 0;
                countries[indexCountry] = new Country(countryNames[indexCountry], unemploymentRate, valuesF, valuesM);

                if (params.length != 1){
                    return null;
                }
                downloadData = urlStub.callWebsite(urlString);
                try {
                    updateMethod = new JSONArray(downloadData);
                    urlStub.updateArrays(updateMethod, indexCountry);
                    return updateMethod;
                }
                catch(Exception e){
                    e.printStackTrace();
                    Log.d("Catch Exception", "Error");
                }
            }
            signal.countDown();
            return null;
        }
    };

    /*

    This needs to go somewhere but I'm not able to make it work. Once this works, I can start testing with assrtTrue,etc

    runOnMainSync(new Runnable() {
        @Override
        public void run() {
            backgroundTask.execute("Run");
        }
    });

    */
}
