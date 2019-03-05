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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class TextFileHelper {

	public static void append(String filename, String message) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
		writer.write(message);
		writer.close();
	}

	public static void appendLine(String filename, String message) throws IOException {
		String msg = StringHelper.appendNewline(message);
		append(filename, msg);
	}

	public static String readContent(String filePath) throws EcException {

		try {
			return readContentIntr(filePath);
		} catch (Exception e) {
			EcException.report(e.getMessage());
			return null;
		}
	}

	public static String readContentQuiet(String filePath) {

		try {
			return readContentIntr(filePath);
		} catch (Exception e) {
			return null;
		}
	}

	public static String readContentIntr(String filePath) throws Exception {

		InputStream inputStream = new FileInputStream(filePath);

		try {
			return readLines(inputStream);
		} finally {
			inputStream.close();
		}
	}

	private static String readLines(InputStream inputStream) throws IOException {

		StringBuilder resultStringBuilder = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		try {
			String line = bufferedReader.readLine();
			while (line != null) {
				resultStringBuilder.append(line).append("\n");
				line = bufferedReader.readLine();
			}	
		} finally {
			bufferedReader.close();
		}

		return resultStringBuilder.toString();
	}

	public static void writeContent(String filePath, String newContent) throws EcException {

		PrintWriter printWriter = null;
		try {

			printWriter = new PrintWriter(filePath);
			printWriter.println(newContent);
			printWriter.flush();

		} catch (FileNotFoundException e) {

			EcException.report(e.getMessage());

		} finally {

			printWriter.close();
		}
	}	
}
