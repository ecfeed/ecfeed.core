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
import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.BasicParameterNodeHelper;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.CompositeParameterNodeHelper;
import com.ecfeed.core.model.Constraint;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ConstraintType;
import com.ecfeed.core.model.ConstraintsParentNodeHelper;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodDeployer;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.ModelComparator;
import com.ecfeed.core.model.ModelConverter;
import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.NodeMapper;
import com.ecfeed.core.model.RelationStatement;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.serialization.ModelParser;
import com.ecfeed.core.model.serialization.ModelSerializer;
import com.ecfeed.core.model.utils.ParameterWithLinkingContext;
import com.ecfeed.core.utils.EMathRelation;
import com.ecfeed.core.utils.EvaluationResult;
import com.ecfeed.core.utils.ListOfStrings;
import com.ecfeed.core.utils.XmlComparator;

public class ModelSerializerAndParserTest {

	@Test 
	public void shouldSerializeAndParseNestedStructures() {

		RootNode rootNode = new RootNode("root", null, ModelVersionDistributor.getCurrentSoftwareVersion());

		// class and method

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		// local structure 1

		CompositeParameterNode localCompositeParameterNode1 = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode, "LS1", true, null);

		// local structure 2

		CompositeParameterNode localCompositeParameterNode2 = 
				CompositeParameterNodeHelper.addNewCompositeParameter(
						localCompositeParameterNode1, "LS2", true, null);

		// local parameter1 with choice

