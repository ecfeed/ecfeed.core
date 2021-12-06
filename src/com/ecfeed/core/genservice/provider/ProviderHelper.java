package com.ecfeed.core.genservice.provider;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.webservice.client.WebServiceResponse;

import java.io.BufferedReader;

public class ProviderHelper {

    public static void reportInvalidResponseException(WebServiceResponse webServiceResponse) {

        String jsonMessage = getErrorMessage(webServiceResponse);

        String errorMessage = StringHelper.removeToPrefixAndFromPostfix("{\"error\":\"", "\"}", jsonMessage);

        ExceptionHelper.reportRuntimeException(
                "Request failed. Response status: " + webServiceResponse.getResponseStatus() +
                        ". Message: " + errorMessage);
    }

    private static String getErrorMessage(WebServiceResponse webServiceResponse) {

        BufferedReader br = webServiceResponse.getResponseBufferedReader();

        String message = new String();

        try {
            for (String line; (line = br.readLine()) != null; message += line) ;
        } catch (Exception e) {
            return "Unknown error";
        }

        return message;
    }


}
