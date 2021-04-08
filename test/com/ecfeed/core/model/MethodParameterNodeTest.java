/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class MethodParameterNodeTest {

	@Test
	public void createParameterTest() {

		try {
			new MethodParameterNode("par%1", "int", "0", false, null);
			fail();
		} catch (Exception e) {
		}

		try {
			new MethodParameterNode("!", "int", "0", false, null);
			fail();
		} catch (Exception e) {
		}

		try {
			new MethodParameterNode("a b", "int", "0", false, null);
			fail();
		} catch (Exception e) {
		}
	}


}
