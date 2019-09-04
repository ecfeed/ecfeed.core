package com.ecfeed.core.webservice.util;

import com.ecfeed.core.utils.StringHelper;

public class GenServiceHelper {
	
	public static boolean isHostIsAlive(String genServiceIp) {

		String [] tokens = StringHelper.splitIntoTokens(genServiceIp, ":");

		String host = tokens[1];

		if (StringHelper.startsWithPrefix("//", host)) {
			host = host.substring(2);
		}
		
		int port = 443;
		
		if (tokens.length > 2) {
			port = Integer.parseInt(tokens[2]);
		}

		return RemoteHostHelper.isHostAlive(host, port, 5);
	}

}
