package com.dandelion.worldbankinfographic;

public class Country {

    private String name;
    private double[] values;
    private double[] valuesF;
    private double[] valuesM;
    private boolean standardDataStored = false;
    private boolean femaleDataStored = false;
    private boolean maleDataStored = false;

    //'value' is a double array where value[0] is 2013, value[1] is 2012..... value[9] = 2004.
    public Country(String countryName, double[] unemploymentValues, double[] unemploymentValuesF, double[] unemploymentValuesM){
        name = countryName;
        values = unemploymentValues;
        valuesF = unemploymentValuesF;
        valuesM = unemploymentValuesM;
    }


    public void updateValues(double[] unemploymentValues){
        values = unemploymentValues;
    }
    public void updateValuesF(double[] unemploymentValuesF){
        valuesF = unemploymentValuesF;
    }
    public void updateValuesM(double[] unemploymentValuesM){
        valuesM = unemploymentValuesM;
    }


    public double[] getValues(){
        return values;
    }
    public double[] getValuesF(){
        return valuesF;
    }
    public double[] getValuesM(){
        return valuesM;
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
    public String valuesToStringF(){
        String output = "";
        for (int i = 0; i < valuesF.length; i++){
            int x = 2013 - i;
            output += String.valueOf(x) + ": " + valuesF[i] + "\n";
        }
        return output;
    }
    public String valuesToStringM(){
        String output = "";
        for (int i = 0; i < valuesM.length; i++){
            int x = 2013 - i;
            output += String.valueOf(x) + ": " + valuesM[i] + "\n";
        }
        return output;
    }

    public void setStandardDataStored(){
        standardDataStored = true;
    }
    public void setFemaleDataStored(){
        femaleDataStored = true;
    }
    public void setMaleDataStored(){
        maleDataStored = true;
    }

    public boolean getStandardDataStored(){
        return standardDataStored;
    }
    public boolean getFemaleDataStored(){
        return femaleDataStored;
    }
    public boolean getMaleDataStored(){
        return maleDataStored;
    }

}
