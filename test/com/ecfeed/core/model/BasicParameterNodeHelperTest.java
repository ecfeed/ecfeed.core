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

import org.junit.Test;

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import com.ecfeed.core.utils.IParameterConversionItemPart;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.ParameterConversionDefinition;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.ParameterConversionItemPartForValue;

public class BasicParameterNodeHelperTest {

	String tBoolean = JavaLanguageHelper.TYPE_NAME_BOOLEAN;
	String tByte = JavaLanguageHelper.TYPE_NAME_BYTE;
	String tInt = JavaLanguageHelper.TYPE_NAME_INT;
	String tFloat = JavaLanguageHelper.TYPE_NAME_FLOAT;
	String tDouble = JavaLanguageHelper.TYPE_NAME_DOUBLE;
	String tString = JavaLanguageHelper.TYPE_NAME_STRING;

	private enum WhatToTest {
		CONSTRAINTS,
		CHOICES
	}

	private enum IsChoiceRandomized {
		FALSE,
		TRUE
	}

	private enum SuccessExpected {
		FALSE,
		TRUE
	}

	@Test
	public void getQualifiedNameTest() {

		RootNode rootNode = new RootNode("root", null);

		BasicParameterNode globalParameterNode = new BasicParameterNode("global_1", "String", "0", false, null);
		globalParameterNode.setParent(rootNode);

		String qualifiedName = AbstractParameterSignatureHelper.createPathToTopContainerNewStandard(
				globalParameterNode, new ExtLanguageManagerForJava());

		assertEquals("global_1", qualifiedName);

		qualifiedName = AbstractParameterSignatureHelper.createPathToTopContainerNewStandard(
				globalParameterNode, new ExtLanguageManagerForSimple());

		assertEquals("global 1", qualifiedName);


		String type = 
				AbstractParameterSignatureHelper.createSignatureOfParameterTypeNewStandard(
						globalParameterNode, new ExtLanguageManagerForJava());

		assertEquals("String", type);

		qualifiedName = 
				AbstractParameterSignatureHelper.createSignatureOfParameterTypeNewStandard(
						globalParameterNode, new ExtLanguageManagerForSimple());

		assertEquals("Text", qualifiedName);
	}

	@Test
	public void convertParameterWithChoicesWithCheckIfPossible() {

		RootNode rootNode = new RootNode("Root", null);

		// add global parameter of root and choice node

		final String stringParameterType = "String";

		// add class node

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// add method node

		MethodNode methodNode = ClassNodeHelper.addNewMethod(classNode, "Method", true, null);

		// add parameter and choice to method

		BasicParameterNode methodParameterNode = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(methodNode, "MP1", stringParameterType);

		ChoiceNode choiceNodeOfMethod = 
				MethodParameterNodeHelper.addNewChoice(methodParameterNode, "MC1", "");

		ParameterConversionDefinition parameterConversionDefinition = new ParameterConversionDefinition();

		BasicParameterConversionTester checker = 
				new BasicParameterConversionTester(
						methodParameterNode, 
						choiceNodeOfMethod,
						parameterConversionDefinition);

		performTypeConversion(WhatToTest.CHOICES, checker);
	}

	@Test
	public void convertParameterWithConstraintsWithCheckIfPossible() {

		RootNode rootNode = new RootNode("Root", null);

		// add global parameter of root and choice node

		final String stringParameterType = "String";

		// add class node

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// add method node

		MethodNode methodNode = ClassNodeHelper.addNewMethod(classNode, "Method", true, null);

		// add parameter

		BasicParameterNode methodParameterNode = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(methodNode, "MP1", stringParameterType);

		ParameterConversionDefinition parameterConversionDefinition = new ParameterConversionDefinition();

		BasicParameterConversionTester valueOperator = 
				new BasicParameterConversionTester(
						methodParameterNode, 
						null,
						parameterConversionDefinition);

		performTypeConversion(WhatToTest.CONSTRAINTS, valueOperator);
	}

	private void performTypeConversion(WhatToTest whatToTest, BasicParameterConversionTester operator) {

		ParameterConversionDefinition resultConversionDefinition = operator.getParameterConversionDefinition();

		operator.convertParameterAndCheckResults(whatToTest, IsChoiceRandomized.FALSE, tString, tString, "ABC", SuccessExpected.TRUE, "ABC");
		assertEquals(0, resultConversionDefinition.getItemCount());

		operator.convertParameterAndCheckResults(whatToTest, IsChoiceRandomized.FALSE, tString, tInt, "ABC", SuccessExpected.FALSE, "123");
		assertEquals(1, resultConversionDefinition.getItemCount());
		ParameterConversionItem parameterConversionItem = resultConversionDefinition.getCopyOfItem(0);
		IParameterConversionItemPart srcPart = parameterConversionItem.getSrcPart();
		String description = srcPart.getDescription();
		assertEquals("ABC[value]", description);
	}

	private static class BasicParameterConversionTester {

