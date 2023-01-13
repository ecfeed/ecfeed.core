/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.type.adapter.TypeAdapterProviderForJava;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;

public class GenericRemoveNodesOperationTest {

	@Test
	public void removeClass() {

		RootNode rootNode = new RootNode("Root", null);
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);

		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(classNode);

		GenericRemoveNodesOperation genericRemoveNodesOperation = createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation.execute();

		List<ClassNode> classNodes = rootNode.getClasses();

		assertTrue(classNodes.isEmpty());
		
		genericRemoveNodesOperation.getReverseOperation().execute();
		classNodes = rootNode.getClasses();
		
		assertEquals(1, classNodes.size());
		assertEquals(classNode, classNodes.get(0));
	}

	@Test
	public void removeMethods() {

		RootNode rootNode = new RootNode("Root", null);
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);
		
		MethodNode methodNode1 = new MethodNode("Method1");
		classNode.addMethod(methodNode1);
		MethodNode methodNode2 = new MethodNode("Method2");
		classNode.addMethod(methodNode2);

		// removing the first method
		
		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(methodNode1);

		GenericRemoveNodesOperation genericRemoveNodesOperation1 = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation1.execute();

		List<MethodNode> methodNodes = classNode.getMethods();

		assertEquals(1, methodNodes.size());
		
		nodesToDelete.clear();
		nodesToDelete.add(methodNode2);
		
		GenericRemoveNodesOperation genericRemoveNodesOperation2 = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation2.execute();
		
		assertEquals(0, methodNodes.size());
	}

	@Test
	public void removeMethodBasicParameter() {

		RootNode rootNode = new RootNode("Root", null);
		ClassNode classNode = new ClassNode("Class", null);
		rootNode.addClass(classNode);
		MethodNode methodNode = new MethodNode("Method");
		classNode.addMethod(methodNode);
		BasicParameterNode basicParameterNode = 
				new BasicParameterNode(
						"BasicParam", "String", "", false, null);
		methodNode.addParameter(basicParameterNode);
		
		List<IAbstractNode> nodesToDelete = new ArrayList<>();
		nodesToDelete.add(basicParameterNode);
		
		// constraint
		ConstraintNode constraintNode = createConstraintNodeWithValuePostcondition(basicParameterNode,"ABC");
		methodNode.addConstraint(constraintNode);
		assertFalse(methodNode.getConstraintNodes().isEmpty());

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				createRemovingNodesOperation(nodesToDelete, rootNode);
		genericRemoveNodesOperation.execute();

		List<AbstractParameterNode> parameterNodes = methodNode.getParameters();
		assertTrue(parameterNodes.isEmpty());
		
		List<ConstraintNode> constraintNodes = methodNode.getConstraintNodes();
		assertTrue(constraintNodes.isEmpty());
	}
	
	private ConstraintNode createConstraintNodeWithValuePostcondition(
			BasicParameterNode basicParameterNode, String value) {
		
		StaticStatement staticStatement = new StaticStatement(EvaluationResult.TRUE);

		RelationStatement relationStatement2 = 
				RelationStatement.createRelationStatementWithValueCondition(
						basicParameterNode, EMathRelation.EQUAL, value);

		Constraint constraint = new Constraint(
				"constraint", 
				ConstraintType.EXTENDED_FILTER, 
				staticStatement, 
				relationStatement2, 
				null);

		ConstraintNode constraintNode = new ConstraintNode("constraintNode", constraint, null);
		return constraintNode;
	}

	private GenericRemoveNodesOperation createRemovingNodesOperation(
			List<IAbstractNode> nodesToDelete, 
			IAbstractNode nodeToBeSelectedAfterOperation) {

		GenericRemoveNodesOperation genericRemoveNodesOperation = 
				new GenericRemoveNodesOperation(
						nodesToDelete, 
						new TypeAdapterProviderForJava(), 
						true, 
						nodeToBeSelectedAfterOperation, 
						nodeToBeSelectedAfterOperation, 
						new ExtLanguageManagerForJava());

		return genericRemoveNodesOperation;
	}

}
