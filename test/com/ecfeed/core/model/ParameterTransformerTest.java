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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.IParameterConversionItemPart;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.ParameterConversionDefinition;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.ParameterConversionItemPartForChoice;
import com.ecfeed.core.utils.ParameterConversionItemPartForLabel;
 import com.ecfeed.core.utils.ParameterConversionItemPartForValue;

public class ParameterTransformerTest {

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
	public void linkMethodParameterToClassParameterBasicUseCaseForChoices() {

		RootNode rootNode = new RootNode("Root", null);

		// names of global parameters	
		// the same name for root global parameter and class global parameter

		final String globalParameterName = "GP1";
		final String globalChoiceName1 = "GC1";

		// add global parameter and choice for root

		GlobalParameterNode globalParameterNodeOfRoot = 
				RootNodeHelper.addGlobalParameterToRoot(rootNode, globalParameterName, "String", null);

		GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
				globalParameterNodeOfRoot, globalChoiceName1, "0", null);

		// add class node

		ClassNode classNode = RootNodeHelper.addClassNodeToRoot(rootNode, "Class1", null);

		// add global parameter and choice for class

		GlobalParameterNode globalParameterNodeOfClass = 
				ClassNodeHelper.addGlobalParameterToClass(classNode, globalParameterName, "String", null);

