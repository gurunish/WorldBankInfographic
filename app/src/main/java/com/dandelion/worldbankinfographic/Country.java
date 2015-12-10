package com.dandelion.worldbankinfographic;

public class Country {

    private String name;
    private double[] values;
    private String stringValues;

    //'value' is a double array where value[0] is 2013, value[1] is 2012..... value[9] = 2004.
    public Country(String countryName, double[] unemploymentValues){
        name = countryName;
        values = unemploymentValues;
        stringValues = null;
    }

    public Country(String countryName, String unemploymentValuesString){
        name = countryName;
        stringValues = unemploymentValuesString;
        values = null;
    }

    public void updateValues(double[] unemploymentValues){
        values = unemploymentValues;
    }

    public double[] getValues(){
        return values;
    }

    public String getStringValues(){
        return stringValues;
    }

    public String getName(){
        return name;
    }

    public String valuesToString(){
        //If countries were saved via SharedPreferences, output is used, else stringValues is returned
        if(values==null){
            return stringValues;
        } else {
            String output = "";
            for (int i = 0; i < values.length; i++){
                int x = 2013 - i;
                output += String.valueOf(x) + ": " + values[i] + "\n";
            }
            return output;
        }
    }
}
