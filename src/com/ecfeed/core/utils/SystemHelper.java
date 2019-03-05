/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class SystemHelper {

	public static String getSystemTemporaryDir() {

		return System.getProperty("java.io.tmpdir");
	}

	public static String getOperatingSystemType() {

		String systemName = System.getProperty("os.name");

		if (systemName.contains("Windows")) {
			return "Windows";
		}

		return systemName;
	}	

	public static String getOperatingSystemArchitecture() {

		return System.getProperty("os.arch");
	}	

	public static String getUserName() {

		return System.getProperty("user.name");
	}	

	public static boolean isOperatingSystemLinux() {

		String os = getOperatingSystemType();

		if (os.equals("Linux")) {
			return true;
		}
		return false;
	}	

	public static boolean isOperatingSystemMacOs() {

		String os = getOperatingSystemType();

		if (os.equals("Mac OS X")) {
			return true;
		}
		return false;
	}	

	public static String getLocalHostName() {

		try {
			InetAddress address = InetAddress.getLocalHost();
			return address.getHostName();
		} catch (UnknownHostException e) {
			return null;
		}
	}

	public static String getFormattedLocalHostName() {

		if (getLocalHostName() == null) {
			return "UNKNOWN";
		}

		return StringHelper.removeFromPostfix(".", getLocalHostName());
	}

	public static String getLocalHostIpAddress() {

		try {
			InetAddress address = InetAddress.getLocalHost();
			return address.getHostAddress();
		} catch (UnknownHostException e) {
			return null;
		}

	}

	public static String createEcId() {

		String source = 
				getFormattedLocalHostName() + "+" +
						getUserName() + "+" +
						getOperatingSystemType();

		return CheckSumHelper.calculateSha1(source);
	}

}