		ChoiceNode globalChoiceNodeForClass = 
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfClass, globalChoiceName1, "0", null);

		// add methodNode 

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "method", null);

		// add parameter and choice to method

		final String methodParameterName = "P1";
		final String methodChoiceName1 = "C1";

		MethodParameterNode methodParameterNode = 
				MethodNodeHelper.addParameterToMethod(methodNode, methodParameterName, "String");

		ChoiceNode methodChoiceNode1 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode, methodChoiceName1, "0");

		// add constraint

		addSimpleChoiceConstraintToMethod(methodNode, "c1", methodParameterNode, methodChoiceNode1, methodChoiceNode1);

		// creating choice conversion list

		ParameterConversionDefinition parameterConversionDefinition = new ParameterConversionDefinition();

		ParameterConversionItemPartForChoice srcPart = new ParameterConversionItemPartForChoice(methodChoiceNode1);
		ParameterConversionItemPartForChoice dstPart = new ParameterConversionItemPartForChoice(globalChoiceNodeForClass);

		ParameterConversionItem parameterConversionItemForChoice = 
				new ParameterConversionItem(srcPart, dstPart, (String)null);

		parameterConversionDefinition.addItemWithMergingDescriptions(parameterConversionItemForChoice);

		// linking

		ListOfModelOperations reverseOperations = new ListOfModelOperations();
		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		ParameterTransformer.linkMethodParameteToGlobalParameter(
				methodParameterNode, 
				globalParameterNodeOfClass, 
				parameterConversionDefinition, 
				reverseOperations, 
				extLanguageManager);

		// check global parameter of class

		assertEquals(1, classNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfClass.getChoiceCount());
		ChoiceNode choiceNodeFromGlobalParam = globalParameterNodeOfClass.getChoice(globalChoiceName1);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(1, methodParameterNode.getChoiceCount()); // sees choices from global parameter because linked

		MethodParameterNode methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(true, methodParameterNode2.isLinked());
		assertEquals(globalParameterNodeOfClass, methodParameterNode2.getLinkToGlobalParameter());

		// check choices from constraints

		ChoiceNode choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromPrecondition);

		ChoiceNode choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromPostcondition);

		// reverse operation

		reverseOperations.executeFromTail();

		// check global parameter

		assertEquals(1, classNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfClass.getChoiceCount());
		choiceNodeFromGlobalParam = globalParameterNodeOfClass.getChoice(globalChoiceName1);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(1, methodParameterNode.getChoiceCount());

		methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(false, methodParameterNode2.isLinked());
		assertNull(methodParameterNode2.getLinkToGlobalParameter());

		ChoiceNode choiceNodeFromMethodParam = methodParameterNode.getChoice(methodChoiceName1);
		assertEquals(methodChoiceNode1, choiceNodeFromMethodParam);

		// check choices from constraints

		choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
		assertEquals(methodChoiceNode1, choiceNodeFromPrecondition);

		choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
		assertEquals(methodChoiceNode1, choiceNodeFromPostcondition);
	}

	@Test
	public void linkLabelToLabel() {

		RootNode rootNode = new RootNode("Root", null);

		// names of global parameters	
		// the same name for root global parameter and class global parameter

		final String globalParameterName = "GP1";
		final String globalChoiceName1 = "GC1";
		final String globalLabel1 = "GL1";

		// add global parameter and choice for root

		GlobalParameterNode globalParameterNodeOfRoot = 
				RootNodeHelper.addGlobalParameterToRoot(rootNode, globalParameterName, "String", null);

		GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
				globalParameterNodeOfRoot, globalChoiceName1, "0", null);

		// add class node

		ClassNode classNode = RootNodeHelper.addClassNodeToRoot(rootNode, "Class1", null);

		// add global parameter and choice for class

		GlobalParameterNode globalParameterNodeOfClass = 
				ClassNodeHelper.addGlobalParameterToClass(classNode, globalParameterName, "String", null);

		ChoiceNode globalChoiceNodeForClass = 
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfClass, globalChoiceName1, "0", null);

		globalChoiceNodeForClass.addLabel(globalLabel1);

		// add methodNode 

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "method", null);

		// add parameter and choice to method

		final String methodParameterName = "P1";
		final String methodChoiceName1 = "C1";
		String methodLabel1 = "L1";

		MethodParameterNode methodParameterNode = 
				MethodNodeHelper.addParameterToMethod(methodNode, methodParameterName, "String");

		ChoiceNode methodChoiceNode1 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode, methodChoiceName1, "0");

		methodChoiceNode1.addLabel(methodLabel1);

		// add constraint

		addSimpleLabelConstraintToMethod(methodNode, "c1", methodParameterNode, methodLabel1, methodLabel1);

		// creating choice conversion list

		ParameterConversionDefinition parameterConversionDefinition = new ParameterConversionDefinition();

		ParameterConversionItem parameterConversionItemForChoice = 
				new ParameterConversionItem(
						new ParameterConversionItemPartForLabel(methodLabel1), 
						new ParameterConversionItemPartForLabel(globalLabel1), 
						(String)null);

		parameterConversionDefinition.addItemWithMergingDescriptions(parameterConversionItemForChoice);

		// linking

		ListOfModelOperations reverseOperations = new ListOfModelOperations();
		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		ParameterTransformer.linkMethodParameteToGlobalParameter(
				methodParameterNode, 
				globalParameterNodeOfClass, 
				parameterConversionDefinition, 
				reverseOperations, 
				extLanguageManager);

		// check global parameter of class

		assertEquals(1, classNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfClass.getChoiceCount());
		ChoiceNode choiceNodeFromGlobalParam = globalParameterNodeOfClass.getChoice(globalChoiceName1);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(1, methodParameterNode.getChoiceCount()); // sees choices from global parameter because linked

		MethodParameterNode methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(true, methodParameterNode2.isLinked());
		assertEquals(globalParameterNodeOfClass, methodParameterNode2.getLinkToGlobalParameter());

		// check choices from constraints

		String labelFromPrecondition = getLabelFromConstraintPrecondition(methodNode, 0);
		assertEquals(globalLabel1, labelFromPrecondition);

		String labelFromPostcondition = getLabelFromConstraintPostcondition(methodNode, 0);
		assertEquals(globalLabel1, labelFromPostcondition);

		// reverse operation

		reverseOperations.executeFromTail();

		// check global parameter

		assertEquals(1, classNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfClass.getChoiceCount());
		choiceNodeFromGlobalParam = globalParameterNodeOfClass.getChoice(globalChoiceName1);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(1, methodParameterNode.getChoiceCount());

		methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(false, methodParameterNode2.isLinked());
		assertNull(methodParameterNode2.getLinkToGlobalParameter());

		ChoiceNode choiceNodeFromMethodParam = methodParameterNode.getChoice(methodChoiceName1);
		assertEquals(methodChoiceNode1, choiceNodeFromMethodParam);

		// check choices from constraints

		labelFromPrecondition = getLabelFromConstraintPrecondition(methodNode, 0);
		assertEquals(methodLabel1, labelFromPrecondition);

		labelFromPostcondition = getLabelFromConstraintPostcondition(methodNode, 0);
		assertEquals(methodLabel1, labelFromPostcondition);
	}

	@Test
	public void linkLabelToChoice() {

		RootNode rootNode = new RootNode("Root", null);

		// names of global parameters	
		// the same name for root global parameter and class global parameter

		final String globalParameterName = "GP1";
		final String globalChoiceName1 = "GC1";

		// add global parameter and choice for root

		GlobalParameterNode globalParameterNodeOfRoot = 
				RootNodeHelper.addGlobalParameterToRoot(rootNode, globalParameterName, "String", null);

		GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
				globalParameterNodeOfRoot, globalChoiceName1, "0", null);

		// add class node

		ClassNode classNode = RootNodeHelper.addClassNodeToRoot(rootNode, "Class1", null);

		// add global parameter and choice for class

		GlobalParameterNode globalParameterNodeOfClass = 
				ClassNodeHelper.addGlobalParameterToClass(classNode, globalParameterName, "String", null);

		ChoiceNode globalChoiceNodeForClass = 
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfClass, globalChoiceName1, "0", null);

		// add methodNode 

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "method", null);

		// add parameter and choice to method

		final String methodParameterName = "P1";
		final String methodChoiceName1 = "C1";
		String methodLabel1 = "L1";

		MethodParameterNode methodParameterNode = 
				MethodNodeHelper.addParameterToMethod(methodNode, methodParameterName, "String");

		ChoiceNode methodChoiceNode1 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode, methodChoiceName1, "0");

		methodChoiceNode1.addLabel(methodLabel1);

		// add constraint

		addSimpleLabelConstraintToMethod(methodNode, "c1", methodParameterNode, methodLabel1, methodLabel1);

		// creating choice conversion list

		ParameterConversionDefinition parameterConversionDefinition = new ParameterConversionDefinition();

		ParameterConversionItem parameterConversionItemForChoice = 
				new ParameterConversionItem(
						new ParameterConversionItemPartForLabel(methodLabel1), 
						new ParameterConversionItemPartForChoice(globalChoiceNodeForClass), 
						(String)null);

		parameterConversionDefinition.addItemWithMergingDescriptions(parameterConversionItemForChoice);

		// linking

		ListOfModelOperations reverseOperations = new ListOfModelOperations();
		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		ParameterTransformer.linkMethodParameteToGlobalParameter(
				methodParameterNode, 
				globalParameterNodeOfClass, 
				parameterConversionDefinition, 
				reverseOperations, 
				extLanguageManager);

		// check global parameter of class

		assertEquals(1, classNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfClass.getChoiceCount());
		ChoiceNode choiceNodeFromGlobalParam = globalParameterNodeOfClass.getChoice(globalChoiceName1);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(1, methodParameterNode.getChoiceCount()); // sees choices from global parameter because linked

		MethodParameterNode methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(true, methodParameterNode2.isLinked());
		assertEquals(globalParameterNodeOfClass, methodParameterNode2.getLinkToGlobalParameter());

		// check choices from constraints

		ChoiceNode choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromPrecondition);

		ChoiceNode choiceFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
		assertEquals(globalChoiceNodeForClass, choiceFromPostcondition);

		// reverse operation

		reverseOperations.executeFromTail();

		// check global parameter

		assertEquals(1, classNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfClass.getChoiceCount());
		choiceNodeFromGlobalParam = globalParameterNodeOfClass.getChoice(globalChoiceName1);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(1, methodParameterNode.getChoiceCount());

		methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(false, methodParameterNode2.isLinked());
		assertNull(methodParameterNode2.getLinkToGlobalParameter());

		ChoiceNode choiceNodeFromMethodParam = methodParameterNode.getChoice(methodChoiceName1);
		assertEquals(methodChoiceNode1, choiceNodeFromMethodParam);

		// check choices from constraints

		String labelFromPrecondition = getLabelFromConstraintPrecondition(methodNode, 0);
		assertEquals(methodLabel1, labelFromPrecondition);

		String labelFromPostcondition = getLabelFromConstraintPostcondition(methodNode, 0);
		assertEquals(methodLabel1, labelFromPostcondition);
	}

	@Test
	public void linkChoiceToLabel() {

		RootNode rootNode = new RootNode("Root", null);

		// names of global parameters	
		// the same name for root global parameter and class global parameter

		final String globalParameterName = "GP1";
		final String globalChoiceName1 = "GC1";
		final String globalLabel1 = "GL1";		

		// add global parameter and choice for root

		GlobalParameterNode globalParameterNodeOfRoot = 
				RootNodeHelper.addGlobalParameterToRoot(rootNode, globalParameterName, "String", null);

		ChoiceNode globalChoiceNode = 
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfRoot, globalChoiceName1, "0", null);

		globalChoiceNode.addLabel(globalLabel1);

		// add class node

		ClassNode classNode = RootNodeHelper.addClassNodeToRoot(rootNode, "Class1", null);

		// add global parameter and choice for class

		GlobalParameterNode globalParameterNodeOfClass = 
				ClassNodeHelper.addGlobalParameterToClass(classNode, globalParameterName, "String", null);

		ChoiceNode globalChoiceNodeForClass = 
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfClass, globalChoiceName1, "0", null);

		// add methodNode 

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "method", null);

		// add parameter and choice to method

		final String methodParameterName = "P1";
		final String methodChoiceName1 = "C1";

		MethodParameterNode methodParameterNode = 
				MethodNodeHelper.addParameterToMethod(methodNode, methodParameterName, "String");

		ChoiceNode methodChoiceNode1 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode, methodChoiceName1, "0");

		// add constraint

		addSimpleChoiceConstraintToMethod(
				methodNode, "c1", methodParameterNode, methodChoiceNode1, methodChoiceNode1);

		// creating choice conversion list

		ParameterConversionDefinition parameterConversionDefinition = new ParameterConversionDefinition();

		ParameterConversionItem parameterConversionItemForChoice =
				new ParameterConversionItem(
						new ParameterConversionItemPartForChoice(methodChoiceNode1),
						new ParameterConversionItemPartForLabel(globalLabel1),
						(String)null);

		parameterConversionDefinition.addItemWithMergingDescriptions(parameterConversionItemForChoice);

		// linking

		ListOfModelOperations reverseOperations = new ListOfModelOperations();
		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		ParameterTransformer.linkMethodParameteToGlobalParameter(
				methodParameterNode, 
				globalParameterNodeOfClass, 
				parameterConversionDefinition, 
				reverseOperations, 
				extLanguageManager);

		// check global parameter of class

		assertEquals(1, classNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfClass.getChoiceCount());
		ChoiceNode choiceNodeFromGlobalParam = globalParameterNodeOfClass.getChoice(globalChoiceName1);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(1, methodParameterNode.getChoiceCount()); // sees choices from global parameter because linked

		MethodParameterNode methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(true, methodParameterNode2.isLinked());
		assertEquals(globalParameterNodeOfClass, methodParameterNode2.getLinkToGlobalParameter());

		// check choices from constraints

		String labelFromPrecondition = getLabelFromConstraintPrecondition(methodNode, 0);
		assertEquals(globalLabel1, labelFromPrecondition);

		String labelFromPostcondition = getLabelFromConstraintPostcondition(methodNode, 0);
		assertEquals(globalLabel1, labelFromPostcondition);

		// reverse operation

		reverseOperations.executeFromTail();

		// check global parameter

		assertEquals(1, classNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfClass.getChoiceCount());
		choiceNodeFromGlobalParam = globalParameterNodeOfClass.getChoice(globalChoiceName1);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(1, methodParameterNode.getChoiceCount());

		methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(false, methodParameterNode2.isLinked());
		assertNull(methodParameterNode2.getLinkToGlobalParameter());

		ChoiceNode choiceNodeFromMethodParam = methodParameterNode.getChoice(methodChoiceName1);
		assertEquals(methodChoiceNode1, choiceNodeFromMethodParam);

		// check choices from constraints

		ChoiceNode choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
		assertEquals(methodChoiceNode1, choiceNodeFromPrecondition);

		ChoiceNode choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
		assertEquals(methodChoiceNode1, choiceNodeFromPostcondition);
	}

	@Test
	public void linkMethodParameterToRootParameterBasicUseCase() {

		RootNode rootNode = new RootNode("Root", null);

		// names of global parameters
		// the same name for root global parameter and class global parameter

		final String globalParameterName = "GP1";
		final String globalChoiceName1 = "GC1";

		// add global parameter and choice for root

		GlobalParameterNode globalParameterNodeOfRoot = 
				RootNodeHelper.addGlobalParameterToRoot(rootNode, globalParameterName, "String", null);

		ChoiceNode globalChoiceNodeOfRoot = 
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfRoot, globalChoiceName1, "0", null);

		// add class node

		ClassNode classNode = RootNodeHelper.addClassNodeToRoot(rootNode, "Class1", null);

		// add global parameter and choice for class

		GlobalParameterNode globalParameterNodeOfClass = 
				ClassNodeHelper.addGlobalParameterToClass(classNode, globalParameterName, "String", null);

		GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
				globalParameterNodeOfClass, globalChoiceName1, "0", null);

		// add methodNode 

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "method", null);

		// add parameter and choice to method

		final String methodParameterName = "P1";
		final String methodChoiceName1 = "C1";

		MethodParameterNode methodParameterNode = 
				MethodNodeHelper.addParameterToMethod(methodNode, methodParameterName, "String");

		ChoiceNode methodChoiceNode1 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode, methodChoiceName1, "0");

		// add constraint

		addSimpleChoiceConstraintToMethod(methodNode, "c1", methodParameterNode, methodChoiceNode1, methodChoiceNode1);

		// creating choice conversion list

		ParameterConversionDefinition parameterConversionDefinition = new ParameterConversionDefinition();

		ParameterConversionItemPartForChoice srcPart = new ParameterConversionItemPartForChoice(methodChoiceNode1);
		ParameterConversionItemPartForChoice dstPart = new ParameterConversionItemPartForChoice(globalChoiceNodeOfRoot);

		ParameterConversionItem parameterConversionItemForChoice = 
				new ParameterConversionItem(srcPart, dstPart, (String)null);

		parameterConversionDefinition.addItemWithMergingDescriptions(parameterConversionItemForChoice);

		// linking

		ListOfModelOperations reverseOperations = new ListOfModelOperations();
		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		ParameterTransformer.linkMethodParameteToGlobalParameter(
				methodParameterNode, 
				globalParameterNodeOfRoot, 
				parameterConversionDefinition, 
				reverseOperations, 
				extLanguageManager);

		// check global parameter of root

		assertEquals(1, rootNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfRoot.getChoiceCount());
		ChoiceNode choiceNodeFromGlobalParam = globalParameterNodeOfRoot.getChoice(globalChoiceName1);
		assertEquals(globalChoiceNodeOfRoot, choiceNodeFromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(1, methodParameterNode.getChoiceCount()); // sees choices from global parameter because linked

		MethodParameterNode methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(true, methodParameterNode2.isLinked());
		assertEquals(globalParameterNodeOfRoot, methodParameterNode2.getLinkToGlobalParameter());

		// check choices from constraints

		ChoiceNode choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
		assertEquals(globalChoiceNodeOfRoot, choiceNodeFromPrecondition);

		ChoiceNode choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
		assertEquals(globalChoiceNodeOfRoot, choiceNodeFromPostcondition);

		// reverse operation

		reverseOperations.executeFromTail();

		// check global parameter

		assertEquals(1, rootNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfRoot.getChoiceCount());
		choiceNodeFromGlobalParam = globalParameterNodeOfRoot.getChoice(globalChoiceName1);
		assertEquals(globalChoiceNodeOfRoot, choiceNodeFromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(1, methodParameterNode.getChoiceCount());

		methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(false, methodParameterNode2.isLinked());
		assertNull(methodParameterNode2.getLinkToGlobalParameter());

		ChoiceNode choiceNodeFromMethodParam = methodParameterNode.getChoice(methodChoiceName1);
		assertEquals(methodChoiceNode1, choiceNodeFromMethodParam);

		// check choices from constraints

		choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
		assertEquals(methodChoiceNode1, choiceNodeFromPrecondition);

		choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
		assertEquals(methodChoiceNode1, choiceNodeFromPostcondition);
	}

	@Test
	public void linkMethodParameterToClassParameterWithMergingChoices() {

		RootNode rootNode = new RootNode("Root", null);

		// names of global parameters
		// the same name for root global parameter and class global parameter

		final String globalParameterName = "GP1";
		final String globalChoiceName1 = "GC1";

		// add global parameter and choice for root

		GlobalParameterNode globalParameterNodeOfRoot = 
				RootNodeHelper.addGlobalParameterToRoot(rootNode, globalParameterName, "String", null);

		GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
				globalParameterNodeOfRoot, globalChoiceName1, "0", null);

		// add class node

		ClassNode classNode = RootNodeHelper.addClassNodeToRoot(rootNode, "Class1", null);

		// add global parameter and choice for class

		GlobalParameterNode globalParameterNodeOfClass = 
				ClassNodeHelper.addGlobalParameterToClass(classNode, globalParameterName, "String", null);

		ChoiceNode globalChoiceNodeForClass = 
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfClass, globalChoiceName1, "0", null);

		// add methodNode 

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "method", null);

		// add parameter and choice to method

		final String methodParameterName = "P1";
		final String methodChoiceName1 = "C1";
		final String methodChoiceName2 = "C2";

		MethodParameterNode methodParameterNode = 
				MethodNodeHelper.addParameterToMethod(methodNode, methodParameterName, "String");

		ChoiceNode methodChoiceNode1 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode, methodChoiceName1, "0");

		ChoiceNode methodChoiceNode2 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode, methodChoiceName2, "0");

		// add constraint

		addSimpleChoiceConstraintToMethod(methodNode, "c1", methodParameterNode, methodChoiceNode1, methodChoiceNode2);

		// creating choice conversion list - to method choices to one global choice

		ParameterConversionDefinition parameterConversionDefinition = new ParameterConversionDefinition();

		ParameterConversionItem parameterConversionItemForChoice1 = 
				new ParameterConversionItem(
						new ParameterConversionItemPartForChoice(methodChoiceNode1), 
						new ParameterConversionItemPartForChoice(globalChoiceNodeForClass), 
						(String)null);

		parameterConversionDefinition.addItemWithMergingDescriptions(parameterConversionItemForChoice1);


		ParameterConversionItem parameterConversionItemForChoice2 = 
				new ParameterConversionItem(
						new ParameterConversionItemPartForChoice(methodChoiceNode2), 
						new ParameterConversionItemPartForChoice(globalChoiceNodeForClass), 
						(String)null);

		parameterConversionDefinition.addItemWithMergingDescriptions(parameterConversionItemForChoice2);

		// linking

		ListOfModelOperations reverseOperations = new ListOfModelOperations();
		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		ParameterTransformer.linkMethodParameteToGlobalParameter(
				methodParameterNode, 
				globalParameterNodeOfClass, 
				parameterConversionDefinition, 
				reverseOperations, 
				extLanguageManager);

		// check global parameter of class

		assertEquals(1, classNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfClass.getChoiceCount());
		ChoiceNode choiceNodeFromGlobalParam = globalParameterNodeOfClass.getChoice(globalChoiceName1);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(1, methodParameterNode.getChoiceCount()); // sees choices from global parameter because linked

		MethodParameterNode methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(true, methodParameterNode2.isLinked());
		assertEquals(globalParameterNodeOfClass, methodParameterNode2.getLinkToGlobalParameter());

		// check choices from constraints

		ChoiceNode choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromPrecondition);

		ChoiceNode choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromPostcondition);

		// reverse operation

		reverseOperations.executeFromTail();

		// check global parameter

		assertEquals(1, classNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfClass.getChoiceCount());
		choiceNodeFromGlobalParam = globalParameterNodeOfClass.getChoice(globalChoiceName1);
		assertEquals(globalChoiceNodeForClass, choiceNodeFromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(2, methodParameterNode.getChoiceCount());

		methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(false, methodParameterNode2.isLinked());
		assertNull(methodParameterNode2.getLinkToGlobalParameter());

		ChoiceNode choiceNodeFromMethodParam = methodParameterNode.getChoice(methodChoiceName1);
		assertEquals(methodChoiceNode1, choiceNodeFromMethodParam);

		// check choices from constraints

		choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
		assertEquals(methodChoiceNode1, choiceNodeFromPrecondition);

		choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
		assertEquals(methodChoiceNode2, choiceNodeFromPostcondition);
	}

	@Test
	public void filterChoicesNotUsedInConstraints() {

		RootNode rootNode = new RootNode("Root", null);

		// add class node

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// add global parameter of class and choice node

		final String parameterType = "String";
		final String choiceValueString = "1";

		GlobalParameterNode globalParameterNodeOfClass1 = 
				ClassNodeHelper.addGlobalParameterToClass(classNode, "CP1", parameterType, null);

		ChoiceNode globalChoiceNode11 = 
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfClass1, "CC11", choiceValueString, null);

		// add method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		// add parameter and choice to method

		MethodParameterNode methodParameterNode1 = 
				MethodNodeHelper.addParameterToMethod(methodNode, "MP1", parameterType);

		ChoiceNode choiceNodeOfMethod11 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode1, "MC11", choiceValueString);

		ChoiceNode choiceNodeOfMethod12 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode1, "MC12", choiceValueString);

		ChoiceNode choiceNodeOfMethod121 =	
				ChoiceNodeHelper.addChoiceToChoice(choiceNodeOfMethod12, "MC121", choiceValueString);

		ChoiceNode choiceNodeOfMethod122 =		
				ChoiceNodeHelper.addChoiceToChoice(choiceNodeOfMethod12, "MC122", choiceValueString);

		ChoiceNode choiceNodeOfMethod1221 =
				ChoiceNodeHelper.addChoiceToChoice(choiceNodeOfMethod122, "MC1221", choiceValueString);

		addSimpleChoiceConstraintToMethod(methodNode, "C1" , methodParameterNode1, choiceNodeOfMethod11, choiceNodeOfMethod11);


		// creating choice conversion list

		ParameterConversionDefinition parameterConversionDefinition = new ParameterConversionDefinition();

		ParameterConversionItem parameterConversionItemForChoice = 
				new ParameterConversionItem(
						new ParameterConversionItemPartForChoice(choiceNodeOfMethod11), 
						new ParameterConversionItemPartForChoice(globalChoiceNode11), 
						(String)null);

		parameterConversionDefinition.addItemWithMergingDescriptions(parameterConversionItemForChoice);

		// linking

		ListOfModelOperations reverseOperations = new ListOfModelOperations();
		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		ParameterTransformer.linkMethodParameteToGlobalParameter(
				methodParameterNode1, 
				globalParameterNodeOfClass1, 
				parameterConversionDefinition, 
				reverseOperations, 
				extLanguageManager);

		int globalParamChoiceCount = globalParameterNodeOfClass1.getChoiceCount();
		assertEquals(1, globalParamChoiceCount);

		ChoiceNode resultChoiceNode = globalParameterNodeOfClass1.getChoices().get(0);
		assertEquals(globalChoiceNode11.getName(), resultChoiceNode.getName());

		methodParameterNode1.setLinked(false); // temporary change to check if all choices were deleted
		List<ChoiceNode> methodParameterChoices = methodParameterNode1.getChoices();
		assertEquals(0, methodParameterChoices.size());
		methodParameterNode1.setLinked(true); 

		// reverting
		reverseOperations.executeFromTail();

		// checking choices for method parameter

		List<ChoiceNode> tmpChoices = methodParameterNode1.getChoices();
		assertEquals(2, methodParameterNode1.getChoiceCount());

		ChoiceNode tmpChoice11 = tmpChoices.get(0);
		assertEquals(choiceNodeOfMethod11, tmpChoice11);

		ChoiceNode tmpChoice12 = tmpChoices.get(1);
		assertEquals(choiceNodeOfMethod12, tmpChoice12);

		List<ChoiceNode> tmpChoices12 = tmpChoice12.getChoices();
		assertEquals(2, tmpChoices12.size());

		ChoiceNode tmpChoice121 = tmpChoices12.get(0);
		assertEquals(choiceNodeOfMethod121, tmpChoice121);

		ChoiceNode tmpChoice122 = tmpChoices12.get(1);
		assertEquals(choiceNodeOfMethod122, tmpChoice122);

		List<ChoiceNode> tmpChoices122 = tmpChoice122.getChoices();

		ChoiceNode tmpChoice1221 = tmpChoices122.get(0);
		assertEquals(choiceNodeOfMethod1221, tmpChoice1221);
	}

	@Test
	public void linkingWithAbstractChoices() {

		RootNode rootNode = new RootNode("Root", null);

		// add class node

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// add global parameter of class and choice node

		final String parameterType = "String";
		final String choiceValueString = "1";

		GlobalParameterNode globalParameterNodeOfClass1 = 
				ClassNodeHelper.addGlobalParameterToClass(classNode, "CP1", parameterType, null);

		ChoiceNode globalChoiceNode1 = 
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfClass1, "CC1", choiceValueString, null);

		ChoiceNode globalChoiceNode11 = 
				ChoiceNodeHelper.addChoiceToChoice(globalChoiceNode1, "CC11", choiceValueString);

		// add method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		// add parameter and choice to method

		MethodParameterNode methodParameterNode1 = 
				MethodNodeHelper.addParameterToMethod(methodNode, "MP1", parameterType);

		ChoiceNode choiceNodeOfMethod1 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode1, "MC1", choiceValueString);

		ChoiceNode choiceNodeOfMethod11 = 
				ChoiceNodeHelper.addChoiceToChoice(choiceNodeOfMethod1, "MC11", choiceValueString);

		addSimpleChoiceConstraintToMethod(
				methodNode, "constraint1", methodParameterNode1, choiceNodeOfMethod11, choiceNodeOfMethod11);

		ParameterConversionDefinition parameterConversionDefinition = new ParameterConversionDefinition();

		ParameterConversionItem parameterConversionItemForChoice = 
				new ParameterConversionItem(
						new ParameterConversionItemPartForChoice(choiceNodeOfMethod11), 
						new ParameterConversionItemPartForChoice(globalChoiceNode1), 
						(String)null);

		parameterConversionDefinition.addItemWithMergingDescriptions(parameterConversionItemForChoice);

		// linking

		ListOfModelOperations reverseOperations = new ListOfModelOperations();
		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		ParameterTransformer.linkMethodParameteToGlobalParameter(
				methodParameterNode1, 
				globalParameterNodeOfClass1, 
				parameterConversionDefinition, 
				reverseOperations, 
				extLanguageManager);


		// check global parameter of class

		assertEquals(1, classNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfClass1.getChoiceCount());
		ChoiceNode choiceNode1FromGlobalParam = globalParameterNodeOfClass1.getChoices().get(0);
		assertEquals(globalChoiceNode1, choiceNode1FromGlobalParam);

		assertEquals(1, choiceNode1FromGlobalParam.getChoiceCount());
		ChoiceNode choiceNode11FromGlobalParam = choiceNode1FromGlobalParam.getChoices().get(0);
		assertEquals(globalChoiceNode11, choiceNode11FromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(1, methodParameterNode1.getChoiceCount()); // sees choices from global parameter because linked

		MethodParameterNode methodParameterNode2 = (MethodParameterNode)methodNode.getParameter(0);
		assertEquals(true, methodParameterNode2.isLinked());
		assertEquals(globalParameterNodeOfClass1, methodParameterNode2.getLinkToGlobalParameter());

		// check choices from constraints

		ChoiceNode choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
		assertEquals(globalChoiceNode1, choiceNodeFromPrecondition);

		ChoiceNode choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
		assertEquals(globalChoiceNode1, choiceNodeFromPostcondition);

		// reverse operation

		reverseOperations.executeFromTail();

		// check global parameter

		assertEquals(1, classNode.getParametersCount());
		assertEquals(1, globalParameterNodeOfClass1.getChoiceCount());
		choiceNode1FromGlobalParam = globalParameterNodeOfClass1.getChoices().get(0);
		assertEquals(globalChoiceNode1, choiceNode1FromGlobalParam);

		assertEquals(1, choiceNode1FromGlobalParam.getChoiceCount());
		choiceNode11FromGlobalParam = choiceNode1FromGlobalParam.getChoices().get(0);
		assertEquals(globalChoiceNode11, choiceNode11FromGlobalParam);

		// check local parameter 

		assertEquals(1, methodNode.getParametersCount());
		MethodParameterNode resultMethodParameterNode = (MethodParameterNode) methodNode.getParameter(0);
		assertEquals(1, resultMethodParameterNode.getChoiceCount());

		assertEquals(false, resultMethodParameterNode.isLinked());
		assertNull(resultMethodParameterNode.getLinkToGlobalParameter());

		// check local choices

		ChoiceNode resultChoiceNodeOfMethod1 = resultMethodParameterNode.getChoices().get(0);
		assertEquals(choiceNodeOfMethod1, resultChoiceNodeOfMethod1);

		assertEquals(1, resultChoiceNodeOfMethod1.getChoiceCount());
		ChoiceNode resultChoiceNodeOfMethod11 = resultChoiceNodeOfMethod1.getChoices().get(0);
		assertEquals(choiceNodeOfMethod11, resultChoiceNodeOfMethod11);

		// check choices from constraints

		choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
		assertEquals(choiceNodeOfMethod11, choiceNodeFromPrecondition);

		choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
		assertEquals(choiceNodeOfMethod11, choiceNodeFromPostcondition);
	}

	@Test
	public void deletingTestCases() {

		RootNode rootNode = new RootNode("Root", null);

		// add class node

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// add global parameter of class and choice node

		final String parameterType = "String";
		final String choiceValueString = "1";

		GlobalParameterNode globalParameterNodeOfClass1 = 
				ClassNodeHelper.addGlobalParameterToClass(classNode, "CP1", parameterType, null);

		ChoiceNode globalChoiceOfClass11 =
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfClass1, "CC11", choiceValueString, null);

		// add method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		// add parameter and choice to method

		MethodParameterNode methodParameterNode1 = 
				MethodNodeHelper.addParameterToMethod(methodNode, "MP1", parameterType);

		ChoiceNode choiceNodeOfMethod11 = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode1, "MC11", choiceValueString);

		addSimpleChoiceConstraintToMethod(
				methodNode, "constraint1", methodParameterNode1, choiceNodeOfMethod11, choiceNodeOfMethod11);

		// add test case

		List<ChoiceNode> choices = new ArrayList<>();
		choices.add(choiceNodeOfMethod11);

		TestCaseNode testCaseNode = new TestCaseNode(choices);
		List<TestCaseNode> testCaseNodes = new ArrayList<TestCaseNode>();
		testCaseNodes.add(testCaseNode);

		methodNode.addTestCase(testCaseNode);


		// creating choice conversion list

		ParameterConversionDefinition parameterConversionDefinition = new ParameterConversionDefinition();

		ParameterConversionItem parameterConversionItemForChoice = 
				new ParameterConversionItem(
						new ParameterConversionItemPartForChoice(choiceNodeOfMethod11), 
						new ParameterConversionItemPartForChoice(globalChoiceOfClass11), 
						(String)null);

		parameterConversionDefinition.addItemWithMergingDescriptions(parameterConversionItemForChoice);

		// linking

		ListOfModelOperations reverseOperations = new ListOfModelOperations();
		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		ParameterTransformer.linkMethodParameteToGlobalParameter(
				methodParameterNode1, 
				globalParameterNodeOfClass1, 
				parameterConversionDefinition, 
				reverseOperations, 
				extLanguageManager);

		// checking test cases - should be deleted

		List<TestCaseNode> resultTestCases = methodNode.getTestCases();

		assertEquals(0, resultTestCases.size());

		// reverse operation

		reverseOperations.executeFromTail();

		assertEquals(1, resultTestCases.size());
	}

	@Test
	public void unlinkingMethodParameter() {

		RootNode rootNode = new RootNode("Root", null);

		// add global parameter of root and choice node

		final String parameterType = "String";

		GlobalParameterNode globalParameterNodeOfRoot1 = 
				RootNodeHelper.addGlobalParameterToRoot(rootNode, "RP1", parameterType, null);

		final String choiceValueString = "1";

		ChoiceNode globalChoiceNodeOfRoot1 = 
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfRoot1, "C1", choiceValueString, null);

		ChoiceNode globalChoiceNodeOfRoot2 = 
				GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
						globalParameterNodeOfRoot1, "C2", choiceValueString, null);

		// add class node

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// add method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		// add parameter and choice to method

		MethodParameterNode methodParameterNode = 
				MethodNodeHelper.addParameterToMethod(methodNode, "MP1", parameterType);

		methodParameterNode.setLinkToGlobalParameter(globalParameterNodeOfRoot1);
		methodParameterNode.setLinked(true);

		// constraint

		addSimpleChoiceConstraintToMethod(
				methodNode, "constraint1", methodParameterNode, globalChoiceNodeOfRoot1, globalChoiceNodeOfRoot1);

		// unlink

		ListOfModelOperations reverseOperations = new ListOfModelOperations();
		IExtLanguageManager extLanguageManager = new ExtLanguageManagerForJava();

		ParameterTransformer.unlinkMethodParameteFromGlobalParameter(
				methodParameterNode, globalParameterNodeOfRoot1, reverseOperations, extLanguageManager);

		// check if linked

		assertFalse(methodParameterNode.isLinked());
		assertNull(methodParameterNode.getLinkToGlobalParameter());

		// change names of global choices to avoid confusion during check

		globalChoiceNodeOfRoot1.setName("RC1");
		globalChoiceNodeOfRoot2.setName("RC2");

		// check choices copied to method parameter

		List<ChoiceNode> resultChoices = methodParameterNode.getChoices();
		assertEquals(2, resultChoices.size());

		assertEquals("C1", resultChoices.get(0).getName());
		assertEquals("C2", resultChoices.get(1).getName());

		// check choices in constraints

		ChoiceNode choiceNodeFromPrecondition = getChoiceNodeFromConstraintPrecondition(methodNode, 0);
		assertEquals(resultChoices.get(0), choiceNodeFromPrecondition);

		ChoiceNode choiceNodeFromPostcondition = getChoiceNodeFromConstraintPostcondition(methodNode, 0);
		assertEquals(resultChoices.get(0), choiceNodeFromPostcondition);

		// reverse operation

		reverseOperations.executeFromTail();

		// check if returned to original state

		assertTrue(methodParameterNode.isLinked());
		assertEquals(globalParameterNodeOfRoot1, methodParameterNode.getLinkToGlobalParameter());

		// change Linked property for checking child choices only

		methodParameterNode.setLinked(false);

		List<ChoiceNode> resultChoiceNodes = methodParameterNode.getChoices();
		assertEquals(0, resultChoiceNodes.size());

		methodParameterNode.setLinked(true);
	}

	@Test
	public void checkValueConversionsForDifferentTypesAndValues() {
	
		assertFalse(canConvert("ABC", tString, tInt, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("ABC", tString, tString, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("1", tString, tInt, IsChoiceRandomized.FALSE));
		
		assertTrue(canConvert("123.0", tDouble, tInt, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("123.0:123.0", tDouble, tInt, IsChoiceRandomized.TRUE));

		assertFalse(canConvert("123.1", tDouble, tInt, IsChoiceRandomized.FALSE));
		assertFalse(canConvert("123.1:123.1", tDouble, tInt, IsChoiceRandomized.TRUE));

		assertFalse(canConvert("123.54e+7", tDouble, tInt, IsChoiceRandomized.FALSE));
		assertFalse(canConvert("123.54e+7:123.54e+7", tDouble, tInt, IsChoiceRandomized.TRUE));

		assertTrue(canConvert("1234", tFloat, tDouble, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("1234:1234", tFloat, tDouble, IsChoiceRandomized.TRUE));

		assertTrue(canConvert("1234", tFloat, tInt, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("1234:1234", tFloat, tInt, IsChoiceRandomized.TRUE));

		assertFalse(canConvert("1234", tFloat, tByte, IsChoiceRandomized.FALSE));
		assertFalse(canConvert("1234:1234", tFloat, tByte, IsChoiceRandomized.TRUE));

		assertTrue(canConvert("123", tFloat, tByte, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("123:123", tFloat, tByte, IsChoiceRandomized.TRUE));
		
		assertFalse(canConvert("false", tBoolean, tByte, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("false", tBoolean, tString, IsChoiceRandomized.FALSE));
		
		assertTrue(canConvert("false", tBoolean, tBoolean, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("true", tBoolean, tBoolean, IsChoiceRandomized.FALSE));
		
		assertFalse(canConvert("1", tBoolean, tBoolean, IsChoiceRandomized.FALSE));
		assertTrue(canConvert("false", tString, tBoolean, IsChoiceRandomized.FALSE));
	}

	private boolean canConvert(
			String value, 
			String oldType, 
			String newType, 
			IsChoiceRandomized isChoiceRandomized) {
		
		boolean isRandomized = false;
		
		if (isChoiceRandomized == IsChoiceRandomized.TRUE) {
			isRandomized = true;
		}
		
		boolean isCompatible = ParameterTransformer.isValueCompatibleWithType(value, newType, isRandomized);
		return isCompatible;
	}
	
	@Test
	public void convertChoicesWithCheckIfPossible() {

		RootNode rootNode = new RootNode("Root", null);

		// add global parameter of root and choice node

		final String stringParameterType = "String";

		// add class node

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// add method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		// add parameter and choice to method

		MethodParameterNode methodParameterNode = 
				MethodNodeHelper.addParameterToMethod(methodNode, "MP1", stringParameterType);

		ChoiceNode choiceNodeOfMethod = 
				MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode, "MC1", "");

		ParameterConversionDefinition parameterConversionDefinition = new ParameterConversionDefinition();

		ValueConversionOperator checker = 
				new ValueConversionOperator(
						methodParameterNode, 
						choiceNodeOfMethod,
						parameterConversionDefinition);

		performTypeOperation(WhatToTest.CHOICES, checker);
	}

	@Test
	public void covertConstraintsWithCheckIfPossible() {

		RootNode rootNode = new RootNode("Root", null);

		// add global parameter of root and choice node

		final String stringParameterType = "String";

		// add class node

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// add method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		// add parameter

		MethodParameterNode methodParameterNode = 
				MethodNodeHelper.addParameterToMethod(methodNode, "MP1", stringParameterType);

		ParameterConversionDefinition parameterConversionDefinition = new ParameterConversionDefinition();

		ValueConversionOperator valueOperator = 
				new ValueConversionOperator(
						methodParameterNode, 
						null,
						parameterConversionDefinition);

		performTypeOperation(WhatToTest.CONSTRAINTS, valueOperator);
	}

	private void performTypeOperation(WhatToTest whatToTest, ValueConversionOperator operator) {
		
		ParameterConversionDefinition resultConversionDefinition = operator.getParameterConversionDefinition();

		operator.operate(whatToTest, IsChoiceRandomized.FALSE, tString, tString, "ABC", SuccessExpected.TRUE, "ABC");
		assertEquals(0, resultConversionDefinition.getItemCount());
		
		operator.operate(whatToTest, IsChoiceRandomized.FALSE, tString, tInt, "ABC", SuccessExpected.FALSE, "123");
		assertEquals(1, resultConversionDefinition.getItemCount());
		ParameterConversionItem parameterConversionItem = resultConversionDefinition.getCopyOfItem(0);
		IParameterConversionItemPart srcPart = parameterConversionItem.getSrcPart();
		String description = srcPart.getDescription();
		assertEquals("ABC[value]", description);
	}

	private static class ValueConversionOperator {

		private MethodParameterNode fMethodParameterNode;
		private ChoiceNode fChoiceNodeOfMethod;
		private ParameterConversionDefinition fParameterConversionDefinition;

		public ValueConversionOperator(
				MethodParameterNode methodParameterNode, 
				ChoiceNode choiceNodeOfMethod,
				ParameterConversionDefinition parameterConversionDefinition) {

			fMethodParameterNode = methodParameterNode;
			fChoiceNodeOfMethod = choiceNodeOfMethod;
			fParameterConversionDefinition = parameterConversionDefinition;
		}

		public void operate(
				WhatToTest testChoices, 
				IsChoiceRandomized isRandomized, 
				String oldType, 
				String newType, 
				String value, 
				SuccessExpected successExpected,
				String newValue) {

			if (isRandomized == IsChoiceRandomized.TRUE && testChoices == WhatToTest.CONSTRAINTS) {
				return; // randomized for choices only
			}

			fParameterConversionDefinition.clear();

			fMethodParameterNode.setType(oldType);

			if (testChoices == WhatToTest.CHOICES) {
				fChoiceNodeOfMethod.setValueString(value);

				if (isRandomized == IsChoiceRandomized.TRUE) {
					fChoiceNodeOfMethod.setRandomizedValue(true);
				} else {
					fChoiceNodeOfMethod.setRandomizedValue(false);
				}

			} else {
				MethodNode methodNode = fMethodParameterNode.getMethod();

				methodNode.removeAllConstraints();

				addSimpleValueConstraintToMethod(
						methodNode,
						"C1",
						fMethodParameterNode,
						value,
						value);
			}

			ParameterTransformer.verifyConversionOfParameterToType(
					newType, fMethodParameterNode, fParameterConversionDefinition);

			if (successExpected == SuccessExpected.TRUE) {
				assertFalse(fParameterConversionDefinition.hasItems());
			} else {
				assertTrue(fParameterConversionDefinition.hasItems());
			}

			convertParameter(newType, newValue);

			if (testChoices == WhatToTest.CHOICES) {
				checkValueOfChoice(fChoiceNodeOfMethod, newValue);
			} else {
				checkValueFromConstraint(fMethodParameterNode.getMethod(), newValue);
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
			ParameterConversionItemPartForValue dstPart = new ParameterConversionItemPartForValue(newValue);

			ParameterConversionItem newParameterConversionItem = 
					new ParameterConversionItem(srcPart, dstPart, (String)null);

			fParameterConversionDefinition.setItem(0, newParameterConversionItem);

			ParameterTransformer.convertChoicesAndConstraintsToType(
					fMethodParameterNode, fParameterConversionDefinition);
		}

		public ParameterConversionDefinition getParameterConversionDefinition() {
			return fParameterConversionDefinition;
		}
	}

	private void addSimpleChoiceConstraintToMethod(
			MethodNode methodNode,
			String constraintName,
			MethodParameterNode methodParameterNode,
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

	private void addSimpleLabelConstraintToMethod(
			MethodNode methodNode,
			String constraintName,
			MethodParameterNode methodParameterNode,
			String label1,
			String label2) {

		RelationStatement relationStatement1 = 
				RelationStatement.createRelationStatementWithLabelCondition(
						methodParameterNode, EMathRelation.EQUAL, label1);

		RelationStatement relationStatement2 = 
				RelationStatement.createRelationStatementWithLabelCondition(
						methodParameterNode, EMathRelation.LESS_THAN, label2);

		Constraint constraint = new Constraint(
				constraintName, 
				ConstraintType.EXTENDED_FILTER, 
				relationStatement1, 
				relationStatement2, 
				null);

		ConstraintNode constraintNode = new ConstraintNode(constraintName, constraint, null);

		methodNode.addConstraint(constraintNode);
	}

	private static void addSimpleValueConstraintToMethod(
			MethodNode methodNode,
			String constraintName,
			MethodParameterNode methodParameterNode,
			String value1,
			String value2) {

		RelationStatement relationStatement1 = 
				RelationStatement.createRelationStatementWithValueCondition(
						methodParameterNode, EMathRelation.EQUAL, value1);

		RelationStatement relationStatement2 = 
				RelationStatement.createRelationStatementWithValueCondition(
						methodParameterNode, EMathRelation.LESS_THAN, value2);

		Constraint constraint = new Constraint(
				constraintName, 
				ConstraintType.EXTENDED_FILTER, 
				relationStatement1, 
				relationStatement2, 
				null);

		ConstraintNode constraintNode = new ConstraintNode(constraintName, constraint, null);

		methodNode.addConstraint(constraintNode);
	}

	private ChoiceNode getChoiceNodeFromConstraintPostcondition(
			MethodNode methodNode, int constraintIndex) {

		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(constraintIndex);

		AbstractStatement postcondition = constraintNode.getConstraint().getPostcondition();

		ChoiceNode choiceNode = getChoiceNodeFromChoiceCondition(postcondition);

		return choiceNode;
	}

	private ChoiceNode getChoiceNodeFromConstraintPrecondition(
			MethodNode methodNode, 
			int constraintIndex) {

		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(constraintIndex);

		AbstractStatement precondition = constraintNode.getConstraint().getPrecondition();

		ChoiceNode choiceNode = getChoiceNodeFromChoiceCondition(precondition);

		return choiceNode;
	}

	private String getLabelFromConstraintPrecondition(MethodNode methodNode, int constraintIndex) {

		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(constraintIndex);

		AbstractStatement precondition = constraintNode.getConstraint().getPrecondition();

		String label = getLabelFromChoiceCondition(precondition);

		return label;
	}

	private String getLabelFromConstraintPostcondition(MethodNode methodNode, int constraintIndex) {

		ConstraintNode constraintNode = methodNode.getConstraintNodes().get(constraintIndex);

		AbstractStatement postcondition = constraintNode.getConstraint().getPostcondition();

		String label = getLabelFromChoiceCondition(postcondition);

		return label;
	}

	private ChoiceNode getChoiceNodeFromChoiceCondition(AbstractStatement abstractStatement) {

		RelationStatement relationStatement = (RelationStatement)abstractStatement; 

		IStatementCondition statementCondition = relationStatement.getCondition();

		ChoiceCondition choiceCondition = (ChoiceCondition)statementCondition;

		ChoiceNode choiceNode = choiceCondition.getRightChoice();

		return choiceNode;
	}

	private String getLabelFromChoiceCondition(AbstractStatement abstractStatement) {

		RelationStatement relationStatement = (RelationStatement)abstractStatement; 

		IStatementCondition statementCondition = relationStatement.getCondition();

		LabelCondition labelCondition = (LabelCondition)statementCondition;

		String label = labelCondition.getRightLabel();

		return label;
	}

}
