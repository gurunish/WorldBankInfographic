package com.dandelion.worldbankinfographic;

public class URLStub extends MainActivity{
    private String callWebsiteUrl;
    public static final String RESPONSE = "Response from callWebsite()";

    protected String callWebsite(String url) {
        callWebsiteUrl = url;
        return RESPONSE;
    }

    public int getResponseCode(){
        return 200;
    }
}