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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.TestHelper;

public class MethodDeployerTest {

	@Test
	public void deployNull() {

		try {
			NodeMapper nodeMapper = new NodeMapper();
			MethodDeployer.deploy(null, nodeMapper);
		} catch (Exception e) {
		}
	}

	@Test
	public void deployMethodWithoutParameters() {

		MethodNode sourceMethod = new MethodNode("method");

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode deployedMethod = MethodDeployer.deploy(sourceMethod, nodeMapper);

		assertNotNull(deployedMethod);
		assertFalse(sourceMethod.hashCode() == deployedMethod.hashCode());
		assertEquals(sourceMethod.getName() + "_" + MethodDeployer.POSTFIX, deployedMethod.getName());

		assertEquals(0, deployedMethod.getParameters().size());
	}

	@Test
	public void deployMethodWithOneParameter() {

		MethodNode sourceMethod = new MethodNode("method");
		BasicParameterNode methodParameterNode = new BasicParameterNode("parameter", "String", "A", true, null);
		sourceMethod.addParameter(methodParameterNode);

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode deployedMethod = MethodDeployer.deploy(sourceMethod, nodeMapper);

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
		BasicParameterNode methodParameterNode = new BasicParameterNode("parameter", "String", "A", true, null);
		sourceMethod.addParameter(methodParameterNode);

		ChoiceNode sourceChoiceNode = new ChoiceNode("choice", "A");
		methodParameterNode.addChoice(sourceChoiceNode);

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode deployedMethod = MethodDeployer.deploy(sourceMethod, nodeMapper);

		assertEquals(1, deployedMethod.getParameters().size());

		BasicParameterNode deployedParameter = (BasicParameterNode)deployedMethod.getParameters().get(0);

		ChoiceNode deployedChoiceNode = deployedParameter.getChoices().get(0);

		IAbstractNode parent = deployedChoiceNode.getParent();
		assertEquals(parent, deployedParameter);

		assertTrue(sourceChoiceNode.hashCode() != deployedChoiceNode.hashCode());
		assertEquals(sourceChoiceNode.getName(), deployedChoiceNode.getName());
		assertEquals(sourceChoiceNode.getValueString(), deployedChoiceNode.getValueString());

		ChoiceNode originalChoiceNode = nodeMapper.getSourceNode(deployedChoiceNode);
		assertEquals(sourceChoiceNode.hashCode(), originalChoiceNode.hashCode());
	}

	@Test
	public void deployMethodWithSimpleConstraint() {

		MethodNode sourceMethod = new MethodNode("method");
		BasicParameterNode methodParameterNode = new BasicParameterNode("parameter", "String", "A", true, null);
		sourceMethod.addParameter(methodParameterNode);

		ChoiceNode sourceChoiceNode = new ChoiceNode("choice", "A");
		methodParameterNode.addChoice(sourceChoiceNode);

		TestHelper.addSimpleChoiceConstraintToMethod(
				sourceMethod, "c", methodParameterNode, sourceChoiceNode, sourceChoiceNode);

		NodeMapper nodeMapper = new NodeMapper();

		MethodNode deployedMethod = MethodDeployer.deploy(sourceMethod, nodeMapper);

		BasicParameterNode deployedParameter = (BasicParameterNode)deployedMethod.getParameters().get(0);

		ChoiceNode deployedChoiceNode =
				deployedParameter.getChoices().get(0);

		ChoiceNode choiceNodeFromConstraint1 =
				TestHelper.getChoiceNodeFromConstraintPrecondition(deployedMethod);

		assertEquals(deployedChoiceNode.hashCode(), choiceNodeFromConstraint1.hashCode());

		ChoiceNode choiceNodeFromConstraint2 =
				TestHelper.getChoiceNodeFromConstraintPostcondition(deployedMethod);

		assertEquals(deployedChoiceNode.hashCode(), choiceNodeFromConstraint2.hashCode());

		// TODO MO-RE check parameter
	}

	@Test
	public void deployParameterLinkedInStructure() {

		RootNode rootNode = new RootNode("Root", null);

		// add global parameter of root and choice node

		BasicParameterNode globalParameterNodeOfRoot = 
				RootNodeHelper.addNewBasicParameterToRoot(rootNode, "RP1", "String", "", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				globalParameterNodeOfRoot, "RC11", "100", false, false, null);

		// add class node

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// add method node

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "Method", true, null);

		// add composite parameter

		CompositeParameterNode compositeParameterNode = 
				ParametersAndConstraintsParentNodeHelper.addNewCompositeParameterToParent(methodNode, "S1");

		// add linked basic parameter to composite parameter

		BasicParameterNode basicParameterNode = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(
						compositeParameterNode, "P1", "String");

		basicParameterNode.setLinkToGlobalParameter(globalParameterNodeOfRoot);

		// Root
		//   RP1 
		//     RC11
		//   Class
		//     Method
		//       S1 : Structure
		//         P1->RP1  

		// deploy 

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode deployedMethod = MethodDeployer.deploy(methodNode, nodeMapper);

		// check

		assertEquals(1, deployedMethod.getParametersCount());

		BasicParameterNode deployedParameterNode = (BasicParameterNode) deployedMethod.getParameter(0);

		String deployedParameterName = deployedParameterNode.getName();
		//assertEquals(deployedParameterName, "RP1");
		assertEquals(deployedParameterName, "P1");

