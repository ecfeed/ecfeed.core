/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.ecfeed.core.utils.ExceptionHelper;


public class HttpCommunicator implements IHttpCommunicator {

	@Override
	public String sendGetRequest(String url, List<HttpProperty> properties, int timeoutInSeconds) throws RuntimeException {

		URL obj;
		try {
			obj = new URL(url);

			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

			connection.setRequestMethod("GET");
			connection.setConnectTimeout(1000*timeoutInSeconds);
			connection.setReadTimeout(1000*timeoutInSeconds);

			if (properties != null) {
				for (HttpProperty httpProperty : properties) {
					connection.setRequestProperty(httpProperty.getKey(), httpProperty.getValue());
				}
			}

			if (connection.getResponseCode() != 200) {
				ExceptionHelper.reportRuntimeException("Http get request failed.");
			}

			return getResponse(connection);
		} catch (Exception e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
			return null;
		}
	}

	private static String getResponse(HttpURLConnection connection) throws IOException {

		BufferedReader reader = 
				new BufferedReader(
						new InputStreamReader(connection.getInputStream()));

		StringBuffer response = new StringBuffer();

		String inputLine;

		try {
			while ((inputLine = reader.readLine()) != null) {
				response.append(inputLine);
				response.append("\n");
			}
		} finally {

			reader.close();
		}

		return response.toString();
	}

}
