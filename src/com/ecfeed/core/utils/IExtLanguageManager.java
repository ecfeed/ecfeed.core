package com.ecfeed.core.utils;

import java.util.List;

import com.ecfeed.core.model.RootNode;

public interface IExtLanguageManager {

	public String verifySeparatorsInText(String textInExternalLanguage);
	public String verifySeparatorsInName(String nameInExternalLanguage);
	public boolean isLogicalTypeName(String type);
	public String convertTextFromExtToIntrLanguage(String text);
	public String convertTextFromIntrToExtLanguage(String text);
	public String convertTypeFromIntrToExtLanguage(String type);
	public String convertTypeFromExtToIntrLanguage(String type);
	public String convertToMinimalTypeFromExtToIntrLanguage(String type);
	public String conditionallyConvertSpecialValueToExtLanguage(String valueInIntrLanguage, String typeInIntrLanguage);
	public List<String> getSymbolicNamesOfSpecialValues(String typeName);
	public String[] createListListOfSupportedTypes();
	//public String getPackageName(String name);
	public String getQualifiedName(String qualfiedName);
	public String checkIsModelCompatibleWithExtLanguage(RootNode rootNode);
	public String chooseString(String stringForJavalang, String stringForSimpleLang);
	public String verifyIsAllowedType(String typeName);
	public String[] getSupportedTypes();
	public boolean getPackageVisibility();
	public String createQualifiedName(String packageName, String name);
	public boolean isSymbolicValueAllowed();
	public String formatNumber(String number);
	public String getTypeSeparator();
	public ExtLanguage getLanguage();
}
