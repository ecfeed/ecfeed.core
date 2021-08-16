/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.serialization;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.utils.CShartLanguageHelper;
import com.ecfeed.core.utils.CppLanguageHelper;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public class LanguageMethodParser {

	public enum Language {

		JAVA("Java"),
		CSHARP("C#"),
		CPP("C++");

		private String fLanguateName;

		Language(String languageName) {
			fLanguateName = languageName;
		}

		public static String[] getAvailableLanguageDescriptions() {

			List<String> names = new ArrayList<>();

			for (Language language : Language.values()) { 
				names.add(language.fLanguateName);
			}

			String[] namesArray = names.stream().toArray(String[]::new);

			return namesArray;
		}

		public static Language getDefaultLanguage() {
			return Language.JAVA;
		}
		
		public static String getDefaultLanguageDescription() {
			return Language.JAVA.fLanguateName;
		}
		
		public static Language parse(String languageName) {

			for (Language language : Language.values()) { 
				if (language.fLanguateName.equals(languageName)) {
					return language;
				}
			}

			return null;
		}
	}

	public static final String NOT_A_VALID_JAVA_IDENTIFIER = "not a valid Java identifier";
	public static final String ENDING_BRACKET_NOT_FOUND = "Ending bracket not found.";
	public static final String STARTING_BRACKET_NOT_FOUND = "Starting bracket not found.";
	public static final String MISSING_PARAMETER = "Missing parameter.";


	public static MethodNode parseJavaMethodSignature(String methodSignature, Language language) {


		String firstPart = StringHelper.getFirstToken(methodSignature, "(");

		if (firstPart == null) {
			ExceptionHelper.reportRuntimeException(STARTING_BRACKET_NOT_FOUND);
		}

		String mainPart = StringHelper.getFirstToken(methodSignature, ")");

		if (mainPart == null) {
			ExceptionHelper.reportRuntimeException(ENDING_BRACKET_NOT_FOUND);
		}

		String methodName = StringHelper.getLastToken(firstPart, " ");

		if (!JavaLanguageHelper.isValidJavaIdentifier(methodName)) {
			ExceptionHelper.reportRuntimeException(
					"Method name: " +  methodName + " is " + NOT_A_VALID_JAVA_IDENTIFIER + ".");
		}

		if (methodName == null) {
			ExceptionHelper.reportRuntimeException("Method name not found.");
		}

		MethodNode methodNode  = new MethodNode(methodName, null);

		String parametersPart = StringHelper.getLastToken(mainPart, "(");

		parseParameters(parametersPart, methodNode, language);

		return methodNode;
	}

	private static void parseParameters(String parametersPart, MethodNode inOutMethodNode, Language language) {

		if (StringHelper.isTrimmedEmpty(parametersPart)) {
			return;
		}

		String[] parameters = parametersPart.split(",");

		for (String parameterText : parameters) {

			MethodParameterNode methodParameterNode = createMethodParameter(parameterText, language);

			inOutMethodNode.addParameter(methodParameterNode);
		}
	}

	private static MethodParameterNode createMethodParameter(String parameterText, Language language) {

		String paramTextTrimmed = parameterText.trim();

		if (paramTextTrimmed.isEmpty()) {
			ExceptionHelper.reportRuntimeException(MISSING_PARAMETER);
		}

		String type = createJavaType(paramTextTrimmed, language);

		String name = StringHelper.getLastToken(paramTextTrimmed, " ");

		name = name.trim();

		MethodParameterNode methodParameterNode = 
				new MethodParameterNode(
						name, type, JavaLanguageHelper.getDefaultValue(type), false, false, null, null);

		return methodParameterNode;
	}

	private static String createJavaType(String paramTextTrimmed, Language language) {

		if (language == Language.JAVA) {
			return createParameterFromTextInJava(paramTextTrimmed);
		}

		if (language == Language.CSHARP) {
			return createParameterFromTextInCSharp(paramTextTrimmed);
		}
		
		if (language == Language.CPP) {
			return createParameterFromTextInCpp(paramTextTrimmed);
		}

		ExceptionHelper.reportRuntimeException("Invalid language.");
		return null;
	}

	private static String createParameterFromTextInJava(String paramTextTrimmed) {

		String type = StringHelper.getFirstToken(paramTextTrimmed, " ");

		type = type.trim();

		if (!JavaLanguageHelper.isJavaType(type)) {
			ExceptionHelper.reportRuntimeException("Not allowed type: " + type);
		}

		return type;
	}

	private static String createParameterFromTextInCSharp(String paramTextTrimmed) {

		String type = StringHelper.getFirstToken(paramTextTrimmed, " ");

		type = type.trim();

		if (!CShartLanguageHelper.isAllowedType(type)) {
			ExceptionHelper.reportRuntimeException("Not allowed type: " + type);
		}

		String javaType = CShartLanguageHelper.convertCppTypeToJavaType(type);
		
		return javaType;
	}
	
	private static String createParameterFromTextInCpp(String paramTextTrimmed) {

		String argName = StringHelper.getLastToken(paramTextTrimmed, " ");

		String type = StringHelper.getFirstToken(paramTextTrimmed, argName);

		type = type.trim();

		type = type.replace("*", "");

		type = StringHelper.convertWhiteCharsToSingleSpaces(type);

		if (!CppLanguageHelper.isAllowedType(type)) {
			ExceptionHelper.reportRuntimeException("Not allowed type: " + type);
		}

		String javaType = CppLanguageHelper.convertCppTypeToJavaType(type);

		return javaType;
	}

}
