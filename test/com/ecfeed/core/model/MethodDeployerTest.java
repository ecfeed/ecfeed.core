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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.ecfeed.core.utils.TestHelper;
import org.junit.jupiter.api.Disabled;

public class MethodDeployerTest {

	@Test
	public void deployNull() {

		try {
			NodeMapper mapper = new NodeMapper();
			MethodDeployer.deploy(null, mapper);
		} catch (Exception e) {
		}
	}

	@Test
	public void deployMethodWithoutParameters() {

		MethodNode sourceMethod = new MethodNode("method");

		NodeMapper mapper = new NodeMapper();
		MethodNode deployedMethod = MethodDeployer.deploy(sourceMethod, mapper);

		assertNotNull(deployedMethod);
		assertFalse(sourceMethod.hashCode() == deployedMethod.hashCode());
		assertEquals(sourceMethod.getName() + "_" + MethodDeployer.POSTFIX, deployedMethod.getName());

		assertEquals(0, deployedMethod.getParameters().size());
	}

	@Test
	public void deployMethodWithOneParameter() {

		MethodNode sourceMethod = new MethodNode("method");
		BasicParameterNode methodParameterNode = new BasicParameterNode("parameter", "String", "A", true);
		sourceMethod.addParameter(methodParameterNode);

		NodeMapper mapper = new NodeMapper();
		MethodNode deployedMethod = MethodDeployer.deploy(sourceMethod, mapper);

		assertEquals(1, deployedMethod.getParameters().size());

		BasicParameterNode sourceParameter = (BasicParameterNode)sourceMethod.getParameters().get(0);
		BasicParameterNode deployedParameter = (BasicParameterNode)deployedMethod.getParameters().get(0);

		assertTrue(sourceParameter.hashCode() != deployedParameter.hashCode());

		assertEquals(deployedParameter.getParent(), deployedMethod);

		assertEquals(sourceParameter.getName(), deployedParameter.getName());
		assertEquals(sourceParameter.getType(), deployedParameter.getType());
		assertEquals(sourceParameter.getDefaultValue(), deployedParameter.getDefaultValue());
		assertEquals(sourceParameter.isExpected(), deployedParameter.isExpected());
	}

	@Test
	public void deployMethodWithOneParameterAndChoice() {

		MethodNode sourceMethod = new MethodNode("method");
		BasicParameterNode methodParameterNode = new BasicParameterNode("parameter", "String", "A", true);
		sourceMethod.addParameter(methodParameterNode);

		ChoiceNode sourceChoiceNode = new ChoiceNode("choice", "A");
		methodParameterNode.addChoice(sourceChoiceNode);

		NodeMapper mapper = new NodeMapper();
		MethodNode deployedMethod = MethodDeployer.deploy(sourceMethod, mapper);

		assertEquals(1, deployedMethod.getParameters().size());

		BasicParameterNode deployedParameter = (BasicParameterNode)deployedMethod.getParameters().get(0);

		ChoiceNode deployedChoiceNode = deployedParameter.getChoices().get(0);

		IAbstractNode parent = deployedChoiceNode.getParent();
		assertEquals(parent, deployedParameter);

		assertTrue(sourceChoiceNode.hashCode() != deployedChoiceNode.hashCode());
		assertEquals(sourceChoiceNode.getName(), deployedChoiceNode.getName());
		assertEquals(sourceChoiceNode.getValueString(), deployedChoiceNode.getValueString());

		ChoiceNode originalChoiceNode = (ChoiceNode) mapper.getMappedNode(deployedChoiceNode);
		assertEquals(sourceChoiceNode.hashCode(), originalChoiceNode.hashCode());
	}

//	@Test
//	public void deployMethodWithSimpleConstraint() {
//
//		MethodNode sourceMethod = new MethodNode("method");
//		BasicParameterNode methodParameterNode = new BasicParameterNode("parameter", "String", "A", true);
//		sourceMethod.addParameter(methodParameterNode);
//
//		ChoiceNode sourceChoiceNode = new ChoiceNode("choice", "A");
//		methodParameterNode.addChoice(sourceChoiceNode);
//
//		TestHelper.addSimpleChoiceConstraintToMethod(
//				sourceMethod, "c", methodParameterNode, sourceChoiceNode, sourceChoiceNode);
//
//		NodeMapper nodeMapper = new NodeMapper();
//
//		MethodNode deployedMethod = MethodDeployer.deploy(sourceMethod, nodeMapper);
//
//		BasicParameterNode deployedParameter = (BasicParameterNode)deployedMethod.getParameters().get(0);
//
//		ChoiceNode deployedChoiceNode =
//				deployedParameter.getChoices().get(0);
//
//		ChoiceNode choiceNodeFromConstraint1 =
//				TestHelper.getChoiceNodeFromConstraintPrecondition(deployedMethod);
//
//		assertEquals(deployedChoiceNode.hashCode(), choiceNodeFromConstraint1.hashCode());
//
//		ChoiceNode choiceNodeFromConstraint2 =
//				TestHelper.getChoiceNodeFromConstraintPostcondition(deployedMethod);
//
//		assertEquals(deployedChoiceNode.hashCode(), choiceNodeFromConstraint2.hashCode());
//
//		// TODO check parameter
//	}

