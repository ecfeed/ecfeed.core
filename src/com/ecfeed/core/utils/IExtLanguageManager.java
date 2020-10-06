package com.ecfeed.core.utils;

import java.util.List;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ClassNode;

public interface IExtLanguageManager {
	
	public String verifySeparatorsInName(String nameInExternalLanguage);
	public boolean isLogicalTypeName(String type);
	public String convertTextFromExtToIntrLanguage(String text);
	public String convertTextFromIntrToExtLanguage(String text);
	public String convertTypeFromIntrToExtLanguage(String type);
	public String convertTypeFromExtToIntrLanguage(String type);
	public String conditionallyConvertSpecialValueToExtLanguage(String valueInIntrLanguage, String typeInIntrLanguage);
	public List<String> getSymbolicNamesOfSpecialValues(String typeName);
	public void reportExceptionAllTypesAreUsed(); // TODO SIMPLE-VIEW remove ?
	public String[] createListListOfSupportedTypes();
	public String getPackageName(String name);
	public String createClassNameSignature(String className);
	public String getQualifiedName(String name);
	public String checkIsNewClassNameValid(ClassNode classNode, String className);
	public String checkIsModelCompatibleWithExtLanguage(AbstractNode anyNode);
	public String chooseString(String stringForJavalang, String stringForSimpleLang);
	public boolean isAllowedType(String typeName); // TODO SIMPLE-VIEW unit tests
	public String verifyIsAllowedType(String typeName); // TODO SIMPLE-VIEW unit tests

}
