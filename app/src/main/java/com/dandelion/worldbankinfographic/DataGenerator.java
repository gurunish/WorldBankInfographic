package com.dandelion.worldbankinfographic;

public class DataGenerator {

    private Country[] countries;
    private Country country;

    private static String[] countryNames = {"Albania","Andorra","Armenia","Austria","Azerbaijan",
            "Belarus","Belgium","Bosnia & Herzegovina","Bulgaria","Croatia","Cyprus","Czech Republic",
            "Denmark","Estonia","Faroe Islands","Finland","France","Georgia","Germany","Gibraltar","Greece",
            "Hungary","Iceland","Ireland","Israel","Italy","Kazakhstan","Latvia","Liechtenstein","Lithuania",
            "Luxembourg","Macedonia","Malta","Moldova","Monaco","Netherlands","Norway","Poland","Portugal",
            "Romania","Russia","San Marino","Slovakia","Slovenia","Spain","Sweden","Switzerland","Turkey",
            "Ukraine","United Kingdom"};

    private static String[] countryID = {"ALB","AND","ARM","AUT","AZE","BLR","BEL","BIH","BGR",
            "HRV","CYP","CZE","DNK","EST","FRO","FIN","FRA","GEO","DEU","GIB","GRC","HUN","ISL","IRL","ISR",
            "ITA","KAZ","LVA","LIT","LTU","LUX","MKD","MLT","MDA","MCO","NLD","NOR","POL","PRT","ROM","RUS",
            "SMR","SVK","SVN","ESP","SWE","CHE","TUR","UKR","GBR"};

    public DataGenerator(){
        countries = new Country[50];
        createCountriesAndAddToList();
    }

    public void createCountriesAndAddToList(){
        for(int i = 0;i<50;i++){
            // create Country objects here (fetch doubles from data)
            country = new Country(countryID[i],countryNames[i]);
            countries[i] = country;
        }
    }

    public Country[] getCountries(){
        return countries;
    }

    public String returnCountryCode(String selectedCountry){
        int i = 0;
        while (selectedCountry != countryID[i]){
            i++;
        }
        return countryID[i];
    }
}

