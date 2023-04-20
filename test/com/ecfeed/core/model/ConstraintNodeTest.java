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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ecfeed.core.type.adapter.JavaPrimitiveTypePredicate;
import com.ecfeed.core.utils.EMathRelation;

public class ConstraintNodeTest {

	@Test
	public void compare(){
		ConstraintNode c1 = 
				new ConstraintNode(
						"c", 
						new Constraint(
								"c", 
								ConstraintType.EXTENDED_FILTER, 
								new StaticStatement(true, null), 
								new StaticStatement(true, null), 
								null), 
						null);

		ConstraintNode c2 = 
				new ConstraintNode("c", 
						new Constraint(
								"c", 
								ConstraintType.EXTENDED_FILTER, 
								new StaticStatement(true, null), 
								new StaticStatement(true, null), 
								null), 
						null);

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

	@Test
	public void derandomizeTest(){

		BasicParameterNode methodParameterNode = 
				new BasicParameterNode("par1",	"int", "0",	false, null);

		for (int counter = 0; counter < 5; counter++) {
			ChoiceNode choiceNode = new ChoiceNode("choice1", "1:5", true, null);

			choiceNode.setParent(methodParameterNode);

			RelationStatement precondition = 
					RelationStatement.createRelationStatementWithChoiceCondition(
							methodParameterNode, null, EMathRelation.EQUAL, choiceNode);

			StaticStatement postcondition = new StaticStatement(true, null);	


			Constraint constraint = new Constraint(
					"name", 
					ConstraintType.BASIC_FILTER, 
					precondition, 
					postcondition, 
					null);

			ConstraintNode cn1 = new ConstraintNode("cnode",	constraint,	null);

			cn1.derandomize();

			assertFalse(choiceNode.isRandomizedValue());
			String valueString = choiceNode.getValueString();

			Integer value = Integer.parseInt(valueString);
			assertTrue(value <=5);
			assertTrue(value >=1);
		}
	}
	
	@Test
	public void copyConstraintTest(){
		MethodNode method = new MethodNode("method", null);
		BasicParameterNode par1 = new BasicParameterNode("par1", "int", "0", false, null);
		BasicParameterNode par2 = new BasicParameterNode("par2", "int", "0", true, null);
		ChoiceNode choice1 = new ChoiceNode("choice1", "0", null);
		choice1.addLabel("label");
		par1.addChoice(choice1);

		ChoiceNode expectedChoice = new ChoiceNode("expected", "0", null);
		expectedChoice.setParent(par2);

		method.addParameter(par1);
		method.addParameter(par2);

		StatementArray precondition = new StatementArray(StatementArrayOperator.OR, null);
		precondition.addStatement(new StaticStatement(true, null));
		precondition.addStatement(RelationStatement.createRelationStatementWithChoiceCondition(par1, null, EMathRelation.EQUAL, choice1));
		precondition.addStatement(RelationStatement.createRelationStatementWithLabelCondition(par1, null, EMathRelation.NOT_EQUAL, "label"));
		ExpectedValueStatement postcondition = new ExpectedValueStatement(par2, null, expectedChoice, new JavaPrimitiveTypePredicate());

		ConstraintNode constraint = new ConstraintNode("constraint", new Constraint("constraint", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null), null);
		method.addConstraint(constraint);

		ConstraintNode copy = constraint.makeClone();
		assertTrue(constraint.isMatch(copy));
	}
	

}
