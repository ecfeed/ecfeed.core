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

import java.io.File;
import java.net.URI;

public class UriHelper {

	public static String convertUriToFilePath(URI uri) {
		String pathWithFileName = uri.toString();

		pathWithFileName = StringHelper.removeToPrefix("file:", pathWithFileName);
		pathWithFileName = removePrefixForWindows(pathWithFileName);
		pathWithFileName = convertUriSeparatorsToPathSeparators(pathWithFileName);
		pathWithFileName = convertSpaces(pathWithFileName);

		return pathWithFileName;
	}

	private static String removePrefixForWindows(String pathWithFileName) {

		final String uriSeparator = "/";
		final char windowsDiskLetterSeparator = ':';

		if (pathWithFileName.charAt(0) == uriSeparator.charAt(0) &&
				pathWithFileName.charAt(2) == windowsDiskLetterSeparator) {
			pathWithFileName = StringHelper.removeToPrefix(uriSeparator, pathWithFileName);
		}

		return pathWithFileName;
	}

	private static String convertUriSeparatorsToPathSeparators(String pathWithFileName) {
		final String windowsSeparator = "\\"; 
		if (File.separator.equals(windowsSeparator)) {
			pathWithFileName = pathWithFileName.replace('/', windowsSeparator.charAt(0));
		}
		return pathWithFileName;
	}
	
	private static String convertSpaces(String pathWithFileName) {
		pathWithFileName = pathWithFileName.replace("%20", " ");
		return pathWithFileName;
	}

}
