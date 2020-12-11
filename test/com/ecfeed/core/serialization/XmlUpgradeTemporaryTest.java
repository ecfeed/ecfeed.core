/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.serialization;

import com.ecfeed.core.model.*;
import com.ecfeed.core.model.serialization.*;
import com.ecfeed.core.testutils.ModelStringifier;
import com.ecfeed.core.testutils.RandomModelGenerator;
import com.ecfeed.core.type.adapter.JavaPrimitiveTypePredicate;
import com.ecfeed.core.utils.EMathRelation;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.ecfeed.core.testutils.TestUtilConstants.SUPPORTED_TYPES;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

public class XmlUpgradeTemporaryTest {

	@Test
	public void generateFilteringConstraintExampleInVersion3() {

		RootNode root = new RootNode("root", null, 3);

		ClassNode classNode = new ClassNode("classNode", null);
		root.addClass(classNode);

		MethodNode methodNode = new MethodNode("method", null);
		classNode.addMethod(methodNode);

		// method parameter 1 node with choice

		MethodParameterNode methodParameterNode1 = new MethodParameterNode(
				"par1",
				"int",
				"1",
				false,
				null);

		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode11 = new ChoiceNode("choice11", "11",  null);
		methodParameterNode1.addChoice(choiceNode11);

		// method parameter 2 node with choice

		MethodParameterNode methodParameterNode2 = new MethodParameterNode(
				"par2",
				"int",
				"2",
				false,
				null);

		methodNode.addParameter(methodParameterNode2);

		ChoiceNode choiceNode21 = new ChoiceNode("choice21", "21",  null);
		methodParameterNode2.addChoice(choiceNode21);

		// constraint

		AbstractStatement precondition =
				RelationStatement.createStatementWithChoiceCondition(methodParameterNode1, EMathRelation.EQUAL, choiceNode11);


		AbstractStatement postcondition =
				RelationStatement.createStatementWithChoiceCondition(methodParameterNode2, EMathRelation.EQUAL, choiceNode21);

		Constraint constraint = new Constraint(
				"constraint",
				null,
				precondition,
				postcondition);

		ConstraintNode constraintNode = new ConstraintNode("cn", constraint, null);
		methodNode.addConstraint(constraintNode);

		// serializing to stream

		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		ModelSerializer serializer = new ModelSerializer(ostream, 3);

		try {
			serializer.serialize(root);
		} catch (Exception e) {
			fail();
		}

		System.out.println(ostream.toString());
	}

	@Test
	public void generateExpectedValueConstraintExampleInVersion3() {

		RootNode root = new RootNode("root", null, 3);

		ClassNode classNode = new ClassNode("classNode", null);
		root.addClass(classNode);

		MethodNode methodNode = new MethodNode("method", null);
		classNode.addMethod(methodNode);

		// method parameter 1 node with choice

		MethodParameterNode methodParameterNode1 = new MethodParameterNode(
				"par1",
				"int",
				"1",
				true,
				null);

		methodNode.addParameter(methodParameterNode1);

		ChoiceNode choiceNode11 = new ChoiceNode("choice11", "11",  null);
		methodParameterNode1.addChoice(choiceNode11);

		// constraint

		AbstractStatement precondition = RelationStatement.createStatementWithChoiceCondition(methodParameterNode1, EMathRelation.EQUAL, choiceNode11);

		ChoiceNode choiceNodeForPostcondition = new ChoiceNode("expected", "5", null);

		AbstractStatement postcondition =
				new ExpectedValueStatement(methodParameterNode1, choiceNodeForPostcondition, new JavaPrimitiveTypePredicate());

		Constraint constraint = new Constraint(
				"constraint",
				null,
				precondition,
				postcondition);

		ConstraintNode constraintNode = new ConstraintNode("cn", constraint, null);
		methodNode.addConstraint(constraintNode);

		// serializing to stream

		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		ModelSerializer serializer = new ModelSerializer(ostream, 3);

		try {
			serializer.serialize(root);
		} catch (Exception e) {
			fail();
		}

		//System.out.println(ostream.toString());
	}


}
