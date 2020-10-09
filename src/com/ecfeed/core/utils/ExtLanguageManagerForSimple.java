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
import com.ecfeed.core.model.ModelHelper;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.SimpleLanguageModelVerifier;

public class ExtLanguageManagerForSimple implements IExtLanguageManager {


	@Override
	public String verifySeparators(String nameInExternalLanguage) {

		return SimpleLanguageHelper.verifySeparators(nameInExternalLanguage);
	}

	@Override
	public boolean isLogicalTypeName(String type) {

		return SimpleLanguageHelper.isLogicalTypeName(type);
	}

	@Override
	public String convertTextFromExtToIntrLanguage(String text)  {

		String errorMessage = verifySeparators(text);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
		}

		text = SimpleLanguageHelper.convertTextFromSimpleToJavaLanguage(text);

		return text;
	}

	@Override
	public String convertTextFromIntrToExtLanguage(String text) {

		String errorMessage = JavaLanguageHelper.verifySeparators(text);

		if (errorMessage != null) {
			ExceptionHelper.reportRuntimeException(errorMessage);
		}

		text = SimpleLanguageHelper.convertTextFromJavaToSimpleLanguage(text);

		return text;
	}

	@Override
	public String convertTypeFromIntrToExtLanguage(String type) {

		if (!JavaLanguageHelper.isValidComplexTypeIdentifier(type)) {
			ExceptionHelper.reportRuntimeException("Attempt to convert an invalid identifier.");
		}

		type = SimpleLanguageHelper.conditionallyConvertJavaTypeToSimpleType(type);

		return type;
	}

	@Override
	public String convertTypeFromExtToIntrLanguage(String type) {

		type = SimpleLanguageHelper.conditionallyConvertSimpleTypeToJavaType(type);

		if (!JavaLanguageHelper.isJavaType(type)) {
			ExceptionHelper.reportRuntimeException("Attempt to convert non java type.");
		}

		return type;
	}

	@Override
	public String conditionallyConvertSpecialValueToExtLanguage(
			String valueInIntrLanguage, String typeInIntrLanguage) {

		if (!JavaLanguageHelper.isJavaType(typeInIntrLanguage)) {
			return valueInIntrLanguage;
		}

		String convertedValue = JavaLanguageHelper.conditionallyConvertSpecialValueToNumeric(typeInIntrLanguage, valueInIntrLanguage);

		return convertedValue;
	}

	@Override
	public List<String> getSymbolicNamesOfSpecialValues(String typeName) {

		List<String> items = JavaLanguageHelper.getSymbolicNamesOfSpecialValuesForNonNumericTypes(typeName);

		return items;
	}

	@Override
	public String[] createListListOfSupportedTypes() {

		String[] typeList = SimpleLanguageHelper.getSupportedSimpleViewTypes();

		return typeList;
	}

	@Override
	public String getPackageName(String name) {

		return "";
	}

	@Override
	// TODO SIMPLE-VIEW test
	public String createClassNameSignature(String className) {

		className = StringHelper.getLastTokenOrInputString(className, ".");
		className = convertTextFromIntrToExtLanguage(className);

		return className;
	}

	@Override
	// TODO SIMPLE-VIEW test
	public String getQualifiedName(String name) {

		name = ModelHelper.getNonQualifiedName(name);
		name = convertTextFromIntrToExtLanguage(name);

		return name;
	}

	@Override
	// TODO SIMPLE-VIEW test
	public String checkIsModelCompatibleWithExtLanguage(AbstractNode anyNode) {

		RootNode rootNode = ModelHelper.findRoot(anyNode);
		String result = SimpleLanguageModelVerifier.checkIsModelCompatibleWithSimpleLanguage(rootNode);

		return result;
	}

	@Override
	public String chooseString(String stringForJavalang, String stringForSimpleLang) {

		return stringForSimpleLang;
	}

	@Override
	public String verifyIsAllowedType(String typeName) {

		return SimpleLanguageHelper.verifyIsAllowedType(typeName);
	}

	@Override
	public String[] getSupportedTypes() {

		return SimpleLanguageHelper.getSupportedSimpleViewTypes();
	}

	@Override
	public boolean getPackageVisibility() {

		return false;
	}

	@Override
	public String getExtendedTypeForValue(String value, String currentType) {

		if (JavaLanguageHelper.isCharTypeName(currentType) && value.length() > 1) {

			return JavaLanguageHelper.TYPE_NAME_STRING;		
		}

		if (JavaLanguageHelper.isNumericTypeName(currentType)) {

			String typeCompatibleWithValue = JavaLanguageHelper.getCompatibleNumericType(value);

			if (JavaLanguageHelper.isNumericTypeLarger(typeCompatibleWithValue, currentType)) {
				return typeCompatibleWithValue;
			}
		}

		return currentType;
	}

	@Override
	public String createQualifiedName(String packageName, String name) {

		return name;
	}

}
