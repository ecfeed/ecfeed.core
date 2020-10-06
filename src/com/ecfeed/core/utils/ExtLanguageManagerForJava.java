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
import com.ecfeed.core.model.ModelHelper;

public class ExtLanguageManagerForJava implements IExtLanguageManager {

	@Override
	public String verifySeparatorsInName(String nameInExternalLanguage) {

		return JavaLanguageHelper.verifySeparators(nameInExternalLanguage);
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

	@Override
	public String chooseString(String stringForJavalang, String stringForSimpleLang) {

		return stringForJavalang;
	}

	@Override
	public boolean isAllowedType(String typeName) {
		
		if (JavaLanguageHelper.isJavaType(typeName)) {
			return true;
		}
		
		if (JavaLanguageHelper.isValidComplexTypeIdentifier(typeName)) {
			return true;
		}
		
		return false;
	}
}
