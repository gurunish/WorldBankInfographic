package com.dandelion.worldbankinfographic;

public class Country {

    private String name;
    private double[] values;

    //'value' is a double array where value[0] is 2013, value[1] is 2012..... value[9] = 2004.
    public Country(String countryName, double[] unemploymentValues){
        name = countryName;
        values = unemploymentValues;
    }

    public double[] getValues(){
        return values;
    }

    public String getName(){
        return name;
    }

}
