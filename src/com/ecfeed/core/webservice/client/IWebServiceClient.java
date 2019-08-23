package com.ecfeed.core.webservice.client;

public interface IWebServiceClient {

    WebServiceResponse sendPostRequest(String requestType, String request);
    WebServiceResponse sendGetRequest();

    void close();

}
