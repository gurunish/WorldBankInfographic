package com.dandelion.worldbankinfographic;

/**
 * Created by M on 28/11/2015.
 */
public class Country {
    private String id;
    private String name;
    private double unemployed2011;
    private double unemployed2012;
    private double unemployed2013;
    private double average;
    public Country(String iD,String countryName,double unemp2011,double unemp2012, double unemp2013){
        id=iD;
        name = countryName;
        unemployed2011 = unemp2011;
        unemployed2012 = unemp2012;
        unemployed2013 = unemp2013;
    }
    public void calculateAverage(){
        average = (unemployed2011+unemployed2012+unemployed2013)/3;
    }
    public double getAverage(){
        return average;
    }
}

