package com.dandelion.worldbankinfographic;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class CountryTest {

    double[] tempArray = new double[4];
    String tempString = "name";
    Country temp = new Country(tempString, tempArray);

    @Test
    public void testGetName() throws Exception{
        assertEquals(temp.getName(), tempString);
    }

    @Test
    public void testGetValues() throws Exception{
        assertEquals(temp.getValues(), tempArray);
    }

    @Test
    public void testValuesToString() throws Exception{
        String tempOutput = "";
        for (int i = 0; i < tempArray.length; i++){
            int x = 2013 - i;
            tempOutput += String.valueOf(x) + ": " + tempArray[i] + "\n";
        }
        assertEquals(temp.valuesToString(), tempOutput);
    }
}