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

	public static String printModel(String message, IAbstractNode someNodeOfModel) {

		String text = "";

		IAbstractNode topNode = AbstractNodeHelper.findTopNode(someNodeOfModel);

		int indent = 0;

		if (topNode == null) {
			return printIndentedLine("Root not found.", indent);
		}

		text += printIndentedLine("Model BEG ------------------------------------------------------------------------------------------------------------------", indent);
		text += printIndentedLine("Message: " + message, indent);
		text += printChildren(topNode, indent);
		text += printIndentedLine("Model END ------------------------------------------------------------------------------------------------------------------", indent);

		return text;
	}

	private static String printIndentedLine(String line, int indent) {

		String indentStr = new String(new char[indent]).replace("\0", " ");
		return (indentStr + line + "\n");
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

	private static String printFieldLine(String line, int indent) {

		return printIndentedLine("F:" + line, indent);
	}	

	private static String printObjectLine(IAbstractNode abstractNode, String fieldName, int indent) {

		String line = 
				abstractNode.getName()+
				"  (" + abstractNode.getClass().getSimpleName() + ")" +
				"   #" + abstractNode.hashCode();

		//		String line = 
		//				getIsFieldStr(fieldName) + 
		//				abstractNode.getClass().getSimpleName() +
		//				getFieldStr(fieldName) +
		//				", " + abstractNode.getName()+ 
		//				", #" + abstractNode.hashCode();

		return printIndentedLine(line, indent);
	}

	private static String printAbstractNode(IAbstractNode abstractNode, int indent) {

		if (abstractNode == null) {
			return printIndentedLine("Abstract node is null", indent);
		}

		if (abstractNode instanceof TestCaseNode) {
			return printTestCaseNode((TestCaseNode)abstractNode, null, indent);
		}

		if (abstractNode instanceof ConstraintNode) {
			return printConstraintNode((ConstraintNode)abstractNode, null, indent);
		}

		if (abstractNode instanceof MethodNode) {
			return printParametersParentNode((MethodNode)abstractNode, null, indent);
		}

		if (abstractNode instanceof BasicParameterNode) {
			return printParameterNode((BasicParameterNode)abstractNode, null, indent);
		}

		if (abstractNode instanceof CompositeParameterNode) {
			return printParameterNode((CompositeParameterNode)abstractNode, null, indent);
		}
		
		if (abstractNode instanceof ChoiceNode) {
			return printChoiceNode((ChoiceNode)abstractNode, null, indent);
		}

		return printObjectLine(abstractNode, null, indent);
	}

	private static String printTestCaseNode(TestCaseNode testCaseNode, String fieldName, int indent) {

		String text = printObjectLine(testCaseNode, fieldName, indent);

		List<ChoiceNode> choices = testCaseNode.getTestData();

		for (ChoiceNode choice : choices) {
			text += printAbstractNode(choice, indent + indentIncrement);
		}

		return text;
	}

	private static String printConstraintNode(ConstraintNode constraintNode, String fieldName, int indent) {

		if (constraintNode == null) {
			return printIndentedLine("ConstraintNode is null", indent);
		}	

		String text = printObjectLine(constraintNode, fieldName, indent);

		IAbstractNode parent = constraintNode.getParent();
		text += printParametersParentNode((IParametersParentNode)parent, "parent", indent + indentIncrement);

		AbstractStatement precondition = constraintNode.getConstraint().getPrecondition();
		text += printAbstractStatement(precondition, "Precondition", indent + indentIncrement);

		AbstractStatement postcondition = constraintNode.getConstraint().getPostcondition();
		text += printAbstractStatement(postcondition, "Postcondition", indent + indentIncrement);

		return text;
	}

	private static String printParametersParentNode(IParametersParentNode parametersParentNode, String fieldName, int indent) {

		if (parametersParentNode == null) {
			return printIndentedLine("MethodNode is null", indent);
		}

		return printObjectLine(parametersParentNode, fieldName, indent);
	}

	private static String printParameterNode(
			AbstractParameterNode abstractParameterNode, String fieldName, int indent) {
		
		if (abstractParameterNode == null) {
			return printIndentedLine("MethodNode is null", indent);
		}

		String text = printObjectLine(abstractParameterNode, fieldName, indent);

		if (abstractParameterNode instanceof BasicParameterNode) {
			text += printFieldLine(((BasicParameterNode)abstractParameterNode).getType(), indent + indentIncrement);
		}

		if (abstractParameterNode.isLinked()) {
			
			AbstractParameterNode globalParameterNode = abstractParameterNode.getLinkToGlobalParameter();
			
			if (globalParameterNode == null) {
				text += printIndentedLine("Link is null", indent + indentIncrement);
			} else {
				text += printIndentedLine("Link:" + globalParameterNode.getName(), indent + indentIncrement);
			}
		}

		return text;
	}

	private static String printChoiceNode(ChoiceNode choiceNode, String fieldName, int indent) {

		String text = printObjectLine(choiceNode, fieldName, indent);
		text += printObjectLine(choiceNode.getParameter(), "Parameter", indent + indentIncrement);

		return text;
	}

	private static String printAbstractStatement(AbstractStatement abstractStatement, String fieldName, int indent) {
		return printIndentedLine(
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
