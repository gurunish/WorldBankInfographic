package com.dandelion.worldbankinfographic;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class AsyncTaskTest {
    String downloadData = "";
    Country[] countries = new Country[50];
    double[] unemploymentRate;
    private static final String TAG_VALUE = "value";

    String testDownloadData ="[{\"page\":1,\"pages\":1,\"per_page\":\"3000\",\"total\":10}," +
            "[{\"indicator\":{\"id\":\"SL.UEM.TOTL.ZS\",\"value\":\"Unemployment, total (% of total labor force)" +
            " (modeled ILO estimate)\"},\"country\":{\"id\":\"AM\",\"value\":\"Armenia\"},\"value\":\"16.2000007629395\"," +
            "\"decimal\":\"1\",\"date\":\"2013\"},{\"indicator\":{\"id\":\"SL.UEM.TOTL.ZS\",\"value\"" +
            ":\"Unemployment, total (% of total labor force) (modeled ILO estimate)\"},\"country\":{\"id" +
            "\":\"AM\",\"value\":\"Armenia\"},\"value\":\"17.2999992370605\",\"decimal\":\"1\",\"date\":" +
            "\"2012\"},{\"indicator\":{\"id\":\"SL.UEM.TOTL.ZS\",\"value\":\"Unemployment, total (% of " +
            "total labor force) (modeled ILO estimate)\"},\"country\":{\"id\":\"AM\",\"value\":\"Armenia" +
            "\"},\"value\":\"18.3999996185303\",\"decimal\":\"1\",\"date\":\"2011\"},{\"indicator\":{\"id" +
            "\":\"SL.UEM.TOTL.ZS\",\"value\":\"Unemployment, total (% of total labor force) (modeled ILO estimate)" +
            "\"},\"country\":{\"id\":\"AM\",\"value\":\"Armenia\"},\"value\":\"19\",\"decimal\":\"1\",\"" +
            "date\":\"2010\"},{\"indicator\":{\"id\":\"SL.UEM.TOTL.ZS\",\"value\":\"Unemployment, total " +
            "(% of total labor force) (modeled ILO estimate)\"},\"country\":{\"id\":\"AM\",\"value\":\"" +
            "Armenia\"},\"value\":\"18.7000007629395\",\"decimal\":\"1\",\"date\":\"2009\"},{\"indicator\"" +
            ":{\"id\":\"SL.UEM.TOTL.ZS\",\"value\":\"Unemployment, total (% of total labor force) (modeled " +
            "ILO estimate)\"},\"country\":{\"id\":\"AM\",\"value\":\"Armenia\"},\"value\":\"16.399999618530" +
            "3\",\"decimal\":\"1\",\"date\":\"2008\"},{\"indicator\":{\"id\":\"SL.UEM.TOTL.ZS\",\"value\"" +
            ":\"Unemployment, total (% of total labor force) (modeled ILO estimate)\"},\"country\":{\"id\":" +
            "\"AM\",\"value\":\"Armenia\"},\"value\":\"28.3999996185303\",\"decimal\":\"1\",\"date\":\"2007\"" +
            "},{\"indicator\":{\"id\":\"SL.UEM.TOTL.ZS\",\"value\":\"Unemployment, total (% of total labor " +
            "force) (modeled ILO estimate)\"},\"country\":{\"id\":\"AM\",\"value\":\"Armenia\"},\"value\":\"" +
            "28.6000003814697\",\"decimal\":\"1\",\"date\":\"2006\"},{\"indicator\":{\"id\":\"SL.UEM.TOTL.ZS\"" +
            ",\"value\":\"Unemployment, total (% of total labor force) (modeled ILO estimate)\"},\"country\":" +
            "{\"id\":\"AM\",\"value\":\"Armenia\"},\"value\":\"27.7999992370605\",\"decimal\":\"1\",\"date\"" +
            ":\"2005\"},{\"indicator\":{\"id\":\"SL.UEM.TOTL.ZS\",\"value\":\"Unemployment, total (% of total " +
            "labor force) (modeled ILO estimate)\"},\"country\":{\"id\":\"AM\",\"value\":\"Armenia\"},\"value\"" +
            ":\"33.5999984741211\",\"decimal\":\"1\",\"date\":\"2004\"}]]";

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

    public void testUpdateArrays() {
        if (testDownloadData != null) {
            try {
                int indexCountry = 0;
                JSONArray testJSONArray = new JSONArray(testDownloadData);
                JSONArray countryList = testJSONArray.getJSONArray(1);
                JSONObject country;
                for (int i = 0; i < countryList.length(); i++) {
                    country = countryList.getJSONObject(i);
                    String unemploymentString = country.getString(TAG_VALUE);
                    double unemploymentValue = Double.parseDouble(unemploymentString);
                    unemploymentRate[i] = unemploymentValue;
                }
                countries[indexCountry].updateValues(unemploymentRate);
            } catch (Exception e) {
                Log.d("Catch Exception", "Error");
            }
        }
    }

    final AsyncTask<String, Double, JSONArray> myTask = new AsyncTask<String, Double, JSONArray>() {
        int indexCountry = 0;
        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray updateMethod = null;
            double[] unemploymentRate = new double[10];

            String urlString = params[0];
            String countryCode = Character.toString(urlString.charAt(35)) + Character.toString(urlString.charAt(36)) + Character.toString(urlString.charAt(37));
            for (int i = 0; i < 50; i++) {
                if (countryCode.equals(countryID[i])) {
                    indexCountry = i;
                    Log.d("COUNTRY CODE", countryCode + " was extracted and the index for this country is: " + String.valueOf(i));
                }
            }

            countries[indexCountry] = new Country(countryNames[indexCountry], unemploymentRate);
            Log.d("OBJECT CREATED", "Country object created for " + countries[indexCountry].getName());
            if (params.length != 1) {
                return null;
            }
            try {
                URL url = new URL(params[0]);
                InputStream is = url.openStream();
                DataInputStream dataInputStream = new DataInputStream(is);
                byte[] buffer = new byte[1024];
                int bufferLength;
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                while ((bufferLength = dataInputStream.read(buffer)) > 0) {
                    output.write(buffer, 0, bufferLength);
                }
                downloadData = output.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                updateMethod = new JSONArray(downloadData);
                //testUpdateArrays(updateMethod, indexCountry);
                return updateMethod;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Catch Exception", "Error");
            }
            return null;
        }
    };
}
