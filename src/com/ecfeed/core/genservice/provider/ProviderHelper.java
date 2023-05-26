package com.ecfeed.core.genservice.provider;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.webservice.client.WebServiceResponse;

import java.io.BufferedReader;

public class ProviderHelper {

    public static void reportInvalidResponseException(
            WebServiceResponse webServiceResponse,
            boolean isJsonFormat) {

        String responseMessage = getErrorMessage(webServiceResponse);
        int responseStatus = webServiceResponse.getResponseStatus();

        if (isJsonFormat) {
            responseMessage = StringHelper.removeToPrefixAndFromPostfix("{\"error\":\"", "\"}", responseMessage);
            responseMessage = responseMessage.replace("\\n", "\n");
        } 

        if (responseStatus >= 400 && responseStatus < 500) {
             ExceptionHelper.reportRuntimeException(
            		 "\nError code - " + responseStatus +
            		 "\nThe error seems to have been caused by the client." +
            		 "\nPlease make sure that generation options are correct." +
                     "\n\nError message:\n" + responseMessage);
        } else if (responseStatus >= 500 && responseStatus < 600) {
        	ExceptionHelper.reportRuntimeException(
        			"\nError code - " + responseStatus +
           		 	"\nThe error has been caused by the server." +
           		 	"\nPlease try again later." +
                    "\n\nError message:\n" + responseMessage);
        } else {
        	ExceptionHelper.reportRuntimeException(
        			"\nError code - " + responseStatus +
                    "\n\nError message:\n" + responseMessage);
        }
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