		BasicParameterNode localBasicParameterNode1 = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						localCompositeParameterNode2, "LP1", "int", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				localBasicParameterNode1, "GC11", "0", false, true, null);

		// local parameter2 with choice

		BasicParameterNode localBasicParameterNode2 = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						localCompositeParameterNode2, "LP2", "int", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				localBasicParameterNode2, "GC21", "0", false, true, null);

		// constraint with parameter condition

		RelationStatement precondition =
				RelationStatement.createRelationStatementWithParameterCondition(
						localBasicParameterNode1, null, 
						EMathRelation.EQUAL, 
						localBasicParameterNode1, null);

		StaticStatement postcondition = new StaticStatement(EvaluationResult.TRUE); 

		Constraint constraint = new Constraint(
				"constraint", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);

		ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint, true, null);

		// root
		//   class
		//     method
		//       LS1
		//         LS2
		//           LP1
		//           LP2
		//		 constraint: LS1:LS2:LP1 = LS1:LS2:LP2 => true

		try {
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			ModelSerializer serializer = new ModelSerializer(ostream, ModelVersionDistributor.getCurrentSoftwareVersion());

			serializer.serialize(rootNode);
			String xml = ostream.toString();

			String tags =
					"<Constraint name=\"constraint\" type=\"EF\">\n" + 
							"  <Premise>\n" + 
							"    <ParameterStatement rightParameter=\"LS1:LS2:LP1\" parameter=\"LS1:LS2:LP1\" relation=\"equal\"/>\n" + 
							"  </Premise>\n" + 
							"  <Consequence>\n" + 
							"    <StaticStatement value=\"true\"/>\n" + 
							"  </Consequence>\n" + 
							"</Constraint>";

			if (!XmlComparator.containsConsecutiveTags(xml, tags)) {
				fail();
			}

			InputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			ModelParser parser = new ModelParser();
			ListOfStrings errorList = new ListOfStrings();

			RootNode parsedModel = parser.parseModel(istream, null, errorList);

			checkErrorList(errorList);
			ModelComparator.compareRootNodes(rootNode, parsedModel);

		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test 
	public void shouldSerializeAndParseNestedLocalStructureWithConstraintAtStructureLevel() {

		RootNode rootNode = new RootNode("root", null, ModelVersionDistributor.getCurrentSoftwareVersion());

		// class and method

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		// local structure 1

		CompositeParameterNode compositeParameterNode = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode, "LS1", true, null);

		// local parameter1 with choice

		BasicParameterNode basicParameterNode = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						compositeParameterNode, "LP1", "int", "0", true, null);

		ChoiceNode choiceNode = 
				BasicParameterNodeHelper.addNewChoiceToBasicParameter(
						basicParameterNode, "LC1", "0", false, true, null);

		// constraint with parameter condition

		RelationStatement precondition =
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode, null, 
						EMathRelation.EQUAL, 
						choiceNode);

		StaticStatement postcondition = new StaticStatement(EvaluationResult.TRUE); 

		Constraint constraint = new Constraint(
				"constraint", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);

		ConstraintsParentNodeHelper.addNewConstraintNode(compositeParameterNode, constraint, true, null);

		// root
		//   class
		//     method
		//       LS1
		//          LP1
		//				LC1
		//		 	constraint: LS1:LP1 = LC1 => true

		try {
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			ModelSerializer serializer = new ModelSerializer(ostream, ModelVersionDistributor.getCurrentSoftwareVersion());

			serializer.serialize(rootNode);
			String xml = ostream.toString();

			String tags = "<Constraint name=\"constraint\" type=\"EF\">\n" + 
					"                    <Premise>\n" + 
					"                        <Statement choice=\"LC1\" parameter=\"LS1:LP1\" relation=\"equal\"/>\n" + 
					"                    </Premise>\n" + 
					"                    <Consequence>\n" + 
					"                        <StaticStatement value=\"true\"/>\n" + 
					"                    </Consequence>\n" + 
					"                </Constraint>";

			if (!XmlComparator.containsConsecutiveTags(xml, tags)) {
				fail();
			}

			InputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			ModelParser parser = new ModelParser();
			ListOfStrings errorList = new ListOfStrings();

			RootNode parsedModel = parser.parseModel(istream, null, errorList);

			checkErrorList(errorList);
			ModelComparator.compareRootNodes(rootNode, parsedModel);

		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test 
	public void shouldSerializeAndParseGlobalStructureWithConstraintAtStructureLevel() { // XYX 1

		RootNode rootNode = new RootNode("root", null, ModelVersionDistributor.getCurrentSoftwareVersion());

		// local structure 1

		CompositeParameterNode globalCompositeParameterNode = 
				RootNodeHelper.addNewCompositeParameterToRoot(rootNode, "GS1", true, null);

		// local parameter1 with choice

		BasicParameterNode basicParameterNode = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						globalCompositeParameterNode, "GP1", "int", "0", true, null);

		ChoiceNode choiceNode = 
				BasicParameterNodeHelper.addNewChoiceToBasicParameter(
						basicParameterNode, "GC1", "0", false, true, null);

		// constraint with parameter condition

		RelationStatement precondition =
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode, null, 
						EMathRelation.EQUAL, 
						choiceNode);

		StaticStatement postcondition = new StaticStatement(EvaluationResult.TRUE); 

		Constraint constraint = new Constraint(
				"constraint", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);

		ConstraintsParentNodeHelper.addNewConstraintNode(globalCompositeParameterNode, constraint, true, null);


		// class and method

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		CompositeParameterNode localCompositeParameterNode = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode, "LS1", true, null);

		//ParameterToRoot(rootNode, name, setParent, modelChangeRegistrator)
		//addNewCompositeParameterToRoot(methodNode, "GS1", true, null);

		localCompositeParameterNode.setLinkToGlobalParameter(globalCompositeParameterNode);

		// root
		// 	 GS1
		//     GP1
		//		 GC1
		//	   constraint: GS1:GP1 = GC1 => true
		//   class
		//     method
		//       LS1->GS1

		try {
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			ModelSerializer serializer = new ModelSerializer(ostream, ModelVersionDistributor.getCurrentSoftwareVersion());

			serializer.serialize(rootNode);
			String xml = ostream.toString();

			String tags = 
					"        <Parameter name=\"GP1\" type=\"int\">\n" + 
							"            <Properties>\n" + 
							"                <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" + 
							"            </Properties>\n" + 
							"            <Comments>\n" + 
							"                <TypeComments/>\n" + 
							"            </Comments>\n" + 
							"            <Choice name=\"GC1\" value=\"0\" isRandomized=\"false\"/>\n" + 
							"        </Parameter>\n" + 
							"        <Constraint name=\"constraint\" type=\"EF\">\n" + 
							"            <Premise>\n" + 
							"                <Statement choice=\"GC1\" parameter=\"@root:GS1:GP1\" relation=\"equal\"/>\n" + 
							"            </Premise>\n" + 
							"            <Consequence>\n" + 
							"                <StaticStatement value=\"true\"/>\n" + 
							"            </Consequence>\n" + 
							"        </Constraint>\n" + 
							""; 

			if (!XmlComparator.containsConsecutiveTags(xml, tags)) {
				fail();
			}

			InputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			ModelParser parser = new ModelParser();
			ListOfStrings errorList = new ListOfStrings();

			RootNode parsedModel = parser.parseModel(istream, null, errorList);

			checkErrorList(errorList);
			ModelComparator.compareRootNodes(rootNode, parsedModel);

		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test 
	public void shouldSerializeAndParseNestedStructureWithParameterOneLevelDeeperThanConstraint() { // XYX 2

		RootNode rootNode = new RootNode("root", null, ModelVersionDistributor.getCurrentSoftwareVersion());

		// class and method

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		// structure 1

		CompositeParameterNode compositeParameterNode1 = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode, "LS1", true, null);

		// nested structure 2

		CompositeParameterNode compositeParameterNode2 = 
				CompositeParameterNodeHelper.addNewCompositeParameter(compositeParameterNode1, "LS2", true, null);

		// local parameter1 under structure 2, with choice

		BasicParameterNode basicParameterNode = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						compositeParameterNode2, "LP1", "int", "0", true, null);

		ChoiceNode choiceNode = 
				BasicParameterNodeHelper.addNewChoiceToBasicParameter(
						basicParameterNode, "LC1", "0", false, true, null);

		// constraint with parameter condition under structure 1

		RelationStatement precondition =
				RelationStatement.createRelationStatementWithChoiceCondition(
						basicParameterNode, null, 
						EMathRelation.EQUAL, 
						choiceNode);

		StaticStatement postcondition = new StaticStatement(EvaluationResult.TRUE); 

		Constraint constraint = new Constraint(
				"constraint", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);

		ConstraintsParentNodeHelper.addNewConstraintNode(compositeParameterNode1, constraint, true, null);

		// root
		//   class
		//     method
		//       LS1
		//			LS2
		//          	LP1
		//					LC1
		//		 	constraint: LS1:LS2:LP1 = LC1 => true

		try {
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			ModelSerializer serializer = new ModelSerializer(ostream, ModelVersionDistributor.getCurrentSoftwareVersion());

			serializer.serialize(rootNode);
			String xml = ostream.toString();

			String tags = "<Constraint name=\"constraint\" type=\"EF\">\n" + 
					"                    <Premise>\n" + 
					"                        <Statement choice=\"LC1\" parameter=\"LS1:LS2:LP1\" relation=\"equal\"/>\n" + 
					"                    </Premise>\n" + 
					"                    <Consequence>\n" + 
					"                        <StaticStatement value=\"true\"/>\n" + 
					"                    </Consequence>\n" + 
					"                </Constraint>";

			if (!XmlComparator.containsConsecutiveTags(xml, tags)) {
				fail();
			}

			InputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			ModelParser parser = new ModelParser();
			ListOfStrings errorList = new ListOfStrings();

			RootNode parsedModel = parser.parseModel(istream, null, errorList);

			checkErrorList(errorList);
			ModelComparator.compareRootNodes(rootNode, parsedModel);

		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test 
	public void shouldSerializeAndParseLinkedStructureAndConstraintWithParameterCondition() {

		RootNode rootNode = new RootNode("root", null, ModelVersionDistributor.getCurrentSoftwareVersion());

		CompositeParameterNode globalCompositeParameterNode = 
				RootNodeHelper.addNewCompositeParameterToRoot(rootNode, "GS", true, null);

		// global parameter1 with choice

		BasicParameterNode globalBasicParameterNode1 = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						globalCompositeParameterNode, "GP1", "int", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				globalBasicParameterNode1, "GC11", "0", false, true, null);


		// global parameter2 with choice

		BasicParameterNode globalBasicParameterNode2 = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						globalCompositeParameterNode, "GP2", "int", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				globalBasicParameterNode2, "GC21", "0", false, true, null);

		// class and method

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		// local structure

		CompositeParameterNode localCompositeParameterNode = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode, "LS", true, null);

		localCompositeParameterNode.setLinkToGlobalParameter(globalCompositeParameterNode);

		// constraint with parameter condition

		RelationStatement precondition =
				RelationStatement.createRelationStatementWithParameterCondition(
						globalBasicParameterNode1, localCompositeParameterNode, 
						EMathRelation.EQUAL, 
						globalBasicParameterNode2, localCompositeParameterNode);

		StaticStatement postcondition = new StaticStatement(EvaluationResult.TRUE); 

		Constraint constraint = new Constraint(
				"constraint", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);

		ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint, true, null);

		// root
		//   GS
		// 	   GP1
		//       GC11
		//     GP2
		//       GC21
		//   class
		//     method
		//       LS->GS
		//		 constraint: LS->GS:P1=LS->GS:GP2=>true

		try {
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			ModelSerializer serializer = new ModelSerializer(ostream, ModelVersionDistributor.getCurrentSoftwareVersion());

			serializer.serialize(rootNode);
			String xml = ostream.toString();

			String tags1 =
					"<Structure name=\"LS\" linked=\"true\" link=\"@root:GS\">";

			if (!XmlComparator.containsConsecutiveTags(xml, tags1)) {
				fail();
			}

			String tags2 =
					"<Constraint name=\"constraint\" type=\"EF\">\n" + 
							"  <Premise>\n" + 
							"    <ParameterStatement rightParameter=\"@root:GS:GP2\" rightParameterContext=\"LS\" parameter=\"@root:GS:GP1\" parameterContext=\"LS\" relation=\"equal\"/>\n" + 
							"  </Premise>\n" + 
							"  <Consequence>\n" + 
							"    <StaticStatement value=\"true\"/>\n" + 
							"  </Consequence>\n" + 
							"</Constraint>\n"; 

			if (!XmlComparator.containsConsecutiveTags(xml, tags2)) {
				fail();
			}

			InputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			ModelParser parser = new ModelParser();
			ListOfStrings errorList = new ListOfStrings();

			RootNode parsedModel = parser.parseModel(istream, null, errorList);

			checkErrorList(errorList);
			ModelComparator.compareRootNodes(rootNode, parsedModel);

		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test 
	public void shouldSerializeAndParseDoubleLinkedStructureAndConstraintWithParameterCondition() {

		RootNode rootNode = new RootNode("root", null, ModelVersionDistributor.getCurrentSoftwareVersion());

		// global structures

		CompositeParameterNode globalCompositeParameterNode1 = 
				RootNodeHelper.addNewCompositeParameterToRoot(rootNode, "GS1", true, null);

		CompositeParameterNode globalCompositeParameterNode2 = 
				CompositeParameterNodeHelper.addNewCompositeParameter(
						globalCompositeParameterNode1, "GS2", true, null);

		// global parameter1 with choice

		BasicParameterNode globalBasicParameterNode1 = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						globalCompositeParameterNode2, "GP1", "int", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				globalBasicParameterNode1, "GC11", "0", false, true, null);

		// class and method

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		// local structures

		CompositeParameterNode localCompositeParameterNode1 = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode, "LS1", true, null);

		localCompositeParameterNode1.setLinkToGlobalParameter(globalCompositeParameterNode1);

		CompositeParameterNode localCompositeParameterNode2 = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode, "LS2", true, null);

		localCompositeParameterNode2.setLinkToGlobalParameter(globalCompositeParameterNode1);

		// constraint with parameter condition

		RelationStatement precondition =
				RelationStatement.createRelationStatementWithParameterCondition(
						globalBasicParameterNode1, localCompositeParameterNode1, 
						EMathRelation.EQUAL, 
						globalBasicParameterNode1, localCompositeParameterNode2);

		StaticStatement postcondition = new StaticStatement(EvaluationResult.TRUE); 

		Constraint constraint = new Constraint(
				"constraint", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);

		ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint, true, null);

		// root
		//   GS1
		//     GS2
		// 	     GP1
		//         GC11
		//   class
		//     method
		//       LS1->GS
		//       LS2->GS		
		//		 constraint: LS1->GS1:GS2:GP1 = LS2->GS1:GS2:GP1 => true

		try {
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			ModelSerializer serializer = new ModelSerializer(ostream, ModelVersionDistributor.getCurrentSoftwareVersion());

			serializer.serialize(rootNode);
			String xml = ostream.toString();

			String tags1 = "<Structure name=\"LS1\" linked=\"true\" link=\"@root:GS1\">";

			if (!XmlComparator.containsConsecutiveTags(xml, tags1)) {
				fail();
			}

			String tags2 = "<Structure name=\"LS2\" linked=\"true\" link=\"@root:GS1\">";

			if (!XmlComparator.containsConsecutiveTags(xml, tags2)) {
				fail();
			}

			String tags3 =
					"<Constraint name=\"constraint\" type=\"EF\">\n" + 
							"  <Premise>\n" + 
							"    <ParameterStatement rightParameter=\"@root:GS1:GS2:GP1\" rightParameterContext=\"LS2\" parameter=\"@root:GS1:GS2:GP1\" parameterContext=\"LS1\" relation=\"equal\"/>\n" + 
							"  </Premise>\n" + 
							"  <Consequence>\n" + 
							"    <StaticStatement value=\"true\"/>\n" + 
							"  </Consequence>\n" + 
							"</Constraint>";

			if (!XmlComparator.containsConsecutiveTags(xml, tags3)) {
				fail();
			}

			InputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			ModelParser parser = new ModelParser();
			ListOfStrings errorList = new ListOfStrings();

			RootNode parsedModel = parser.parseModel(istream, null, errorList);

			checkErrorList(errorList);
			ModelComparator.compareRootNodes(rootNode, parsedModel);

		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test 
	public void shouldSerializeAndParseLinkedParametersAndConstraintWithParameterCondition() {

		RootNode rootNode = new RootNode("root", null, ModelVersionDistributor.getCurrentSoftwareVersion());

		// global parameter1 with choice

		BasicParameterNode globalBasicParameterNode1 = 
				RootNodeHelper.addNewBasicParameterToRoot(rootNode, "GP1", "int", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				globalBasicParameterNode1, "GC11", "0", false, true, null);

		// global parameter2 with choice

		BasicParameterNode globalBasicParameterNode2 = 
				RootNodeHelper.addNewBasicParameterToRoot(rootNode, "GP2", "int", "0", true, null);

		BasicParameterNodeHelper.addNewChoiceToBasicParameter(
				globalBasicParameterNode2, "GC21", "0", false, true, null);

		// class and method

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		// local parameter1

		BasicParameterNode basicParameterNode1 = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "P1", "int", "0", true, null);

		basicParameterNode1.setLinkToGlobalParameter(globalBasicParameterNode1);

		// local parameter2

		BasicParameterNode basicParameterNode2 = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "P2", "int", "0", true, null);

		basicParameterNode2.setLinkToGlobalParameter(globalBasicParameterNode2);

		// constraint with parameter condition

		RelationStatement precondition =
				RelationStatement.createRelationStatementWithParameterCondition(
						basicParameterNode1, null, 
						EMathRelation.EQUAL, 
						basicParameterNode2, null);


		StaticStatement postcondition = new StaticStatement(EvaluationResult.TRUE); 

		Constraint constraint = new Constraint(
				"constraint", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);

		ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint, true, null);

		// root
		// 	 GP1
		//     GC11
		//   GP2
		//     GC21
		//   class
		//     method
		//       P1->GP1
		//       P2->GP2
		//		 constraint: P1==P2=>true

		try {
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			ModelSerializer serializer = new ModelSerializer(ostream, ModelVersionDistributor.getCurrentSoftwareVersion());

			serializer.serialize(rootNode);
			String xml = ostream.toString();

			String tags1 =
					"<Parameter name=\"P1\" type=\"int\" isExpected=\"false\" "
							+ "expected=\"0\" linked=\"true\" link=\"@root:GP1\">";

			if (!XmlComparator.containsConsecutiveTags(xml, tags1)) {
				fail();
			}

			String tags2 =
					"<Parameter name=\"P2\" type=\"int\" isExpected=\"false\" "
							+ "expected=\"0\" linked=\"true\" link=\"@root:GP2\">";

			if (!XmlComparator.containsConsecutiveTags(xml, tags2)) {
				fail();
			}

			String tags3 =
					"<Constraint name=\"constraint\" type=\"EF\">\n" + 
							"  <Premise>\n" + 
							"    <ParameterStatement rightParameter=\"P2\" parameter=\"P1\" relation=\"equal\"/>\n" + 
							"  </Premise>\n" + 
							"  <Consequence>\n" + 
							"    <StaticStatement value=\"true\"/>\n" + 
							"  </Consequence>\n" + 
							"</Constraint>";

			if (!XmlComparator.containsConsecutiveTags(xml, tags3)) {
				fail();
			}

			InputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			ModelParser parser = new ModelParser();
			ListOfStrings errorList = new ListOfStrings();
			RootNode parsedModel = parser.parseModel(istream, null, errorList);

			checkErrorList(errorList);
			ModelComparator.compareRootNodes(rootNode, parsedModel);

		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test 
	public void shouldSerializeAndParseLinkedStructureAndConstraint() {

		RootNode rootNode = new RootNode("root", null, ModelVersionDistributor.getCurrentSoftwareVersion());

		CompositeParameterNode globalCompositeParameterNode1 = 
				RootNodeHelper.addNewCompositeParameterToRoot(rootNode, "GS1", true, null);

		BasicParameterNode globalBasicParameterNode = 
				CompositeParameterNodeHelper.addNewBasicParameterToComposite(
						globalCompositeParameterNode1, "GP", "int", "o", true, null);

		ChoiceNode choice1 = 
				BasicParameterNodeHelper.addNewChoiceToBasicParameter(
						globalBasicParameterNode, "choice1", "0", false, true, null);

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		CompositeParameterNode localCompositeParameterNode1 = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode, "LS1", true, null);

		localCompositeParameterNode1.setLinkToGlobalParameter(globalCompositeParameterNode1);

		RelationStatement precondition = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						globalBasicParameterNode, localCompositeParameterNode1, EMathRelation.EQUAL, choice1);

		StaticStatement postcondition = new StaticStatement(EvaluationResult.TRUE); 

		Constraint constraint = new Constraint(
				"constraint", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null);

		ConstraintsParentNodeHelper.addNewConstraintNode(methodNode, constraint, true, null);

		// root
		//   GS1
		//     GP
		//   class
		//     method
		//       LS1->GS
		//		 constraint: gp1==choice1=>true

		try {
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			ModelSerializer serializer = new ModelSerializer(ostream, ModelVersionDistributor.getCurrentSoftwareVersion());

			serializer.serialize(rootNode);
			String xml = ostream.toString();

			String tags1 =
					"<Structure name=\"LS1\" linked=\"true\" link=\"@root:GS1\">";

			if (!XmlComparator.containsConsecutiveTags(xml, tags1)) {
				fail();
			}

			String tags2 =
					"<Constraint name=\"constraint\" type=\"EF\">\n" + 
							"  <Premise>\n" + 
							"    <Statement choice=\"choice1\" parameter=\"@root:GS1:GP\" parameterContext=\"LS1\" relation=\"equal\"/>\n" + 
							"  </Premise>\n" + 
							"  <Consequence>\n" + 
							"    <StaticStatement value=\"true\"/>\n" + 
							"  </Consequence>\n" + 
							"</Constraint>";

			if (!XmlComparator.containsConsecutiveTags(xml, tags2)) {
				fail();
			}

			InputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			ModelParser parser = new ModelParser();
			ListOfStrings errorList = new ListOfStrings();

			RootNode parsedModel = parser.parseModel(istream, null, errorList);

			checkErrorList(errorList);
			ModelComparator.compareRootNodes(rootNode, parsedModel);

		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test 
	public void shouldSerializeDeployedLinkedParametersWithConflictingNamesAndMultipleLinks() {

		RootNode rootNode = new RootNode("root", null, ModelVersionDistributor.getCurrentSoftwareVersion());

		CompositeParameterNode globalCompositeParameterNode1 = 
				RootNodeHelper.addNewCompositeParameterToRoot(rootNode, "GS1", true, null);

		// BasicParameterNode globalBasicParameterNode1 = 
		CompositeParameterNodeHelper.addNewBasicParameterToComposite(
				globalCompositeParameterNode1, "GP", "int", "o", true, null);

		CompositeParameterNode globalCompositeParameterNode2 = 
				CompositeParameterNodeHelper.addNewCompositeParameterNodeToCompositeParameter(
						globalCompositeParameterNode1, "GS2", null);

		// BasicParameterNode globalBasicParameterNode2 =
		CompositeParameterNodeHelper.addNewBasicParameterToComposite(
				globalCompositeParameterNode2, "GP", "int", "o", true, null);

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		CompositeParameterNode localCompositeParameterNode1 = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode, "LS1", true, null);

		localCompositeParameterNode1.setLinkToGlobalParameter(globalCompositeParameterNode1);


		CompositeParameterNode localCompositeParameterNode2 = 
				MethodNodeHelper.addNewCompositeParameterToMethod(methodNode, "LS2", true, null);

		localCompositeParameterNode2.setLinkToGlobalParameter(globalCompositeParameterNode1);

		// root
		//   GS1
		//     GP
		//	   GS2 
		//       GP
		//   class
		//   method
		//    LS1->GS
		//    LS2->GS

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode deployedMethodNode = MethodDeployer.deploy(methodNode, nodeMapper);
		MethodDeployer.copyDeployedParametersWithConversionToOriginals(deployedMethodNode, methodNode, nodeMapper);

		// List<ParameterWithLinkingContext> deployedParamsWithContexts = 
		//		methodNode.getDeployedParametersWithLinkingContexs();

		try {
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			ModelSerializer serializer = new ModelSerializer(ostream, ModelVersionDistributor.getCurrentSoftwareVersion());

			serializer.serialize(rootNode);
			String xml = ostream.toString();

			String tags = 
					"<Deployment>\n" + 
							"  <Parameter pathOfParameter=\"@root:GS1:GP\" pathOfContext=\"LS1\"/>\n" + 
							"  <Parameter pathOfParameter=\"@root:GS1:GS2:GP\" pathOfContext=\"LS1\"/>\n" + 
							"  <Parameter pathOfParameter=\"@root:GS1:GP\" pathOfContext=\"LS2\"/>\n" + 
							"  <Parameter pathOfParameter=\"@root:GS1:GS2:GP\" pathOfContext=\"LS2\"/>\n" + 
							"</Deployment>";

			if (!XmlComparator.containsConsecutiveTags(xml, tags)) {
				fail();
			}

			InputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			ModelParser parser = new ModelParser();
			ListOfStrings errorList = new ListOfStrings();

			RootNode parsedModel = parser.parseModel(istream, null, errorList);

			checkErrorList(errorList);
			ModelComparator.compareRootNodes(rootNode, parsedModel);

		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void shouldSerializeDeployedLocalParametersWithConflictingNames() {

		RootNode rootNode = new RootNode("root", null, ModelVersionDistributor.getCurrentSoftwareVersion());

		ClassNode classNode = RootNodeHelper.addNewClassNodeToRoot(rootNode, "class", true, null);

		MethodNode methodNode = ClassNodeHelper.addNewMethodToClass(classNode, "method", true, null);

		MethodNodeHelper.addNewBasicParameter(methodNode, "LP", "int", "0", true, null);

		CompositeParameterNode compositeParameterNode = MethodNodeHelper.addNewCompositeParameterToMethod(methodNode, "LS", true, null);

		CompositeParameterNodeHelper.addNewBasicParameterToComposite(
				compositeParameterNode, "LP", "int", "0", true, null);

		// root
		//   class
		//   method
		//    LP : int
		//    LS
		//      LP : int

		NodeMapper nodeMapper = new NodeMapper();
		MethodNode deployedMethodNode = MethodDeployer.deploy(methodNode, nodeMapper);
		MethodDeployer.copyDeployedParametersWithConversionToOriginals(deployedMethodNode, methodNode, nodeMapper);

		try {
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			ModelSerializer serializer = new ModelSerializer(ostream, ModelVersionDistributor.getCurrentSoftwareVersion());

			serializer.serialize(rootNode);

			String xml = ostream.toString();

			String tags =
					"<Deployment>\n" + 
							"  <Parameter pathOfParameter=\"LP\"/>\n" + 
							"  <Parameter pathOfParameter=\"LS:LP\"/>\n" + 
							"</Deployment>";

			if (!XmlComparator.containsConsecutiveTags(xml, tags)) {
				fail();
			}

			InputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			ModelParser parser = new ModelParser();
			ListOfStrings errorList = new ListOfStrings();

			RootNode parsedModel = parser.parseModel(istream, null, errorList);
			checkErrorList(errorList);

			// check parsed deployed parameter 1

			ClassNode parsedClass = parsedModel.getClasses().get(0);
			MethodNode parsedMethodNode = parsedClass.getMethods().get(0);
			List<ParameterWithLinkingContext> parsedDeployedParams = 
					parsedMethodNode.getDeployedParametersWithLinkingContexts();

			ParameterWithLinkingContext parsedDeployedParam1 = parsedDeployedParams.get(0);
			IParametersParentNode parsedParent1 = parsedDeployedParam1.getParameter().getParent();
			assertEquals(parsedMethodNode, parsedParent1);

			// check parsed deployed parameter 2

			ParameterWithLinkingContext parsedDeployedParam2 = parsedDeployedParams.get(1);
			IParametersParentNode parsedParent2 = parsedDeployedParam2.getParameter().getParent();
			assertEquals("LS", parsedParent2.getName());

			IParametersParentNode parsedParent3 = (IParametersParentNode) parsedParent2.getParent();
			assertEquals(parsedMethodNode, parsedParent3);

			ModelComparator.compareRootNodes(rootNode, parsedModel);

		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void shouldSerializeModelWithClassesAndGlobalBasicParameters() {

		int version = ModelVersionDistributor.getCurrentSoftwareVersion();
		RootNode model = new RootNode("model", null, version);

		model.addClass(new ClassNode("com.example.TestClass1", null));
		model.addClass(new ClassNode("com.example.TestClass2", null));
		model.addParameter(new BasicParameterNode("globalParameter1", "int", null, false, null));
		model.addParameter(new BasicParameterNode("globalParameter2", "com.example.UserType", null, false, null));

		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		ModelSerializer serializer = new ModelSerializer(ostream, version);
		try {
			serializer.serialize(model);

			String xml = ostream.toString();

			String tags =
					"    <Class name=\"com.example.TestClass1\"/>\n" + 
							"    <Class name=\"com.example.TestClass2\"/>\n" + 
							"    <Parameter name=\"globalParameter1\" type=\"int\">\n" + 
							"        <Properties>\n" + 
							"            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" + 
							"        </Properties>\n" + 
							"        <Comments>\n" + 
							"            <TypeComments/>\n" + 
							"        </Comments>\n" + 
							"    </Parameter>\n" + 
							"    <Parameter name=\"globalParameter2\" type=\"com.example.UserType\">\n" + 
							"        <Properties>\n" + 
							"            <Property name=\"wbIsOptional\" type=\"boolean\" value=\"false\"/>\n" + 
							"        </Properties>\n" + 
							"        <Comments>\n" + 
							"            <TypeComments/>\n" + 
							"        </Comments>\n" + 
							"    </Parameter>";

			if (!XmlComparator.containsConsecutiveTags(xml, tags)) {
				fail();
			}

			InputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			ModelParser parser = new ModelParser();
			ListOfStrings errorList = new ListOfStrings();

			RootNode parsedModel = parser.parseModel(istream, null, errorList);

			checkErrorList(errorList);
			assertElementsEqual(model, parsedModel);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
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

	private RootNode createModel(int version) {

		ChoiceNode choice = new ChoiceNode("choice", "0", null);

		BasicParameterNode parameter = new BasicParameterNode("parameter", "int", "0", false, null);
		parameter.addChoice(choice);

		MethodNode methodNode = new MethodNode("testMethod1", null);
		methodNode.addParameter(parameter);

		RelationStatement relationStatementWithChoiceCondition = 
				RelationStatement.createRelationStatementWithChoiceCondition(
						parameter, null, EMathRelation.EQUAL, choice);

		Constraint constraint = new Constraint(
				"constraint",
				ConstraintType.EXTENDED_FILTER,
				relationStatementWithChoiceCondition, 
				relationStatementWithChoiceCondition, 
				null);

		ConstraintNode constraintNode = new ConstraintNode("name1", constraint, null);
		methodNode.addConstraint(constraintNode);

		ClassNode classNode = new ClassNode("com.example.TestClass", null);
		classNode.addMethod(methodNode);

		RootNode model = new RootNode("model", null, version);
		model.addClass(classNode);
		model.setVersion(version);

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

	private void checkErrorList(ListOfStrings errorList) {

		if (errorList.isEmpty()) {
			return;
		}

		fail(errorList.getFirstString());
	}

}
