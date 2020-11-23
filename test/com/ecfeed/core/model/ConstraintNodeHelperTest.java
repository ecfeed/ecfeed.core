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

		ConstraintNode c1 = new ConstraintNode("c", new Constraint("c", ConstraintType.IMPLICATION, new StaticStatement(true, null), new StaticStatement(true, null), null), null);

		c1.setName("c_1");

		c1.getConstraint().setPrecondition(new StaticStatement(false, null));

		c1.getConstraint().setPostcondition(new StaticStatement(false, null));

		String signature = createSignature(c1,  new ExtLanguageManagerForJava());
		assertEquals("c_1: false => false", signature);

		signature = createSignature(c1,  new ExtLanguageManagerForSimple());
		assertEquals("c 1: false => false", signature);
	}

	@Test
	public void createSignatureTest2() {

		final ExtLanguageManagerForJava extLanguageManagerForJava = new ExtLanguageManagerForJava();
		final ExtLanguageManagerForSimple extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		MethodParameterNode parameter1 = new MethodParameterNode("par_1", "int", "0", false, null);

		AbstractStatement precondition1 =
				RelationStatement.createStatementWithValueCondition(
						parameter1, EMathRelation.EQUAL, "A");

		AbstractStatement precondition = precondition1;

		MethodParameterNode parameter2 = new MethodParameterNode("par_2", "int", "0", false, null);

		AbstractStatement postcondition1 =
				RelationStatement.createStatementWithValueCondition(
						parameter2, EMathRelation.EQUAL, "C");

		AbstractStatement postcondition = postcondition1;

		Constraint constraint = new Constraint("co_1", ConstraintType.IMPLICATION, precondition, postcondition, null);

		ConstraintNode c1 = new ConstraintNode("cn", constraint, null);

		String signature = ConstraintNodeHelper.createSignature(c1, extLanguageManagerForJava);
		assertEquals("co_1: par_1=A => par_2=C", signature);

		signature = ConstraintNodeHelper.createSignature(c1, extLanguageManagerForSimple);
		assertEquals("co 1: par 1=A => par 2=C", signature);
	}

	@Test
	public void createSignatureTest3() {

		final ExtLanguageManagerForJava extLanguageManagerForJava = new ExtLanguageManagerForJava();
		final ExtLanguageManagerForSimple extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		ChoiceNode choice1 = new ChoiceNode("choice_1", "value1", null);
		ChoiceNode choice2 = new ChoiceNode("choice 2", "value2", null);

		MethodParameterNode parameter1 = new MethodParameterNode("par_1", "int", "0", false, null);

		AbstractStatement precondition =
				RelationStatement.createStatementWithChoiceCondition(
						parameter1, EMathRelation.EQUAL, choice1);

		MethodParameterNode parameter2 = new MethodParameterNode("par_2", "int", "0", false, null);

		AbstractStatement postcondition =
				RelationStatement.createStatementWithChoiceCondition(
						parameter2, EMathRelation.EQUAL, choice2);

		Constraint constraint = new Constraint("co", ConstraintType.IMPLICATION, precondition, postcondition, null);

		ConstraintNode constraintNode = new ConstraintNode("cn", constraint, null);

		String signature = ConstraintNodeHelper.createSignature(constraintNode, extLanguageManagerForJava);
		assertEquals("co: par_1=choice_1[choice] => par_2=choice 2[choice]", signature);

		signature = ConstraintNodeHelper.createSignature(constraintNode, extLanguageManagerForSimple);
		assertEquals("co: par 1=choice_1[choice] => par 2=choice 2[choice]", signature);
	}

}
