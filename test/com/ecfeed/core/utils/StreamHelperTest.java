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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

public class StreamHelperTest{

	private void testConvertDeconvert(String expectedResult) throws EcException {
		InputStream is = new ByteArrayInputStream(expectedResult.getBytes());
		String result = StreamHelper.streamToString(is);
		assertEquals(expectedResult, result);
	}

	@Test
	public void shouldConvertSimpleStreamToString() throws EcException{
		testConvertDeconvert(new String("12345"));
	}

	@Test
	public void shouldConvertEmptyStreamToString() throws EcException{
		testConvertDeconvert(new String());	
	}	

	@Test
	public void shouldConvertStreamWithNewlinesToString() throws EcException{
		testConvertDeconvert(new String("111\n222\n"));	
	}	
}
