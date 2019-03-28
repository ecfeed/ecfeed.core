package com.ecfeed.core.webservice.client;

import java.io.BufferedReader;

public class WebServiceResponse {

    private int fResponseStatus;
    private BufferedReader fResponseBufferedReader;

    public WebServiceResponse(int responseStatus, BufferedReader responseBufferedReader) {
        fResponseStatus = responseStatus;
        fResponseBufferedReader = responseBufferedReader;
    }

    public int getResponseStatus() {
        return fResponseStatus;
    }

    public boolean isResponseStatusOk() {
        return (fResponseStatus / 100) == 2;
    }

    public BufferedReader getResponseBufferedReader() {
        return fResponseBufferedReader;
    }

}
