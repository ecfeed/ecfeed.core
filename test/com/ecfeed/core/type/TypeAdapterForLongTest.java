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

import com.ecfeed.core.type.adapter.TypeAdapterForLong;
import com.ecfeed.core.type.adapter.TypeAdapterForNumericType;
import com.ecfeed.core.type.adapter.TypeAdapterHelper;
import com.ecfeed.core.utils.ERunMode;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.TestHelper;

public class TypeAdapterForLongTest {

	@Test
	public void convertSingleValueForJavaTest() {

		IExtLanguageManager extLanguageManagerForJava = new ExtLanguageManagerForJava();

		TypeAdapterForLong typeAdapterForLong = new TypeAdapterForLong();

		assertEquals("1", typeAdapterForLong.adaptSingleValue("1", ERunMode.QUIET, extLanguageManagerForJava));
		assertEquals("1", typeAdapterForLong.adaptSingleValue("1.0", ERunMode.QUIET, extLanguageManagerForJava));
		assertEquals("0", typeAdapterForLong.adaptSingleValue("ab", ERunMode.QUIET, extLanguageManagerForJava));

		try {
			typeAdapterForLong.adaptSingleValue("ab", ERunMode.WITH_EXCEPTION, extLanguageManagerForJava);
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, TypeAdapterHelper.CANNOT_CONVERT_VALUE);
		}

		assertEquals("MAX_VALUE", typeAdapterForLong.adaptSingleValue("MAX_VALUE", ERunMode.QUIET, extLanguageManagerForJava));
	}

	@Test
	public void convertSingleValueForSimpleTest() {

		IExtLanguageManager extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		TypeAdapterForLong typeAdapterForLong = new TypeAdapterForLong();

		assertEquals("1", typeAdapterForLong.adaptSingleValue("1", ERunMode.QUIET, extLanguageManagerForSimple));
		assertEquals("1", typeAdapterForLong.adaptSingleValue("1.0", ERunMode.QUIET, extLanguageManagerForSimple));

		// invalid value

		assertEquals("0", typeAdapterForLong.adaptSingleValue("ab", ERunMode.QUIET, extLanguageManagerForSimple));

		try {
			typeAdapterForLong.adaptSingleValue("ab", ERunMode.WITH_EXCEPTION, extLanguageManagerForSimple);
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, TypeAdapterHelper.CANNOT_CONVERT_VALUE);
		}

		// symbolic value

		assertEquals("0", typeAdapterForLong.adaptSingleValue("MAX_VALUE", ERunMode.QUIET, extLanguageManagerForSimple));

		try {
			typeAdapterForLong.adaptSingleValue("MAX_VALUE", ERunMode.WITH_EXCEPTION, extLanguageManagerForSimple);
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, TypeAdapterForNumericType.SPECIAL_VALUES_ARE_NOT_ALLOWED);
		}
	}

}
