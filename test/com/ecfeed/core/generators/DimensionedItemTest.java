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

import static org.junit.Assert.*;

import org.junit.Test;


public class DimensionedItemTest {
	
	@Test
	public void shouldReturnDimensionItem() {

		String item = new String();
		int dim = 0;

		DimensionedItem<String> item2 = new DimensionedItem<String>(dim, item);

		assertEquals(item, item2.getItem());
		assertEquals(dim, item2.getDimension());
	}

	@Test
	public void shouldCompareItems(){

		DimensionedItem<String> item1 = new DimensionedItem<String>(0, new String());

		DimensionedItem<String> item2 = new DimensionedItem<String>(0, new String());
		assertTrue(item1.equals(item2));

		DimensionedItem<String> item3 = new DimensionedItem<String>(10, "blabla");
		assertFalse(item1.equals(item3));
	}
	
}
