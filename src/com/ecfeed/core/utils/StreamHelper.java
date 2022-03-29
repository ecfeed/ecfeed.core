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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class StreamHelper {

	private final static String UTF_8_CODING = "UTF-8";
	private final static String REGEX_BEGINNING_OF_TEXT = "\\A"; 

	public static String streamToString(InputStream is) {
		Scanner scanner = new Scanner(is, UTF_8_CODING);
		String result = null;

		try {
			result = readFromScanner(scanner);
		} finally {
			scanner.close();
		}
		return result;
	}

	private static String readFromScanner(Scanner scanner) {
		if (!scanner.hasNext()) {
			return new String();
		}
		// the entire content will be read 
		return scanner.useDelimiter(REGEX_BEGINNING_OF_TEXT).next();
	}

	public static FileOutputStream requireCreateFileOutputStream(String pathWithFileName) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(pathWithFileName);
		} catch (FileNotFoundException e) {
			ExceptionHelper.reportRuntimeException("Can not create output stream." + e.getMessage());
		}
		return outputStream;
	}

	public static FileInputStream openInputStream(String filePath) throws EcException {

		FileInputStream inputStream = null;

		try {
			inputStream = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			EcException.report(e.getMessage());
		}

		return inputStream;
	}

	public static void closeInputStream(FileInputStream inputStream) throws EcException {

		try {
			inputStream.close();
		} catch (IOException e) {
			EcException.report(e.getMessage());
		}
	}

	public static void writeAndFlushLine(String strg, OutputStream outputStream) {

		try {
			writeStringToStream(strg, outputStream);
			writeStringToStream("\n", outputStream);
			flushStream(outputStream);
		} catch (IOException e) {
			ExceptionHelper.reportRuntimeException("", e);
		}
	}

	public static void writeAndFlushNewLine(OutputStream outputStream) {

		try {
			writeStringToStream("\n", outputStream);
			flushStream(outputStream);
		} catch (IOException e) {
			ExceptionHelper.reportRuntimeException("", e);
		}
	}

	public static void writeAndFlushString(String str, OutputStream outputStream) {

		try {
			writeStringToStream(str, outputStream);
			flushStream(outputStream);
		} catch (IOException e) {
			ExceptionHelper.reportRuntimeException("", e);
		}
	}

	private static void writeStringToStream(String strg, OutputStream outputStream) throws IOException {

		outputStream.write(strg.getBytes());
	}

	private static void flushStream(OutputStream outputStream) throws IOException {

		outputStream.flush();
	}

}
