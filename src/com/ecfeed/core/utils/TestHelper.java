package com.ecfeed.core.utils;

import static org.junit.Assert.fail;

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceCondition;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.IStatementCondition;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.RelationStatement;

public class TestHelper {

	public static void checkExceptionMessage(Exception e, String... expectedItems) { 

		String message = e.getMessage();

		checkMessageIntr(message, expectedItems);
	}

	public static void checkMessage(String message, String... expectedItems) {

		checkMessageIntr(message, expectedItems);
	}

	public static void checkMessageIntr(String message, String[] expectedItems) {

		int index = 0;
		int itemIndex;

		for (String expectedItem : expectedItems ) {

			if (!message.contains(expectedItem)) {
				ExceptionHelper.reportRuntimeException("Expected item: " + expectedItem + " not found.");
			}

			itemIndex = message.indexOf(expectedItem);

			if (itemIndex < index) {
				ExceptionHelper.reportRuntimeException("Invalid order of expected items.");
			}

			index = itemIndex;
		}
	}

	public static void addSimpleChoiceConstraintToMethod(
			MethodNode methodNode,
			String constraintName,
			BasicParameterNode methodParameterNode,
			ChoiceNode choiceNode1,
			ChoiceNode choiceNode2) {

		RelationStatement relationStatement1 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						methodParameterNode, EMathRelation.EQUAL, choiceNode1);

		RelationStatement relationStatement2 = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						methodParameterNode, EMathRelation.LESS_THAN, choiceNode2);

		Constraint constraint = new Constraint(
				constraintName, 
				ConstraintType.EXTENDED_FILTER, 
				relationStatement1, 
				relationStatement2, 
				null);

		ConstraintNode constraintNode = new ConstraintNode(constraintName, constraint, null);

		methodNode.addConstraint(constraintNode);
	}

	public static ChoiceNode getChoiceNodeFromConstraintPrecondition(
			MethodNode methodNode, 
			int constraintIndex) {

		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(constraintIndex);

		AbstractStatement precondition = constraintNode.getConstraint().getPrecondition();

		ChoiceNode choiceNode = getChoiceNodeFromChoiceCondition(precondition);

		return choiceNode;
	}

	public static ChoiceNode getChoiceNodeFromConstraintPrecondition(
			MethodNode methodNode) {

		return getChoiceNodeFromConstraintPrecondition( methodNode, 0);
	}


	public static ChoiceNode getChoiceNodeFromChoiceCondition(AbstractStatement abstractStatement) {

		RelationStatement relationStatement = (RelationStatement)abstractStatement; 

		IStatementCondition statementCondition = relationStatement.getCondition();

		ChoiceCondition choiceCondition = (ChoiceCondition)statementCondition;

		ChoiceNode choiceNode = choiceCondition.getRightChoice();

		return choiceNode;
	}

	public static ChoiceNode getChoiceNodeFromConstraintPostcondition(
			MethodNode methodNode, int constraintIndex) {

		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(constraintIndex);

		AbstractStatement postcondition = constraintNode.getConstraint().getPostcondition();

		ChoiceNode choiceNode = TestHelper.getChoiceNodeFromChoiceCondition(postcondition);

		return choiceNode;
	}

	public static ChoiceNode getChoiceNodeFromConstraintPostcondition(MethodNode methodNode) {

		return getChoiceNodeFromConstraintPostcondition(methodNode, 0);
	}
	
	public static void assertEqualsByLines(String expectedResult, String result) {

		final String lineSeparator = System.getProperty("line.separator");

		String[] expectedResultLines = expectedResult.split(lineSeparator);
		String[] resultLines = result.split(lineSeparator);

		int minLines = Math.min(expectedResultLines.length, resultLines.length);

		for (int lineIndex = 0; lineIndex < minLines; lineIndex++) {

			String expectedLine = expectedResultLines[lineIndex];
			expectedLine = expectedLine.replace("\r", "");

			String resultLine = resultLines[lineIndex];
			resultLine = resultLine.replace("\r", "");

			if (!StringHelper.isEqual(expectedLine, resultLine)) {
				fail("Line: " + (lineIndex + 1) + " differs.");
			}
		}
		
		if (expectedResultLines.length != resultLines.length) {
			fail("Content does not match");
			return;
		}
	}	

}

