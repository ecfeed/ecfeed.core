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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DiskFileHelper {

	// TODO - check if used
	public static final String JAVA_EXTENSION = "java";
	public static final String APK_EXTENSION = "apk";
	public static final String BIN_SUBDIRECTORY = "bin";

	public static boolean fileExists(String pathWithFileName) {
		
		File file = new File(pathWithFileName);

		if(file.exists() && !file.isDirectory()) {
			return true;
		}
		return false;
	}

	public static long fileModificationTime(String path) {
		File file = new File(path);

		if (!file.exists()) {
			return 0;
		}

		return file.lastModified();
	}

	public static void createNewFile(String pathWithFileName) {
		
		File file = new File(pathWithFileName);
		if (file.exists()) {
			ExceptionHelper.reportRuntimeException("File: " + pathWithFileName + " already exists.");
		}

		boolean fileCreated = false; 
		try {
			fileCreated = file.createNewFile();
		} catch (IOException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}

		if (!fileCreated) {
			ExceptionHelper.reportRuntimeException("Can not create file: " + pathWithFileName + " .");
		}
	}

	public static void deleteFile(String pathWithFileName) {
		File file = new File(pathWithFileName);
		if (!file.delete()) {
			ExceptionHelper.reportRuntimeException("Can not delete file: " + pathWithFileName + " .");
		}
	}

	public static void deleteFilesFromFolder(String folderName) {

		File[] files = getFilesFromFolder(folderName);

		for (File file : files) {
			deleteFile(file);
		}
	}

	private static void deleteFile(File file) {

		if (!file.isFile()) {
			return;
		}

		if (!file.delete()) {
			ExceptionHelper.reportRuntimeException("Can not delete file: " + file.getAbsolutePath());
		}
	}

	public static File[] getFilesFromFolder(String folderName) {

		File folderFile = new File(folderName);
		File[] files = folderFile.listFiles();
		return files;
	}

	public static void deleteEmptyFolder(String folder) {

		File file = new File(folder);

		if (!file.isDirectory()) {
			return; 
		}

		if (!file.delete()) {
			ExceptionHelper.reportRuntimeException("Can not delete folder: " + folder);
		}

	}

	public static void saveStringToFile(String pathWithFileName, String newContents) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(pathWithFileName, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			ExceptionHelper.reportRuntimeException(
					"Can not write string to file: " + pathWithFileName + " ,reason: " + e.getMessage());
		}
		writer.print(newContents);
		writer.close();
	}

	public static String readStringFromFile(String pathWithFileName) {
		String result = null;
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(pathWithFileName));
			result = new String(encoded, "UTF-8");
		} catch (IOException e) {
			ExceptionHelper.reportRuntimeException(
					"Can not read string from file: " + pathWithFileName + " ,reason: " + e.getMessage());
		}
		return result;
	}

}
