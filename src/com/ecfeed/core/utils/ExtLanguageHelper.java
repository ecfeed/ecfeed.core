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


	public static String verifySeparatorsInName(String nameInExternalLanguage, ExtLanguageManager extLanguage) {

		if (extLanguage == ExtLanguageManager.JAVA) {
			return JavaLanguageHelper.verifySeparators(nameInExternalLanguage);
		}

		if (extLanguage == ExtLanguageManager.SIMPLE) {
			return SimpleLanguageHelper.verifySeparators(nameInExternalLanguage);
		}

		ExceptionHelper.reportRuntimeException("Invalid external language.");
		return null;
	}

	public static String validateType(String parameterTypeInExtLanguage, ExtLanguageManager extLanguage) {

		if (extLanguage == ExtLanguageManager.JAVA) {
			return JavaLanguageHelper.validateBasicJavaType(parameterTypeInExtLanguage);
		}

		if (extLanguage == ExtLanguageManager.SIMPLE) {
			return SimpleLanguageHelper.validateType(parameterTypeInExtLanguage);
		}

		ExceptionHelper.reportRuntimeException("Invalid external language.");
		return null;
	}

	public static boolean isLogicalTypeName(String type, ExtLanguageManager extLanguage) {

		if (extLanguage == ExtLanguageManager.SIMPLE) {
			return SimpleLanguageHelper.isLogicalTypeName(type);
		}

		return JavaLanguageHelper.isBooleanTypeName(type);
	}

	public static String convertTextFromExtToIntrLanguage(String text, ExtLanguageManager extLanguage)  {

		String errorMessage = verifySeparatorsInName(text, extLanguage);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
		}

		if (extLanguage == ExtLanguageManager.SIMPLE) {
			text = SimpleLanguageHelper.convertTextFromSimpleToJavaLanguage(text);
		}

		return text;
	}

	public static String convertTextFromIntrToExtLanguage(String text, ExtLanguageManager extLanguage) {

		String errorMessage = JavaLanguageHelper.verifySeparators(text);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
		}

		if (extLanguage == ExtLanguageManager.SIMPLE) {
			text = SimpleLanguageHelper.convertTextFromJavaToSimpleLanguage(text);
		}

		return text;
	}

	public static String convertTypeFromIntrToExtLanguage(String type, ExtLanguageManager extLanguage) {

		if (!JavaLanguageHelper.isValidComplexTypeIdentifier(type)) {
			ExceptionHelper.reportRuntimeException("Attempt to convert an invalid identifier.");
		}

		if (extLanguage == ExtLanguageManager.SIMPLE) {
			type = SimpleLanguageHelper.conditionallyConvertJavaTypeToSimpleType(type);
		}

		return type;
	}

	public static String convertTypeFromExtToIntrLanguage(String type, ExtLanguageManager extLanguage) {

		if (extLanguage == ExtLanguageManager.SIMPLE) {
			type = SimpleLanguageHelper.conditionallyConvertSimpleTypeToJavaType(type);
		}

		if (!JavaLanguageHelper.isJavaType(type)) {
			ExceptionHelper.reportRuntimeException("Attempt to convert non java type.");
		}

		return type;
	}

	public static String conditionallyConvertSpecialValueToExtLanguage(
			String valueInIntrLanguage, String typeInIntrLanguage, ExtLanguageManager extLanguage) {

		if (extLanguage == ExtLanguageManager.JAVA) {
			return valueInIntrLanguage;
		}

		if (!JavaLanguageHelper.isJavaType(typeInIntrLanguage)) {
			ExceptionHelper.reportRuntimeException("Cannot convert special value. Invalid type.");
		}

		String convertedValue = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric(typeInIntrLanguage, valueInIntrLanguage);

		return convertedValue;
	}

	public static List<String> getSymbolicNamesOfSpecialValues(String typeName, ExtLanguageManager extLanguage) {

		List<String> items;

		if (extLanguage == ExtLanguageManager.JAVA) {
			items = JavaLanguageHelper.getSymbolicNamesOfSpecialValues(typeName);
		} else {
			items = JavaLanguageHelper.getSymbolicNamesOfSpecialValuesForNonNumericTypes(typeName);
		}

		return items;
	}

	public static void reportExceptionAllTypesAreUsed(ExtLanguageManager extLanguage) {

		if (extLanguage == ExtLanguageManager.SIMPLE) {
			ExceptionHelper.reportClientException("Cannot find not used parameter type. All possible types are already used.");
		}
	}

	// TODO SIMPLE-VIEW test
	public static String[] createListListOfSupportedTypes(ExtLanguageManager extLanguage) {

		String[] typeList;

		if (extLanguage == ExtLanguageManager.SIMPLE) {
			typeList = SimpleLanguageHelper.getSupportedSimpleViewTypes();
		} else {
			typeList = JavaLanguageHelper.getSupportedJavaTypes();
		}

		return typeList;
	}

	// TODO SIMPLE-VIEW test
	public static String getPackageName(String name, ExtLanguageManager extLanguage) {

		if (extLanguage  == ExtLanguageManager.SIMPLE) {
			return "";
		}

		return ModelHelper.getPackageName(name);
	}

	// TODO SIMPLE-VIEW test
	public static String createClassNameSignature(String className, ExtLanguageManager extLanguage) {

		if (extLanguage == ExtLanguageManager.SIMPLE) {
			className = StringHelper.getLastTokenOrInputString(className, ".");
		}

		className = ExtLanguageHelper.convertTextFromIntrToExtLanguage(className, extLanguage);
		return className;
	}

	// TODO SIMPLE-VIEW test
	public static String getQualifiedName(String name, ExtLanguageManager extLanguage) {

		if (extLanguage == ExtLanguageManager.SIMPLE) {
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
	public static String checkIsModelCompatibleWithExtLanguage(AbstractNode anyNode, ExtLanguageManager extLanguage) {

		RootNode rootNode = ModelHelper.findRoot(anyNode);

		if (extLanguage == ExtLanguageManager.SIMPLE) {
			String result = SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode);
			return result;
		}

		return null;
	}

	// TODO SIMPLE-VIEW test
	public static Pair<String, String> createPairOfMethodSignatures(MethodNode methodNode) {

		Pair<String,String> pairOfSignatures = 
				new Pair<String, String>(
						MethodNodeHelper.createSignature(methodNode, ExtLanguageManager.SIMPLE),
						MethodNodeHelper.createSignature(methodNode, ExtLanguageManager.JAVA));

		return pairOfSignatures;
	}

	public static String chooseString(String stringForJavalang, String stringForSimpleLang, ExtLanguageManager extLanguage) {

		if  (extLanguage == ExtLanguageManager.JAVA)  {
			return stringForJavalang;
		}

		return stringForSimpleLang;
	}

}
