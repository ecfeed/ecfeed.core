package com.ecfeed.core.JavaAPI;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

public class EcFeedTest {

	String DEFAULT_GENSERVER = "gen.ecfeed.com";
	String DEFAULT_KEYSTORE_PASSWORD = "changeit";
	
	String genserver;
	String model;
	String keystore_path;
	String password;
	
	// Constructor;
	EcFeedTest(){
		
		this.genserver = DEFAULT_GENSERVER;
		this.model = "";
		this.keystore_path = default_key_store_path();
		this.password = DEFAULT_KEYSTORE_PASSWORD;
		
	}

	// export existing test data from ecFeed server
	Enumeration<String> export() throws MalformedURLException, IOException {
		
		InputStream input = new URL(DEFAULT_GENSERVER).openStream();
		
		Reader reader = new InputStreamReader(input, "UTF-8");
		
		Enumeration<String> test_export = null;
		
		return test_export;
	}

	// generate "on-the-air" test data 
	Enumeration<Object[]> generate() {
		
		Enumeration<Object[]> testdata_export = null;
		
		return testdata_export;
	}

	
	String default_key_store_path() {
		
		String OS = System.getProperty("os.name").toLowerCase();

		if (OS == "win32")
			return expanduser("~/ecfeed/security.p12");
		else
			return expanduser("~/.ecfeed/security.p12");
	}
	

	String expanduser(String path) {
		
		String user = System.getProperty("user.home");
		return path.replaceFirst("~", user);
	}

}
