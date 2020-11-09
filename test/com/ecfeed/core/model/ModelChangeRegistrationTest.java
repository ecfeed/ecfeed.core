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

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class ModelChangeRegistrationTest {

	@Test
	public void registerChangesForClassAndMethod() {

		ModelChangeRegistrator changeCounter = new ModelChangeRegistrator();

		changeCounter.registerModelSaved();
		RootNode rootNode = new RootNode("Root", changeCounter);
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		rootNode.setName("NEW_NAME");
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		rootNode.setDescription("desc");
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		ClassNode classNode = new ClassNode("class1", rootNode.getModelChangeRegistrator());
		rootNode.addClass(classNode);
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		rootNode.removeClass(classNode);
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		rootNode.addClass(classNode);
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		MethodNode methodNode = new MethodNode("method1", classNode.getModelChangeRegistrator());
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		classNode.addMethod(methodNode);
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		classNode.removeMethod(methodNode);
		assertTrue(changeCounter.isModelChangedSinceLastSave());		
	}

	@Test
	public void registerChangesGlobalParameter() {

		ModelChangeRegistrator changeCounter = new ModelChangeRegistrator();

		RootNode rootNode = new RootNode("Root", changeCounter);
		GlobalParameterNode globalParameterNode = new GlobalParameterNode("g1", "t", changeCounter);

		changeCounter.registerModelSaved();
		rootNode.addParameter(globalParameterNode);
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		ChoiceNode choice = new ChoiceNode("c1", "0", changeCounter);

		changeCounter.registerModelSaved();
		globalParameterNode.addChoice(choice);
		assertTrue(changeCounter.isModelChangedSinceLastSave());
		
		changeCounter.registerModelSaved();
		globalParameterNode.removeChoice(choice);
		assertTrue(changeCounter.isModelChangedSinceLastSave());
	}

	@Test
	public void registerChangesForMethodParameter() {

		ModelChangeRegistrator changeCounter = new ModelChangeRegistrator();

		RootNode rootNode = new RootNode("Root", changeCounter);
		ClassNode classNode = new ClassNode("class1", rootNode.getModelChangeRegistrator());
		rootNode.addClass(classNode);
		MethodNode methodNode = new MethodNode("method1", classNode.getModelChangeRegistrator());

		changeCounter.registerModelSaved();
		classNode.addMethod(methodNode);
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		MethodParameterNode methodParameterNode = 
				new MethodParameterNode(
						"par1", "int", "0", false, methodNode.getModelChangeRegistrator());
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		methodNode.addParameter(methodParameterNode);
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		methodNode.removeParameter(methodParameterNode);
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		methodNode.addParameter(methodParameterNode);
		assertTrue(changeCounter.isModelChangedSinceLastSave());
	}

	@Test
	public void registerChangesForConstraint() {

		ModelChangeRegistrator changeCounter = new ModelChangeRegistrator();

		RootNode rootNode = new RootNode("Root", changeCounter);
		ClassNode classNode = new ClassNode("class1", rootNode.getModelChangeRegistrator());
		rootNode.addClass(classNode);
		MethodNode methodNode = new MethodNode("method1", classNode.getModelChangeRegistrator());

		changeCounter.registerModelSaved();
		classNode.addMethod(methodNode);
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		Constraint constraint1 = new Constraint("name1", changeCounter, new StaticStatement(true, changeCounter), new StaticStatement(false, changeCounter));
		ConstraintNode constraintNode1 = new ConstraintNode("name1", constraint1, null);

		changeCounter.registerModelSaved();
		methodNode.addConstraint(constraintNode1);
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		methodNode.removeAllConstraints();
		assertTrue(changeCounter.isModelChangedSinceLastSave());
	}

	@Test
	public void calculateChangesForTestCase() {

		ModelChangeRegistrator changeCounter = new ModelChangeRegistrator();

		RootNode rootNode = new RootNode("Root", changeCounter);
		ClassNode classNode = new ClassNode("class1", rootNode.getModelChangeRegistrator());
		rootNode.addClass(classNode);
		MethodNode methodNode = new MethodNode("method1", classNode.getModelChangeRegistrator());

		changeCounter.registerModelSaved();
		classNode.addMethod(methodNode);
		assertTrue(changeCounter.isModelChangedSinceLastSave());


		TestCaseNode testCase1 = new TestCaseNode("suite 1", null, new ArrayList<ChoiceNode>());

		changeCounter.registerModelSaved();
		methodNode.addTestCase(testCase1);
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		methodNode.removeTestCase(testCase1);
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		methodNode.addTestCase(testCase1);
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		methodNode.removeTestSuite("suite 1");
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		methodNode.addTestCase(testCase1);
		assertTrue(changeCounter.isModelChangedSinceLastSave());

		changeCounter.registerModelSaved();
		methodNode.removeAllTestCases();
		assertTrue(changeCounter.isModelChangedSinceLastSave());
	}

}
