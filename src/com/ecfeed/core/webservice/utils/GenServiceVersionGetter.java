package com.ecfeed.core.webservice.utils;

import java.io.BufferedReader;
import java.util.Optional;

import com.ecfeed.core.webservice.client.GenWebServiceClient;
import com.ecfeed.core.webservice.client.GenWebServiceClientType;
import com.ecfeed.core.webservice.client.WebServiceResponse;

public class GenServiceVersionGetter {
	
	public static String getVersion(String genServiceIp, GenWebServiceClientType genWebServiceClientType) {

		if (!GenServiceHelper.isHostIsAlive(genServiceIp)) {
			return "NOT AVAILABLE"; 
		}

		Optional<String> emptyKeyStorePath = Optional.empty();

		GenWebServiceClient genWebServiceClient = new GenWebServiceClient(
				genServiceIp,
				GenWebServiceClient.getGenServiceVersionEndPoint(),
				genWebServiceClientType.toString(),
				emptyKeyStorePath); 
		
		WebServiceResponse webServiceResponse = genWebServiceClient.sendGetRequest();
		
        if (!webServiceResponse.isResponseStatusOk()) {
            return "ERROR";
        }

        BufferedReader bufferedReader = webServiceResponse.getResponseBufferedReader();

        String version;
        
        try {
            version = bufferedReader.readLine();
        } catch (Exception e) {
        	return "ERROR";
        }
		
		return version;
	}

}
