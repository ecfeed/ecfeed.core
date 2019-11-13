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

import static org.junit.Assert.*;

import org.junit.Test;

public class ConstraintNodeTest {

	@Test
	public void compare(){
		ConstraintNode c1 = new ConstraintNode("c", null, new ImplicationConstraint("c", null, new StaticStatement(true, null), new StaticStatement(true, null)));
		ConstraintNode c2 = new ConstraintNode("c", null, new ImplicationConstraint("c", null, new StaticStatement(true, null), new StaticStatement(true, null)));

		assertTrue(c1.isMatch(c2));

		c1.setFullName("c1");
		assertFalse(c1.isMatch(c2));
		c2.setFullName("c1");
		assertTrue(c1.isMatch(c2));

		c1.getConstraint().setPremise(new StaticStatement(false, null));
		assertFalse(c1.isMatch(c2));
		c2.getConstraint().setPremise(new StaticStatement(false, null));
		assertTrue(c1.isMatch(c2));

		c1.getConstraint().setConsequence(new StaticStatement(false, null));
		assertFalse(c1.isMatch(c2));
		c2.getConstraint().setConsequence(new StaticStatement(false, null));
		assertTrue(c1.isMatch(c2));
	}
}
