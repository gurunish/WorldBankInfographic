package com.dandelion.worldbankinfographic;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class CountryTest {

    String tempName = "name";
    double[] values = new double[10];
    double[] valuesF = new double[10];
    double[] valuesM = new double[10];
    Country tempAPI = new Country(tempName, values, valuesF, valuesM);

    @Test
    public void testGetName() throws Exception{
        assertEquals(tempAPI.getName(), tempName);
    }

    @Test
    public void testGetValues() throws Exception{
        assertEquals(tempAPI.getValues(), values);
    }

    @Test
    public void testUpdateValues(){
        double[] newValues = new double[10];
        tempAPI.updateValues(newValues);
        assertEquals(tempAPI.getValues(), newValues);
    }

    @Test
    public void testValuesToString() throws Exception{
        String tempOutput = "";
        for (int i = 0; i < values.length; i++){
            int x = 2013 - i;
            tempOutput += String.valueOf(x) + ": " + values[i] + "\n";
        }
        assertEquals(tempAPI.valuesToString(), tempOutput);
    }

    @Test
    public void getValues(){
        assertEquals(values, tempAPI.getValues());
    }

    @Test
    public void getValuesF(){
        assertEquals(valuesF, tempAPI.getValuesF());
    }

    @Test
    public void getValuesM(){
        assertEquals(valuesM, tempAPI.getValuesM());
    }

}