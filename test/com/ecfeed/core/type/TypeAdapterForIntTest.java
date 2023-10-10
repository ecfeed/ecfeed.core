/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.ecfeed.core.type.adapter.TypeAdapterForInt;
import com.ecfeed.core.type.adapter.TypeAdapterHelper;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.TestHelper;

public class TypeAdapterForIntTest {

	@Test
	public void convertSingleValueForJavaTest() {

		IExtLanguageManager extLanguageManagerForJava = new ExtLanguageManagerForJava();

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		assertEquals("1", typeAdapterForInt.adaptSingleValue("1", ERunMode.QUIET, extLanguageManagerForJava));
		assertEquals("1", typeAdapterForInt.adaptSingleValue("1.0", ERunMode.QUIET, extLanguageManagerForJava));
		assertEquals("0", typeAdapterForInt.adaptSingleValue("ab", ERunMode.QUIET, extLanguageManagerForJava));

		try {
			typeAdapterForInt.adaptSingleValue("ab", ERunMode.WITH_EXCEPTION, extLanguageManagerForJava);
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, TypeAdapterHelper.CANNOT_CONVERT_VALUE);
		}

		assertEquals("MAX_VALUE", typeAdapterForInt.adaptSingleValue("MAX_VALUE", ERunMode.QUIET, extLanguageManagerForJava));
	}

	//	@Test
	//	public void convertSingleValueForSimpleTest() {
	//
	//		IExtLanguageManager extLanguageManagerForSimple = new ExtLanguageManagerForSimple();
	//
	//		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();
	//
	//		assertEquals("1", typeAdapterForInt.adaptSingleValue("1", ERunMode.QUIET, extLanguageManagerForSimple));
	//		assertEquals("1", typeAdapterForInt.adaptSingleValue("1.0", ERunMode.QUIET, extLanguageManagerForSimple));
	//
	//		// invalid value
	//
	//		assertEquals("0", typeAdapterForInt.adaptSingleValue("ab", ERunMode.QUIET, extLanguageManagerForSimple));
	//
	//		try {
	//			typeAdapterForInt.adaptSingleValue("ab", ERunMode.WITH_EXCEPTION, extLanguageManagerForSimple);
	//			fail();
	//		} catch (Exception e) {
	//			TestHelper.checkExceptionMessage(e, TypeAdapterHelper.CANNOT_CONVERT_VALUE);
	//		}
	//
	//		// symbolic value
	//
	//		assertEquals("0", typeAdapterForInt.adaptSingleValue("MAX_VALUE", ERunMode.QUIET, extLanguageManagerForSimple));
	//
	//		try {
	//			typeAdapterForInt.adaptSingleValue("MAX_VALUE", ERunMode.WITH_EXCEPTION, extLanguageManagerForSimple);
	//			fail();
	//		} catch (Exception e) {
	//			TestHelper.checkExceptionMessage(e, TypeAdapterForNumericType.SPECIAL_VALUES_ARE_NOT_ALLOWED);
	//		}
	//	}

	@Test
	public void convertRangeTest() {

		//		IExtLanguageManager extLanguageManagerForSimple = new ExtLanguageManagerForSimple();
		//
		//		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();
		//
		//		assertEquals("1:2", typeAdapterForInt.adapt("1:2", true, ERunMode.QUIET, extLanguageManagerForSimple));
		//
		//		String result =
		//				typeAdapterForInt.adapt("MAX_VALUE:MAX_VALUE", true, ERunMode.QUIET, extLanguageManagerForSimple);
		//
		//		assertEquals("0:0", result);

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		IExtLanguageManager extLanguageManagerForJava = new ExtLanguageManagerForJava();

		assertEquals("1:2", typeAdapterForInt.adapt("1:2", true, ERunMode.QUIET, extLanguageManagerForJava));

		String result = typeAdapterForInt.adapt("MAX_VALUE:MAX_VALUE", true, ERunMode.QUIET, extLanguageManagerForJava);

		assertEquals("MAX_VALUE:MAX_VALUE", result);
	}

}
