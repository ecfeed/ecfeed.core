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

import java.util.Arrays;

public class CorrelationHelper {

	enum IgnoreCase {
		ON,
		OFF
	}

	public static double calculateCorrelation(String str1, String str2, IgnoreCase ignoreCase) {

		if (str1 == null) {
			ExceptionHelper.reportRuntimeException("Empty string 1.");
		}

		if (str2 == null) {
			ExceptionHelper.reportRuntimeException("Empty string 2.");
		}

		int maxLength = calculateCommonSizeForArrays(str1, str2);

		String str1b = convertStr(str1, ignoreCase);		
		String str2b = convertStr(str2, ignoreCase);

		int[] array1 = createArrayOfInts(maxLength, str1b);
		int[] array2 = createArrayOfInts(maxLength, str2b);

		double result = calculateCorrelationForArrays(array1, array2);

		return Math.abs(result);
	}

	private static int calculateCommonSizeForArrays(String str1, String str2) {

		int size = Math.max(str1.length(), str2.length());

		size++; // create base level filled with 0 do distinguish "ab" from "AB"

		return size;
	}

	private static String convertStr(String str, IgnoreCase ignoreCase) {

		if (ignoreCase == IgnoreCase.ON) {
			return str.toUpperCase();
		} else {
			return str;
		}
	}

	private static int[] createArrayOfInts(int length, String str) {

		int[] array = new int[length];
		Arrays.fill(array, 0);

		int stringLength = str.length();

		for (int index = 0; index < stringLength; index++) {

			array[index] = (int)str.charAt(index);
		}

		return array;
	}

	private static double calculateCorrelationForArrays(int[] arrayX, int[] arrayY) {

		if (arrayX == null) {
			ExceptionHelper.reportRuntimeException("Empty array 1.");
		}

		if (arrayY == null) {
			ExceptionHelper.reportRuntimeException("Empty array 2.");
		}

		if (arrayX.length != arrayY.length) {
			ExceptionHelper.reportRuntimeException("Arrays have different lengths.");
		}

		double result = calculateCorrelationForArraysIntr(arrayX, arrayY);

		if (Double.isNaN(result)) {
			return 0;
		}

		return result;
	}

	private static double calculateCorrelationForArraysIntr(int[] arrayX, int[] arrayY) {

		double sumOfX = 0.0;
		double sumOfY = 0.0;
		double sumOfXX = 0.0;
		double sumOfYY = 0.0;
		double sumOfXY = 0.0;

		int arrayLength = arrayX.length;

		for (int index = 0; index < arrayLength; ++index) {

			double x = arrayX[index];
			double y = arrayY[index];

			sumOfX += x;
			sumOfY += y;
			sumOfXX += x * x;
			sumOfYY += y * y;
			sumOfXY += x * y;
		}

		double covariation = sumOfXY / arrayLength - sumOfX * sumOfY / arrayLength / arrayLength;

		double standardErrorOfX = Math.sqrt(sumOfXX / arrayLength -  sumOfX * sumOfX / arrayLength / arrayLength);

		double standardErrorOfY = Math.sqrt(sumOfYY / arrayLength -  sumOfY * sumOfY / arrayLength / arrayLength);

		double normalizedCovariation = covariation / standardErrorOfX / standardErrorOfY;

		return normalizedCovariation;
	}	


	public static void main(String args[]) 
	{  
		//		double result = calculateCorrelation(new int[] {1, 2, 3, 4}, new int[] {1, 2, 3, 4});
		//		System.out.println(result);
		//
		//		result = calculateCorrelation(new int[] {1, 2, 2, 4}, new int[] {1, 2, 3, 4});
		//		System.out.println(result);
		//
		//		result = calculateCorrelation(new int[] {1, 3, 2, 4}, new int[] {1, 2, 3, 4});
		//		System.out.println(result);
		//
		//		result = calculateCorrelation(new int[] {1, 1, 1, 4}, new int[] {1, 2, 3, 4});
		//		System.out.println(result);
		//
		//		result = calculateCorrelation(new int[] {1, 1, 2, 1}, new int[] {1, 2, 3, 4});
		//		System.out.println(result);
		//
		//		result = calculateCorrelation(new int[] {1, 1, 1, 1}, new int[] {1, 2, 3, 4});
		//		System.out.println(result);

		double result = calculateCorrelation("ABCD", "ABCD", IgnoreCase.ON);
		System.out.println(result);

		//		result = calculateCorrelation("abcd", "ABCD", IgnoreCase.OFF);
		//		System.out.println(result);

		result = calculateCorrelation("ABcD", "ABCD", IgnoreCase.OFF);
		System.out.println(result);

		result = calculateCorrelation("ABxD", "ABCD", IgnoreCase.OFF);
		System.out.println(result);

		//		result = calculateCorrelation("abcd", "ABCD", IgnoreCase.OFF);
		//		System.out.println(result);
		//		
		//		result = calculateCorrelation("ABCD", "ABC", IgnoreCase.ON);
		//		System.out.println(result);
		//
		//		result = calculateCorrelation("ABC", "ABCD", IgnoreCase.ON);
		//		System.out.println(result);
		//
		//		result = calculateCorrelation("AAAD", "ABCD", IgnoreCase.ON);
		//		System.out.println(result);
		//
		//		result = calculateCorrelation("AD", "ABCD", IgnoreCase.ON);
		//		System.out.println(result);
	}  
}
