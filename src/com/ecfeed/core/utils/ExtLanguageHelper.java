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

import java.util.List;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.ModelHelper;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.SimpleLanguageModelVerifier;

public class ExtLanguageHelper {


	public static String verifySeparatorsInName(String nameInExternalLanguage, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.JAVA) {
			return JavaLanguageHelper.verifySeparators(nameInExternalLanguage);
		}

		if (extLanguage == ExtLanguage.SIMPLE) {
			return SimpleLanguageHelper.verifySeparators(nameInExternalLanguage);
		}

		ExceptionHelper.reportRuntimeException("Invalid external language.");
		return null;
	}

	public static String validateType(String parameterTypeInExtLanguage, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.JAVA) {
			return JavaLanguageHelper.validateBasicJavaType(parameterTypeInExtLanguage);
		}

		if (extLanguage == ExtLanguage.SIMPLE) {
			return SimpleLanguageHelper.validateType(parameterTypeInExtLanguage);
		}

		ExceptionHelper.reportRuntimeException("Invalid external language.");
		return null;
	}

	public static boolean isLogicalTypeName(String type, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.SIMPLE) {
			return SimpleLanguageHelper.isLogicalTypeName(type);
		}

		return JavaLanguageHelper.isBooleanTypeName(type);
	}

	public static String convertTextFromExtToIntrLanguage(String text, ExtLanguage extLanguage)  {

		String errorMessage = verifySeparatorsInName(text, extLanguage);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
		}

		if (extLanguage == ExtLanguage.SIMPLE) {
			text = SimpleLanguageHelper.convertTextFromSimpleToJavaLanguage(text);
		}

		return text;
	}

	public static String convertTextFromIntrToExtLanguage(String text, ExtLanguage extLanguage) {

		String errorMessage = JavaLanguageHelper.verifySeparators(text);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
		}

		if (extLanguage == ExtLanguage.SIMPLE) {
			text = SimpleLanguageHelper.convertTextFromJavaToSimpleLanguage(text);
		}

		return text;
	}

	public static String convertTypeFromIntrToExtLanguage(String type, ExtLanguage extLanguage) {

		if (!JavaLanguageHelper.isValidComplexTypeIdentifier(type)) {
			ExceptionHelper.reportRuntimeException("Attempt to convert an invalid identifier.");
		}

		if (extLanguage == ExtLanguage.SIMPLE) {
			type = SimpleLanguageHelper.convertJavaTypeToSimpleType(type);
		}

		return type;
	}

	public static String convertTypeFromExtToIntrLanguage(String type, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.SIMPLE) {
			type = SimpleLanguageHelper.convertSimpleTypeToJavaType(type);
		}

		if (!JavaLanguageHelper.isJavaType(type)) {
			ExceptionHelper.reportRuntimeException("Attempt to convert non java type.");
		}

		return type;
	}

	public static String conditionallyConvertSpecialValueToExtLanguage(
			String valueInIntrLanguage, String typeInIntrLanguage, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.JAVA) {
			return valueInIntrLanguage;
		}

		if (!JavaLanguageHelper.isJavaType(typeInIntrLanguage)) {
			ExceptionHelper.reportRuntimeException("Cannot convert special value. Invalid type.");
		}

		String convertedValue = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric(typeInIntrLanguage, valueInIntrLanguage);

		return convertedValue;
	}

	public static List<String> getSymbolicNamesOfSpecialValues(String typeName, ExtLanguage extLanguage) {

		List<String> items;

		if (extLanguage == ExtLanguage.JAVA) {
			items = JavaLanguageHelper.getSymbolicNamesOfSpecialValues(typeName);
		} else {
			items = JavaLanguageHelper.getSymbolicNamesOfSpecialValuesForNonNumericTypes(typeName);
		}

		return items;
	}

	public static void reportExceptionAllTypesAreUsed(ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.SIMPLE) {
			ExceptionHelper.reportClientException("Cannot find not used parameter type. All possible types are already used.");
		}
	}

	// TODO SIMPLE-VIEW test
	public static String[] createListListOfSupportedTypes(ExtLanguage extLanguage) {

		String[] typeList;

		if (extLanguage == ExtLanguage.SIMPLE) {
			typeList = SimpleLanguageHelper.getSupportedSimpleViewTypes();
		} else {
			typeList = JavaLanguageHelper.getSupportedJavaTypes();
		}

		return typeList;
	}

	// TODO SIMPLE-VIEW test
	public static String getPackageName(String name, ExtLanguage extLanguage) {

		if (extLanguage  == ExtLanguage.SIMPLE) {
			return "";
		}

		return ModelHelper.getPackageName(name);
	}

	// TODO SIMPLE-VIEW test
	public static String createClassNameSignature(String className, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.SIMPLE) {
			className = StringHelper.getLastTokenOrInputString(className, ".");
		}

		className = ExtLanguageHelper.convertTextFromIntrToExtLanguage(className, extLanguage);
		return className;
	}

	// TODO SIMPLE-VIEW test
	public static String getQualifiedName(String name, ExtLanguage extLanguage) {

		if (extLanguage == ExtLanguage.SIMPLE) {
			name = ModelHelper.getNonQualifiedName(name);
		}

		name = ExtLanguageHelper.convertTextFromIntrToExtLanguage(name,  extLanguage);
		return name;
	}

	// TODO SIMPLE-VIEW test
	public static String checkIsNewClassNameValid(ClassNode classNode, String className) {

		return SimpleLanguageModelVerifier.checkIsNewClassNameValid(classNode, className); // TODO SIMPLE-VIEW check
	}

	// TODO SIMPLE-VIEW test
	public static String checkIsModelCompatibleWithExtLanguage(AbstractNode anyNode, ExtLanguage extLanguage) {

		RootNode rootNode = ModelHelper.findRoot(anyNode);

		if (extLanguage == ExtLanguage.SIMPLE) {
			String result = SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode);
			return result;
		}

		return null;
	}

	// TODO SIMPLE-VIEW test
	public static Pair<String, String> createPairOfMethodSignatures(MethodNode methodNode) {

		Pair<String,String> pairOfSignatures = 
				new Pair<String, String>(
						MethodNodeHelper.createSignature(methodNode, ExtLanguage.SIMPLE),
						MethodNodeHelper.createSignature(methodNode, ExtLanguage.JAVA));

		return pairOfSignatures;
	}

	public static String chooseString(String stringForJavalang, String stringForSimpleLang, ExtLanguage extLanguage) {

		if  (extLanguage == ExtLanguage.JAVA)  {
			return stringForJavalang;
		}

		return stringForSimpleLang;
	}

}
