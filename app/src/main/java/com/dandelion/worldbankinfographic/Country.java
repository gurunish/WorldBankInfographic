package com.dandelion.worldbankinfographic;

public class Country {

    private String name;
    private double[] values;

    //'value' is a double array where value[0] is 2013, value[1] is 2012..... value[9] = 2004.
    public Country(String countryName, double[] unemploymentValues){
        name = countryName;
        values = unemploymentValues;
    }

    public void updateValues(double[] unemploymentValues){
        values = unemploymentValues;
    }

    public double[] getValues(){
        return values;
    }

    public String getName(){
        return name;
    }

    public String valuesToString(){
        String output = "";
        for (int i = 0; i < values.length; i++){
            int x = 2013 - i;
            output += String.valueOf(x) + ": " + values[i] + "\n";
        }
        return output;
    }

}
