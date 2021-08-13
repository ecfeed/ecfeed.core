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

import com.ecfeed.core.type.adapter.TypeAdapterForString;

public class TypeAdapterForStringTest {

	@Test
	public void convertForQuietModeTest() {

		TypeAdapterForString typeAdapterForString = new TypeAdapterForString();

		String value = typeAdapterForString.generateValue("abc");
		assertEquals("abc",  value);

		try {
			typeAdapterForString.generateValue("{a-z}[6]{1-9}[3]@gmail.com");
			fail();
		} catch (Exception e) {
		}

		try {
			typeAdapterForString.generateValue("[a-z]{6}[1-9]{3}\\@gmail\\.com");
		} catch (Exception e) {
			fail();
		}
	}

}
