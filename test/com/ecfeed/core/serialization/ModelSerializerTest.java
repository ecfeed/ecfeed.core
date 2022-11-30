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

import static com.ecfeed.core.testutils.ModelTestUtils.assertElementsEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.ecfeed.core.model.*;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.ModelNodeType;
import org.junit.Test;

import com.ecfeed.core.model.serialization.ModelParser;
import com.ecfeed.core.model.serialization.ModelSerializer;
import com.ecfeed.core.testutils.RandomModelGenerator;
import com.ecfeed.core.utils.EMathRelation;

public class ModelSerializerTest {

	RandomModelGenerator fGenerator = new RandomModelGenerator();

	@Test
	public void modelSerializerTest() {
		for (int version = 1; version <= ModelVersionDistributor.getCurrentSoftwareVersion(); version++) {
			modelSerializerTest(version);
		}
	}

	private void modelSerializerTest(int version) { 
		RootNode model = new RootNode("model", null, version);

		model.addClass(new ClassNode("com.example.TestClass1", null));
		model.addClass(new ClassNode("com.example.TestClass2", null));
		model.addParameter(new BasicParameterNode("globalParameter1", "int", null));
		model.addParameter(new BasicParameterNode("globalParameter2", "com.example.UserType", null));

		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		ModelSerializer serializer = new ModelSerializer(ostream, version);
		try {
			serializer.serialize(model);

			InputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			ModelParser parser = new ModelParser();
			RootNode parsedModel = parser.parseModel(istream, null, new ListOfStrings());

			assertElementsEqual(model, parsedModel);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void classSerializerTestWithoutAndroidBaseRunner(){
		classSerializerTest(false, null, 1);
	}

	@Test
	public void classSerializerTest() {
		for (int version = 1; version <= ModelVersionDistributor.getCurrentSoftwareVersion(); version++) {
			classSerializerTest(false, null, version);
		}
	}

	private void classSerializerTest(boolean runOnAndroid, String androidBaseRunner, int version){
		ClassNode classNode = new ClassNode("com.example.TestClass", null, runOnAndroid, androidBaseRunner);
		classNode.addMethod(new MethodNode("testMethod1", null));
		classNode.addMethod(new MethodNode("testMethod2", null));
		classNode.addParameter(new BasicParameterNode("parameter1", "int", null));
		classNode.addParameter(new BasicParameterNode("parameter2", "float", null));
		classNode.addParameter(new BasicParameterNode("parameter3", "com.example.UserType", null));

		RootNode model = new RootNode("model", null, version);
		model.addClass(classNode);

		OutputStream ostream = new ByteArrayOutputStream();
		ModelSerializer serializer = new ModelSerializer(ostream, version);
		try {
			serializer.serialize(model);
			InputStream istream = new ByteArrayInputStream(((ByteArrayOutputStream)ostream).toByteArray());
			ModelParser parser = new ModelParser();
			RootNode parsedModel = parser.parseModel(istream, null, new ListOfStrings());

			assertElementsEqual(model, parsedModel);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void classSerializerTestVersion5() {
		int version = 5;

		RootNode model = createModelComposite(version);

		OutputStream ostream = new ByteArrayOutputStream();
		ModelSerializer serializer = new ModelSerializer(ostream, version);

		try {
			serializer.serialize(model);
			InputStream istream = new ByteArrayInputStream(((ByteArrayOutputStream)ostream).toByteArray());
			ModelParser parser = new ModelParser();
			RootNode parsedModel = parser.parseModel(istream, null, new ListOfStrings());

			assertElementsEqual(model, parsedModel);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	@Test
	public void wrongTypeStreamTest(){
		int version = ModelVersionDistributor.getCurrentSoftwareVersion();
		RootNode r = new RootNode("model", null, version);

		OutputStream ostream = new ByteArrayOutputStream();
		ModelSerializer serializer = new ModelSerializer(ostream, version);
		try {
			serializer.serialize(r);
			InputStream istream = new ByteArrayInputStream(((ByteArrayOutputStream)ostream).toByteArray());
			ModelParser parser = new ModelParser();
			parser.parseClass(istream, new ListOfStrings());
			fail("Exception expected");
		} catch (Exception e) {
			//			System.out.println("Exception caught: " + e.getMessage());
		}
	}

	private RootNode createModel(int version) {

		ChoiceNode choice = new ChoiceNode("choice", "0", null);

		BasicParameterNode parameter = new BasicParameterNode("parameter", "int", "0", false, null);
		parameter.addChoice(choice);

		MethodNode methodNode = new MethodNode("testMethod1", null);
		methodNode.addParameter(parameter);

		Constraint constraint = new Constraint(
				"constraint",
				ConstraintType.EXTENDED_FILTER,
				RelationStatement.createRelationStatementWithChoiceCondition(parameter, EMathRelation.EQUAL, choice), RelationStatement.createRelationStatementWithChoiceCondition(parameter, EMathRelation.EQUAL, choice), null
        );

		ConstraintNode constraintNode = new ConstraintNode("name1", constraint, null);
		methodNode.addConstraint(constraintNode);

		ClassNode classNode = new ClassNode("com.example.TestClass", null, false, null);
		classNode.addMethod(methodNode);

		RootNode model = new RootNode("model", null, version);
		model.addClass(classNode);
		model.setVersion(version);

		return model;
	}

	private RootNode createModelComposite(int version) {
		RootNode model = new RootNode("model", null, version);
		BasicParameterNode p1 = RootNodeHelper.addGlobalParameterToRoot(model, "P1", "int", null);
		p1.addChoice(new ChoiceNode("P1C1", "1"));
		p1.addChoice(new ChoiceNode("P1C2", "2"));
		p1.addChoice(new ChoiceNode("P1C3", "3"));

		CompositeParameterNode p2 = new CompositeParameterNode("P2", null);
		BasicParameterNode p21 = MethodNodeHelper.addParameterToMethod(p2, "P21", "int");
		p21.addChoice(new ChoiceNode("P21C1", "1"));
		p21.addChoice(new ChoiceNode("P21C2", "2"));
		p21.addChoice(new ChoiceNode("P21C3", "3"));
		BasicParameterNode p22 = MethodNodeHelper.addParameterToMethod(p2, "P22", "int");
		p22.addChoice(new ChoiceNode("P22C1", "1"));
		p22.addChoice(new ChoiceNode("P22C2", "2"));
		p22.addChoice(new ChoiceNode("P22C3", "3"));

		ClassNode c1 = new ClassNode("Class", null);

		BasicParameterNode c1p1 = ClassNodeHelper.addGlobalParameterToClass(c1, "C1P1", "int", null);
		c1p1.addChoice(new ChoiceNode("C1P1C1", "1"));
		c1p1.addChoice(new ChoiceNode("C1P1C2", "2"));
		c1p1.addChoice(new ChoiceNode("C1P1C3", "3"));

		CompositeParameterNode c1p2 = new CompositeParameterNode("C1P2", null);
		BasicParameterNode c1p21 = MethodNodeHelper.addParameterToMethod(c1p2, "C1P21", "int");
		c1p21.addChoice(new ChoiceNode("C1P21C1", "1"));
		c1p21.addChoice(new ChoiceNode("C1P21C2", "2"));
		c1p21.addChoice(new ChoiceNode("C1P21C3", "3"));
		BasicParameterNode c1p22 = MethodNodeHelper.addParameterToMethod(c1p2, "C1P22", "int");
		c1p22.addChoice(new ChoiceNode("C1P22C1", "1"));
		c1p22.addChoice(new ChoiceNode("C1P22C2", "2"));
		c1p22.addChoice(new ChoiceNode("C1P22C3", "3"));

		MethodNode m1 = ClassNodeHelper.addMethodToClass(c1, "Method1", null);

		BasicParameterNode m1p1 = new BasicParameterNode("M1P1", "int", "0", false);
		ChoiceNode m1p1c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p1, "M1P1C1", "1");
		ChoiceNode m1p1c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p1, "M1P1C2", "2");
		ChoiceNode m1p1c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p1, "M1P1C3", "3");

		CompositeParameterNode m1p2 = new CompositeParameterNode("M1P2", null);
		BasicParameterNode m1p21 = MethodNodeHelper.addParameterToMethod(m1p2, "M1P21", "int");
		ChoiceNode m1p21c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p21, "M1P21C1", "1");
		ChoiceNode m1p21c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p21, "M1P21C2", "2");
		ChoiceNode m1p21c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p21, "M1P21C3", "3");
		BasicParameterNode m1p22 = MethodNodeHelper.addParameterToMethod(m1p2, "M1P22", "int");
		ChoiceNode m1p22c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p22, "M1P22C1", "1");
		ChoiceNode m1p22c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p22, "M1P22C2", "2");
		ChoiceNode m1p22c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p22, "M1P22C3", "3");

		BasicParameterNode m1p3 = new BasicParameterNode("M1P3", "int", "0", false);
		ChoiceNode m1p3c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p3, "M1P3C1", "1");
		ChoiceNode m1p3c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p3, "M1P3C2", "2");
		ChoiceNode m1p3c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p3, "M1P3C3", "3");

		CompositeParameterNode m1p4 = new CompositeParameterNode("M1P4", null);
		BasicParameterNode m1p41 = MethodNodeHelper.addParameterToMethod(m1p4, "M1P41", "int");
		ChoiceNode m1p41c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p41, "M1P41C1", "1");
		ChoiceNode m1p41c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p41, "M1P41C2", "2");
		ChoiceNode m1p41c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p41, "M1P41C3", "3");
		BasicParameterNode m1p42 = MethodNodeHelper.addParameterToMethod(m1p4, "M1P42", "int");
		ChoiceNode m1p42c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p42, "M1P42C1", "1");
		ChoiceNode m1p42c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p42, "M1P42C2", "2");
		ChoiceNode m1p42c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p42, "M1P42C3", "3");

		BasicParameterNode m1p5 = new BasicParameterNode("M1P5", "int", "0", false);
		ChoiceNode m1p5c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p5, "M1P5C1", "1");
		ChoiceNode m1p5c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p5, "M1P5C2", "2");
		ChoiceNode m1p5c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p5, "M1P5C3", "3");

		CompositeParameterNode m1p6 = new CompositeParameterNode("M1P6", null);
		CompositeParameterNode m1p61 = new CompositeParameterNode("M1P61", null);
		BasicParameterNode m1p611 = MethodNodeHelper.addParameterToMethod(m1p61, "M1P611", "int");
		ChoiceNode m1p611c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p611, "M1P611C1", "1");
		ChoiceNode m1p611c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p611, "M1P611C2", "2");
		ChoiceNode m1p611c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p611, "M1P611C3", "3");
		CompositeParameterNode m1p62 = new CompositeParameterNode("M1P62", null);
		BasicParameterNode m1p621 = MethodNodeHelper.addParameterToMethod(m1p62, "M1P621", "int");
		ChoiceNode m1p621c1 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p621, "M1P621C1", "1");
		ChoiceNode m1p621c2 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p621, "M1P621C2", "2");
		ChoiceNode m1p621c3 = MethodParameterNodeHelper.addChoiceToMethodParameter(m1p621, "M1P621C3", "3");

		RelationStatement m1r1 = RelationStatement.createRelationStatementWithChoiceCondition(m1p1, EMathRelation.EQUAL, m1p1c1);
		RelationStatement m1r2 = RelationStatement.createRelationStatementWithChoiceCondition(m1p3, EMathRelation.LESS_THAN, m1p3c3);

		Constraint m1c1 = new Constraint("M1C1", ConstraintType.EXTENDED_FILTER, m1r1, m1r2,null);

		model.addParameter(p2);

		c1.addParameter(c1p2);

		m1.addParameter(m1p1);
		m1.addParameter(m1p2);
		m1.addParameter(m1p3);
		m1.addParameter(m1p4);
		m1.addParameter(m1p5);

		m1p6.addParameter(m1p61);
		m1p6.addParameter(m1p62);

		m1.addParameter(m1p6);

		m1.addConstraint(new ConstraintNode("M1C1", m1c1, null));

		model.addClass(c1);

		MethodDeployer.deploy(m1);

//		m1.removeParameter(m1p1);
		m1p1.setName("blablabla");

		return model;
	}

	private String getSerializedString(RootNode model) {
		OutputStream modelStream = new ByteArrayOutputStream();
		ModelSerializer serializer = 
				new ModelSerializer(modelStream, ModelVersionDistributor.getCurrentSoftwareVersion());

		try {
			serializer.serialize(model);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}

		return modelStream.toString();
	}

	@Test
	public void serializerTestWithModelConversion(){
		RootNode convertedModel = null;
		try {
			convertedModel = ModelConverter.convertToCurrentVersion(createModel(0));
		} catch (Exception e) {
			fail();
		}
		String convertedString = getSerializedString(convertedModel);

		RootNode currentModel = createModel(ModelVersionDistributor.getCurrentSoftwareVersion());
		String currentString = getSerializedString(currentModel);

		assertEquals(ModelVersionDistributor.getCurrentSoftwareVersion(), convertedModel.getModelVersion());
		assertEquals(ModelVersionDistributor.getCurrentSoftwareVersion(), currentModel.getModelVersion());

		assertEquals(currentString, convertedString);
	}
}
