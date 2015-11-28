package com.dandelion.worldbankinfographic;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BackgroundTask dataFetch = new BackgroundTask();

        //URL of API
        String url = new String("");
        dataFetch.execute(url);
    }

    /**
     * This class executes codes in the background
     */
    private class BackgroundTask extends AsyncTask<String,String,String> {
        private String readData(String urlName) throws IOException {
            StringBuffer buffer = new StringBuffer();
            URL url = new URL(urlName);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            BufferedReader in;
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
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
                Log.e("BackgroundTask", "Error when downloading from URL");
                return "Error occurred when reading data from URL";
            }
        }

        protected void onPostExecute(String result) {
            data = result;
            Log.d("BackgroundTask", "Data has been updated");
        }
    }

}
