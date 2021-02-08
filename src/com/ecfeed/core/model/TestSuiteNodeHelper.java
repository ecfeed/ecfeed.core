package com.ecfeed.core.model;

import com.ecfeed.core.utils.IExtLanguageManager;

public class TestSuiteNodeHelper {

	public static String createSignature(TestSuiteNode testSuiteNode, IExtLanguageManager extLanguageManager) {
		
		return AbstractNodeHelper.getName(testSuiteNode, extLanguageManager);
	}
	
}
