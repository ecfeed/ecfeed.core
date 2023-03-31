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


	public static String printModel(String message, IAbstractNode someNodeOfModel) {
		
		String text = "";
		
		IAbstractNode topNode = AbstractNodeHelper.findTopNode(someNodeOfModel);

		if (topNode == null) {
			return formatLine("Root not found.");
		}
		text += formatLine("Model vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
		text += formatLine("Message: " + message);
		text += printChildren(topNode, 0);
		text += formatLine("Model ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		
		return text;
	}

	private static String formatLine(Object object) {
		return object + "\n";
	}

	private static String printChildren(IAbstractNode abstractNode, int indent) {

		String text = "";
		
		text += printAbstractNode(abstractNode, indent);

		List<IAbstractNode> children = abstractNode.getChildren();

		if (children.size() == 0) {
			return text;
		}

		for (IAbstractNode child : children) {
			text += printChildren(child, indent + indentIncrement);
		}
		
		return text;
	}

	private static String printIndentedLine(String line, int indent) {
		
		String indentStr = new String(new char[indent]).replace("\0", " ");
		return (indentStr + line);

	}

	private static String printFieldLine(String line, int indent) {
		
		return printIndentedLine("F:" + line, indent);
	}	

	private static String printObjectLine(IAbstractNode abstractNode, String fieldName, int indent) {
		
		return printIndentedLine(
				getIsFieldStr(fieldName) + 
				abstractNode.getClass().getSimpleName() +
				getFieldStr(fieldName) +
				", " + abstractNode.getName()+ 
				", #" + abstractNode.hashCode(), indent);
	}

	private static String printAbstractNode(IAbstractNode abstractNode, int indent) {

		if (abstractNode == null) {
			return formatLine(printIndentedLine("Abstract node is null", indent));
		}
		if (abstractNode instanceof TestCaseNode) {
			return formatLine(printTestCaseNode((TestCaseNode)abstractNode, null, indent));
		}
		if (abstractNode instanceof ConstraintNode) {
			return formatLine(printConstraintNode((ConstraintNode)abstractNode, null, indent));
		}
		if (abstractNode instanceof MethodNode) {
			return formatLine(printMethodNode((MethodNode)abstractNode, null, indent));
		}
		if (abstractNode instanceof BasicParameterNode) {
			return formatLine(printMethodParameterNode((BasicParameterNode)abstractNode, null, indent));
		}		
		if (abstractNode instanceof ChoiceNode) {
			return formatLine(printChoiceNode((ChoiceNode)abstractNode, null, indent));
		}
		return printObjectLine(abstractNode, null, indent);
	}

	private static String printTestCaseNode(TestCaseNode testCaseNode, String fieldName, int indent) {
		
		String text = formatLine(printObjectLine(testCaseNode, fieldName, indent));

		List<ChoiceNode> choices = testCaseNode.getTestData();

		for (ChoiceNode choice : choices) {
			text += formatLine(printAbstractNode(choice, indent + indentIncrement));
		}
		
		return text;
	}

	private static String printConstraintNode(ConstraintNode constraintNode, String fieldName, int indent) {
		
		if (constraintNode == null) {
			return formatLine(printIndentedLine("ConstraintNode is null", indent));
		}	
		
		String text = formatLine(printObjectLine(constraintNode, fieldName, indent));

		IAbstractNode parent = constraintNode.getParent();
		text += formatLine(printMethodNode((MethodNode)parent, "parentMethod", indent + indentIncrement));

		AbstractStatement precondition = constraintNode.getConstraint().getPrecondition();
		text += formatLine(printAbstractStatement(precondition, "Precondition", indent + indentIncrement));

		AbstractStatement postcondition = constraintNode.getConstraint().getPostcondition();
		text += formatLine(printAbstractStatement(postcondition, "Postcondition", indent + indentIncrement));
		
		return text;
	}

	private static String printMethodNode(MethodNode methodNode, String fieldName, int indent) {
		
		if (methodNode == null) {
			return printIndentedLine("MethodNode is null", indent);
		}

		return printObjectLine(methodNode, fieldName, indent);
	}

	private static String printMethodParameterNode(BasicParameterNode methodParameterNode, String fieldName, int indent) {
		if (methodParameterNode == null) {
			return printIndentedLine("MethodNode is null", indent);
		}
		
		String text = formatLine(printObjectLine(methodParameterNode, fieldName, indent));

		boolean isLinked = methodParameterNode.isLinked();
		text += formatLine(printFieldLine(methodParameterNode.getType() + " [isLinked]=" + isLinked, indent + indentIncrement));

		if (isLinked) {
			BasicParameterNode globalParameterNode = (BasicParameterNode) methodParameterNode.getLinkToGlobalParameter();
			if (globalParameterNode == null) {
				text += formatLine(printIndentedLine("GlobalParameterNode is null", indent + indentIncrement));
			} else {
				text += formatLine(printAbstractNode(globalParameterNode, indent + indentIncrement));
			}
		}
		
		return text;
	}	

	private static String printChoiceNode(ChoiceNode choiceNode, String fieldName, int indent) {
		
		String text = formatLine(printObjectLine(choiceNode, fieldName, indent));
		text += formatLine(printObjectLine(choiceNode.getParameter(), "Parameter", indent + indentIncrement));
		
		return text;
	}

	private static String printAbstractStatement(AbstractStatement abstractStatement, String fieldName, int indent) {
		return formatLine(printIndentedLine(
				getIsFieldStr(fieldName) + 
				abstractStatement.getClass().getSimpleName() +
				getFieldStr(fieldName) +
				", #" + abstractStatement.hashCode() +
				"  (" + abstractStatement.toString() + ")", 
				indent));
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
