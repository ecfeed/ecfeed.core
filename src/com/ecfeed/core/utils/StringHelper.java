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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {

	public static boolean isNullOrEmpty(String str) {

		if (str == null) {
			return true;
		}
		if (str.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isNullOrBlank(String str) {

		if (str == null) {
			return true;
		}
		if (isTrimmedEmpty(str)) {
			return true;
		}
		return false;
	}

	public static boolean hasNonBlankContents(String str) {

		if (isNullOrBlank(str)) {
			return false;
		}
		return true;
	}

	public static boolean isTrimmedEmpty(String str) {

		String str2 = new String(str);

		if (str2.trim().isEmpty()) {
			return true;
		}

		return false;
	}

	public static String cutToMaxSize(String stringToCut, int maxSize) {

		if (stringToCut.length() <= maxSize) {
			return stringToCut;
		}

		return stringToCut.substring(0, maxSize);
	}

	public static String removeToPrefix(String prefix, String fromStr) {

		int index = fromStr.indexOf(prefix);

		if (index == -1) {
			return fromStr;
		}
		return fromStr.substring(index + prefix.length());
	}

	public static String removeFromPostfix(String postfix, String fromStr) {

		int index = fromStr.lastIndexOf(postfix);

		if (index == -1) {
			return fromStr;
		}
		return fromStr.substring(0, index);
	}	

	public static String removeFromLastNewline(String fromString) {

		return removeFromPostfix(newLine(), fromString);
	}

	public static String removeFromNumericPostfix(String fromString) {

		String numericPostfix = findNumericPostfix(fromString);

		if (numericPostfix == null) {
			return fromString;
		}

		return removeStrgAtEnd(numericPostfix, fromString);
	}

	public static String findNumericPostfix(String fromStr) {

		int lastIndex = fromStr.length() - 1;
		Character lastChar = fromStr.charAt(lastIndex);

		if (!Character.isDigit(lastChar)) {
			return null;
		}

		while (lastIndex > 0 && Character.isDigit(fromStr.charAt(lastIndex - 1))) {
			lastIndex--;
		}

		return fromStr.substring(lastIndex);
	}

	public static String removeStrgAtEnd(String pattern, String strg) {

		int index = strg.lastIndexOf(pattern);

		if (index == -1) {
			return strg;
		}

		if (index != (strg.length() - pattern.length())) {
			return strg;
		}

		return strg.substring(0, index);
	}

	public static String removeNewlineAtEnd(String fromString) {

		return removeStrgAtEnd(newLine(), fromString);
	}	

	public static String appendNewline(String line) {

		return line + StringHelper.newLine();
	}

	public static String appendSpacesToLength(String line, int lengthAfterAppend) {

		if (line.length() >= lengthAfterAppend) {
			return line;
		}

		return line + createString(" ", lengthAfterAppend - line.length());
	}	

	public static String insertSpacesToLength(String line, int lengthAfterInsert) {

		return insertCharsToLength(line, " ", lengthAfterInsert);
	}	

	public static String insertZerosToLength(String line, int lengthAfterInsert) {

		return insertCharsToLength(line, "0", lengthAfterInsert);
	}	

	public static String insertCharsToLength(String line, String paddingChar, int lengthAfterInsert) {

		if (line.length() >= lengthAfterInsert) {
			return line;
		}

		return createString(paddingChar, lengthAfterInsert - line.length()) + line;
	}	

	public static String centerStringToLength(String line, int lengthAfterInsert) {

		if (line.length() >= lengthAfterInsert) {
			return line;
		}

		int spacesBefore = (lengthAfterInsert - line.length()) / 2;
		String leadingSpaces = createString(" ", spacesBefore);

		return appendSpacesToLength(leadingSpaces + line, lengthAfterInsert);
	}

	public static String newLine() {

		return System.lineSeparator();
	}

	public static String getSubstringStartingFromFindString(String str, String findString) {

		int separatorPosition = str.indexOf(findString);

		if (separatorPosition == -1) {
			return null;
		}

		return str.substring(separatorPosition);
	}

	public static String getLastToken(String tokenizedString, String tokenSeparator) {

		int separatorPosition = tokenizedString.lastIndexOf(tokenSeparator);

		if (separatorPosition == -1) {
			return null;
		}
		return tokenizedString.substring(separatorPosition+1);
	}

	public static String getLastTokenOrInputString(String tokenizedString, String tokenSeparator) {

		int separatorPosition = tokenizedString.lastIndexOf(tokenSeparator);

		if (separatorPosition == -1) {
			return tokenizedString;
		}
		return tokenizedString.substring(separatorPosition+1);
	}

	public static String getFirstToken(String tokenizedString, String tokenSeparator) {

		int separatorPosition = tokenizedString.indexOf(tokenSeparator);

		if (separatorPosition == -1) {
			return null;
		}
		return tokenizedString.substring(0, separatorPosition);
	}

	public static String[] splitIntoTokens(String tokenizedString, String tokenSeparatorRegex) {

		return tokenizedString.split(tokenSeparatorRegex);
	}

	public static String getAllBeforeLastToken(String str, String tokenSeparator) { 

		int separatorPosition = str.lastIndexOf(tokenSeparator);

		if (separatorPosition == -1) {
			return null;
		}

		return str.substring(0, separatorPosition);
	}

	public static String getPackageWithClass(String methodSignature) {

		String signatureWithoutModifiers = methodSignature
				.replaceAll("public", "")
				.replaceAll("void", "");

		String signatureSimplified;

		if (signatureWithoutModifiers.contains("(")) {
			signatureSimplified = signatureWithoutModifiers.substring(0, signatureWithoutModifiers.indexOf('('));
		} else {
			signatureSimplified = signatureWithoutModifiers;
		}

		String packageWithClass;

		if (signatureSimplified.contains(".")) {
			packageWithClass = signatureSimplified.substring(0, signatureSimplified.lastIndexOf('.'));
		} else {
			packageWithClass = "";
		}

		return packageWithClass.trim();
	}

	public static String getMethodShortSignature(String methodSignature) {

		String simplifiedSignature;

		if (methodSignature.contains("(")) {
			simplifiedSignature = methodSignature.substring(0, methodSignature.indexOf('('));
		} else {
			simplifiedSignature = methodSignature;
		}

		int indexOfLastDot = simplifiedSignature.lastIndexOf('.') + 1;
		String methodShortSignature = methodSignature.substring(indexOfLastDot);

		return methodShortSignature.trim();

	}

	public static boolean isCharAt(int index, String strg, String chr) {

		if (strg.charAt(index) == chr.charAt(0)) {
			return true;
		}
		return false;
	}

	public static String containsOnlyAllowedChars(String str, String allowedCharsRegex) {

		int len = str.length();

		for (int index = 0; index < len; ++index) {

			String substr = str.substring(index, index+1);
			if (!substr.matches(allowedCharsRegex)) {
				return substr;
			}
		}
		return null;
	}

	public static boolean startsWithPrefix(String prefix, String str) {

		int index = str.indexOf(prefix);

		if (index == 0) {
			return true;
		}

		return false;
	}

	public static boolean endsWithPostfix(String postfix, String str) {

		return str.endsWith(postfix);
	}

	public static int countOccurencesOfChar(String str, char charToCount) {

		int len = str.length();
		int occurences = 0;
		String strgToCount = Character.toString(charToCount);

		for (int index = 0; index < len; ++index) {

			String substr = str.substring(index, index+1);

			if (strgToCount.equals(substr)) {
				occurences++;
			}
		}
		return occurences;
	}

	public static String createString(String baseString, int repetitions) {

		StringBuilder builder = new StringBuilder();

		for (int cnt = 0; cnt < repetitions; ++ cnt) {
			builder.append(baseString);
		}

		return builder.toString();
	}

	public static boolean isEqual(String s1, String s2) {

		if (ObjectHelper.isEqual(s1, s2)) {
			return true;
		}

		return false;
	}

	public static boolean isEqualIgnoreCase(String s1, String s2) {

		if (s1 == null || s2 == null) {
			return ObjectHelper.isEqualWhenOneOrTwoNulls(s1, s2);
		}

		if (s1.equalsIgnoreCase(s2)) {
			return true;
		}

		return false;
	}

	public static String getSubstringWithBoundaries(String source, int boundaryChar) {

		if (source == null) {
			return null;
		}

		int begIndex = source.indexOf(boundaryChar);
		if (begIndex == -1) {
			return null;
		}

		if (begIndex >= source.length() - 1) {
			return null;
		}

		int endIndex = source.indexOf(boundaryChar, begIndex+1);
		if (endIndex == -1) {
			return null;
		}		

		return source.substring(begIndex, endIndex+1);
	}

	public static String replaceSubstringWithBoundaries(String source, int boundaryChar, String strToReplace) {

		String substr = getSubstringWithBoundaries(source, boundaryChar);
		if (substr == null) {
			return null;
		}

		return source.replace(substr, strToReplace);
	}

	public static String convertToMultilineString(Collection<String> strings){

		String consolidated = "";
		for(String string : strings){
			consolidated += string + "\n";
		}
		return consolidated;
	}	

	public static String getMatch(String source, String regularExpression) {
		return getMatch(source, regularExpression, 0);
	}

	public static String getMatch(String source, String regularExpression, int index) {

		Matcher matcher = Pattern.compile(regularExpression).matcher(source);

		String expressionSequence = null;

		int indexCounter = 0;

		while(matcher.find()) {

			expressionSequence = matcher.group();

			if (index == indexCounter) {
				return expressionSequence;
			}

			indexCounter++;
		}				

		return null;
	}

	public static String removeToPrefixAndFromPostfix(String prefix, String postfix, String fromString) {

		String s1 = StringHelper.removeToPrefix(prefix, fromString);
		String s2 = StringHelper.removeFromPostfix(postfix, s1);

		return s2;
	}

	public static String bytesToHexString(final byte[] hash) {

		Formatter formatter = new Formatter();

		for (byte b : hash) {
			formatter.format("%02x", b);
		}

		String result = formatter.toString();
		formatter.close();

		return result;
	}

	public static boolean containsOnly(char character, String str) {

		for (int i = 0; i < str.length(); i++){
			char charInStr = str.charAt(i);

			if (charInStr != character) {
				return false;
			}
		}

		return true;
	}

	public static String convertWhiteCharsToSingleSpaces(String str) {

		str = str.replace("\n", " ");
		str = str.replace("\t", " ");

		while (str.contains("  ")) {
			str = str.replace("  ", " ");
		}

		return str;
	}

	public static int findFirstDifference(String str1, String str2) {

		if (str1 == null || str2 == null) {
			ExceptionHelper.reportRuntimeException("Invalid parameters in function find first difference.");
		}

		int length1 = str1.length();
		int length2 = str2.length();

		int minLength = Math.min(length1, length2);

		int index = 0;

		for (; index < minLength; index++) {

			char chr1 = str1.charAt(index);
			char chr2 = str2.charAt(index);

			if (chr1 != chr2) {
				return index;
			}
		}

		if (length1 == length2) {
			return -1; // no differences
		}

		return index;  // the first char of the longer string
	}

	public static String convertListToStringWithSeparators(List<String> listOfStrings, String separator) {

		int listSize = listOfStrings.size();
		int lastIndex = listSize - 1;

		String convertedString = "";

		for (int index = 0; index < listSize; index++) {

			convertedString += listOfStrings.get(index);

			if (index < lastIndex) {
				convertedString += separator;
			}
		}

		return convertedString;
	}

	public static List<String> removeDuplicates(List<String> strings) {

		HashSet<String> set = new HashSet<String>(strings);
		List<String> result = new ArrayList<>(set);

		return result;
	}

	public static String isEqualByLines(String[] expectedResultLines, String[] resultLines) {


		int minLines = Math.min(expectedResultLines.length, resultLines.length);

		for (int lineIndex = 0; lineIndex < minLines; lineIndex++) {

			String expectedLine = expectedResultLines[lineIndex];
			expectedLine = expectedLine.replace("\r", "");

			String resultLine = resultLines[lineIndex];
			resultLine = resultLine.replace("\r", "");

			if (!StringHelper.isEqual(expectedLine, resultLine)) {
				return ("Line: " + (lineIndex + 1) + " differs.");
			}
		}
		
		if (expectedResultLines.length != resultLines.length) {
			return "Count of lines does not match";
		}
		
		return null;
	}	

	public static void compareStrings(String str1, String str2, String errorMessage) {

		if (StringHelper.isEqual(str1, str2)) {
			return;
		}

		ExceptionHelper.reportRuntimeException(errorMessage);
	}

	
}
