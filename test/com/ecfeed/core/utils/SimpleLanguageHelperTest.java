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

public class SimpleLanguageHelperTest {

	@Test
	public void verifySeparatorsTest() throws EcException{

		String errorMessage = SimpleLanguageHelper.verifySeparators("abc");
		assertNull(errorMessage);

		errorMessage = SimpleLanguageHelper.verifySeparators("a_b");
		assertNotNull(errorMessage);

		errorMessage = SimpleLanguageHelper.verifySeparators(" ab");
		assertNotNull(errorMessage);
	}

	@Test
	public void isSimpleTypeTest() throws EcException{

		assertTrue(SimpleLanguageHelper.isSimpleType("Number"));
		assertTrue(SimpleLanguageHelper.isSimpleType("Text"));
		assertTrue(SimpleLanguageHelper.isSimpleType("Logical"));
		assertFalse(SimpleLanguageHelper.isSimpleType("int"));
		assertFalse(SimpleLanguageHelper.isSimpleType("double"));
		assertFalse(SimpleLanguageHelper.isSimpleType("boolean"));
		assertFalse(SimpleLanguageHelper.isSimpleType("String"));
	}

}
