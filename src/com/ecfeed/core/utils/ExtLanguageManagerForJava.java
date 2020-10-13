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

import com.ecfeed.core.model.RootNode;

public class ExtLanguageManagerForJava implements IExtLanguageManager {

	@Override
	public String verifySeparators(String nameInExternalLanguage) {

		return JavaLanguageHelper.verifySeparators(nameInExternalLanguage);
	}

	@Override
	public boolean isLogicalTypeName(String type) {

		return JavaLanguageHelper.isBooleanTypeName(type);
	}

	@Override
	public String convertTextFromExtToIntrLanguage(String text)  {

		String errorMessage = verifySeparators(text);

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
	public String[] createListListOfSupportedTypes() {

		String[] typeList = JavaLanguageHelper.getSupportedJavaTypes();

		return typeList;
	}

	@Override
	public String getPackageName(String name) {

		return QualifiedNameHelper.getPackage(name);
	}

	@Override
	public String checkIsModelCompatibleWithExtLanguage(RootNode rootNode) {

		return null;
	}

	@Override
	public String chooseString(String stringForJavalang, String stringForSimpleLang) {

		return stringForJavalang;
	}

	@Override
	public String verifyIsAllowedType(String typeName) {

		return JavaLanguageHelper.verifyIsAllowedType(typeName);
	}

	@Override
	public String[] getSupportedTypes() {

		return JavaLanguageHelper.getSupportedJavaTypes();
	}

	@Override
	public boolean getPackageVisibility() {

		return true;
	}

	@Override
	public String getExtendedTypeForValue(String value, String currentType) {

		return currentType; // no type extending in Java view
	}

	@Override
	public String createQualifiedName(String packageName, String name) {

		return packageName + "." + name;
	}

}
