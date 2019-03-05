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

import org.junit.Test;

import com.ecfeed.core.utils.CheckSumHelper;

public class CheckSumHelperTest{

	@Test
	public void shouldCalculateSha1A(){

		String result = CheckSumHelper.calculateSha1("ABC");

		assertEquals("3c01bdbb26f358bab27f267924aa2c9a03fcfdb8", result);
	}

	@Test
	public void shouldCalculateSha1B(){

		String result = CheckSumHelper.calculateSha1("X");

		assertEquals("c032adc1ff629c9b66f22749ad667e6beadf144b", result);
	}	

}
