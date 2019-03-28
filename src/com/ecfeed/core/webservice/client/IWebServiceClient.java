package com.ecfeed.core.webservice.client;

public interface IWebServiceClient {

    WebServiceResponse postRequest(String requestType, String request);

    void close();

}
