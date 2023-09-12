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

public class RegexTest {

	boolean matchTextWithRegex(String text, String regex) {
		return text.matches(regex);
	}

	boolean matchTextWithRegexAlphaSp64(String text) {
		return matchTextWithRegex(text, RegexHelper.REGEX_ALPHANUMERIC_WITH_SPACES_300);
	}

	@Test
	public void shouldMatchTextWithoutSpaces(){
		assertTrue(matchTextWithRegexAlphaSp64("abc"));
	}

	@Test
	public void shouldMatchTextWithSpaces(){
		assertTrue(matchTextWithRegexAlphaSp64("a b c "));
	}	

	@Test
	public void shouldNotMatchTextWithInvalidCharacters(){
		assertFalse(matchTextWithRegexAlphaSp64("a^bc"));
	}	

	@Test
	public void shouldNotMatchTextWithLeadingSpace(){
		assertFalse(matchTextWithRegexAlphaSp64(" abc"));
	}	

}
