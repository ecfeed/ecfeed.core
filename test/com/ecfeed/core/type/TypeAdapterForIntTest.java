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

import com.ecfeed.core.type.adapter.TypeAdapterForInt;
import com.ecfeed.core.type.adapter.TypeAdapterForNumericType;
import com.ecfeed.core.type.adapter.TypeAdapterHelper;
import com.ecfeed.core.utils.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TypeAdapterForIntTest {

	@Test
	public void convertSingleValueForJavaTest() {

		IExtLanguageManager extLanguageManagerForJava = new ExtLanguageManagerForJava();

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		assertEquals("1", typeAdapterForInt.convertSingleValue("1", ERunMode.QUIET, extLanguageManagerForJava));
		assertEquals("1", typeAdapterForInt.convertSingleValue("1.0", ERunMode.QUIET, extLanguageManagerForJava));
		assertEquals("0", typeAdapterForInt.convertSingleValue("ab", ERunMode.QUIET, extLanguageManagerForJava));

		try {
			typeAdapterForInt.convertSingleValue("ab", ERunMode.WITH_EXCEPTION, extLanguageManagerForJava);
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, TypeAdapterHelper.CANNOT_CONVERT_VALUE);
		}

		assertEquals("MAX_VALUE", typeAdapterForInt.convertSingleValue("MAX_VALUE", ERunMode.QUIET, extLanguageManagerForJava));
	}

	@Test
	public void convertSingleValueForSimpleTest() {

		IExtLanguageManager extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		assertEquals("1", typeAdapterForInt.convertSingleValue("1", ERunMode.QUIET, extLanguageManagerForSimple));
		assertEquals("1", typeAdapterForInt.convertSingleValue("1.0", ERunMode.QUIET, extLanguageManagerForSimple));

		// invalid value

		assertEquals("0", typeAdapterForInt.convertSingleValue("ab", ERunMode.QUIET, extLanguageManagerForSimple));

		try {
			typeAdapterForInt.convertSingleValue("ab", ERunMode.WITH_EXCEPTION, extLanguageManagerForSimple);
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, TypeAdapterHelper.CANNOT_CONVERT_VALUE);
		}

		// symbolic value

		assertEquals("0", typeAdapterForInt.convertSingleValue("MAX_VALUE", ERunMode.QUIET, extLanguageManagerForSimple));

		try {
			typeAdapterForInt.convertSingleValue("MAX_VALUE", ERunMode.WITH_EXCEPTION, extLanguageManagerForSimple);
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, TypeAdapterForNumericType.SPECIAL_VALUES_ARE_NOT_ALLOWED);
		}
	}

	@Test
	public void convertRangeTest() {

		IExtLanguageManager extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		TypeAdapterForInt typeAdapterForInt = new TypeAdapterForInt();

		assertEquals("1:2", typeAdapterForInt.convert("1:2", true, ERunMode.QUIET, extLanguageManagerForSimple));

		String result =
				typeAdapterForInt.convert("MAX_VALUE:MAX_VALUE", true, ERunMode.QUIET, extLanguageManagerForSimple);

		assertEquals("0:0", result);


		IExtLanguageManager extLanguageManagerForJava = new ExtLanguageManagerForJava();

		assertEquals("1:2", typeAdapterForInt.convert("1:2", true, ERunMode.QUIET, extLanguageManagerForJava));

		result = typeAdapterForInt.convert("MAX_VALUE:MAX_VALUE", true, ERunMode.QUIET, extLanguageManagerForJava);

		assertEquals("MAX_VALUE:MAX_VALUE", result);
	}

}
