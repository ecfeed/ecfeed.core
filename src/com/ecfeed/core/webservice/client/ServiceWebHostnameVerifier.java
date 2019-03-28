package com.ecfeed.core.webservice.client;

import com.ecfeed.core.utils.ExceptionHelper;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

// TODO - convert to helper with static methods
public class ServiceWebHostnameVerifier {

	private ServiceWebHostnameVerifier() {
		ExceptionHelper.reportRuntimeException("It is not possible to create instances of this class.");
	}

	static HostnameVerifier noSecurity() {
		
		HostnameVerifier verifier = new HostnameVerifier() {

			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
			
		};
		
		return verifier;
	}
	
}
