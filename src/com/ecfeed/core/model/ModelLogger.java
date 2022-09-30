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

import java.util.List;


public class ModelLogger {

	private static final int indentIncrement = 4;

	//	public static void printListOfChoices(String message, List<ChoiceNode> choices, int indent) {
	//		for (ChoiceNode choice : choices) {
	//			printChoiceNode(choice, indent);
	//		}
	//	}


	public static void printModel(String message, AbstractNode someNodeOfModel) {
		AbstractNode root = ModelHelper.findRoot(someNodeOfModel);

		if (root == null) {
			System.out.println("Root not found.");
		}
		System.out.println("Model vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
		System.out.println("Message: " + message);
		printChildren(root, 0);
		System.out.println("Model ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
	}

	private static void printChildren(AbstractNode abstractNode, int indent) {

		printAbstractNode(abstractNode, indent);

		List<? extends AbstractNode> children = abstractNode.getChildren();

		if (children.size() == 0) {
			return;
		}

		for (AbstractNode child : children) {
			printChildren(child, indent + indentIncrement);
		}
	}

	private static void printIndentedLine(String line, int indent) {
		String indentStr = new String(new char[indent]).replace("\0", " ");
		System.out.println(indentStr + line);

	}

	private static void printFieldLine(String line, int indent) {
		printIndentedLine("F:" + line, indent);
	}	

	private static void printObjectLine(AbstractNode abstractNode, String fieldName, int indent) {
		printIndentedLine(
				getIsFieldStr(fieldName) + 
				abstractNode.getClass().getSimpleName() +
				getFieldStr(fieldName) +
				", " + abstractNode.getName()+ 
				", #" + abstractNode.hashCode(), indent);
	}

	private static void printAbstractNode(AbstractNode abstractNode, int indent) {

		if (abstractNode == null) {
			printIndentedLine("Abstract node is null", indent);
			return;
		}
		if (abstractNode instanceof TestCaseNode) {
			printTestCaseNode((TestCaseNode)abstractNode, null, indent);
			return;
		}
		if (abstractNode instanceof ConstraintNode) {
			printConstraintNode((ConstraintNode)abstractNode, null, indent);
			return;
		}
		if (abstractNode instanceof MethodNode) {
			printMethodNode((MethodNode)abstractNode, null, indent);
			return;
		}
		if (abstractNode instanceof MethodParameterNode) {
			printMethodParameterNode((MethodParameterNode)abstractNode, null, indent);
			return;
		}		
		if (abstractNode instanceof ChoiceNode) {
			printChoiceNode((ChoiceNode)abstractNode, null, indent);
			return;
		}
		printObjectLine(abstractNode, null, indent);
	}


	private static void printTestCaseNode(TestCaseNode testCaseNode, String fieldName, int indent) {
		printObjectLine(testCaseNode, fieldName, indent);

		List<ChoiceNode> choices = testCaseNode.getTestData();

		for (ChoiceNode choice : choices) {
			printAbstractNode(choice, indent + indentIncrement);
		}
	}

	private static void printConstraintNode(ConstraintNode constraintNode, String fieldName, int indent) {
		if (constraintNode == null) {
			printIndentedLine("ConstraintNode is null", indent);
			return;
		}		
		printObjectLine(constraintNode, fieldName, indent);

		AbstractNode parent = constraintNode.getParent();
		printMethodNode((MethodNode)parent, "parentMethod", indent + indentIncrement);

		AbstractStatement precondition = constraintNode.getConstraint().getPrecondition();
		printAbstractStatement(precondition, "Precondition", indent + indentIncrement);

		AbstractStatement postcondition = constraintNode.getConstraint().getPostcondition();
		printAbstractStatement(postcondition, "Postcondition", indent + indentIncrement);
	}

	private static void printMethodNode(MethodNode methodNode, String fieldName, int indent) {
		if (methodNode == null) {
			printIndentedLine("MethodNode is null", indent);
			return;
		}

		printObjectLine(methodNode, fieldName, indent);
	}

	private static void printMethodParameterNode(MethodParameterNode methodParameterNode, String fieldName, int indent) {
		if (methodParameterNode == null) {
			printIndentedLine("MethodNode is null", indent);
			return;
		}
		printObjectLine(methodParameterNode, fieldName, indent);

		boolean isLinked = methodParameterNode.isLinked();
		printFieldLine(methodParameterNode.getType() + " [isLinked]=" + isLinked, indent + indentIncrement);

		if (isLinked) {
			GlobalParameterNode globalParameterNode = methodParameterNode.getLinkToGlobalParameter();
			if (globalParameterNode == null) {
				printIndentedLine("GlobalParameterNode is null", indent + indentIncrement);
			} else {
				printAbstractNode(globalParameterNode, indent + indentIncrement);
			}
		}
	}	

	private static void printChoiceNode(ChoiceNode choiceNode, String fieldName, int indent) {
		printObjectLine(choiceNode, fieldName, indent);
		printObjectLine(choiceNode.getParameter(), "Parameter", indent + indentIncrement);
	}

	private static void printAbstractStatement(AbstractStatement abstractStatement, String fieldName, int indent) {
		printIndentedLine(
				getIsFieldStr(fieldName) + 
				abstractStatement.getClass().getSimpleName() +
				getFieldStr(fieldName) +
				", #" + abstractStatement.hashCode() +
				"  (" + abstractStatement.toString() + ")", 
				indent);
	}

	private static String getIsFieldStr(String fieldName) {
		String isFieldStr = "";
		if (fieldName != null) {
			isFieldStr = "F:";
		}
		return isFieldStr;
	}

	private static String getFieldStr(String fieldName) {
		String fieldStr = "";
		if (fieldName != null) {
			fieldStr = "[" + fieldName + "]";
		}
		return fieldStr;
	}	
}
