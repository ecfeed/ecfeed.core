package com.ecfeed.core.utils;

import java.util.List;

import com.ecfeed.core.model.AbstractNode;

public interface IExtLanguageManager {

	public String verifySeparators(String nameInExternalLanguage);
	public boolean isLogicalTypeName(String type);
	public String convertTextFromExtToIntrLanguage(String text);
	public String convertTextFromIntrToExtLanguage(String text);
	public String convertTypeFromIntrToExtLanguage(String type);
	public String convertTypeFromExtToIntrLanguage(String type);
	public String conditionallyConvertSpecialValueToExtLanguage(String valueInIntrLanguage, String typeInIntrLanguage);
	public List<String> getSymbolicNamesOfSpecialValues(String typeName);
	public String[] createListListOfSupportedTypes();
	public String getPackageName(String name);
	public String createClassNameSignature(String className);
	public String getQualifiedName(String name);
	public String checkIsModelCompatibleWithExtLanguage(AbstractNode anyNode);
	public String chooseString(String stringForJavalang, String stringForSimpleLang);
	public String verifyIsAllowedType(String typeName); // TODO SIMPLE-VIEW unit tests
	public String[] getSupportedTypes();
	public boolean getPackageVisibility();
	public String getExtendedTypeForValue(String value, String currentType); // TODO SIMPLE-VIEW unit tests
	public String createQualifiedName(String packageName, String name);
}
