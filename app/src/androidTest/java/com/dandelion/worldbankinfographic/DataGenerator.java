package com.dandelion.worldbankinfographic;

/**
 * Created by M on 28/11/2015.
 */
public class DataGenerator {

    private static String[] countryIds = {"ALB","AND","ARM","AUT","AZE","BLR","BEL","BIH","BGR",
    "HRV","CYP","CZE","DNK","EST","FRO","FIN","FRA","GEO","DEU","GIB","GRC","HUN","ISL","IRL","ISR",
    "ITA","KAZ","LVA","LIT","LTU","LUX","MKD","MLT","MDA","MCO","NLD","NOR","POL","PRT","ROM","RUS",
    "SMR","SVK","SVN","ESP","SWE","CHE","TUR","UKR","GBR"};
    private static String[] countryNames = {"Albania","Andorra","Armenia","Austria","Azerbaijan",
    "Belarus","Belgium","Bosnia & Herzegovina","Bulgaria","Croatia","Cyprus","Czech Republic",
    "Denmark","Estonia","Faroe Islands","Finland","France","Georgia","Germany","Gibraltar","Greece",
    "Hungary","Iceland","Ireland","Israel","Italy","Kazakhstan","Latvia","Liechtenstein","Lithuania",
    "Luxembourg","Macedonia","Malta","Moldova","Monaco","Netherlands","Norway","Poland","Portugal",
    "Romania","Russia","San Marino","Slovakia","Slovenia","Spain","Sweden","Switzerland","Turkey",
    "Ukraine","United Kingdom"};
    private Country[] countries;
    private Country country;

    public DataGenerator(){
        countries = new Country[50];
        createCountriesAndAddToList();
    }
    public void createCountriesAndAddToList(){
        for(int i = 0;i<50;i++){
            // create Country objects here (fetch doubles from data)
            country = new Country(countryIds[i],countryNames[i],0,0,0);
            countries [i] = country;
        }

    }
    public Country[] getCountries(){
        return countries;
    }
}