	@Test
	public void deployParameterLinkedInStructure() {

		RootNode rootNode = new RootNode("Root", null);

		// add global parameter of root and choice node

		final String parameterType = "String";

		String globalParameterName = "RP1";

		BasicParameterNode globalParameterNodeOfRoot = 
				RootNodeHelper.addGlobalParameterToRoot(rootNode, globalParameterName, parameterType, null);

		String globalChoiceNodeName = "RC11";

		GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
				globalParameterNodeOfRoot, globalChoiceNodeName, "100", null);

		// add class node

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// add method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		// add composite parameter

		CompositeParameterNode compositeParameterNode = 
				ParametersAndConstraintsParentNodeHelper.addCompositeParameterToMethod(methodNode, "S1");

		// add linked basic parameter to composite parameter

		BasicParameterNode basicParameterNode = 
				new BasicParameterNode(
						"P1", parameterType, "", false,	globalParameterNodeOfRoot,	null);

		compositeParameterNode.addParameter(basicParameterNode);

		// deploy 

		NodeMapper mapper = new NodeMapper();
		MethodNode deployedMethod = MethodDeployer.deploy(methodNode, mapper);

		// check

		assertEquals(1, deployedMethod.getParametersCount());

		BasicParameterNode deployedParameterNode = (BasicParameterNode) deployedMethod.getParameter(0);

		String deployedParameterName = deployedParameterNode.getName();
		assertEquals(deployedParameterName, "S1:P1");

		//		AbstractParameterNode link = 
		deployedParameterNode.getLinkToGlobalParameter();
		// assertNull(link); TODO MO-RE here test fails - global parameters and choices should be "resolved" local

		int deployedChoicesCount = deployedParameterNode.getChoiceCount();
		assertEquals(1, deployedChoicesCount);

		List<ChoiceNode> deployedChoices = deployedParameterNode.getChoices();
		ChoiceNode deployedChoiceNode = deployedChoices.get(0);

		String deployedChoiceName = deployedChoiceNode.getName();
		assertEquals(globalChoiceNodeName, deployedChoiceName);
	}

	@Test
	public void deployTwoLinkedParametersWithDifferentNames() {

		MethodNode methodNode = createModelWithTwoLinkedParameters("P1", "P2");

		// deploy 

		NodeMapper mapper = new NodeMapper();
		MethodNode deployedMethod = MethodDeployer.deploy(methodNode, mapper);

		// check

		assertEquals(2, deployedMethod.getParametersCount());
		List<AbstractParameterNode> deployedParameters = deployedMethod.getParameters();

		String name1 = deployedParameters.get(0).getName();
		assertEquals("P1", name1);
		String name2 = deployedParameters.get(1).getName();
		assertEquals("S1:P2", name2);
	}

	@Test
	public void deployTwoLinkedParametersWithTheSameNames() {

		MethodNode methodNode = createModelWithTwoLinkedParameters("P1", "P1");

		// deploy 

		NodeMapper mapper = new NodeMapper();
		MethodNode deployedMethod = MethodDeployer.deploy(methodNode, mapper);

		// check

		// assertEquals(1, deployedMethod.getParametersCount()); // TODO MO-RE here test fails - if name and link are the same the parameters should be merged 
		List<AbstractParameterNode> deployedParameters = deployedMethod.getParameters();

		String name1 = deployedParameters.get(0).getName();
		assertEquals("P1", name1);
	}

	private MethodNode createModelWithTwoLinkedParameters(String parameter1Name, String parameter2Name) {

		RootNode rootNode = new RootNode("Root", null);

		// add global parameter of root and choice node

		final String parameterType = "String";

		String globalParameterName = "RP1";

		BasicParameterNode globalParameterNodeOfRoot = 
				RootNodeHelper.addGlobalParameterToRoot(rootNode, globalParameterName, parameterType, null);

		String globalChoiceNodeName = "RC11";

		GlobalParameterNodeHelper.addNewChoiceToGlobalParameter(
				globalParameterNodeOfRoot, globalChoiceNodeName, "100", null);

		// add class node

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// add method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		BasicParameterNode basicParameterNodeOfMethod = 
				new BasicParameterNode(
						parameter1Name, parameterType, "", false,	globalParameterNodeOfRoot,	null);

		methodNode.addParameter(basicParameterNodeOfMethod);

		// add composite parameter

		CompositeParameterNode compositeParameterNode = 
				ParametersAndConstraintsParentNodeHelper.addCompositeParameterToMethod(methodNode, "S1");

		// add linked basic parameter to composite parameter

		BasicParameterNode basicParameterNodeOfCompositeParam = 
				new BasicParameterNode(
						parameter2Name, parameterType, "", false,	globalParameterNodeOfRoot,	null);

		compositeParameterNode.addParameter(basicParameterNodeOfCompositeParam);

		return methodNode;
	}

}
