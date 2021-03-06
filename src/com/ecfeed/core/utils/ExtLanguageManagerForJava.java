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

	public static final String ATTEMPT_TO_CONVERT_NON_JAVA_TYPE = "Attempt to convert non java type.";

	@Override
	public String verifySeparatorsInName(String nameInExternalLanguage) {

		return JavaLanguageHelper.verifySeparatorsInName(nameInExternalLanguage);
	}

	@Override
	public String verifySeparatorsInText(String nameInExternalLanguage) {

		return JavaLanguageHelper.verifySeparatorsInText(nameInExternalLanguage);
	}

	@Override
	public boolean isLogicalTypeName(String type) {

		return JavaLanguageHelper.isBooleanTypeName(type);
	}

	@Override
	public String convertTextFromExtToIntrLanguage(String text)  {

		String errorMessage = verifySeparatorsInText(text);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
		}

		return text;
	}

	@Override
	public String convertTextFromIntrToExtLanguage(String text) {

		String errorMessage = JavaLanguageHelper.verifySeparatorsInText(text);

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
			ExceptionHelper.reportRuntimeException(ATTEMPT_TO_CONVERT_NON_JAVA_TYPE);
		}

		return type;
	}

	@Override
	public String convertToMinimalTypeFromExtToIntrLanguage(String type) {

		if (SimpleLanguageHelper.isSimpleType(type)) {
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
	public String getQualifiedName(String qualfiedName) {
		return qualfiedName;
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
	public String getExtendedTypeForValue(String value, String currentType, boolean isRandomizedValue) {

		return currentType; // no type extending in Java view
	}

	@Override
	public String createQualifiedName(String packageName, String nonQualifiedName) {

		if (packageName == null) {
			return nonQualifiedName;
		}

		return packageName + "." + nonQualifiedName;
	}

	@Override
	public boolean isSymbolicValueAllowed() {
		return true;
	}

	@Override
	public String formatNumber(String number) {
		return number;
	}

	@Override
	public String getTypeSeparator() {
		return "";
	}

}
