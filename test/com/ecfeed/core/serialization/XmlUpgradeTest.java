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

import com.ecfeed.core.evaluator.DummyEvaluator;
import com.ecfeed.core.generators.GeneratorValue;
import com.ecfeed.core.generators.RandomGenerator;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IGeneratorValue;
import com.ecfeed.core.model.*;
import com.ecfeed.core.model.serialization.ModelParser;
import com.ecfeed.core.model.serialization.ModelSerializer;
import com.ecfeed.core.model.serialization.ParserException;
import com.ecfeed.core.model.serialization.SerializationConstants;
import com.ecfeed.core.type.adapter.JavaPrimitiveTypePredicate;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.SimpleProgressMonitor;
import com.ecfeed.core.utils.StringHelper;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class XmlUpgradeTest {

	@Test
	public void upgradeFromVersion3To4Test() {

		// TODO CONSTRAINTS-NEW get xml from version 3, parse it and, serialize in version 4 and check xml.

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

		AbstractStatement precondition = new StaticStatement(true, null);

		ChoiceNode choiceNodeForPostcondition = new ChoiceNode("expected", "5", null);

		AbstractStatement postcondition =
				new ExpectedValueStatement(methodParameterNode1, choiceNodeForPostcondition, new JavaPrimitiveTypePredicate());

		// constraint

		Constraint constraint = new Constraint(
				"constraint",
				ConstraintType.EXTENDED_FILTER,
				precondition,
				postcondition,
				null);

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

		// System.out.println(ostream.toString());

		// parsing from stream

		ByteArrayInputStream istream = new ByteArrayInputStream(ostream.toByteArray());

		ModelParser parser = new ModelParser();

		RootNode parsedModel = null;
		ArrayList<String> errorList = new ArrayList<>();
		try {
			parsedModel = parser.parseModel(istream, null, errorList);
		} catch (ParserException e) {
			fail();
		}

		if (errorList.size() > 0) {
			fail();
		}

//		ModelLogger.printModel("AfterTest", parsedModel);
	}
}
