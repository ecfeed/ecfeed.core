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

public class ConversionPartMatcherTest {

	@Test
	public void findSimilarPairs1() {

		List<ParameterConversionItemPartForRaw> items1 = new ArrayList<>();
		items1.add(new ParameterConversionItemPartForRaw("C", "RC1"));
		items1.add(new ParameterConversionItemPartForRaw("C", "RC2"));

		List<ParameterConversionItemPartForRaw> items2 = new ArrayList<>();
		items2.add(new ParameterConversionItemPartForRaw("C", "MC1"));
		items2.add(new ParameterConversionItemPartForRaw("C", "MC2"));

		List<Pair<ParameterConversionItemPartForRaw, ParameterConversionItemPartForRaw>> pairs = 
				ConversionPartMatcher.createListOfSimilarStringPairs(items1, items2, 0.1);

		assertEquals(2, pairs.size());

		assertPairMatches(pairs.get(0), "RC1", "MC1");
		assertPairMatches(pairs.get(1), "RC2", "MC2");
	}

	private void assertPairMatches(Pair<ParameterConversionItemPartForRaw, ParameterConversionItemPartForRaw> pair, String value1, String value2) {
		assertEquals(value1, pair.getFirst().getName());
		assertEquals(value2, pair.getSecond().getName());
	}

	@Test
	public void findSimilarPairs2() {

		List<ParameterConversionItemPartForRaw> items1 = new ArrayList<>();
		items1.add(new ParameterConversionItemPartForRaw("C", "German"));
		items1.add(new ParameterConversionItemPartForRaw("C", "Chinese"));
		items1.add(new ParameterConversionItemPartForRaw("C", "Eng."));


		List<ParameterConversionItemPartForRaw> items2 = new ArrayList<>();
		items2.add(new ParameterConversionItemPartForRaw("C", "chinese"));
		items2.add(new ParameterConversionItemPartForRaw("C", "ger language"));
		items2.add(new ParameterConversionItemPartForRaw("C", "english"));

		// high similarity

		List<Pair<ParameterConversionItemPartForRaw, ParameterConversionItemPartForRaw>> pairs = 
				ConversionPartMatcher.createListOfSimilarStringPairs(items1, items2, 0.8);

		assertEquals(1, pairs.size());
		Pair<ParameterConversionItemPartForRaw, ParameterConversionItemPartForRaw> pair = pairs.get(0);
		assertPairMatches(pair, "Chinese", "chinese");

		// low similarity

		pairs = ConversionPartMatcher.createListOfSimilarStringPairs(items1, items2, 0.1);

		assertEquals(3, pairs.size());

		assertPairMatches(pair, "Chinese", "chinese");
		assertPairMatches(pairs.get(1), "Eng.", "english");
		assertPairMatches(pairs.get(2), "German", "ger language");
	}

}
