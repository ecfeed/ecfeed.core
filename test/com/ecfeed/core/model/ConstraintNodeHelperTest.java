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

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ExtLanguage;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import org.junit.Test;

import static com.ecfeed.core.model.ConstraintNodeHelper.createSignature;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConstraintNodeHelperTest {

	@Test
	public void createSignatureTest1(){

		ConstraintNode c1 = new ConstraintNode("c", new Constraint("c", null, new StaticStatement(true, null), new StaticStatement(true, null)), null);

		c1.setName("c_1");

		c1.getConstraint().setPremise(new StaticStatement(false, null));

		c1.getConstraint().setConsequence(new StaticStatement(false, null));

		String signature = createSignature(c1,  new ExtLanguageManagerForJava());
		assertEquals("c_1: false => false", signature);

		signature = createSignature(c1,  new ExtLanguageManagerForSimple());
		assertEquals("c 1: false => false", signature);
	}

	@Test
	public void createSignatureTest2() {

		final ExtLanguageManagerForJava extLanguageManagerForJava = new ExtLanguageManagerForJava();
		final ExtLanguageManagerForSimple extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		AbstractStatement premise = createPremiseWithValueCondition();
		AbstractStatement consequence = createConsequenceWithValueCondition();

		Constraint constraint = new Constraint("co_1", null, premise, consequence);

		ConstraintNode c1 = new ConstraintNode("cn", constraint, null);

		String signature = ConstraintNodeHelper.createSignature(c1, extLanguageManagerForJava);
		assertEquals("co_1: par_1=A => par_2=C", signature);

		signature = ConstraintNodeHelper.createSignature(c1, extLanguageManagerForSimple);
		assertEquals("co 1: par 1=A => par 2=C", signature);

		System.out.println(signature);
	}

	private AbstractStatement createPremiseWithValueCondition() {

		MethodParameterNode parameter1 = new MethodParameterNode("par_1", "int", "0", false, null);

		AbstractStatement premise =
				RelationStatement.createStatementWithValueCondition(
						parameter1, EMathRelation.EQUAL, "A");

		return premise;
	}

	private AbstractStatement createConsequenceWithValueCondition() {

		MethodParameterNode parameter2 = new MethodParameterNode("par_2", "int", "0", false, null);

		AbstractStatement consequence =
				RelationStatement.createStatementWithValueCondition(
						parameter2, EMathRelation.EQUAL, "C");

		return consequence;
	}


}
