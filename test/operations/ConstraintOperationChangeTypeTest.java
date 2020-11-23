/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package operations;

import com.ecfeed.core.model.*;
import com.ecfeed.core.operations.ConstraintOperationChangeType;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.utils.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ConstraintOperationChangeTypeTest {

	@Test
	public void renameImplicationToInvariantTest() {

		MethodNode methodNode = new MethodNode("method", null);

		MethodParameterNode methodParameterNode1 =
				new MethodParameterNode("par1", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode1 = new ChoiceNode("choice1", "1", null);
		methodParameterNode1.addChoice(choiceNode1);

		MethodParameterNode methodParameterNode2 =
				new MethodParameterNode("par2", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode2 = new ChoiceNode("choice2", "2", null);
		methodParameterNode2.addChoice(choiceNode2);

		RelationStatement initialPrecondition =
				RelationStatement.createStatementWithChoiceCondition(methodParameterNode1, EMathRelation.EQUAL, choiceNode1);

		RelationStatement initialPostcondition =
				RelationStatement.createStatementWithChoiceCondition(methodParameterNode2, EMathRelation.EQUAL, choiceNode2);

		Constraint constraint = new Constraint("constraint", ConstraintType.IMPLICATION, initialPrecondition, initialPostcondition, null);

		Constraint initialConstraint = constraint.getCopy();

		ConstraintNode constraintNode = new ConstraintNode("cnode", constraint, null);

		// executing operation

		IModelOperation changeTypeOperation =
				new ConstraintOperationChangeType(
						constraintNode,
						ConstraintType.INVARIANT,
						new ExtLanguageManagerForJava());

		try {
			changeTypeOperation.execute();
		} catch (Exception e) {
		}

		// checking precondition

		AbstractStatement precondition = constraintNode.getConstraint().getPrecondition();

		if (!(precondition instanceof StaticStatement)) {
			fail();
		}

		StaticStatement staticPrecondition = (StaticStatement)precondition;

		if (EvaluationResult.TRUE != staticPrecondition.getValue()) {
			fail();
		}

		// checking postcondition

		AbstractStatement postcondition = constraintNode.getConstraint().getPostcondition();

		if (!(postcondition instanceof RelationStatement)) {
			fail();
		}

		RelationStatement relationStatementPostcondition = (RelationStatement)postcondition;

		assertEquals(initialPostcondition, relationStatementPostcondition);

		// reverse operation

		IModelOperation reverseOperation = changeTypeOperation.getReverseOperation();

		try {
			reverseOperation.execute();
		} catch (Exception e) {
		}

		// checking precondition

		precondition = constraintNode.getConstraint().getPrecondition();

		assertTrue(precondition.isEqualTo(initialPrecondition));

		// checking postcondition

		postcondition = constraintNode.getConstraint().getPostcondition();

		assertTrue(postcondition.isEqualTo(initialPostcondition));
	}

}
