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

import com.ecfeed.core.testutils.RandomModelGenerator;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ExtLanguage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MethodNodeHelperTest {

	@Test
	public void test1(){

		MethodNode m1 = new MethodNode("method_1", null);

		String signature = MethodNodeHelper.createSignature(m1, ExtLanguage.JAVA);
		assertEquals("method_1()", signature);

		signature = MethodNodeHelper.createSignature(m1, ExtLanguage.SIMPLE);
		assertEquals("method 1()", signature);
	}

}
