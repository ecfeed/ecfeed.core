package com.ecfeed.core.utils;

public interface IExtLanguageManager {
	
	public String verifySeparatorsInName(String nameInExternalLanguage);
	public String validateType(String parameterTypeInExtLanguage);
	public boolean isLogicalTypeName(String type);
	public String convertTextFromExtToIntrLanguage(String text);
	public String convertTextFromIntrToExtLanguage(String text);
	public String convertTypeFromIntrToExtLanguage(String type);


}
