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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TypeHelperTest{

	String tBoolean = JavaLanguageHelper.TYPE_NAME_BOOLEAN;
	String tByte = JavaLanguageHelper.TYPE_NAME_BYTE;
	String tInt = JavaLanguageHelper.TYPE_NAME_INT;
	String tFloat = JavaLanguageHelper.TYPE_NAME_FLOAT;
	String tDouble = JavaLanguageHelper.TYPE_NAME_DOUBLE;
	String tString = JavaLanguageHelper.TYPE_NAME_STRING;

	private enum IsChoiceRandomized {
		FALSE,
		TRUE
	}

	@Test
	public void checkValueConversionsForDifferentTypesAndValues() {

		assertFalse(canConvert("ABC", tString, tInt, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("ABC", tString, tString, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("1", tString, tInt, IsChoiceRandomized.FALSE));

		assertTrue(canConvert("123.0", tDouble, tInt, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("123.0:123.0", tDouble, tInt, IsChoiceRandomized.TRUE));

		assertFalse(canConvert("123.1", tDouble, tInt, IsChoiceRandomized.FALSE));
		assertFalse(canConvert("123.1:123.1", tDouble, tInt, IsChoiceRandomized.TRUE));

		assertFalse(canConvert("123.54e+7", tDouble, tInt, IsChoiceRandomized.FALSE));
		assertFalse(canConvert("123.54e+7:123.54e+7", tDouble, tInt, IsChoiceRandomized.TRUE));

		assertTrue(canConvert("1234", tFloat, tDouble, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("1234:1234", tFloat, tDouble, IsChoiceRandomized.TRUE));

		assertTrue(canConvert("1234", tFloat, tInt, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("1234:1234", tFloat, tInt, IsChoiceRandomized.TRUE));

		assertFalse(canConvert("1234", tFloat, tByte, IsChoiceRandomized.FALSE));
		assertFalse(canConvert("1234:1234", tFloat, tByte, IsChoiceRandomized.TRUE));

		assertTrue(canConvert("123", tFloat, tByte, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("123:123", tFloat, tByte, IsChoiceRandomized.TRUE));

		assertFalse(canConvert("false", tBoolean, tByte, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("false", tBoolean, tString, IsChoiceRandomized.FALSE));

		assertTrue(canConvert("false", tBoolean, tBoolean, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("true", tBoolean, tBoolean, IsChoiceRandomized.FALSE));

		assertFalse(canConvert("1", tBoolean, tBoolean, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("false", tString, tBoolean, IsChoiceRandomized.FALSE));
	}

	private boolean canConvert(
			String value, 
			String oldType, 
			String newType, 
			IsChoiceRandomized isChoiceRandomized) {

		boolean isRandomized = false;

		if (isChoiceRandomized == IsChoiceRandomized.TRUE) {
			isRandomized = true;
		}

		boolean isCompatible = TypeHelper.isValueCompatibleWithType(value, newType, isRandomized);
		return isCompatible;
	}

}
