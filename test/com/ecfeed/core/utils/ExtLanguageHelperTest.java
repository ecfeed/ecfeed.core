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

import org.junit.Test;

import static org.junit.Assert.*;

public class ExtLanguageHelperTest {

	@Test
	public void verifySeparatorsTest() {

		String errorMessage = ExtLanguageHelper.verifySeparatorsInName("abc", ExtLanguage.JAVA);
		assertNull(errorMessage);

		errorMessage = ExtLanguageHelper.verifySeparatorsInName("abc", ExtLanguage.SIMPLE);
		assertNull(errorMessage);

		// underline

		errorMessage = ExtLanguageHelper.verifySeparatorsInName("ab_c", ExtLanguage.JAVA);
		assertNull(errorMessage);

		errorMessage = ExtLanguageHelper.verifySeparatorsInName("ab_c", ExtLanguage.SIMPLE);
		assertNotNull(errorMessage);

		// space

		errorMessage = ExtLanguageHelper.verifySeparatorsInName("ab c", ExtLanguage.JAVA);
		assertNotNull(errorMessage);

		errorMessage = ExtLanguageHelper.verifySeparatorsInName("ab c", ExtLanguage.SIMPLE);
		assertNull(errorMessage);
		}

	@Test
	public void validateTypeTest() {

		String errorMessage = ExtLanguageHelper.validateType("int", ExtLanguage.JAVA);
		assertNull(errorMessage);

		errorMessage = ExtLanguageHelper.validateType("intr", ExtLanguage.JAVA);
		assertNotNull(errorMessage);

		errorMessage = ExtLanguageHelper.validateType("Number", ExtLanguage.SIMPLE);
		assertNull(errorMessage);

		errorMessage = ExtLanguageHelper.validateType("Num", ExtLanguage.SIMPLE);
		assertNotNull(errorMessage);
	}

	@Test
	public void convertTextToExtLanguageTest() {

		String text = ExtLanguageHelper.convertTextFromIntrToExtLanguage("ab_c", ExtLanguage.JAVA);
		assertEquals("ab_c", text);

		text = ExtLanguageHelper.convertTextFromIntrToExtLanguage("ab_c", ExtLanguage.SIMPLE);
		assertEquals("ab c", text);

		try {
			ExtLanguageHelper.convertTextFromIntrToExtLanguage("ab c", ExtLanguage.JAVA);
			fail();
		} catch (Exception e) {
		}

		try {
			ExtLanguageHelper.convertTextFromIntrToExtLanguage("ab c", ExtLanguage.SIMPLE);
			fail();
		} catch (Exception e) {
		}
	}


}
