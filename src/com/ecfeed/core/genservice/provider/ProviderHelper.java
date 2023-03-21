package com.ecfeed.core.genservice.provider;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.webservice.client.WebServiceResponse;

import java.io.BufferedReader;

public class ProviderHelper {

    public static void reportInvalidResponseException(
            WebServiceResponse webServiceResponse,
            boolean isJsonFormat) {

        String message = getErrorMessage(webServiceResponse);

        if (!isJsonFormat) {
            message =
                    StringHelper.removeToPrefixAndFromPostfix(
                            "{\"error\":\"", "\"}", message);
        }

        ExceptionHelper.reportRuntimeException(
                "Request failed. Response status: " + webServiceResponse.getResponseStatus() +
                        ". Message: " + message);
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
