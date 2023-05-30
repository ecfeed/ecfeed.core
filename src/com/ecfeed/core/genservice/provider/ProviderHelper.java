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
        
        String message = "\nError code - " + responseStatus;

        if (responseStatus >= 400 && responseStatus < 500) {
        	message +=  "\nThe error seems to have been caused by the client." +
           		 "\nPlease make sure that generation options are correct.";
        } else if (responseStatus >= 500 && responseStatus < 600) {
        	message += "\nThe error has been caused by the server." +
           		 	"\nPlease try again later.";
        }
        
        message +=  "\n\nError message:\n" + responseMessage;
        
        ExceptionHelper.reportRuntimeException(message);
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
