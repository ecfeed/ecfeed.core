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

public class StringSimilarityCalculator {

	public static double calculateSimilarityIndex(
			ParameterConversionItemPartForRaw item1, 
			ParameterConversionItemPartForRaw item2) {

		if (item1 == null) {
			ExceptionHelper.reportRuntimeException("Empty item1 1.");
		}

		if (item2 == null) {
			ExceptionHelper.reportRuntimeException("Empty item 2.");
		}

		if (item2.getName().length() > item1.getName().length()) {

			ParameterConversionItemPartForRaw tmp = item1;
			item1 = item2;
			item2 = tmp;
		}

		double result1 = 
				calculateSimilarityForStrings(
						item1.getType().toString(), 
						item2.getType().toString());

		double result2 = calculateSimilarityForStrings(item1.getName(), item2.getName());

		return (result1 + result2) / 2;
	}

	private static double calculateSimilarityForStrings(String str1, String str2) {

		int similarityValue11 = calculateSimilarityValue(str1, 0, str1, 0, str1.length());
		int similarityValue22 = calculateSimilarityValue(str2, 0, str2, 0, str2.length());

		int bestSimilarityValue = Math.max(similarityValue11, similarityValue22);

		int similarityValue12 = calculateSimilarityValue(str1, str2);

		double similarity = (double)similarityValue12 / (double)bestSimilarityValue;

		return similarity;
	}

	private static int calculateSimilarityValue(String longerStr, String shorterStr) {

		int shorterLength = shorterStr.length();
		int longerLength = longerStr.length();

		if (longerLength == shorterLength) {
			return calculateSimilarityValue(longerStr, 0, shorterStr, 0, longerLength);
		}

		int lengthDifference = longerLength - shorterLength;

		int maxSimilarityValue = 0;

		for (int shift = 0; shift <= lengthDifference; shift++) {

			int similarityValue = calculateSimilarityValue(
					longerStr, shift, 
					shorterStr, 0,
					shorterLength);

			if (similarityValue > maxSimilarityValue) {
				maxSimilarityValue = similarityValue;
			}
		}

		return maxSimilarityValue;
	}

	private static int calculateSimilarityValue(
			String str1, int index1, 
			String str2, int index2,
			int length) {

		int totalSimilarityValue = 0;

		for (int counter = 0; counter < length; counter++) {

			char char1 = str1.charAt(index1 + counter);
			char char2 = str2.charAt(index2 + counter);

			int similarityValue = calculateSimilarityForChars(char1, char2);

			totalSimilarityValue += similarityValue;
		}

		return totalSimilarityValue;
	}

	private static int calculateSimilarityForChars(char char1, char char2) {

		if (char1 == char2) {
			return 3;
		}

		if (Character.toUpperCase(char1) == Character.toUpperCase(char2)) {
			return 2;
		}

		if (Character.isLetter(char1) && Character.isLetter(char2)) {
			return 1;
		}

		if (Character.isDigit(char1) && Character.isDigit(char2)) {
			return 1;
		}

		return 0;
	}

}