		private BasicParameterNode fMethodParameterNode;
		private ChoiceNode fChoiceNodeOfMethod;
		private ParameterConversionDefinition fParameterConversionDefinition;

		public BasicParameterConversionTester(
				BasicParameterNode methodParameterNode, 
				ChoiceNode choiceNodeOfMethod,
				ParameterConversionDefinition parameterConversionDefinition) {

			fMethodParameterNode = methodParameterNode;
			fChoiceNodeOfMethod = choiceNodeOfMethod;
			fParameterConversionDefinition = parameterConversionDefinition;
		}

		public void convertParameterAndCheckResults(
				WhatToTest whatToTest, 
				IsChoiceRandomized isRandomized, 
				String oldType, 
				String newType, 
				String value, 
				SuccessExpected successExpected,
				String newValue) {

			if (isRandomized == IsChoiceRandomized.TRUE && whatToTest == WhatToTest.CONSTRAINTS) {
				return; // randomized for choices only
			}

			fParameterConversionDefinition.clear();

			fMethodParameterNode.setType(oldType);

			if (whatToTest == WhatToTest.CHOICES) {
				fChoiceNodeOfMethod.setValueString(value);

				if (isRandomized == IsChoiceRandomized.TRUE) {
					fChoiceNodeOfMethod.setRandomizedValue(true);
				} else {
					fChoiceNodeOfMethod.setRandomizedValue(false);
				}

			} else {
				MethodNode methodNode = (MethodNode) fMethodParameterNode.getParent();

				methodNode.removeAllConstraints();

				addSimpleValueConstraintToMethod(
						methodNode,
						"C1",
						fMethodParameterNode,
						value,
						value);
			}

			BasicParameterNodeHelper.verifyConversionOfParameterToType( // XYX this is the tested function
					newType, fMethodParameterNode, fParameterConversionDefinition);

			if (successExpected == SuccessExpected.TRUE) {
				assertFalse(fParameterConversionDefinition.hasItems());
			} else {
				assertTrue(fParameterConversionDefinition.hasItems());
			}

			convertParameter(newType, newValue);

			if (whatToTest == WhatToTest.CHOICES) {
				checkValueOfChoice(fChoiceNodeOfMethod, newValue);
			} else {
				checkValueFromConstraint((MethodNode) fMethodParameterNode.getParent(), newValue);
			}
		}

		private void checkValueFromConstraint(MethodNode methodNode, String newValue) {

			ConstraintNode constraintNode = methodNode.getConstraintNodes().get(0);

			AbstractStatement precondition = constraintNode.getConstraint().getPrecondition();

			RelationStatement relationStatement = (RelationStatement)precondition; 

			IStatementCondition statementCondition = relationStatement.getCondition();

			ValueCondition choiceCondition = (ValueCondition)statementCondition;

			String currentValue = choiceCondition.getRightValue();

			assertEquals(newValue, currentValue);
		}

		private void checkValueOfChoice(ChoiceNode choiceNode, String expectedValue) {

			String currentValue = choiceNode.getValueString();
			assertEquals(expectedValue, currentValue);
		}

		private void convertParameter(String newType, String newValue) {

			if (fParameterConversionDefinition.getItemCount() == 0) {
				return;
			}

			ParameterConversionItem parameterConversionItem = fParameterConversionDefinition.getCopyOfItem(0);

			IParameterConversionItemPart srcPart = parameterConversionItem.getSrcPart();
			ParameterConversionItemPartForValue dstPart = 
					new ParameterConversionItemPartForValue(srcPart.getParameter(), srcPart.getLinkingContext(), newValue);

			ParameterConversionItem newParameterConversionItem = 
					new ParameterConversionItem(srcPart, dstPart, (String)null);

			fParameterConversionDefinition.setItem(0, newParameterConversionItem);

			BasicParameterNodeHelper.convertChoicesAndConstraintsToType( // XYX this is tested function
					fMethodParameterNode, fParameterConversionDefinition);
		}

		public ParameterConversionDefinition getParameterConversionDefinition() {
			return fParameterConversionDefinition;
		}
	}

	private static void addSimpleValueConstraintToMethod(
			MethodNode methodNode,
			String constraintName,
			BasicParameterNode methodParameterNode,
			String value1,
			String value2) {

		RelationStatement relationStatement1 = 
				RelationStatement.createRelationStatementWithValueCondition(
						methodParameterNode, null, EMathRelation.EQUAL, value1);

		RelationStatement relationStatement2 = 
				RelationStatement.createRelationStatementWithValueCondition(
						methodParameterNode, null, EMathRelation.LESS_THAN, value2);

		Constraint constraint = new Constraint(
				constraintName, 
				ConstraintType.EXTENDED_FILTER, 
				relationStatement1, 
				relationStatement2, 
				null);

		ConstraintNode constraintNode = new ConstraintNode(constraintName, constraint, null);

		methodNode.addConstraint(constraintNode);
	}

}
