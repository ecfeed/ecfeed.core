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

public class ExtLanguageManagerForJava implements IExtLanguageManager {

	@Override
	public String verifySeparatorsInName(String nameInExternalLanguage) {
		
		return JavaLanguageHelper.verifySeparators(nameInExternalLanguage);
	}

	@Override
	public String validateType(String parameterTypeInExtLanguage) {
		
		return JavaLanguageHelper.validateBasicJavaType(parameterTypeInExtLanguage);
	}
	
	@Override
	public boolean isLogicalTypeName(String type) {

		return JavaLanguageHelper.isBooleanTypeName(type);
	}

	@Override
	public String convertTextFromExtToIntrLanguage(String text)  {

		String errorMessage = verifySeparatorsInName(text);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
		}

		return text;
	}

	@Override
	public String convertTextFromIntrToExtLanguage(String text) {

		String errorMessage = JavaLanguageHelper.verifySeparators(text);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
		}

		return text;
	}

	@Override
	public String convertTypeFromIntrToExtLanguage(String type) {

		if (!JavaLanguageHelper.isValidComplexTypeIdentifier(type)) {
			ExceptionHelper.reportRuntimeException("Attempt to convert an invalid identifier.");
		}

		return type;
	}

	@Override
	public String convertTypeFromExtToIntrLanguage(String type) {

		if (!JavaLanguageHelper.isJavaType(type)) {
			ExceptionHelper.reportRuntimeException("Attempt to convert non java type.");
		}

		return type;
	}

	@Override
	public String conditionallyConvertSpecialValueToExtLanguage(
			String valueInIntrLanguage, String typeInIntrLanguage) {

		return valueInIntrLanguage;
	}

	@Override
	public List<String> getSymbolicNamesOfSpecialValues(String typeName) {

		List<String> items = JavaLanguageHelper.getSymbolicNamesOfSpecialValues(typeName);

		return items;
	}

	@Override
	public void reportExceptionAllTypesAreUsed() {

	}

	@Override
	public String[] createListListOfSupportedTypes() {

		String[] typeList = JavaLanguageHelper.getSupportedJavaTypes();

		return typeList;
	}

	@Override
	// TODO SIMPLE-VIEW test
	public String getPackageName(String name) {

		return ModelHelper.getPackageName(name);
	}

	@Override
	// TODO SIMPLE-VIEW test
	public String createClassNameSignature(String className) {

		className = convertTextFromIntrToExtLanguage(className);
		
		return className;
	}

	@Override
	// TODO SIMPLE-VIEW test
	public String getQualifiedName(String name) {

		name = convertTextFromIntrToExtLanguage(name);
		return name;
	}

	@Override
	// TODO SIMPLE-VIEW test
	public String checkIsNewClassNameValid(ClassNode classNode, String className) {

		// TODO SIMPLE-VIEW implement
		return null;
	}

	@Override
	// TODO SIMPLE-VIEW test
	public String checkIsModelCompatibleWithExtLanguage(AbstractNode anyNode) {

		return null;
	}

	// TODO SIMPLE-VIEW test
	public static Pair<String, String> createPairOfMethodSignatures(MethodNode methodNode) {

		Pair<String,String> pairOfSignatures = 
				new Pair<String, String>(
						MethodNodeHelper.createSignature(methodNode, IExtLanguageManager.SIMPLE),
						MethodNodeHelper.createSignature(methodNode, IExtLanguageManager.JAVA));

		return pairOfSignatures;
	}

	public static String chooseString(String stringForJavalang, String stringForSimpleLang, IExtLanguageManager extLanguage) {

		if  (extLanguage == IExtLanguageManager.JAVA)  {
			return stringForJavalang;
		}

		return stringForSimpleLang;
	}

}
