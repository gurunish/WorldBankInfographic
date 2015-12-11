package com.dandelion.worldbankinfographic;

import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class MainActivity extends AppCompatActivity {

    public float[] yData = new float[2];
    public String[] xData = {"Men", "Women"};
    private double[] unemploymentRate;
    private double[] unemploymentRateF;
    private double[] unemploymentRateM;
    private static final String TAG_VALUE = "value";
    Country[] countries = new Country[41];
    String downloadData = "";
    //TextView testViewOutput;
    Spinner spinnerCountry;
    TextView textSidebarCountry;
    int mapWidth, mapHeight;
    int previousImgID = 0;
    private int retrieveIndex = 0;
    String url = "http://api.worldbank.org/";
    private static final String dataCache = "DATA_CACHE";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private static Button btn_info;
    private final Context context = this;
    private AlertDialog alertbox;
    int loadCount = 0;

    //---------------------------------------------------------------------------------------------

    private static String[] country_ID = {"ALB","ARM","AUT","AZE","BLR","BEL","BIH","BGR",
            "HRV","CZE","DNK","EST","FIN","FRA","GEO","DEU","GRC","HUN","ISL","IRL","ITA",
            "LVA","LTU","LUX","MKD","MDA","NLD","NOR","POL","PRT","ROM","RUS","SRB","SVK",
            "SVN","ESP","SWE","CHE","TUR","UKR","GBR"};

    private static String[] countryNames = {"Albania","Armenia","Austria","Azerbaijan",
            "Belarus","Belgium","Bosnia & Herzegovina","Bulgaria","Croatia","Czech Republic",
            "Denmark","Estonia","Finland","France","Georgia","Germany","Greece",
            "Hungary","Iceland","Ireland","Italy","Latvia","Lithuania",
            "Luxembourg","Macedonia","Moldova","Netherlands","Norway","Poland","Portugal",
            "Romania","Russia","Serbia","Slovakia","Slovenia","Spain","Sweden","Switzerland",
            "Turkey","Ukraine","United Kingdom"};

    ImageView
            img_Iceland, img_UK, img_Ireland, img_France, img_Spain, img_Finland, img_Sweden,
            img_Norway, img_Germany, img_Italy, img_Poland, img_Russia, img_Denmark, img_Turkey,
            img_Belarus, img_Austria, img_Bulgaria, img_Belgium, img_Czech, img_Estonia,
            img_Greece, img_Latvia, img_Lithuania, img_Luxembourg, img_Moldova, img_Netherlands,
            img_Portugal, img_Romania, img_Switzerland, img_Ukraine, img_Slovakia, img_Hungary,
            img_Slovenia, img_Croatia, img_Serbia, img_Bosnia, img_Albania, img_Macedonia,
            img_Georgia, img_Azerbaijan, img_Armenia, img_Spares;

    AlphaAnimation flux;

    ArrayList<Integer> countryImgID = new ArrayList<Integer>();
    ArrayList countryNamesUI = new ArrayList();

    //---------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences(dataCache, 0);
        editor = sharedPref.edit();

        //Methods for UI interactivity
        pop_up_info();
        spinner_set();

        //Execute Asynctask to start JSON parsing of the 41 URLs of EU countries we chose
        for (int i = 0; i < 41; i++){

            // overall stats
            url = "http://api.worldbank.org/countries/" + country_ID[i] + "/indicators/SL.UEM.TOTL.ZS?per_page=3000&date=2004:2013&format=json";
            new DownloadData().execute(url);

            //female stats
            url = "http://api.worldbank.org/countries/" + country_ID[i] + "/indicators/SL.UEM.TOTL.FE.ZS?per_page=3000&date=2004:2013&format=json";
            new DownloadData().execute(url);

            // male stats
            url = "http://api.worldbank.org/countries/" + country_ID[i] + "/indicators/SL.UEM.TOTL.MA.ZS?per_page=3000&date=2004:2013&format=json";
            new DownloadData().execute(url);
        }

        ImageView img_Key = (ImageView)findViewById(R.id.img_Key);
        img_Key.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        //setting custom fonts
        Typeface segoe_ui_semibold = Typeface.createFromAsset(getAssets(),"fonts/segoe_ui_semibold.ttf");
        TextView textHistory = (TextView)findViewById(R.id.textHistory);
        textHistory.setTypeface(segoe_ui_semibold);
        TextView textSidebarTitle = (TextView)findViewById(R.id.textSidebarTitle);
        textSidebarTitle.setTypeface(segoe_ui_semibold);
        textSidebarCountry = (TextView)findViewById(R.id.textSidebarCountry);
        textSidebarCountry.setTypeface(segoe_ui_semibold);
        TextView textSideLow = (TextView)findViewById(R.id.textSideLow);
        textSideLow.setTypeface(segoe_ui_semibold);
        TextView textSideHigh = (TextView)findViewById(R.id.textSideHigh);
        textSideHigh.setTypeface(segoe_ui_semibold);

        //defining country selected animation
        flux = new AlphaAnimation(1.0f, 0.3f);
        flux.setDuration(1000);
        flux.setRepeatCount(Animation.INFINITE);
        flux.setRepeatMode(Animation.REVERSE);

        //-----------------------------------------------------------------------------------------

        //retrieving dimensions for scaling
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        mapWidth = (screenWidth/100)*70;
        mapHeight = (screenHeight/100)*70;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        if(screenWidth/metrics.densityDpi<=6){
            textHistory.setTextSize(12);
            textSidebarTitle.setTextSize(14);
            textSidebarCountry.setTextSize(20);
            ViewGroup.LayoutParams params = textSidebarTitle.getLayoutParams();
            textSidebarTitle.setLayoutParams(params);
            params = textSidebarCountry.getLayoutParams();
            params.height = 60;
            textSidebarCountry.setLayoutParams(params);
        }

        // scale left-hand bar
        LinearLayout layout = (LinearLayout)findViewById(R.id.search_section);
        ViewGroup.LayoutParams paramsBar = layout.getLayoutParams();
        paramsBar.width = (screenWidth/100)*30;
        layout.setLayoutParams(paramsBar);

        //pie chart scaling
        com.github.mikephil.charting.charts.PieChart pieView = (com.github.mikephil.charting.charts.PieChart)findViewById(R.id.pieChart);
        ViewGroup.LayoutParams paramsPie = pieView.getLayoutParams();
        paramsPie.width = (screenWidth/100)*30;
        paramsPie.height = (screenWidth/100)*30;
        pieView.setLayoutParams(paramsPie);
        if(screenWidth/metrics.densityDpi<=6){
            paramsPie.width = (screenWidth/100)*25;
            paramsPie.height = (screenWidth/100)*25;
        }

        //-----------------------------------------------------------------------------------------

        PieChart p = (PieChart) findViewById(R.id.pieChart);
        p.setNoDataText("No country selected");
        LineChart l = (LineChart) findViewById(R.id.lineChart);
        l.setNoDataText("No country selected");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Loading Data");
        builder.setMessage("Set 0 of 123");

        alertbox = builder.create();
        alertbox.show();
    }

    //-----------------------------------------------------------------------------------------


    public void colourMap(){

        //ordering countries by 2013 value, lowest at 0
        Country[] orderedCountries = countries;
        Country temp;
        for(int i = 0; i<41; i++){
            for(int j = i; j<41; j++){
                if(orderedCountries[j].getValues()[0]<orderedCountries[i].getValues()[0]){
                    temp = orderedCountries[i];
                    orderedCountries[i] = orderedCountries[j];
                    orderedCountries[j] = temp;
                }
            }
        }

        //printing ordered list
        for(int i = 0; i<41; i++){
            System.out.println((i+1)+" "+orderedCountries[i].getName()+" "+orderedCountries[i].getValues()[0]);
        }

        //obtaining correct ImageView IDs in order
        Integer[] orderedIndexes = new Integer[41];
        for(int i = 0; i<41; i++){
            for(int j = 0; j<41; j++){
                if(orderedCountries[i].getName().equals(countryNames[j])){
                    orderedIndexes[i] = j;
                }
            }
        }

        String[] colourInput = new String[41];

        //sets the colour of countries
        //[r]ed, [o]range, [y]ellow, and [g]reen
        for(int i = 0; i<11; i++) colourInput[orderedIndexes[i]] = "g";
        for(int i = 11; i<21; i++) colourInput[orderedIndexes[i]] = "y";
        for(int i = 21; i<31; i++) colourInput[orderedIndexes[i]] = "o";
        for(int i = 31; i<41; i++) colourInput[orderedIndexes[i]] = "r";

        //-----------------------------------------------------------------------------------------

        //populating map with countries
        //yeah it's pretty cumbersome, but don't worry it was mostly generated with jig code
        int x = 0;

        img_Albania = (ImageView) findViewById(R.id.img_Albania);
        img_Albania.setDrawingCacheEnabled(true);
        img_Albania.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Albania.setBackgroundResource(R.drawable.albania_g);
        if(colourInput[x].equals("y")) img_Albania.setBackgroundResource(R.drawable.albania_y);
        if(colourInput[x].equals("o")) img_Albania.setBackgroundResource(R.drawable.albania_o);
        if(colourInput[x++].equals("r")) img_Albania.setBackgroundResource(R.drawable.albania_r);
        img_Albania.getLayoutParams().height = mapHeight;
        img_Albania.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Albania.getId());
        countryNamesUI.add("Albania");

        img_Armenia = (ImageView) findViewById(R.id.img_Armenia);
        img_Armenia.setDrawingCacheEnabled(true);
        img_Armenia.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Armenia.setBackgroundResource(R.drawable.armenia_g);
        if(colourInput[x].equals("y")) img_Armenia.setBackgroundResource(R.drawable.armenia_y);
        if(colourInput[x].equals("o")) img_Armenia.setBackgroundResource(R.drawable.armenia_o);
        if(colourInput[x++].equals("r")) img_Armenia.setBackgroundResource(R.drawable.armenia_r);
        img_Armenia.getLayoutParams().height = mapHeight;
        img_Armenia.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Armenia.getId());
        countryNamesUI.add("Armenia");

        img_Austria = (ImageView) findViewById(R.id.img_Austria);
        img_Austria.setDrawingCacheEnabled(true);
        img_Austria.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Austria.setBackgroundResource(R.drawable.austria_g);
        if(colourInput[x].equals("y")) img_Austria.setBackgroundResource(R.drawable.austria_y);
        if(colourInput[x].equals("o")) img_Austria.setBackgroundResource(R.drawable.austria_o);
        if(colourInput[x++].equals("r")) img_Austria.setBackgroundResource(R.drawable.austria_r);
        img_Austria.getLayoutParams().height = mapHeight;
        img_Austria.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Austria.getId());
        countryNamesUI.add("Austria");

        img_Azerbaijan = (ImageView) findViewById(R.id.img_Azerbaijan);
        img_Azerbaijan.setDrawingCacheEnabled(true);
        img_Azerbaijan.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Azerbaijan.setBackgroundResource(R.drawable.azerbaijan_g);
        if(colourInput[x].equals("y")) img_Azerbaijan.setBackgroundResource(R.drawable.azerbaijan_y);
        if(colourInput[x].equals("o")) img_Azerbaijan.setBackgroundResource(R.drawable.azerbaijan_o);
        if(colourInput[x++].equals("r")) img_Azerbaijan.setBackgroundResource(R.drawable.azerbaijan_r);
        img_Azerbaijan.getLayoutParams().height = mapHeight;
        img_Azerbaijan.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Azerbaijan.getId());
        countryNamesUI.add("Azerbaijan");

        img_Belarus = (ImageView) findViewById(R.id.img_Belarus);
        img_Belarus.setDrawingCacheEnabled(true);
        img_Belarus.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Belarus.setBackgroundResource(R.drawable.belarus_g);
        if(colourInput[x].equals("y")) img_Belarus.setBackgroundResource(R.drawable.belarus_y);
        if(colourInput[x].equals("o")) img_Belarus.setBackgroundResource(R.drawable.belarus_o);
        if(colourInput[x++].equals("r")) img_Belarus.setBackgroundResource(R.drawable.belarus_r);
        img_Belarus.getLayoutParams().height = mapHeight;
        img_Belarus.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Belarus.getId());
        countryNamesUI.add("Belarus");

        img_Belgium = (ImageView) findViewById(R.id.img_Belgium);
        img_Belgium.setDrawingCacheEnabled(true);
        img_Belgium.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Belgium.setBackgroundResource(R.drawable.belgium_g);
        if(colourInput[x].equals("y")) img_Belgium.setBackgroundResource(R.drawable.belgium_y);
        if(colourInput[x].equals("o")) img_Belgium.setBackgroundResource(R.drawable.belgium_o);
        if(colourInput[x++].equals("r")) img_Belgium.setBackgroundResource(R.drawable.belgium_r);
        img_Belgium.getLayoutParams().height = mapHeight;
        img_Belgium.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Belgium.getId());
        countryNamesUI.add("Belgium");

        img_Bosnia = (ImageView) findViewById(R.id.img_Bosnia);
        img_Bosnia.setDrawingCacheEnabled(true);
        img_Bosnia.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Bosnia.setBackgroundResource(R.drawable.bosnia_g);
        if(colourInput[x].equals("y")) img_Bosnia.setBackgroundResource(R.drawable.bosnia_y);
        if(colourInput[x].equals("o")) img_Bosnia.setBackgroundResource(R.drawable.bosnia_o);
        if(colourInput[x++].equals("r")) img_Bosnia.setBackgroundResource(R.drawable.bosnia_r);
        img_Bosnia.getLayoutParams().height = mapHeight;
        img_Bosnia.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Bosnia.getId());
        countryNamesUI.add("Bosnia & Herzegovina");

        img_Bulgaria = (ImageView) findViewById(R.id.img_Bulgaria);
        img_Bulgaria.setDrawingCacheEnabled(true);
        img_Bulgaria.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Bulgaria.setBackgroundResource(R.drawable.bulgaria_g);
        if(colourInput[x].equals("y")) img_Bulgaria.setBackgroundResource(R.drawable.bulgaria_y);
        if(colourInput[x].equals("o")) img_Bulgaria.setBackgroundResource(R.drawable.bulgaria_o);
        if(colourInput[x++].equals("r")) img_Bulgaria.setBackgroundResource(R.drawable.bulgaria_r);
        img_Bulgaria.getLayoutParams().height = mapHeight;
        img_Bulgaria.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Bulgaria.getId());
        countryNamesUI.add("Bulgaria");

        img_Croatia = (ImageView) findViewById(R.id.img_Croatia);
        img_Croatia.setDrawingCacheEnabled(true);
        img_Croatia.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Croatia.setBackgroundResource(R.drawable.croatia_g);
        if(colourInput[x].equals("y")) img_Croatia.setBackgroundResource(R.drawable.croatia_y);
        if(colourInput[x].equals("o")) img_Croatia.setBackgroundResource(R.drawable.croatia_o);
        if(colourInput[x++].equals("r")) img_Croatia.setBackgroundResource(R.drawable.croatia_r);
        img_Croatia.getLayoutParams().height = mapHeight;
        img_Croatia.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Croatia.getId());
        countryNamesUI.add("Croatia");

        img_Czech = (ImageView) findViewById(R.id.img_Czech);
        img_Czech.setDrawingCacheEnabled(true);
        img_Czech.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Czech.setBackgroundResource(R.drawable.czech_g);
        if(colourInput[x].equals("y")) img_Czech.setBackgroundResource(R.drawable.czech_y);
        if(colourInput[x].equals("o")) img_Czech.setBackgroundResource(R.drawable.czech_o);
        if(colourInput[x++].equals("r")) img_Czech.setBackgroundResource(R.drawable.czech_r);
        img_Czech.getLayoutParams().height = mapHeight;
        img_Czech.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Czech.getId());
        countryNamesUI.add("Czech Republic");

        img_Denmark = (ImageView) findViewById(R.id.img_Denmark);
        img_Denmark.setDrawingCacheEnabled(true);
        img_Denmark.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Denmark.setBackgroundResource(R.drawable.denmark_g);
        if(colourInput[x].equals("y")) img_Denmark.setBackgroundResource(R.drawable.denmark_y);
        if(colourInput[x].equals("o")) img_Denmark.setBackgroundResource(R.drawable.denmark_o);
        if(colourInput[x++].equals("r")) img_Denmark.setBackgroundResource(R.drawable.denmark_r);
        img_Denmark.getLayoutParams().height = mapHeight;
        img_Denmark.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Denmark.getId());
        countryNamesUI.add("Denmark");

        img_Estonia = (ImageView) findViewById(R.id.img_Estonia);
        img_Estonia.setDrawingCacheEnabled(true);
        img_Estonia.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Estonia.setBackgroundResource(R.drawable.estonia_g);
        if(colourInput[x].equals("y")) img_Estonia.setBackgroundResource(R.drawable.estonia_y);
        if(colourInput[x].equals("o")) img_Estonia.setBackgroundResource(R.drawable.estonia_o);
        if(colourInput[x++].equals("r")) img_Estonia.setBackgroundResource(R.drawable.estonia_r);
        img_Estonia.getLayoutParams().height = mapHeight;
        img_Estonia.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Estonia.getId());
        countryNamesUI.add("Estonia");

        img_Finland = (ImageView) findViewById(R.id.img_Finland);
        img_Finland.setDrawingCacheEnabled(true);
        img_Finland.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Finland.setBackgroundResource(R.drawable.finland_g);
        if(colourInput[x].equals("y")) img_Finland.setBackgroundResource(R.drawable.finland_y);
        if(colourInput[x].equals("o")) img_Finland.setBackgroundResource(R.drawable.finland_o);
        if(colourInput[x++].equals("r")) img_Finland.setBackgroundResource(R.drawable.finland_r);
        img_Finland.getLayoutParams().height = mapHeight;
        img_Finland.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Finland.getId());
        countryNamesUI.add("Finland");

        img_France = (ImageView) findViewById(R.id.img_France);
        img_France.setDrawingCacheEnabled(true);
        img_France.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_France.setBackgroundResource(R.drawable.france_g);
        if(colourInput[x].equals("y")) img_France.setBackgroundResource(R.drawable.france_y);
        if(colourInput[x].equals("o")) img_France.setBackgroundResource(R.drawable.france_o);
        if(colourInput[x++].equals("r")) img_France.setBackgroundResource(R.drawable.france_r);
        img_France.getLayoutParams().height = mapHeight;
        img_France.getLayoutParams().width = mapWidth;
        countryImgID.add(img_France.getId());
        countryNamesUI.add("France");

        img_Georgia = (ImageView) findViewById(R.id.img_Georgia);
        img_Georgia.setDrawingCacheEnabled(true);
        img_Georgia.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Georgia.setBackgroundResource(R.drawable.georgia_g);
        if(colourInput[x].equals("y")) img_Georgia.setBackgroundResource(R.drawable.georgia_y);
        if(colourInput[x].equals("o")) img_Georgia.setBackgroundResource(R.drawable.georgia_o);
        if(colourInput[x++].equals("r")) img_Georgia.setBackgroundResource(R.drawable.georgia_r);
        img_Georgia.getLayoutParams().height = mapHeight;
        img_Georgia.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Georgia.getId());
        countryNamesUI.add("Georgia");

        img_Germany = (ImageView) findViewById(R.id.img_Germany);
        img_Germany.setDrawingCacheEnabled(true);
        img_Germany.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Germany.setBackgroundResource(R.drawable.germany_g);
        if(colourInput[x].equals("y")) img_Germany.setBackgroundResource(R.drawable.germany_y);
        if(colourInput[x].equals("o")) img_Germany.setBackgroundResource(R.drawable.germany_o);
        if(colourInput[x++].equals("r")) img_Germany.setBackgroundResource(R.drawable.germany_r);
        img_Germany.getLayoutParams().height = mapHeight;
        img_Germany.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Germany.getId());
        countryNamesUI.add("Germany");

        img_Greece = (ImageView) findViewById(R.id.img_Greece);
        img_Greece.setDrawingCacheEnabled(true);
        img_Greece.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Greece.setBackgroundResource(R.drawable.greece_g);
        if(colourInput[x].equals("y")) img_Greece.setBackgroundResource(R.drawable.greece_y);
        if(colourInput[x].equals("o")) img_Greece.setBackgroundResource(R.drawable.greece_o);
        if(colourInput[x++].equals("r")) img_Greece.setBackgroundResource(R.drawable.greece_r);
        img_Greece.getLayoutParams().height = mapHeight;
        img_Greece.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Greece.getId());
        countryNamesUI.add("Greece");

        img_Hungary = (ImageView) findViewById(R.id.img_Hungary);
        img_Hungary.setDrawingCacheEnabled(true);
        img_Hungary.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Hungary.setBackgroundResource(R.drawable.hungary_g);
        if(colourInput[x].equals("y")) img_Hungary.setBackgroundResource(R.drawable.hungary_y);
        if(colourInput[x].equals("o")) img_Hungary.setBackgroundResource(R.drawable.hungary_o);
        if(colourInput[x++].equals("r")) img_Hungary.setBackgroundResource(R.drawable.hungary_r);
        img_Hungary.getLayoutParams().height = mapHeight;
        img_Hungary.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Hungary.getId());
        countryNamesUI.add("Hungary");

        img_Iceland = (ImageView) findViewById(R.id.img_Iceland);
        img_Iceland.setDrawingCacheEnabled(true);
        img_Iceland.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Iceland.setBackgroundResource(R.drawable.iceland_g);
        if(colourInput[x].equals("y")) img_Iceland.setBackgroundResource(R.drawable.iceland_y);
        if(colourInput[x].equals("o")) img_Iceland.setBackgroundResource(R.drawable.iceland_o);
        if(colourInput[x++].equals("r")) img_Iceland.setBackgroundResource(R.drawable.iceland_r);
        img_Iceland.getLayoutParams().height = mapHeight;
        img_Iceland.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Iceland.getId());
        countryNamesUI.add("Iceland");

        img_Ireland = (ImageView) findViewById(R.id.img_Ireland);
        img_Ireland.setDrawingCacheEnabled(true);
        img_Ireland.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Ireland.setBackgroundResource(R.drawable.ireland_g);
        if(colourInput[x].equals("y")) img_Ireland.setBackgroundResource(R.drawable.ireland_y);
        if(colourInput[x].equals("o")) img_Ireland.setBackgroundResource(R.drawable.ireland_o);
        if(colourInput[x++].equals("r")) img_Ireland.setBackgroundResource(R.drawable.ireland_r);
        img_Ireland.getLayoutParams().height = mapHeight;
        img_Ireland.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Ireland.getId());
        countryNamesUI.add("Ireland");

        img_Italy = (ImageView) findViewById(R.id.img_Italy);
        img_Italy.setDrawingCacheEnabled(true);
        img_Italy.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Italy.setBackgroundResource(R.drawable.italy_g);
        if(colourInput[x].equals("y")) img_Italy.setBackgroundResource(R.drawable.italy_y);
        if(colourInput[x].equals("o")) img_Italy.setBackgroundResource(R.drawable.italy_o);
        if(colourInput[x++].equals("r")) img_Italy.setBackgroundResource(R.drawable.italy_r);
        img_Italy.getLayoutParams().height = mapHeight;
        img_Italy.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Italy.getId());
        countryNamesUI.add("Italy");

        img_Latvia = (ImageView) findViewById(R.id.img_Latvia);
        img_Latvia.setDrawingCacheEnabled(true);
        img_Latvia.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Latvia.setBackgroundResource(R.drawable.latvia_g);
        if(colourInput[x].equals("y")) img_Latvia.setBackgroundResource(R.drawable.latvia_y);
        if(colourInput[x].equals("o")) img_Latvia.setBackgroundResource(R.drawable.latvia_o);
        if(colourInput[x++].equals("r")) img_Latvia.setBackgroundResource(R.drawable.latvia_r);
        img_Latvia.getLayoutParams().height = mapHeight;
        img_Latvia.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Latvia.getId());
        countryNamesUI.add("Latvia");

        img_Lithuania = (ImageView) findViewById(R.id.img_Lithuania);
        img_Lithuania.setDrawingCacheEnabled(true);
        img_Lithuania.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Lithuania.setBackgroundResource(R.drawable.lithuania_g);
        if(colourInput[x].equals("y")) img_Lithuania.setBackgroundResource(R.drawable.lithuania_y);
        if(colourInput[x].equals("o")) img_Lithuania.setBackgroundResource(R.drawable.lithuania_o);
        if(colourInput[x++].equals("r")) img_Lithuania.setBackgroundResource(R.drawable.lithuania_r);
        img_Lithuania.getLayoutParams().height = mapHeight;
        img_Lithuania.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Lithuania.getId());
        countryNamesUI.add("Lithuania");

        img_Luxembourg = (ImageView) findViewById(R.id.img_Luxembourg);
        img_Luxembourg.setDrawingCacheEnabled(true);
        img_Luxembourg.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Luxembourg.setBackgroundResource(R.drawable.luxembourg_g);
        if(colourInput[x].equals("y")) img_Luxembourg.setBackgroundResource(R.drawable.luxembourg_y);
        if(colourInput[x].equals("o")) img_Luxembourg.setBackgroundResource(R.drawable.luxembourg_o);
        if(colourInput[x++].equals("r")) img_Luxembourg.setBackgroundResource(R.drawable.luxembourg_r);
        img_Luxembourg.getLayoutParams().height = mapHeight;
        img_Luxembourg.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Luxembourg.getId());
        countryNamesUI.add("Luxembourg");

        img_Macedonia = (ImageView) findViewById(R.id.img_Macedonia);
        img_Macedonia.setDrawingCacheEnabled(true);
        img_Macedonia.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Macedonia.setBackgroundResource(R.drawable.macedonia_g);
        if(colourInput[x].equals("y")) img_Macedonia.setBackgroundResource(R.drawable.macedonia_y);
        if(colourInput[x].equals("o")) img_Macedonia.setBackgroundResource(R.drawable.macedonia_o);
        if(colourInput[x++].equals("r")) img_Macedonia.setBackgroundResource(R.drawable.macedonia_r);
        img_Macedonia.getLayoutParams().height = mapHeight;
        img_Macedonia.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Macedonia.getId());
        countryNamesUI.add("Macedonia");

        img_Moldova = (ImageView) findViewById(R.id.img_Moldova);
        img_Moldova.setDrawingCacheEnabled(true);
        img_Moldova.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Moldova.setBackgroundResource(R.drawable.moldova_g);
        if(colourInput[x].equals("y")) img_Moldova.setBackgroundResource(R.drawable.moldova_y);
        if(colourInput[x].equals("o")) img_Moldova.setBackgroundResource(R.drawable.moldova_o);
        if(colourInput[x++].equals("r")) img_Moldova.setBackgroundResource(R.drawable.moldova_r);
        img_Moldova.getLayoutParams().height = mapHeight;
        img_Moldova.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Moldova.getId());
        countryNamesUI.add("Moldova");

        img_Netherlands = (ImageView) findViewById(R.id.img_Netherlands);
        img_Netherlands.setDrawingCacheEnabled(true);
        img_Netherlands.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Netherlands.setBackgroundResource(R.drawable.netherlands_g);
        if(colourInput[x].equals("y")) img_Netherlands.setBackgroundResource(R.drawable.netherlands_y);
        if(colourInput[x].equals("o")) img_Netherlands.setBackgroundResource(R.drawable.netherlands_o);
        if(colourInput[x++].equals("r")) img_Netherlands.setBackgroundResource(R.drawable.netherlands_r);
        img_Netherlands.getLayoutParams().height = mapHeight;
        img_Netherlands.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Netherlands.getId());
        countryNamesUI.add("Netherlands");

        img_Norway = (ImageView) findViewById(R.id.img_Norway);
        img_Norway.setDrawingCacheEnabled(true);
        img_Norway.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Norway.setBackgroundResource(R.drawable.norway_g);
        if(colourInput[x].equals("y")) img_Norway.setBackgroundResource(R.drawable.norway_y);
        if(colourInput[x].equals("o")) img_Norway.setBackgroundResource(R.drawable.norway_o);
        if(colourInput[x++].equals("r")) img_Norway.setBackgroundResource(R.drawable.norway_r);
        img_Norway.getLayoutParams().height = mapHeight;
        img_Norway.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Norway.getId());
        countryNamesUI.add("Norway");

        img_Poland = (ImageView) findViewById(R.id.img_Poland);
        img_Poland.setDrawingCacheEnabled(true);
        img_Poland.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Poland.setBackgroundResource(R.drawable.poland_g);
        if(colourInput[x].equals("y")) img_Poland.setBackgroundResource(R.drawable.poland_y);
        if(colourInput[x].equals("o")) img_Poland.setBackgroundResource(R.drawable.poland_o);
        if(colourInput[x++].equals("r")) img_Poland.setBackgroundResource(R.drawable.poland_r);
        img_Poland.getLayoutParams().height = mapHeight;
        img_Poland.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Poland.getId());
        countryNamesUI.add("Poland");

        img_Portugal = (ImageView) findViewById(R.id.img_Portugal);
        img_Portugal.setDrawingCacheEnabled(true);
        img_Portugal.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Portugal.setBackgroundResource(R.drawable.portugal_g);
        if(colourInput[x].equals("y")) img_Portugal.setBackgroundResource(R.drawable.portugal_y);
        if(colourInput[x].equals("o")) img_Portugal.setBackgroundResource(R.drawable.portugal_o);
        if(colourInput[x++].equals("r")) img_Portugal.setBackgroundResource(R.drawable.portugal_r);
        img_Portugal.getLayoutParams().height = mapHeight;
        img_Portugal.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Portugal.getId());
        countryNamesUI.add("Portugal");

        img_Romania = (ImageView) findViewById(R.id.img_Romania);
        img_Romania.setDrawingCacheEnabled(true);
        img_Romania.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Romania.setBackgroundResource(R.drawable.romania_g);
        if(colourInput[x].equals("y")) img_Romania.setBackgroundResource(R.drawable.romania_y);
        if(colourInput[x].equals("o")) img_Romania.setBackgroundResource(R.drawable.romania_o);
        if(colourInput[x++].equals("r")) img_Romania.setBackgroundResource(R.drawable.romania_r);
        img_Romania.getLayoutParams().height = mapHeight;
        img_Romania.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Romania.getId());
        countryNamesUI.add("Romania");

        img_Russia = (ImageView) findViewById(R.id.img_Russia);
        img_Russia.setDrawingCacheEnabled(true);
        img_Russia.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Russia.setBackgroundResource(R.drawable.russia_g);
        if(colourInput[x].equals("y")) img_Russia.setBackgroundResource(R.drawable.russia_y);
        if(colourInput[x].equals("o")) img_Russia.setBackgroundResource(R.drawable.russia_o);
        if(colourInput[x++].equals("r")) img_Russia.setBackgroundResource(R.drawable.russia_r);
        img_Russia.getLayoutParams().height = mapHeight;
        img_Russia.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Russia.getId());
        countryNamesUI.add("Russia");

        img_Serbia = (ImageView) findViewById(R.id.img_Serbia);
        img_Serbia.setDrawingCacheEnabled(true);
        img_Serbia.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Serbia.setBackgroundResource(R.drawable.serbia_g);
        if(colourInput[x].equals("y")) img_Serbia.setBackgroundResource(R.drawable.serbia_y);
        if(colourInput[x].equals("o")) img_Serbia.setBackgroundResource(R.drawable.serbia_o);
        if(colourInput[x++].equals("r")) img_Serbia.setBackgroundResource(R.drawable.serbia_r);
        img_Serbia.getLayoutParams().height = mapHeight;
        img_Serbia.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Serbia.getId());
        countryNamesUI.add("Serbia");

        img_Slovakia = (ImageView) findViewById(R.id.img_Slovakia);
        img_Slovakia.setDrawingCacheEnabled(true);
        img_Slovakia.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Slovakia.setBackgroundResource(R.drawable.slovakia_g);
        if(colourInput[x].equals("y")) img_Slovakia.setBackgroundResource(R.drawable.slovakia_y);
        if(colourInput[x].equals("o")) img_Slovakia.setBackgroundResource(R.drawable.slovakia_o);
        if(colourInput[x++].equals("r")) img_Slovakia.setBackgroundResource(R.drawable.slovakia_r);
        img_Slovakia.getLayoutParams().height = mapHeight;
        img_Slovakia.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Slovakia.getId());
        countryNamesUI.add("Slovakia");

        img_Slovenia = (ImageView) findViewById(R.id.img_Slovenia);
        img_Slovenia.setDrawingCacheEnabled(true);
        img_Slovenia.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Slovenia.setBackgroundResource(R.drawable.slovenia_g);
        if(colourInput[x].equals("y")) img_Slovenia.setBackgroundResource(R.drawable.slovenia_y);
        if(colourInput[x].equals("o")) img_Slovenia.setBackgroundResource(R.drawable.slovenia_o);
        if(colourInput[x++].equals("r")) img_Slovenia.setBackgroundResource(R.drawable.slovenia_r);
        img_Slovenia.getLayoutParams().height = mapHeight;
        img_Slovenia.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Slovenia.getId());
        countryNamesUI.add("Slovenia");

        img_Spain = (ImageView) findViewById(R.id.img_Spain);
        img_Spain.setDrawingCacheEnabled(true);
        img_Spain.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Spain.setBackgroundResource(R.drawable.spain_g);
        if(colourInput[x].equals("y")) img_Spain.setBackgroundResource(R.drawable.spain_y);
        if(colourInput[x].equals("o")) img_Spain.setBackgroundResource(R.drawable.spain_o);
        if(colourInput[x++].equals("r")) img_Spain.setBackgroundResource(R.drawable.spain_r);
        img_Spain.getLayoutParams().height = mapHeight;
        img_Spain.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Spain.getId());
        countryNamesUI.add("Spain");

        img_Sweden = (ImageView) findViewById(R.id.img_Sweden);
        img_Sweden.setDrawingCacheEnabled(true);
        img_Sweden.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Sweden.setBackgroundResource(R.drawable.sweden_g);
        if(colourInput[x].equals("y")) img_Sweden.setBackgroundResource(R.drawable.sweden_y);
        if(colourInput[x].equals("o")) img_Sweden.setBackgroundResource(R.drawable.sweden_o);
        if(colourInput[x++].equals("r")) img_Sweden.setBackgroundResource(R.drawable.sweden_r);
        img_Sweden.getLayoutParams().height = mapHeight;
        img_Sweden.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Sweden.getId());
        countryNamesUI.add("Sweden");

        img_Switzerland = (ImageView) findViewById(R.id.img_Switzerland);
        img_Switzerland.setDrawingCacheEnabled(true);
        img_Switzerland.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Switzerland.setBackgroundResource(R.drawable.switzerland_g);
        if(colourInput[x].equals("y")) img_Switzerland.setBackgroundResource(R.drawable.switzerland_y);
        if(colourInput[x].equals("o")) img_Switzerland.setBackgroundResource(R.drawable.switzerland_o);
        if(colourInput[x++].equals("r")) img_Switzerland.setBackgroundResource(R.drawable.switzerland_r);
        img_Switzerland.getLayoutParams().height = mapHeight;
        img_Switzerland.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Switzerland.getId());
        countryNamesUI.add("Switzerland");

        img_Turkey = (ImageView) findViewById(R.id.img_Turkey);
        img_Turkey.setDrawingCacheEnabled(true);
        img_Turkey.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Turkey.setBackgroundResource(R.drawable.turkey_g);
        if(colourInput[x].equals("y")) img_Turkey.setBackgroundResource(R.drawable.turkey_y);
        if(colourInput[x].equals("o")) img_Turkey.setBackgroundResource(R.drawable.turkey_o);
        if(colourInput[x++].equals("r")) img_Turkey.setBackgroundResource(R.drawable.turkey_r);
        img_Turkey.getLayoutParams().height = mapHeight;
        img_Turkey.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Turkey.getId());
        countryNamesUI.add("Turkey");

        img_Ukraine = (ImageView) findViewById(R.id.img_Ukraine);
        img_Ukraine.setDrawingCacheEnabled(true);
        img_Ukraine.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_Ukraine.setBackgroundResource(R.drawable.ukraine_g);
        if(colourInput[x].equals("y")) img_Ukraine.setBackgroundResource(R.drawable.ukraine_y);
        if(colourInput[x].equals("o")) img_Ukraine.setBackgroundResource(R.drawable.ukraine_o);
        if(colourInput[x++].equals("r")) img_Ukraine.setBackgroundResource(R.drawable.ukraine_r);
        img_Ukraine.getLayoutParams().height = mapHeight;
        img_Ukraine.getLayoutParams().width = mapWidth;
        countryImgID.add(img_Ukraine.getId());
        countryNamesUI.add("Ukraine");

        img_UK = (ImageView) findViewById(R.id.img_UK);
        img_UK.setDrawingCacheEnabled(true);
        img_UK.setOnTouchListener(checkTransparentListener);
        if(colourInput[x].equals("g")) img_UK.setBackgroundResource(R.drawable.uk_g);
        if(colourInput[x].equals("y")) img_UK.setBackgroundResource(R.drawable.uk_y);
        if(colourInput[x].equals("o")) img_UK.setBackgroundResource(R.drawable.uk_o);
        if(colourInput[x].equals("r")) img_UK.setBackgroundResource(R.drawable.uk_r);
        img_UK.getLayoutParams().height = mapHeight;
        img_UK.getLayoutParams().width = mapWidth;
        countryImgID.add(img_UK.getId());
        countryNamesUI.add("United Kingdom");

        img_Spares = (ImageView) findViewById(R.id.img_Spares);
        img_Spares.setDrawingCacheEnabled(true);
        img_Spares.setBackgroundResource(R.drawable.spares);
        img_Spares.getLayoutParams().height = mapHeight;
        img_Spares.getLayoutParams().width = mapWidth;

    }

    private void dismissAlert()
    {
        loadCount++;
        alertbox.setMessage("Set "+ loadCount + " of 123");
        if(countries[40]!=null){
            if(countries[40].getMaleDataStored()) {
                alertbox.dismiss();
                colourMap();
            }
        }

    }

    private class DownloadData extends AsyncTask<String,Double,JSONArray> {
        int indexCountry;

        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray updateMethod = null;
            unemploymentRate = new double[10];
            unemploymentRateF = new double[10];
            unemploymentRateM = new double[10];
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
                if(retrieveIndex!=41){
                    retrieveLocalData(retrieveIndex);
                    retrieveIndex++;
                }
                return null;
            }
            else {
                String countryCode = Character.toString(urlString.charAt(35)) + Character.toString(urlString.charAt(36)) + Character.toString(urlString.charAt(37));
                for (int i = 0; i < 41; i++) {
                    if (countryCode.equals(country_ID[i])) {
                        indexCountry = i;
                        Log.d("COUNTRY CODE", countryCode + " was extracted and the index for this country is: " + String.valueOf(i));
                    }
                }

                //run if new country data is fetched
                if (countries[indexCountry] == null) {
                    countries[indexCountry] = new Country(countryNames[indexCountry], unemploymentRate, unemploymentRateF, unemploymentRateM);
                    Log.d("OBJECT CREATED", "Country object created for " + countries[indexCountry].getName());
                }

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
                    Toast.makeText(getApplicationContext(), "Error downloading data, check internet connection.", Toast.LENGTH_LONG).show();
                }
                try {
                    updateMethod = new JSONArray(downloadData);
                    updateArrays(updateMethod, indexCountry);
                    return updateMethod;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Catch Exception", "Error");
                }
            }
            return null;
        }
        protected void onPostExecute(JSONArray a) {
            dismissAlert();
        }
    }

    //-----------------------------------------------------------------------------------------

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
                if(!countries[indexCountry].getStandardDataStored()) {
                    countries[indexCountry].updateValues(unemploymentRate);
                    Log.d("OBJECT UPDATED", "Updated standard array for " + countryNames[indexCountry]);
                    countries[indexCountry].setStandardDataStored();
                    saveData(indexCountry, 's');
                }
                else if(!countries[indexCountry].getFemaleDataStored()){
                    countries[indexCountry].updateValuesF(unemploymentRate);
                    Log.d("OBJECT UPDATED", "Updated female array for " + countryNames[indexCountry]);
                    countries[indexCountry].setFemaleDataStored();
                    saveData(indexCountry, 'f');
                }
                else if(!countries[indexCountry].getMaleDataStored()){
                    countries[indexCountry].updateValuesM(unemploymentRate);
                    Log.d("OBJECT UPDATED", "Updated male array for " + countryNames[indexCountry]);
                    countries[indexCountry].setMaleDataStored();
                    saveData(indexCountry, 'm');
                }
            }
            catch (Exception e) {
                Log.d("Catch Exception", "Error");
            }
        }
    }

    //---------------------------------------------------------------------------------------------

    //Saves data to local storage
    public void saveData(int indexCountry, char type){
        if(type=='s') // standard
            editor.putString(countryNames[indexCountry]+" s",countries[indexCountry].valuesToString());
        if(type=='f') //female
            editor.putString(countryNames[indexCountry]+" f", countries[indexCountry].valuesToStringF());
        if(type=='m') //male
            editor.putString(countryNames[indexCountry]+" m", countries[indexCountry].valuesToStringM());
        editor.commit();
        Log.d("SharedPref UPDATED", "Update array for " + countryNames[indexCountry]);
    }

    //---------------------------------------------------------------------------------------------

    public void retrieveLocalData(int index){
        //retrieval loses precision because double was used
        //
        //never use double

        String temp;
        //standard data
        String tempValues = sharedPref.getString(countryNames[index] + " s", "");
        String[] splitString = tempValues.split("[:]");
        double[] doubleString = new double[10];
        for(int i = 1 ; i < 11; i++){

            temp = splitString[i].substring(1, Math.min(4, splitString[i].length()));
            doubleString[i-1] = Double.parseDouble(temp);

            System.out.println(doubleString[i-1]);
        }

        //female data
        String tempValuesF = sharedPref.getString(countryNames[index]+" f","");
        String[] splitStringF = tempValuesF.split(":");
        double[] doubleStringF = new double[splitString.length];
        for(int i = 1 ; i < 11; i++){

            temp = splitStringF[i].substring(1, Math.min(4, splitStringF[i].length()));
            doubleStringF[i-1] = Double.parseDouble(temp);

            System.out.println(doubleStringF[i-1]);
        }

        //male data
        String tempValuesM = sharedPref.getString(countryNames[index]+" m","");
        String[] splitStringM = tempValuesM.split(":");
        double[] doubleStringM = new double[splitStringM.length];
        for(int i = 1 ; i < 11; i++){

            temp = splitStringM[i].substring(1, Math.min(4, splitStringM[i].length()));
            doubleStringM[i-1] = Double.parseDouble(temp);

            System.out.println(doubleStringM[i-1]);
        }
        countries[index] = new Country(countryNames[index], doubleString, doubleStringF, doubleStringM);
        countries[index].setStandardDataStored();
        countries[index].setFemaleDataStored();
        countries[index].setMaleDataStored();
        Log.d("retrieveLocalData", countryNames[index] + " was created.");
    }

    //---------------------------------------------------------------------------------------------

    //method configuring the pie chart and set its data
    public void addData(float m, float f, float a, float b, float c, float d) {

        float a13 = a;
        float b12 = b;
        float c11 = c;
        float d10 = d;

        yData[0] = m;
        yData[1] = f;
        PieChart chart = (PieChart) findViewById(R.id.pieChart);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColorTransparent(true);
        chart.setHoleRadius(7);
        chart.setTransparentCircleRadius(10);
        chart.setNoDataText("No country selected");

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < yData.length; i++)
            yVals.add(new Entry(yData[i], i));

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);

        PieDataSet barDataSet = new PieDataSet(yVals, "");
        ArrayList<Integer> colors = new ArrayList<>();

        for (int colours : ColorTemplate.LIBERTY_COLORS)
            colors.add(colours);

        colors.add(ColorTemplate.getHoloBlue());
        barDataSet.setColors(colors);
        barDataSet.setSliceSpace(3);
        barDataSet.setSelectionShift(10);

        PieData pieData = new PieData(xVals, barDataSet);
        chart.setData(pieData);
        chart.setDescription("");
        chart.animateXY(2000, 2000);
        chart.getLegend().setEnabled(false);



        LineChart lineChart = (LineChart) findViewById(R.id.lineChart);

        LineData data = new LineData(getXAxisValues(), getDataSet(a13, b12, c11, d10));
        lineChart.setData(data);
        lineChart.setDescription("");
        lineChart.animateXY(2000, 2000);
        lineChart.invalidate();
        lineChart.setTouchEnabled(false);
        lineChart.setNoDataText("No country selected");
    }

    //---------------------------------------------------------------------------------------------

    //Data-sets for line graph
    private ArrayList<LineDataSet> getDataSet(float a, float b, float c, float d) {
        ArrayList<LineDataSet> dataSets = null;
        ArrayList<Entry> valueSet1 = new ArrayList<>();
        Entry v1e1 = new BarEntry(a, 0);
        valueSet1.add(v1e1);
        Entry v1e2 = new BarEntry(b, 1);
        valueSet1.add(v1e2);
        Entry v1e3 = new BarEntry(c, 2);
        valueSet1.add(v1e3);
        Entry v1e4 = new BarEntry(d, 3);
        valueSet1.add(v1e4);

        LineDataSet lineDataSet1 = new LineDataSet(valueSet1, "");
        lineDataSet1.setColor(Color.rgb(75, 201, 252));

        dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        return dataSets;
    }

    //---------------------------------------------------------------------------------------------

    //Years-set for line graph
    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("2010");
        xAxis.add("2011");
        xAxis.add("2012");
        xAxis.add("2013");
        return xAxis;
    }
    //---------------------------------------------------------------------------------------------

    //method to convert map images to bitmap without maxing out memory as getDrawingCache would
    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        v.layout(0,0, v.getLayoutParams().width, v.getLayoutParams().height);
        return b;
    }

    //---------------------------------------------------------------------------------------------

    //checks for country being selected on map and changes data accordingly
    private View.OnTouchListener checkTransparentListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            Bitmap bmp = loadBitmapFromView(v);
            if((int) event.getY()<bmp.getHeight() && (int) event.getX()<bmp.getWidth()) {

                //Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());    //evil old code
                int color = bmp.getPixel((int) event.getX(), (int) event.getY());

                //deselects country if blank area is selected
                if (color == Color.TRANSPARENT) {
                    if (previousImgID != 0) {
                        findViewById(previousImgID).getAnimation().cancel();
                        findViewById(previousImgID).clearAnimation();
                        findViewById(previousImgID).setAnimation(null);
                        previousImgID = 0;
                    }

                    PieChart p = (PieChart) findViewById(R.id.pieChart);
                    p.clear();
                    LineChart l = (LineChart) findViewById(R.id.lineChart);
                    l.clear();

                    textSidebarCountry.setText("");

                    return false;

                    //selects country and changes data sources
                } else {
                    for (int i = 0; i < countryImgID.size(); i++) {
                        if (countryImgID.get(i).equals(v.getId())) {
                            System.out.println(countryNamesUI.get(i));

                            if (previousImgID != 0) {
                                findViewById(previousImgID).getAnimation().cancel();
                                findViewById(previousImgID).clearAnimation();
                                findViewById(previousImgID).setAnimation(null);
                            }
                            findViewById(countryImgID.get(i)).startAnimation(flux);
                            previousImgID = countryImgID.get(i);

                            textSidebarCountry.setText(countryNamesUI.get(i).toString());
                            if(countryNamesUI.get(i).toString().equals("Bosnia & Herzegovina")) textSidebarCountry.setText("Bosnia & Herz.");

                            //search for correct data
                            for(int j = 0; j<41; j++){
                                if(countryNamesUI.get(i).toString()==countries[j].getName()) {

                                    //sets graph contents
                                    addData(
                                            (float) countries[j].getValuesM()[0],
                                            (float) countries[j].getValuesF()[0],
                                            (float) countries[j].getValues()[3],
                                            (float) countries[j].getValues()[2],
                                            (float) countries[j].getValues()[1],
                                            (float) countries[j].getValues()[0]
                                    );
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    return true;
                }
            }
            return false;
        }
    };

    //---------------------------------------------------------------------------------------------

    public void spinner_set() {
        spinnerCountry = (Spinner) findViewById(R.id.countrySpinner);
        ArrayAdapter<CharSequence> adapterCountry = ArrayAdapter.createFromResource(this, R.array.Countries, android.R.layout.simple_spinner_item);
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapterCountry);
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (!spinnerCountry.getSelectedItem().toString().equals("Choose Country")) {
                    for (int i = 0; i < 41; i++) {
                        if (spinnerCountry.getSelectedItem().toString().equals(countryNamesUI.get(i))) {

                            if (previousImgID != 0) {
                                findViewById(previousImgID).getAnimation().cancel();
                                findViewById(previousImgID).clearAnimation();
                                findViewById(previousImgID).setAnimation(null);
                            }
                            findViewById(countryImgID.get(i)).startAnimation(flux);
                            previousImgID = countryImgID.get(i);

                            textSidebarCountry.setText(countryNamesUI.get(i).toString());
                            if(countryNamesUI.get(i).toString().equals("Bosnia & Herzegovina")) textSidebarCountry.setText("Bosnia & Herz.");

                            spinnerCountry.setSelection(0);

                            //search for correct data
                            for(int j = 0; j<41; j++){
                                if(countryNamesUI.get(i).toString()==countries[j].getName()){

                                    //sets graph contents
                                    addData(
                                            (float)countries[j].getValuesM()[0],
                                            (float)countries[j].getValuesF()[0],
                                            (float)countries[j].getValues()[3],
                                            (float)countries[j].getValues()[2],
                                            (float)countries[j].getValues()[1],
                                            (float)countries[j].getValues()[0]
                                    );
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing, just another required interface callback
                // Niiiiice
            }
        });
    }
    public void pop_up_info() {
        btn_info = (Button) findViewById(R.id.btn_info);
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder info_dialog = new AlertDialog.Builder(context);
                info_dialog.setTitle("To view a country's unemployment percentages, select the " +
                        " country from the list in the top left or tap its location on the map");
                info_dialog.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                info_dialog.setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                info_dialog.show();
            }
        });
    }

    //---------------------------------------------------------------------------------------------


}
