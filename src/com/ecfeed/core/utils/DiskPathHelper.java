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
import java.nio.file.Paths;

public class DiskPathHelper { // TODO - rename to DiskPathHelper
	
	private static final String EXTENSION_SEPARATOR = ".";
	// TODO - this looks like an ERROR because this is not platform independent (not working on Windows)
	// TODO - potentially it status management in Windows plugin would fail (get implementation status etc.)
	private static final String FILE_SEPARATOR = File.separator; // platform independent
	private static final String CURRENT_DIR = "."; // TODO - private

	public static String getApplicationPath() {
		return Paths.get("").toAbsolutePath().toString();
	}

	public static String getPathSeparator() {
		return FILE_SEPARATOR;
	}
	
	public static String getGetCurrentDirPathTag() {
		return CURRENT_DIR;
	}	
	
	public static String createFileName(String fileNameWithoutExtension, String extension) {
		return fileNameWithoutExtension + EXTENSION_SEPARATOR + extension;
	}

	public static String joinPathWithFile(String path, String file) {
		return joinItems(path, file);
	}

	public static String joinSubdirectory(String path, String subdir) {
		return joinItems(path, subdir);
	}	

	private static String joinItems(String item1, String item2) {
		
		if (item1.endsWith(FILE_SEPARATOR)) {
			return item1 + item2;
		}

		return item1 + FILE_SEPARATOR + item2;
	}	
	
	public static String extractFileName(String pathWithFileName) {
		return StringHelper.getLastToken(pathWithFileName, FILE_SEPARATOR);
	}

	public static String extractPathWithSeparator(String pathWithFileName) {
		String fileName = StringHelper.getLastToken(pathWithFileName, FILE_SEPARATOR);
		return StringHelper.removeFromPostfix(fileName, pathWithFileName);
	}

	public static String extractPathWithoutSeparator(String pathWithFileName) {
		String pathWithSeparator = extractPathWithSeparator(pathWithFileName);
		String pathWithoutSeparator = StringHelper.removeFromPostfix(FILE_SEPARATOR, pathWithSeparator);
		return pathWithoutSeparator;
	}

	public static String extractFileNameWithoutExtension(String fileNameWithExtension) {
		return StringHelper.getFirstToken(fileNameWithExtension, EXTENSION_SEPARATOR);
	}
	
	
}
