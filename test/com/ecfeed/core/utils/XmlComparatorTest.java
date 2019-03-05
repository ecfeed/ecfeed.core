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

public class XmlComparatorTest{

	@Test
	public void shouldCompare1(){
		assertTrue(XmlComparator.areXmlsEqual(null, null));
	}

	@Test
	public void shouldCompare2(){
		assertTrue(XmlComparator.areXmlsEqual("<>", "<>"));
	}	

	@Test
	public void shouldCompare3(){
		assertFalse(XmlComparator.areXmlsEqual("<X>", "<>"));
	}	

	@Test
	public void shouldCompare4(){
		assertFalse(XmlComparator.areXmlsEqual("<X>", "<Y>"));
	}	

	@Test
	public void shouldCompare5(){
		assertTrue(XmlComparator.areXmlsEqual("<X>", "<X>"));
	}	

	@Test
	public void shouldCompare6(){
		assertTrue(XmlComparator.areXmlsEqual("<><>", "<><>"));
	}	

	@Test
	public void shouldCompare7(){
		assertTrue(XmlComparator.areXmlsEqual("<><>", "<> <>"));
	}

	@Test
	public void shouldCompare8(){
		assertTrue(XmlComparator.areXmlsEqual("<>\t<>", "<> <>"));
	}

	@Test
	public void shouldCompare9(){
		assertTrue(XmlComparator.areXmlsEqual("<>\t<>", "<>\n<>"));
	}	

	@Test
	public void shouldCompare10(){
		assertTrue(XmlComparator.areXmlsEqual("<><>", "<>\n\t<>"));
	}

	@Test
	public void shouldCompare11(){
		assertTrue(XmlComparator.areXmlsEqual("<>          <>", "<>\n\t<>"));
	}

	@Test
	public void shouldCompare12(){
		assertFalse(XmlComparator.areXmlsEqual("<>          <x>", "<>\n\t<>"));
	}	


}
