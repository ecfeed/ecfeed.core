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

import com.ecfeed.core.utils.ExtLanguage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConstraintNodeHelperTest {

	@Test
	public void createSignatureTest(){

		ConstraintNode c1 = new ConstraintNode("c", null, new Constraint("c", null, new StaticStatement(true, null), new StaticStatement(true, null)));

		c1.setName("c_1");

		c1.getConstraint().setPremise(new StaticStatement(false, null));

		c1.getConstraint().setConsequence(new StaticStatement(false, null));

		String signature = ConstraintNodeHelper.createSignature(c1,  ExtLanguage.JAVA);
		assertEquals("c_1: false ⇒ false", signature);

		signature = ConstraintNodeHelper.createSignature(c1,  ExtLanguage.SIMPLE);
		assertEquals("c 1: false ⇒ false", signature);
	}
}