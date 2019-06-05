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

import java.util.Collection;
import java.util.Formatter;
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

		return str.trim().isEmpty();
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

		for (int index = lastIndex; index >= 0; index--) {
			Character character = fromStr.charAt(index);

			if (Character.isDigit(character)) {
				return fromStr.substring(index);
			}
		}

		return fromStr;
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

	public static String getLastToken(String tokenizedString, String tokenSeparator) {

		int separatorPosition = tokenizedString.lastIndexOf(tokenSeparator);

		if (separatorPosition == -1) {
			return null;
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

	// TODO - create specialized method for separating class from method in MethodSignatureHelper or MethodNode
	public static String getAllBeforeLastToken(String packageWithClass, String tokenSeparator) { 

		int separatorPosition = packageWithClass.lastIndexOf(tokenSeparator);

		if (separatorPosition == -1) {
			return null;
		}
		return packageWithClass.substring(0, separatorPosition);
	}

	public static String getPackageWithClass(String methodSignature) {

	    String signatureWithoutModifiers = methodSignature
                .replaceAll("public", "")
                .replaceAll("void", "");
	    String simplifiedSignature = signatureWithoutModifiers
                .substring(0, signatureWithoutModifiers.indexOf('('));
	    String packageWithClass = simplifiedSignature
                .substring(0, simplifiedSignature.lastIndexOf('.'));

	    return packageWithClass.trim();
    }

    public static String getMethodShortSignature(String methodSignature) {

	    String simplifiedSignature = methodSignature
                .substring(0, methodSignature.indexOf('('));
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

	public static Byte convertToByte(String str) throws NumberFormatException {

		Long result = convertToLong(str);

		Long maxIntValue = new Long(Byte.MAX_VALUE);
		if (result > maxIntValue) {
			throw new NumberFormatException();
		}

		Long minIntValue = new Long(Byte.MIN_VALUE);
		if (result < minIntValue) {
			throw new NumberFormatException();
		}		

		return result.byteValue(); 
	}

	public static Short convertToShort(String str) throws NumberFormatException {

		Long result = convertToLong(str);

		Long maxIntValue = new Long(Short.MAX_VALUE);
		if (result > maxIntValue) {
			throw new NumberFormatException();
		}

		Long minIntValue = new Long(Short.MIN_VALUE);
		if (result < minIntValue) {
			throw new NumberFormatException();
		}		

		return result.shortValue(); 
	}	

	public static Integer convertToInteger(String str) throws NumberFormatException {

		Long result = convertToLong(str);

		Long maxIntValue = new Long(Integer.MAX_VALUE);
		if (result > maxIntValue) {
			throw new NumberFormatException();
		}

		Long minIntValue = new Long(Integer.MIN_VALUE);
		if (result < minIntValue) {
			throw new NumberFormatException();
		}		

		return result.intValue(); 
	}

	public static Long convertToLong(String str) throws NumberFormatException {

		Long result = convertToLongDirectly(str);

		if (result != null) {
			return result;
		}

		return convertToLongViaDouble(str);
	}

	public static Long convertToLongDirectly(String str) {

		Long result = null;

		try {
			result = Long.parseLong(str);
		} catch (NumberFormatException e){
			return null;
		}

		return result;
	}

	private static Long convertToLongViaDouble(String str) throws NumberFormatException {

		Double dblResult = null;

		dblResult = Double.parseDouble(str);

		if (dblResult != Math.floor(dblResult)) {
			throw new NumberFormatException();
		}

		return dblResult.longValue();
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
	
}
