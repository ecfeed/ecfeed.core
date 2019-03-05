/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class CulpritTest {


	@Test
	public void ShouldReturnZeroOccurenceAndFailureCount(){
		Culprit culprit = new Culprit();
		assertEquals(0, culprit.getFailureCount());
		assertEquals(0, culprit.getOccurenceCount());
	}

	@Test
	public void ShouldIncrementOccurenceAndFailureCount(){
		Culprit culpritSum = new Culprit();

		assertEquals(0, culpritSum.getFailureCount());

		culpritSum.incrementFailures(1);
		assertEquals(1, culpritSum.getFailureCount());
		culpritSum.incrementFailures(1);
		assertEquals(2, culpritSum.getFailureCount());

		culpritSum.incrementOccurenceCount(1);
		assertEquals(1, culpritSum.getOccurenceCount());
		culpritSum.incrementOccurenceCount(1);
		assertEquals(2, culpritSum.getOccurenceCount());
	}

	@Test
	public void ShouldAggregateOccurencesAndFailures(){
		Culprit culpritSum = new Culprit();
		Culprit culpritOne = new Culprit(new ArrayList<DimensionedString>(), 1, 1);

		assertEquals(0, culpritSum.getFailureCount());

		culpritSum.aggregateOccurencesAndFailures(culpritOne);
		assertEquals(1, culpritSum.getFailureCount());
		assertEquals(1, culpritSum.getOccurenceCount());

		culpritSum.aggregateOccurencesAndFailures(culpritOne);
		assertEquals(2, culpritSum.getFailureCount());
		assertEquals(2, culpritSum.getOccurenceCount());
	}	

	@Test
	public void ShouldMatchEmptyCulprits(){
		Culprit candidate = new Culprit();
		Culprit culprit = new Culprit();
		assertTrue(culprit.isTupleMatch(candidate));
	}
	
	@Test
	public void ShouldMatchClonedCulprit(){
		Culprit culpritone = new Culprit(new ArrayList<DimensionedString>(), 3, 5);
		Culprit clonedculprit = culpritone.makeClone();
		assertTrue(clonedculprit.isBasicMatch(culpritone));
		
	}

}
