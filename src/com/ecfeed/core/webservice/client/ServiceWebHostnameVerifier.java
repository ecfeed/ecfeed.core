package com.ecfeed.core.webservice.client;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public class ServiceWebHostnameVerifier {

	private ServiceWebHostnameVerifier() {
		RuntimeException exception = new RuntimeException(Localization.bundle.getString("classInitializationError"));
		Logger.exception(exception);
		throw exception;
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
