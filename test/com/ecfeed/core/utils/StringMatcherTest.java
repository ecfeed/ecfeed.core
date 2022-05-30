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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class StringMatcherTest{

	@Test
	public void findSimilarPairs1() {

		List<String> strings1 = new ArrayList<>();
		strings1.add("RC1");
		strings1.add("RC2");

		List<String> strings2 = new ArrayList<>();
		strings2.add("MC1");
		strings2.add("MC2");

		List<Pair<String,String>> pairs = StringMatcher.createListOfSimilarStringPairs(strings1, strings2, 0.1);

		assertEquals(2, pairs.size());

		assertPairMatches(pairs.get(0), "RC1", "MC1");
		assertPairMatches(pairs.get(1), "RC2", "MC2");
	}

	private void assertPairMatches(Pair<String, String> pair, String value1, String value2) {
		assertEquals(value1, pair.getFirst());
		assertEquals(value2, pair.getSecond());
	}

	@Test
	public void findSimilarPairs2() {

		List<String> strings1 = new ArrayList<>();
		strings1.add("German");
		strings1.add("Chinese");
		strings1.add("Eng.");


		List<String> strings2 = new ArrayList<>();
		strings2.add("chinese");
		strings2.add("ger language");
		strings2.add("english");

		// high similarity

		List<Pair<String,String>> pairs = StringMatcher.createListOfSimilarStringPairs(strings1, strings2, 0.6);

		assertEquals(1, pairs.size());
		assertPairMatches(pairs.get(0), "Chinese", "chinese");

		// low similarity

		pairs = StringMatcher.createListOfSimilarStringPairs(strings1, strings2, 0.1);

		assertEquals(3, pairs.size());

		assertPairMatches(pairs.get(0), "Chinese", "chinese");
		assertPairMatches(pairs.get(1), "Eng.", "english");
		assertPairMatches(pairs.get(2), "German", "ger language");
	}

}
