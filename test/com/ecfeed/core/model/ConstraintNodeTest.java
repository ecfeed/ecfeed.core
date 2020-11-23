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
		ConstraintNode c1 = new ConstraintNode("c", new Constraint("c", ConstraintType.IMPLICATION, new StaticStatement(true, null), new StaticStatement(true, null), null), null);
		ConstraintNode c2 = new ConstraintNode("c", new Constraint("c", ConstraintType.IMPLICATION, new StaticStatement(true, null), new StaticStatement(true, null), null), null);

		assertTrue(c1.isMatch(c2));

		c1.setName("c1");
		assertFalse(c1.isMatch(c2));
		c2.setName("c1");
		assertTrue(c1.isMatch(c2));

		c1.getConstraint().setPrecondition(new StaticStatement(false, null));
		assertFalse(c1.isMatch(c2));
		c2.getConstraint().setPrecondition(new StaticStatement(false, null));
		assertTrue(c1.isMatch(c2));

		c1.getConstraint().setPostcondition(new StaticStatement(false, null));
		assertFalse(c1.isMatch(c2));
		c2.getConstraint().setPostcondition(new StaticStatement(false, null));
		assertTrue(c1.isMatch(c2));
	}
}
