package com.ecfeed.core.webservice.utils;

import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.webservice.util.RemoteHostHelper;

public class GenServiceHelper {
	
	public static boolean isHostIsAlive(String genServiceIp) {

		String [] tokens = StringHelper.splitIntoTokens(genServiceIp, ":");

		String host = tokens[1];

		if (StringHelper.startsWithPrefix("//", host)) {
			host = host.substring(2);
		}

		return RemoteHostHelper.isHostAlive(host, getGenServicePort(), 5);
	}

	public static int getGenServicePort() {
		return 8095;
	}


}