		AbstractParameterNode link = deployedParameterNode.getLinkToGlobalParameter();
		//assertNull(link); // link should be resolved in deployment
		assertEquals(globalParameterNodeOfRoot, link);

		int deployedChoicesCount = deployedParameterNode.getChoiceCount();
		assertEquals(1, deployedChoicesCount);

		List<ChoiceNode> deployedChoices = deployedParameterNode.getChoices();
		ChoiceNode deployedChoiceNode = deployedChoices.get(0);

		String deployedChoiceName = deployedChoiceNode.getName();
		assertEquals("RC11", deployedChoiceName);
	}

	@Test
	public void AAdeployTwoBasicLinkedParametersWithDifferentNames() {

		MethodNode methodNode = createModelWithTwoBasicLinkedParametersOneAtMethodLevel("P1", "P2");

		// deploy 

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode deployedMethod = MethodDeployer.deploy(methodNode, nodeMapper);

		// check

		assertEquals(2, deployedMethod.getParametersCount());
		List<AbstractParameterNode> deployedParameters = deployedMethod.getParameters();

		String name1 = deployedParameters.get(0).getName();
		assertEquals("P1", name1);
		String name2 = deployedParameters.get(1).getName();
		assertEquals("P2", name2);

		// with linking contexts

		assertEquals(2, deployedMethod.getParametersCount());
		List<ParameterWithLinkingContext> deployedParametersWithContexts = deployedMethod.getParametersWithLinkingContexts();

		// param1

		ParameterWithLinkingContext parameterWithLinkingContext1 = deployedParametersWithContexts.get(0);
		AbstractParameterNode parameter1 = parameterWithLinkingContext1.getParameter();
		assertEquals("P1", parameter1.getName());
		AbstractParameterNode linkingContext1 = parameterWithLinkingContext1.getLinkingContext();
		assertNull(linkingContext1);

		String testedSignature1 = 
				MethodDeployer.createSignatureOfOriginalNodes(parameterWithLinkingContext1, nodeMapper, new ExtLanguageManagerForJava());
		assertEquals("P1->RP1", testedSignature1);

		// param2

		ParameterWithLinkingContext parameterWithLinkingContext2 = deployedParametersWithContexts.get(1);
		assertEquals("P2", parameterWithLinkingContext2.getParameter().getName());
		AbstractParameterNode linkingContext2 = parameterWithLinkingContext2.getLinkingContext();
		assertNull(linkingContext2);

		String testedSignature2 = 
				MethodDeployer.createSignatureOfOriginalNodes(parameterWithLinkingContext2, nodeMapper, new ExtLanguageManagerForJava());
		assertEquals("S1:P2->RP1", testedSignature2);
	}

	//	@Test
	public void deployTwoBasicLinkedParametersWithTheSameNames() {

		MethodNode methodNode = createModelWithTwoBasicLinkedParametersOneAtMethodLevel("P1", "P1");

		// deploy 

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode deployedMethod = MethodDeployer.deploy(methodNode, nodeMapper);

		// check

		//		assertEquals(1, deployedMethod.getParametersCount()); // TODO MO-RE here test fails - if name and link are the same the parameters should be merged
		List<AbstractParameterNode> deployedParameters = deployedMethod.getParameters();

		String name1 = deployedParameters.get(0).getName();
		assertEquals("P1", name1);
	}

	@Test
	public void deployTwoLocalStructuresLinkedToOneGlobalStructure() {

		RootNode rootNode = new RootNode("Root", null);

		// global composite 1

		CompositeParameterNode globalCompositeNode = 
				ParametersAndConstraintsParentNodeHelper.addNewCompositeParameterToParent(rootNode, "GS1");

		// parameter 1 of global composite and choices

		BasicParameterNode globalBasicParameterNode = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(
						globalCompositeNode, "GP1", "String");

		MethodParameterNodeHelper.addNewChoiceToMethodParameter(globalBasicParameterNode, "GC11", "GC11");

		// class node

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "Class", true, null);

		// method node

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "Method", true, null);

		// local composite

		CompositeParameterNode localCompositeNode1 = 
				ParametersAndConstraintsParentNodeHelper.addNewCompositeParameterToParent(methodNode, "S1");

		localCompositeNode1.setLinkToGlobalParameter(globalCompositeNode);

		// local composite 2

		CompositeParameterNode localCompositeNode2 = 
				ParametersAndConstraintsParentNodeHelper.addNewCompositeParameterToParent(methodNode, "S2");

		localCompositeNode2.setLinkToGlobalParameter(globalCompositeNode);

		// Root
		//   GS1
		//     GP1
		//       GC11
		//   Class
		//     Method
		//       S1->GS1
		//       S2->Gs1

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode deployedMethod = MethodDeployer.deploy(methodNode, nodeMapper);

		List<ParameterWithLinkingContext> deployedParameters = deployedMethod.getParametersWithLinkingContexts();

		assertEquals(2, deployedParameters.size());

		// check the first parameter

		ParameterWithLinkingContext deployedPar1 = deployedParameters.get(0);
		String signature1 = 
				AbstractParameterSignatureHelper.createSignatureOfParameterWithContextOrLinkNewStandard(
						deployedPar1.getParameter(), deployedPar1.getLinkingContext(), new ExtLanguageManagerForJava());

		assertEquals("S1->GP1", signature1);

		AbstractParameterNode originalGlobalParameter1 = nodeMapper.getSourceNode(deployedPar1.getParameter());
		assertEquals(globalBasicParameterNode, originalGlobalParameter1);

		AbstractParameterNode originalLinkingContext1 = nodeMapper.getSourceNode(deployedPar1.getLinkingContext());
		assertEquals(localCompositeNode1, originalLinkingContext1);

		String originalSignature1 = 
				AbstractParameterSignatureHelper.createSignatureOfParameterWithContext(
						originalGlobalParameter1, originalLinkingContext1);

		assertEquals("S1->GS1:GP1", originalSignature1);

		// check the second parameter

		ParameterWithLinkingContext deployedPar2 = deployedParameters.get(1);
		String signature2 = 
				AbstractParameterSignatureHelper.createSignatureOfParameterWithContextOrLinkNewStandard(
						deployedPar2.getParameter(), deployedPar2.getLinkingContext(), new ExtLanguageManagerForJava());

		assertEquals("S2->GP1", signature2);

		AbstractParameterNode originalGlobalParameter2 = nodeMapper.getSourceNode(deployedPar2.getParameter());
		assertEquals(globalBasicParameterNode, originalGlobalParameter2);

		AbstractParameterNode originalLinkingContext2 = nodeMapper.getSourceNode(deployedPar2.getLinkingContext());
		assertEquals(localCompositeNode2, originalLinkingContext2);

		String originalSignature2 = 
				AbstractParameterSignatureHelper.createSignatureOfParameterWithContext(
						originalGlobalParameter2, originalLinkingContext2);

		assertEquals("S2->GS1:GP1", originalSignature2);
	}

	//	@Test
	//	public void AAdeployTwoLocalStructuresLinkedToOneGlobalStructure() {
	//		
	//		RootNode rootNode = new RootNode("Root", null);
	//
	//		// global composite 1
	//		
	//		CompositeParameterNode globalCompositeParameterNode1 = 
	//				ParametersAndConstraintsParentNodeHelper.addCompositeParameter(rootNode, "GS1");
	//
	//		// parameter 1 of global composite and choices
	//
	//		BasicParameterNode rootParameterNode1 = 
	//				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(
	//						globalCompositeParameterNode1, "GP1", "String");
	//
	//		MethodParameterNodeHelper.addChoiceToMethodParameter(rootParameterNode1, "GC11", "GC11");
	//
	//		// class node
	//
	//		ClassNode classNode = RootNodeHelper.addClassNodeToRoot(rootNode, "Class", null);
	//
	//		// method node
	//
	//		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);
	//
	//		// local composite
	//
	//		CompositeParameterNode localCompositeParameterNode1 = 
	//				ParametersAndConstraintsParentNodeHelper.addCompositeParameter(methodNode, "S1");
	//
	//		localCompositeParameterNode1.setLinkToGlobalParameter(globalCompositeParameterNode1);
	//
	//		// local composite 2
	//
	//		CompositeParameterNode localCompositeParameterNode2 = 
	//				ParametersAndConstraintsParentNodeHelper.addCompositeParameter(methodNode, "S2");
	//
	//		localCompositeParameterNode2.setLinkToGlobalParameter(globalCompositeParameterNode1);
	//		
	//		NodeMapper mapper = new NodeMapper();
	//		MethodNode deployedMethodNode = MethodDeployer.deploy(methodNode, mapper);
	//	}


	@Test
	public void deployLinkedRootStructure() {

		// TODO MO-RE use helper methods to create and add nodes

		RootNode rootNode = new RootNode("Root", null);
		CompositeParameterNode gs1 = new CompositeParameterNode("GS1", null);
		BasicParameterNode gs1p1 = new BasicParameterNode("GS1P1", "int", "0", false, null);
		ChoiceNode gs1p1c1 = new ChoiceNode("GS1P1C1", "1");
		BasicParameterNode gs1p2 = new BasicParameterNode("GS1P2", "int", "0", false, null);
		ChoiceNode gs1p2c1 = new ChoiceNode("GS1P1C1", "1");
		ClassNode c1 = new ClassNode("Class", null);
		MethodNode c1m1 = new MethodNode("method", null);
		CompositeParameterNode ms1 = new CompositeParameterNode("MS1", null);

		rootNode.addParameter(gs1);
		gs1.addParameter(gs1p1);
		gs1p1.addChoice(gs1p1c1);
		gs1.addParameter(gs1p2);
		gs1p2.addChoice(gs1p2c1);
		rootNode.addClass(c1);
		c1.addMethod(c1m1);
		c1m1.addParameter(ms1);

		ms1.setLinkToGlobalParameter(gs1);

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode deployedMethod = MethodDeployer.deploy(c1m1, nodeMapper);

		List<BasicParameterNode> parameters = c1m1.getNestedBasicParameters(true);
		List<BasicParameterNode> parametersDeployed = deployedMethod.getNestedBasicParameters(false);

		assertEquals(parameters.size(), parametersDeployed.size());

		assertEquals("GS1P1", parametersDeployed.get(0).getName());
		assertEquals("GS1P2", parametersDeployed.get(1).getName());

		assertNotSame(parametersDeployed.get(0), parameters.get(0));
		assertNotSame(parametersDeployed.get(1), parameters.get(1));

		assertEquals(parameters.get(0), nodeMapper.getSourceNode(parametersDeployed.get(0)));
		assertEquals(parameters.get(1), nodeMapper.getSourceNode(parametersDeployed.get(1)));

		List<ChoiceNode> choices = new ArrayList<>();

		choices.addAll(gs1p1.getChoices());
		choices.addAll(gs1p2.getChoices());

		List<ChoiceNode> choicesDeployed = new ArrayList<>();

		choicesDeployed.addAll(parametersDeployed.get(0).getChoices().stream()
				.map(nodeMapper::getSourceNode)
				.collect(Collectors.toList()));

		choicesDeployed.addAll(parametersDeployed.get(1).getChoices().stream()
				.map(nodeMapper::getSourceNode)
				.collect(Collectors.toList()));

		for (ChoiceNode choice : choices) {
			assertTrue(choicesDeployed.contains(choice));
		}
	}

	@Test
	public void deployLinkedClassStructure() {

		// TODO MO-RE use helper methods to create and add nodes

		RootNode rootNode = new RootNode("Root", null);
		ClassNode classNode = new ClassNode("Class", null);
		CompositeParameterNode globalCompositeGS1 = new CompositeParameterNode("GS1", null);
		BasicParameterNode gs1p1 = new BasicParameterNode("GS1P1", "int", "0", false, null);
		ChoiceNode gs1p1c1 = new ChoiceNode("GS1P1C1", "1");
		BasicParameterNode gs1p2 = new BasicParameterNode("GS1P2", "int", "0", false, null);
		ChoiceNode gs1p2c1 = new ChoiceNode("GS1P2C1", "1");
		MethodNode c1m1 = new MethodNode("method", null);
		CompositeParameterNode ms1 = new CompositeParameterNode("MS1", null);

		rootNode.addClass(classNode);
		classNode.addParameter(globalCompositeGS1);
		globalCompositeGS1.addParameter(gs1p1);
		gs1p1.addChoice(gs1p1c1);
		globalCompositeGS1.addParameter(gs1p2);
		gs1p2.addChoice(gs1p2c1);
		classNode.addMethod(c1m1);
		c1m1.addParameter(ms1);

		ms1.setLinkToGlobalParameter(globalCompositeGS1);

		// Root
		// Class
		//   GS1
		//     GS1P1
		//       GS1P1C1
		//     GS1P2
		//       GS1P2C1
		//     method
		//        MS1->GS1

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode deployedMethod = MethodDeployer.deploy(c1m1, nodeMapper);

		// List<BasicParameterNode> parameters = c1m1.getNestedBasicParameters(true);

		List<ParameterWithLinkingContext> parameters2 = 
				MethodNodeHelper.getNestedBasicParametersWithLinkingContexts(c1m1);

		List<BasicParameterNode> deployedParameters = deployedMethod.getNestedBasicParameters(false);

		assertEquals(parameters2.size(), deployedParameters.size());

		String name1 = deployedParameters.get(0).getName();
		assertEquals("GS1P1", name1);
		assertEquals("GS1P2", deployedParameters.get(1).getName());

		assertNotSame(deployedParameters.get(0), parameters2.get(0));
		assertNotSame(deployedParameters.get(1), parameters2.get(1));

		assertEquals(parameters2.get(0).getParameter(), nodeMapper.getSourceNode(deployedParameters.get(0)));
		assertEquals(parameters2.get(1).getParameter(), nodeMapper.getSourceNode(deployedParameters.get(1)));

		List<ChoiceNode> choices = new ArrayList<>();

		choices.addAll(gs1p1.getChoices());
		choices.addAll(gs1p2.getChoices());

		List<ChoiceNode> choicesDeployed = new ArrayList<>();

		choicesDeployed.addAll(deployedParameters.get(0).getChoices().stream()
				.map(nodeMapper::getSourceNode)
				.collect(Collectors.toList()));

		choicesDeployed.addAll(deployedParameters.get(1).getChoices().stream()
				.map(nodeMapper::getSourceNode)
				.collect(Collectors.toList()));

		for (ChoiceNode choice : choices) {
			assertTrue(choicesDeployed.contains(choice));
		}
	}

	@Test
	public void deployLinkedRootStructureWithConstraint() {

		// TODO MO-RE use helper methods to create and add nodes

		RootNode rootNode = new RootNode("Root", null);
		CompositeParameterNode gs1 = new CompositeParameterNode("GS1", null);
		BasicParameterNode gs1p1 = new BasicParameterNode("GS1P1", "int", "0", false, null);
		ChoiceNode gs1p1c1 = new ChoiceNode("GS1P1C1", "1");
		BasicParameterNode gs1p2 = new BasicParameterNode("GS1P2", "int", "0", false, null);
		ChoiceNode gs1p2c1 = new ChoiceNode("GS1P1C1", "1");
		ClassNode c1 = new ClassNode("Class", null);
		MethodNode c1m1 = new MethodNode("method", null);
		CompositeParameterNode ms1 = new CompositeParameterNode("MS1", null);
		BasicParameterNode mp1 = new BasicParameterNode("MP1", "int", "0", false, null);

		rootNode.addParameter(gs1);
		gs1.addParameter(gs1p1);
		gs1p1.addChoice(gs1p1c1);
		gs1.addParameter(gs1p2);
		gs1p2.addChoice(gs1p2c1);
		rootNode.addClass(c1);
		c1.addMethod(c1m1);
		c1m1.addParameter(ms1);
		c1m1.addParameter(mp1);

		ms1.setLinkToGlobalParameter(gs1);

		RelationStatement r1 = RelationStatement.createRelationStatementWithParameterCondition(
				mp1, null, EMathRelation.EQUAL, gs1p1, null);
		RelationStatement r2 = RelationStatement.createRelationStatementWithParameterCondition(
				gs1p1, null, EMathRelation.EQUAL, mp1, null);

		Constraint m1con1 = new Constraint("Constraint", ConstraintType.EXTENDED_FILTER, r1, r2,null);
		ms1.addConstraint(new ConstraintNode("Constraint", m1con1, null));

		validateConstraint(c1m1, mp1, gs1p1);
	}

	@Test
	public void deployLinkedClassStructureWithConstraint() {

		// TODO MO-RE use helper methods to create and add nodes

		RootNode rootNode = new RootNode("Root", null);
		ClassNode c1 = new ClassNode("Class", null);
		CompositeParameterNode gs1 = new CompositeParameterNode("GS1", null);
		BasicParameterNode gs1p1 = new BasicParameterNode("GS1P1", "int", "0", false, null);
		ChoiceNode gs1p1c1 = new ChoiceNode("GS1P1C1", "1");
		BasicParameterNode gs1p2 = new BasicParameterNode("GS1P2", "int", "0", false, null);
		ChoiceNode gs1p2c1 = new ChoiceNode("GS1P1C1", "1");
		MethodNode c1m1 = new MethodNode("method", null);
		CompositeParameterNode ms1 = new CompositeParameterNode("MS1", null);
		BasicParameterNode mp1 = new BasicParameterNode("MP1", "int", "0", false, null);

		rootNode.addClass(c1);
		c1.addParameter(gs1);
		gs1.addParameter(gs1p1);
		gs1p1.addChoice(gs1p1c1);
		gs1.addParameter(gs1p2);
		gs1p2.addChoice(gs1p2c1);
		c1.addMethod(c1m1);
		c1m1.addParameter(ms1);
		c1m1.addParameter(mp1);

		ms1.setLinkToGlobalParameter(gs1);

		RelationStatement r1 = RelationStatement.createRelationStatementWithParameterCondition(
				mp1, null, EMathRelation.EQUAL, gs1p1, null);
		RelationStatement r2 = RelationStatement.createRelationStatementWithParameterCondition(
				gs1p1, null, EMathRelation.EQUAL, mp1, null);

		Constraint m1con1 = new Constraint("Constraint", ConstraintType.EXTENDED_FILTER, r1, r2,null);
		ms1.addConstraint(new ConstraintNode("Constraint", m1con1, null));

		validateConstraint(c1m1, mp1, gs1p1);
	}

	@Test
	public void deployNestedStructureWithConstraint1() {

		// TODO MO-RE use helper methods to create and add nodes

		RootNode rootNode = new RootNode("Root", null);
		ClassNode c1 = new ClassNode("Class", null);
		MethodNode c1m1 = new MethodNode("method", null);
		CompositeParameterNode ms1 = new CompositeParameterNode("MS1", null);
		BasicParameterNode ms1p1 = new BasicParameterNode("MS1P1", "int", "0", false, null);
		ChoiceNode ms1p1c1 = new ChoiceNode("MS1P1C1", "1");
		CompositeParameterNode ms2 = new CompositeParameterNode("MS2", null);
		BasicParameterNode ms2p1 = new BasicParameterNode("MS2P1", "int", "0", false, null);
		ChoiceNode ms2p1c1 = new ChoiceNode("MS2P1C1", "2");
		BasicParameterNode mp1 = new BasicParameterNode("MP1", "int", "0", false, null);

		rootNode.addClass(c1);
		c1.addMethod(c1m1);
		c1m1.addParameter(ms1);
		ms1.addParameter(ms1p1);
		ms1p1.addChoice(ms1p1c1);
		c1m1.addParameter(ms2);
		ms2.addParameter(ms2p1);
		ms2p1.addChoice(ms2p1c1);
		c1m1.addParameter(mp1);

		RelationStatement r1 = RelationStatement.createRelationStatementWithParameterCondition(
				ms1p1, null, EMathRelation.EQUAL, ms2p1, null);
		RelationStatement r2 = RelationStatement.createRelationStatementWithChoiceCondition(
				ms2p1, null, EMathRelation.EQUAL, ms2p1c1);

		Constraint m1con1 = new Constraint("Constraint", ConstraintType.EXTENDED_FILTER, r1, r2,null);
		ms1.addConstraint(new ConstraintNode("Constraint", m1con1, null));

		validateConstraint(c1m1, ms1p1, ms2p1, ms2p1c1);
	}

	@Test
	public void deployNestedStructureWithConstraint2() {

		// TODO MO-RE use helper methods to create and add nodes

		RootNode rootNode = new RootNode("Root", null);
		ClassNode c1 = new ClassNode("Class", null);
		MethodNode c1m1 = new MethodNode("method", null);
		CompositeParameterNode ms1 = new CompositeParameterNode("MS1", null);
		CompositeParameterNode ms1s1 = new CompositeParameterNode("MS1S1", null);
		BasicParameterNode ms1s1p1 = new BasicParameterNode("MS1S1P1", "int", "0", false, null);
		ChoiceNode ms1s1p1c1 = new ChoiceNode("MS1S1P1C1", "1");
		CompositeParameterNode ms1s2 = new CompositeParameterNode("MS1S2", null);
		BasicParameterNode ms1s2p1 = new BasicParameterNode("MS1S2P1", "int", "0", false, null);
		ChoiceNode ms1s2p1c1 = new ChoiceNode("MS1S2P1C1", "2");
		BasicParameterNode mp1 = new BasicParameterNode("MP1", "int", "0", false, null);

		rootNode.addClass(c1);
		c1.addMethod(c1m1);
		c1m1.addParameter(ms1);
		ms1.addParameter(ms1s1);
		ms1s1.addParameter(ms1s1p1);
		ms1s1p1.addChoice(ms1s1p1c1);
		ms1.addParameter(ms1s2);
		ms1s2.addParameter(ms1s2p1);
		ms1s2p1.addChoice(ms1s2p1c1);
		c1m1.addParameter(mp1);

		RelationStatement r1 = RelationStatement.createRelationStatementWithParameterCondition(
				ms1s1p1, null, EMathRelation.EQUAL, ms1s2p1, null);
		RelationStatement r2 = RelationStatement.createRelationStatementWithChoiceCondition(
				ms1s2p1, null, EMathRelation.EQUAL, ms1s2p1c1);

		Constraint m1con1 = new Constraint("Constraint", ConstraintType.EXTENDED_FILTER, r1, r2,null);
		ms1s1.addConstraint(new ConstraintNode("Constraint", m1con1, null));

		validateConstraint(c1m1, ms1s1p1, ms1s2p1, ms1s2p1c1);
	}

	private void validateConstraint(MethodNode method, AbstractNode... references) {
		NodeMapper nodeMapper = new NodeMapper();
		MethodNode methodDeployed = MethodDeployer.deploy(method, nodeMapper);

		assertEquals(1, methodDeployed.getConstraintNodes().size());

		ConstraintNode constraint = methodDeployed.getConstraintNodes().get(0);

		List<AbstractNode> nodesSource = new ArrayList<>();

		for (BasicParameterNode parameter : method.getNestedBasicParameters(true)) {
			nodesSource.add(parameter);
			nodesSource.addAll(parameter.getAllChoices());
		}

		List<AbstractNode> nodesDeployed = new ArrayList<>();

		for (BasicParameterNode parameter : methodDeployed.getParametersAsBasic()) {
			nodesDeployed.add(parameter);
			nodesDeployed.addAll(parameter.getAllChoices());
		}

		Set<AbstractNode> nodesConstraint = new HashSet<>();
		nodesConstraint.addAll(constraint.getConstraint().getReferencedParameters().stream().map(e -> (AbstractNode) e).collect(Collectors.toSet()));
		nodesConstraint.addAll(constraint.getConstraint().getReferencedChoices().stream().map(e -> (AbstractNode) e).collect(Collectors.toSet()));

		// The deployed constraint must contain only nodes included in the deployed method.

		for (AbstractNode parameter : nodesConstraint) {
			assertTrue(nodesDeployed.contains(parameter));
			assertFalse(nodesSource.contains(parameter));
		}

		Set<AbstractNode> nodesConstraintMapped = nodesConstraint.stream()
				.map(nodeMapper::getSourceNode)
				.collect(Collectors.toSet());

		// The deployed (and reversed) constraint must contain only nodes included in the source method.

		for (AbstractNode parameter : nodesConstraintMapped) {
			assertTrue(nodesSource.contains(parameter));
			assertFalse(nodesDeployed.contains(parameter));
		}

		// The deployed (and reversed) constraint must contain only specific nodes included in the source constraint.

		assertEquals(nodesConstraintMapped.size(), references.length);

		for (AbstractNode reference : references) {
			assertTrue(nodesConstraintMapped.contains(reference));
		}
	}

	private MethodNode createModelWithTwoBasicLinkedParametersOneAtMethodLevel(String parameter1Name, String parameter2Name) {

		RootNode rootNode = new RootNode("Root", null);

		// add global parameter of root and choice node

		BasicParameterNode globalParameterNodeOfRoot = 
				RootNodeHelper.addNewBasicParameterToRoot(rootNode, "RP1", "String", "", true, null);

		String globalChoiceNodeName = "RC11";

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				globalParameterNodeOfRoot, globalChoiceNodeName, "100", false, false, null);

		// add class node

		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		// add method node

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "Method", true, null);

		BasicParameterNode basicParameterNodeOfMethod = 
				new BasicParameterNode(
						parameter1Name, "String", "", false,	globalParameterNodeOfRoot,	null);

		methodNode.addParameter(basicParameterNodeOfMethod);

		// add composite parameter

		CompositeParameterNode compositeParameterNode = 
				ParametersAndConstraintsParentNodeHelper.addNewCompositeParameterToParent(methodNode, "S1");

		// add linked basic parameter to composite parameter

		BasicParameterNode basicParameterNodeOfCompositeParam = 
				new BasicParameterNode(
						parameter2Name, "String", "", false,	globalParameterNodeOfRoot,	null);

		compositeParameterNode.addParameter(basicParameterNodeOfCompositeParam);

		// Root
		//   RP1 : String
		//     RC11
		//   Class
		//     Method
		//       [parameter1]->RP1 
		//       S1
		//         [parameter1]->RP1

		return methodNode;
	}

	@Test
	public void checkDeployedParametersWithConflictingParameterNames() {

		RootNode rootNode = new RootNode("model", null, ModelVersionDistributor.getCurrentSoftwareVersion());

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		MethodNodeHelper.addNewBasicParameter(methodNode, "LP", "int", "0", true, null);

		CompositeParameterNode compositeParameterNode = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode, "LC", true, null);

		CompositeParameterNodeHelper.addNewBasicParameterToComposite(
				compositeParameterNode, "LP", "int", "0", true, null);

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode deployedMethodNode = MethodDeployer.deploy(methodNode, nodeMapper);

		List<ParameterWithLinkingContext> parametersWithContextFromDeployedMethod = 
				deployedMethodNode.getParametersWithLinkingContexts();

		ParameterWithLinkingContext parameterWithContext11 = parametersWithContextFromDeployedMethod.get(0);
		assertNull(parameterWithContext11.getLinkingContext());
		assertEquals(deployedMethodNode, parameterWithContext11.getParameter().getParent());

		ParameterWithLinkingContext parameterWithContext12 = parametersWithContextFromDeployedMethod.get(1);
		assertNull(parameterWithContext12.getLinkingContext());
		assertEquals(deployedMethodNode, parameterWithContext12.getParameter().getParent());

		MethodDeployer.copyDeployedParametersWithConversionToOriginals(deployedMethodNode, methodNode, nodeMapper);

		List<ParameterWithLinkingContext> originalDeployedParameters = 
				methodNode.getDeployedParametersWithLinkingContexts();

		ParameterWithLinkingContext parameterWithContext21 = originalDeployedParameters.get(0);
		assertNull(parameterWithContext21.getLinkingContext());
		assertEquals(methodNode, parameterWithContext21.getParameter().getParent());

		ParameterWithLinkingContext parameterWithContext22 = originalDeployedParameters.get(1);
		assertNull(parameterWithContext22.getLinkingContext());
		assertEquals(compositeParameterNode, parameterWithContext22.getParameter().getParent());
	}

	//	private MethodNode createModelWithTwoLinkedParametersInCompositeParameters(String parameter1Name, String parameter2Name) {
	//
	//		RootNode rootNode = new RootNode("Root", null);
	//
	//		// add global parameter of root and choice node
	//
	//		final String parameterType = "String";
	//
	//		String globalParameterName = "RP1";
	//
	//		BasicParameterNode globalParameterNodeOfRoot = 
	//				RootNodeHelper.addGlobalBasicParameterToRoot(rootNode, globalParameterName, parameterType, null);
	//
	//		String globalChoiceNodeName = "RC11";
	//
	//		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
	//				globalParameterNodeOfRoot, globalChoiceNodeName, "100", null);
	//
	//		// add class node
	//
	//		ClassNode classNode = new ClassNode("Class", null);
	//		rootNode.addClass(classNode);
	//
	//		// add method node
	//
	//		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);
	//
	//		// add composite parameter 1 and linked parameter 
	//
	//		CompositeParameterNode compositeParameterNode1 = 
	//				ParametersAndConstraintsParentNodeHelper.addCompositeParameterToMethod(methodNode, "S1");
	//
	//		// add linked basic parameter to composite parameter
	//
	//		BasicParameterNode basicParameter1 = 
	//				new BasicParameterNode(
	//						parameter1Name, parameterType, "", false,	globalParameterNodeOfRoot,	null);
	//
	//		compositeParameterNode1.addParameter(basicParameter1);
	//
	//		// add composite parameter 2 and linked parameter 
	//
	//		CompositeParameterNode compositeParameterNode2 = 
	//				ParametersAndConstraintsParentNodeHelper.addCompositeParameterToMethod(methodNode, "S2");
	//
	//		// add linked basic parameter to composite parameter
	//
	//		BasicParameterNode basicParameter2 = 
	//				new BasicParameterNode(
	//						parameter2Name, parameterType, "", false,	globalParameterNodeOfRoot,	null);
	//
	//		compositeParameterNode2.addParameter(basicParameter2);
	//
	//		// add not linked parameter
	//
	//		BasicParameterNode basicParameter3 = 
	//				new BasicParameterNode(
	//						"OTHER", parameterType, "", false,	null,	null);
	//
	//		compositeParameterNode2.addParameter(basicParameter3);
	//
	//		return methodNode;
	//	}

	// TODO MO-RE create a test with multiple linking to the same global parameter, model xmlExpectedValue2 is from SatSolverConstraintEvaluatorTest  	
	//	@Test
	//	public void TestExpectedValue2() {
	//		assertEquals(4, countGeneratedTestCases(xmlExpectedValue2));
	//	}
	//
	//	private int countGeneratedTestCases(String xmlModel) {
	//
	//		IAlgorithm<ChoiceNode> algorithm = new CartesianProductAlgorithm<>();
	//
	//		NodeMapper mapper = new NodeMapper();
	//
	//		MethodNode method1 = getMethod(xmlModel);
	//		String descr1 = (method1.getParametersWithLinkingContexts()).toString();
	//		
	//		MethodNode method2 = MethodDeployer.deploy(method1, mapper);
	//		String descr2 = (method2.getParametersWithLinkingContexts()).toString();
	//		
	//		assertEquals(descr1, descr2);
	//
	//		// checking parameter 1 in map
	//
	//		BasicParameterNode sourceParameter1 = (BasicParameterNode) method1.getParameter(0);
	//		int hashSource1 = sourceParameter1.hashCode();
	//		int hashSourceLink1 = sourceParameter1.getLinkToGlobalParameter().hashCode();
	//
	//		BasicParameterNode deployedParameter1 = mapper.getDeployedNode(sourceParameter1);
	//		int hashDeployed1 = deployedParameter1.hashCode();
	//		int hashDeployedLink1 = deployedParameter1.getLinkToGlobalParameter().hashCode();
	//
	//		IParametersParentNode sourceParameter1Parent = sourceParameter1.getParent();
	//		assertEquals(method1, sourceParameter1Parent);
	//		
	//		IParametersParentNode deployedParameter1Parent = deployedParameter1.getParent();
	//		assertEquals(method2, deployedParameter1Parent);
	//		
	//		assertEquals(sourceParameter1.getName(), deployedParameter1.getName());
	//		assertNotEquals(hashSource1, hashDeployed1);
	//		assertEquals(hashSourceLink1, hashDeployedLink1);
	//
	//		// checking parameter 2 in map
	//
	//		BasicParameterNode sourceParameter2 = (BasicParameterNode) method1.getParameter(1);
	//		int hashSource2 = sourceParameter2.hashCode();
	//		int hashSourceLink2 = sourceParameter2.getLinkToGlobalParameter().hashCode();
	//
	//		BasicParameterNode deployedParameter2 = mapper.getDeployedNode(sourceParameter2);
	//		int hashDeployed2 = deployedParameter2.hashCode();
	//		int hashDeployedLink2 = deployedParameter2.getLinkToGlobalParameter().hashCode();
	//
	//		IParametersParentNode sourceParameter2Parent = sourceParameter2.getParent();
	//		assertEquals(method1, sourceParameter2Parent);
	//		IParametersParentNode deployedParameter2Parent = deployedParameter2.getParent();
	//		assertEquals(method2, deployedParameter2Parent);
	//		
	//		assertEquals(sourceParameter2.getName(), deployedParameter2.getName());
	//		assertNotEquals(hashSource2, hashDeployed2);
	//		assertEquals(hashSourceLink2, hashDeployedLink2);
	//
	//		Constraint deployedConstraint = method2.getConstraintNodes().get(0).getConstraint();
	//		RelationStatement deployedPrecondition = (RelationStatement) deployedConstraint.getPrecondition();
	//		BasicParameterNode basicPar1 = deployedPrecondition.getLeftParameter();
	//		IParametersParentNode parent1 = basicPar1.getParent();
	//		assertEquals(method2, parent1);
	//
	//		int count1 = countGeneratedTestCases(method1, algorithm);
	//		int count2 = countGeneratedTestCases(method2, algorithm);
	//		assertEquals(count2, count1);
	//
	//		return count1;
	//	}

	// TODO MO-RE 
	//	@Test
	//	public void AAATestDeployWithExpectedValue() {
	//
	//		MethodNode sourceMethod = new MethodNode("method");
	//
	//		BasicParameterNode basicParameterNode = 
	//				MethodNodeHelper.addNewBasicParameter(sourceMethod, "p1", "String", "A", null);
	//		basicParameterNode.setExpected(true);
	//
	//		RelationStatement precondition =
	//				RelationStatement.createRelationStatementWithValueCondition(
	//						basicParameterNode, null, EMathRelation.EQUAL, "X");
	//
	//		AssignmentStatement postcondition = 
	//				AssignmentStatement.createAssignmentWithValueCondition(basicParameterNode, "B");
	//
	//		Constraint constraint = new Constraint("Constraint", ConstraintType.ASSIGNMENT, precondition, postcondition,null);
	//		sourceMethod.addConstraint(new ConstraintNode("Constraint", constraint, null));
	//
	//		String sourceSignature = MethodNodeHelper.createSignatureNewStandard(sourceMethod, new ExtLanguageManagerForJava());
	//		System.out.println(sourceSignature);
	//
	//		NodeMapper nodeMapper = new NodeMapper();
	//		MethodNode deployedMethod = MethodDeployer.deploy(sourceMethod, nodeMapper);
	//
	//		String destinationSignature = MethodNodeHelper.createSignatureNewStandard(deployedMethod, new ExtLanguageManagerForJava());
	//		System.out.println(destinationSignature);
	//
	//		Constraint constraint1 = deployedMethod.getConstraintNodes().get(0).getConstraint();
	//
	//		// check parameter parent from precondition
	//
	//		RelationStatement relationStatement1 = (RelationStatement) constraint1.getPrecondition();
	//		BasicParameterNode basicPar1 = relationStatement1.getLeftParameter();
	//		IParametersParentNode parent1 = basicPar1.getParent();
	//
	//		assertEquals(deployedMethod, parent1);
	//
	//		// check parameter parent from postcondition
	//
	//		AbstractStatement postcondition2 = constraint1.getPostcondition();
	//		AssignmentStatement assignmentStatement2 = (AssignmentStatement) postcondition2;
	//		BasicParameterNode basicPar2 = assignmentStatement2.getLeftParameter();
	//		IParametersParentNode parent2 = basicPar2.getParent();
	//
	//		assertEquals(deployedMethod, parent2);
	//	}

}
