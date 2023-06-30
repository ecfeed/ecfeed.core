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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

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

		NodeMapper nodeMapper = new NodeMapper();
		ConstraintNode copy = constraint.makeClone(Optional.of(nodeMapper));
		assertTrue(constraint.isMatch(copy));
	}

	@Test
	public void replaceReferencesTest() {
		
		MethodNode method = new MethodNode("method", null);
		
		BasicParameterNode par1 = MethodNodeHelper.addNewBasicParameter(method, "par1", "int", "0", true, null);
		
		ChoiceNode choiceNode1 = 
				BasicParameterNodeHelper.addNewChoiceToBasicParameter(par1, "choice1", "1", false, true, null);
				
		BasicParameterNode par2 = MethodNodeHelper.addNewBasicParameter(method, "par2", "int", "0", true, null);

		ChoiceNode choiceNode2 = 
				BasicParameterNodeHelper.addNewChoiceToBasicParameter(par2, "choice1", "2", false, true, null);
		
		
		AbstractStatement precondition = 
				RelationStatement.createRelationStatementWithChoiceCondition(par1, null, EMathRelation.EQUAL, choiceNode1);		

		AbstractStatement postcondition = 
				RelationStatement.createRelationStatementWithChoiceCondition(par1, null, EMathRelation.EQUAL, choiceNode1);		
		
		Constraint constraint = 
				new Constraint(
						"constraint", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);
		
		ConstraintNode constraintNode = new ConstraintNode("constraint", constraint, null);
		
		NodeMapper nodeMapper = new NodeMapper();
		nodeMapper.addMappings(par1, par2);
		nodeMapper.addMappings(choiceNode1, choiceNode2);
		
		// initial check  
		
		BasicParameterNode initialPar1 = constraint.getPrecondition().getLeftParameter();
		assertEquals(par1, initialPar1);
		
		ChoiceNode initialChoice1 = getRightChoiceForReplaceReferencesTest(constraint.getPrecondition());
		assertEquals(choiceNode1, initialChoice1);
		
		// replace par1 to par2 with choices
		
		constraintNode.replaceReferences(nodeMapper, NodeMapper.MappingDirection.SOURCE_TO_DESTINATION);
		
		BasicParameterNode resultPar2 = constraint.getPrecondition().getLeftParameter();
		assertEquals(par2, resultPar2);

		ChoiceNode resultChoice2 = getRightChoiceForReplaceReferencesTest(constraint.getPrecondition());
		assertEquals(choiceNode2, resultChoice2);
		
		// replace par2 to par1 with choices
		
		constraintNode.replaceReferences(nodeMapper, NodeMapper.MappingDirection.DESTINATION_TO_SOURCE);
		
		BasicParameterNode resultPar1 = constraint.getPrecondition().getLeftParameter();
		assertEquals(par1, resultPar1);
		
		ChoiceNode resultChoice1 = getRightChoiceForReplaceReferencesTest(constraint.getPrecondition());
		assertEquals(choiceNode1, resultChoice1);
		
		// remove par1 from mapper
		
		nodeMapper.removeMappings(par1);
		
		// converting without par1

		constraintNode.replaceReferences(nodeMapper, NodeMapper.MappingDirection.SOURCE_TO_DESTINATION);
		
		BasicParameterNode resultPar1b = constraint.getPrecondition().getLeftParameter();
		assertEquals(par1, resultPar1b);

		ChoiceNode resultChoice2b = getRightChoiceForReplaceReferencesTest(constraint.getPrecondition());
		assertEquals(choiceNode2, resultChoice2);
		
	}

	private ChoiceNode getRightChoiceForReplaceReferencesTest(AbstractStatement condition) {
		
		RelationStatement relationStatement = (RelationStatement)condition;
		
		ChoiceCondition choiceCondition = (ChoiceCondition)(relationStatement.getCondition());
		
		return choiceCondition.getRightChoice();
	}

}
