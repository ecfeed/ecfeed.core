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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.ecfeed.core.type.adapter.JavaPrimitiveTypePredicate;
import com.ecfeed.core.utils.EMathRelation;


public class CopyNodeTest{

	@Test
	public void copyRootTest(){
		RootNode root = new RootNode("name", null);
		ClassNode class1 = new ClassNode("class1", null);
		ClassNode class2 = new ClassNode("class2", null);
		BasicParameterNode par1 = new BasicParameterNode("par1", "int", null);
		BasicParameterNode par2 = new BasicParameterNode("par2", "int", null);
		root.addClass(class1);
		root.addClass(class2);
		root.addParameter(par1);
		root.addParameter(par2);

		RootNode copy = root.makeClone();
		assertTrue(root.isMatch(copy));
	}

	@Test
	public void copyClassTest(){
		ClassNode classNode = new ClassNode("Class", null);
		MethodNode method1 = new MethodNode("method1", null);
		MethodNode method2 = new MethodNode("method2", null);
		BasicParameterNode par1 = new BasicParameterNode("par1", "int", null);
		BasicParameterNode par2 = new BasicParameterNode("par2", "int", null);
		classNode.addMethod(method1);
		classNode.addMethod(method2);
		classNode.addParameter(par1);
		classNode.addParameter(par2);

		ClassNode copy = classNode.makeClone();
		assertTrue(classNode.isMatch(copy));
	}

	@Test
	public void copyMethodTest(){
		MethodNode method = new MethodNode("method", null);
		BasicParameterNode par1 = new BasicParameterNode("par1", "int", "0", false, null);
		BasicParameterNode par2 = new BasicParameterNode("par2", "int", "0", true, null);
		ConstraintNode constraint1 = new ConstraintNode("constraint1", new Constraint("constraint1", ConstraintType.EXTENDED_FILTER, new StaticStatement(true, null), new StaticStatement(true, null), null), null);
		ConstraintNode constraint2 = new ConstraintNode("constraint2", new Constraint("constraint2", ConstraintType.EXTENDED_FILTER, new StaticStatement(true, null), new StaticStatement(true, null), null), null);
		ChoiceNode choice1 = new ChoiceNode("choice1", "0", null);
		par1.addChoice(choice1);
		ChoiceNode expectedChoice1 = new ChoiceNode("expected", "0", null);
		expectedChoice1.setParent(par2);
		ChoiceNode expectedChoice2 = new ChoiceNode("expected", "2", null);
		expectedChoice2.setParent(par2);
		TestCaseNode testCase1 = new TestCaseNode("test case 1", null, Arrays.asList(choice1, expectedChoice1));
		TestCaseNode testCase2 = new TestCaseNode("test case 1", null, Arrays.asList(choice1, expectedChoice2));

		method.addParameter(par1);
		method.addParameter(par2);
		method.addConstraint(constraint1);
		method.addConstraint(constraint2);
		method.addTestCase(testCase1);
		method.addTestCase(testCase2);

		MethodNode copy = method.makeClone();
		assertTrue(method.isMatch(copy));
	}

	@Test
	public void copyGlobalParameterTest(){
		BasicParameterNode parameter = new BasicParameterNode("parameter", "int", null);
		ChoiceNode choice1 = new ChoiceNode("choice1", "1", null);
		ChoiceNode choice11 = new ChoiceNode("choice11", "11", null);
		ChoiceNode choice12 = new ChoiceNode("choice12", "12", null);
		ChoiceNode choice2 = new ChoiceNode("choice1", "2", null);
		ChoiceNode choice21 = new ChoiceNode("choice11", "21", null);
		ChoiceNode choice22 = new ChoiceNode("choice12", "22", null);
		choice1.addChoice(choice11);
		choice1.addChoice(choice12);
		choice1.addChoice(choice21);
		choice1.addChoice(choice22);
		parameter.addChoice(choice1);
		parameter.addChoice(choice2);

		BasicParameterNode copy = parameter.makeClone();
		assertTrue(parameter.isMatch(copy));
	}

	@Test
	public void copyMethodParameterTest(){
		BasicParameterNode parameter = new BasicParameterNode("parameter", "int", "0", false, null);
		ChoiceNode choice1 = new ChoiceNode("choice1", "1", null);
		ChoiceNode choice11 = new ChoiceNode("choice11", "11", null);
		ChoiceNode choice12 = new ChoiceNode("choice12", "12", null);
		ChoiceNode choice2 = new ChoiceNode("choice1", "2", null);
		ChoiceNode choice21 = new ChoiceNode("choice11", "21", null);
		ChoiceNode choice22 = new ChoiceNode("choice12", "22", null);
		choice1.addChoice(choice11);
		choice1.addChoice(choice12);
		choice1.addChoice(choice21);
		choice1.addChoice(choice22);
		parameter.addChoice(choice1);
		parameter.addChoice(choice2);

		BasicParameterNode copy = parameter.makeClone();
		assertTrue(parameter.isMatch(copy));
	}

	@Test
	public void copyConstraintTest(){
		MethodNode method = new MethodNode("method", null);
		BasicParameterNode par1 = new BasicParameterNode("par1", "int", "0", false, null);
		BasicParameterNode par2 = new BasicParameterNode("par2", "int", "0", true, null);
		ChoiceNode choice1 = new ChoiceNode("choice1", "0", null);
		choice1.addLabel("label");
		par1.addChoice(choice1);

		ChoiceNode expectedChoice = new ChoiceNode("expected", "0", null);
		expectedChoice.setParent(par2);

		method.addParameter(par1);
		method.addParameter(par2);

		StatementArray precondition = new StatementArray(StatementArrayOperator.OR, null);
		precondition.addStatement(new StaticStatement(true, null));
		precondition.addStatement(RelationStatement.createRelationStatementWithChoiceCondition(par1, EMathRelation.EQUAL, choice1));
		precondition.addStatement(RelationStatement.createRelationStatementWithLabelCondition(par1, EMathRelation.NOT_EQUAL, "label"));
		ExpectedValueStatement postcondition = new ExpectedValueStatement(par2, expectedChoice, new JavaPrimitiveTypePredicate());

		ConstraintNode constraint = new ConstraintNode("constraint", new Constraint("constraint", ConstraintType.EXTENDED_FILTER, precondition, postcondition, null), null);
		method.addConstraint(constraint);

		ConstraintNode copy = constraint.makeClone();
		assertTrue(constraint.isMatch(copy));
	}

	@Test
	public void copyTestCaseTest(){
		MethodNode method = new MethodNode("method", null);
		BasicParameterNode par1 = new BasicParameterNode("par1", "int", "0", false, null);
		BasicParameterNode par2 = new BasicParameterNode("par2", "int", "0", true, null);
		ChoiceNode choice1 = new ChoiceNode("choice1", "0", null);
		par1.addChoice(choice1);
		ChoiceNode expectedChoice1 = new ChoiceNode("expected", "0", null);
		expectedChoice1.setParent(par2);
		ChoiceNode expectedChoice2 = new ChoiceNode("expected", "2", null);
		expectedChoice2.setParent(par2);
		TestCaseNode testCase = new TestCaseNode("test case 1", null, Arrays.asList(choice1, expectedChoice1));

		method.addParameter(par1);
		method.addParameter(par2);
		method.addTestCase(testCase);

		TestCaseNode copy = testCase.makeClone();
		assertTrue(testCase.isMatch(copy));
	}

	@Test
	public void copyChoiceTest(){
		ChoiceNode choice = new ChoiceNode("choice", "0", null);
		ChoiceNode choice1 = new ChoiceNode("choice1", "0", null);
		ChoiceNode choice11 = new ChoiceNode("choice11", "0", null);
		ChoiceNode choice12 = new ChoiceNode("choice12", "0", null);
		ChoiceNode choice2 = new ChoiceNode("choice2", "0", null);
		ChoiceNode choice21 = new ChoiceNode("choice21", "0", null);
		ChoiceNode choice22 = new ChoiceNode("choice22", "0", null);

		choice.addChoice(choice1);
		choice.addChoice(choice2);
		choice1.addChoice(choice11);
		choice1.addChoice(choice12);
		choice2.addChoice(choice21);
		choice2.addChoice(choice22);

		ChoiceNode copy = choice.makeClone();
		assertTrue(choice.isMatch(copy));
	}

	@Test
	public void copyStaticStatementTest(){
		StaticStatement statement1 = new StaticStatement(true, null);
		StaticStatement statement2 = new StaticStatement(false, null);

		StaticStatement copy1 = statement1.makeClone();
		StaticStatement copy2 = statement2.makeClone();

		assertTrue(statement1.isEqualTo(copy1));
		assertTrue(statement2.isEqualTo(copy2));
	}

	@Test
	public void copyStatementArrayTest(){
		for(StatementArrayOperator operator : new StatementArrayOperator[]{StatementArrayOperator.AND, StatementArrayOperator.OR}){
			StatementArray array = new StatementArray(operator, null);
			array.addStatement(new StaticStatement(true, null));
			array.addStatement(new StaticStatement(false, null));
			array.addStatement(new StaticStatement(true, null));
			array.addStatement(new StaticStatement(false, null));

			StatementArray copy = array.makeClone();
			assertTrue(array.isEqualTo(copy));
		}
	}

	@Test
	public void choiceStatementTest(){
		BasicParameterNode parameter = new BasicParameterNode("parameter", "int", "65", false, null);
		ChoiceNode choice = new ChoiceNode("choice", "876", null);
		parameter.addChoice(choice);
		choice.addLabel("label");

		RelationStatement statement1 = 
				RelationStatement.createRelationStatementWithChoiceCondition(parameter, EMathRelation.EQUAL, choice);
		RelationStatement statement2 = 
				RelationStatement.createRelationStatementWithLabelCondition(parameter, EMathRelation.EQUAL, "label");

		RelationStatement copy1 = statement1.makeClone();
		RelationStatement copy2 = statement2.makeClone();

		assertTrue(statement1.isEqualTo(copy1));
		assertTrue(statement2.isEqualTo(copy2));
	}

	@Test
	public void expectedStatementTest(){
		BasicParameterNode parameter = new BasicParameterNode("parameter", "int", "65", true, null);
		ChoiceNode choice = new ChoiceNode("expected", "876", null);
		choice.setParent(parameter);

		ExpectedValueStatement statement = new ExpectedValueStatement(parameter, choice, new JavaPrimitiveTypePredicate());
		ExpectedValueStatement copy = statement.makeClone();
		assertTrue(statement.isEqualTo(copy));
	}
	//	RootNode fRoot;
	//
	//	ClassNode fClass1;
	//	ClassNode fClass2;
	//	MethodNode fMethod1;
	//	MethodNode fMethod2;
	//	MethodParameterNode fPartCat1;
	//	MethodParameterNode fPartCat2;
	//	MethodParameterNode fExCat1;
	//	MethodParameterNode fExCat2;
	//	ChoiceNode fChoice1;
	//	ChoiceNode fChoice2;
	//	ChoiceNode fChoice3;
	//
	//	String fLabel1;
	//	String fLabel2;
	//
	//	// ConstraintNode fConNode1;
	//	// ConstraintNode fConNode2;
	//
	//	@Before
	//	public void setup(){
	//		fRoot = new RootNode("Model");
	//		fClass1 = new ClassNode("com.ecfeed.model.Class1");
	//		fClass2 = new ClassNode("com.ecfeed.model.Class2");
	//		fMethod1 = new MethodNode("firstMethod");
	//		fMethod2 = new MethodNode("secondMethod");
	//		fPartCat1 = new MethodParameterNode("pcat1", "type", "0", false);
	//		fPartCat2 = new MethodParameterNode("pcat2", "type2", "0", false);
	//		fExCat1 = new MethodParameterNode("ecat1", "type", "0", true);
	//		fExCat1.setDefaultValueString("value1");
	//		fExCat2 = new MethodParameterNode("ecat2", "type", "0", true);
	//		fExCat2.setDefaultValueString("value2");
	//		fChoice1 = new ChoiceNode("p1", "value1");
	//		fChoice2 = new ChoiceNode("p2", "value2");
	//		fChoice3 = new ChoiceNode("p3", "value3");
	//		fLabel1 = "label1";
	//		fLabel2 = "label2";
	//
	//		fRoot.addClass(fClass1);
	//		fRoot.addClass(fClass2);
	//		fClass1.addMethod(fMethod1);
	//		fClass2.addMethod(fMethod2);
	//		fMethod1.addParameter(fPartCat1);
	//		fMethod1.addParameter(fExCat1);
	//		fMethod2.addParameter(fPartCat2);
	//		fMethod2.addParameter(fExCat2);
	//		fPartCat1.addChoice(fChoice1);
	//		fPartCat2.addChoice(fChoice3);
	//		fChoice1.addChoice(fChoice2);
	//		fChoice1.addLabel(fLabel1);
	//		fChoice2.addLabel(fLabel2);
	//	}
	//
	//	public void testNode(Abstract Node node, Abstract Node copy){
	//		assertTrue(node.getClass().isInstance(copy));
	//		assertNotEquals(node, copy);
	//		assertEquals(node.getName(), copy.getName());
	//	}
	//
	//	public void testParent(Abstract Node node, Abstract Node parent, boolean isParent){
	//		if(isParent)
	//			assertEquals(node.getParent(), parent);
	//		else
	//			assertNotEquals(node.getParent(), parent);
	//	}
	//
	//	public void testChoices(ChoiceNode choice, ChoiceNode copy){
	//		testNode(choice, copy);
	//		assertEquals(choice.getValueString(), copy.getValueString());
	//	}
	//
	//	public void testChoiceLabels(ChoiceNode choice, ChoiceNode copy){
	//		assertEquals(choice.getLabels().size(), copy.getLabels().size());
	//		assertEquals(choice.getChildren().size(), copy.getChildren().size());
	//		// contains all and no more labels?
	//		assertTrue(copy.getLabels().containsAll(choice.getLabels()));
	//		assertTrue(choice.getLabels().containsAll(copy.getLabels()));
	//	}
	//
	//	public void testChoiceChildrenLabels(ChoiceNode childcopy, String parentlabel, String childlabel){
	//		assertTrue(childcopy.getLabels().contains(childlabel));
	//		assertTrue(childcopy.getAllLabels().contains(parentlabel));
	//	}
	//
	//	@Test
	//	public void choiceCopyTest(){
	//		// single choice copied properly?
	//		ChoiceNode copy = fChoice3.getCopy();
	//		testChoices(fChoice3, copy);
	//		testParent(copy, fChoice3.getParent(), true);
	//		// hierarchical choice copy tests
	//		// labels copied properly?
	//		copy = fChoice1.getCopy();
	//		testChoiceLabels(fChoice1, fChoice1.getCopy());
	//		// children copied properly?
	//		ChoiceNode childcopy = (ChoiceNode)copy.getChild(fChoice2.getName());
	//		testChoices(fChoice2, childcopy);
	//		testParent(childcopy, copy, true);
	//		testParent(fChoice2, childcopy.getParent(), false);
	//
	//		// children labels copied properly?
	//		testChoiceChildrenLabels(childcopy, fLabel1, fLabel2);
	//	}
	//
	//	public void testChoicesParentParameters(MethodParameterNode parameter, MethodParameterNode copy, String parentlabel, String childlabel){
	//		testNode(parameter, copy);
	//		assertEquals(parameter.getChildren().size(), copy.getChildren().size());
	//		assertEquals(parameter.getAllChoiceNames().size(), copy.getAllChoiceNames().size());
	//
	//		// choices copied properly?
	//		ChoiceNode choice = parameter.getChoices().get(0);
	//		ChoiceNode choicecopy = copy.getChoice(choice.getName());
	//		testChoices(choicecopy, choice);
	//		testParent(choicecopy, copy, true);
	//		// labels copied properly?
	//		assertTrue(copy.getLeafLabels().contains(parentlabel));
	//		assertTrue(copy.getLeafLabels().contains(childlabel));
	//		testChoiceLabels(choice, choicecopy);
	//		// children choices copied properly?
	//		ChoiceNode choiceChild = choice.getChoices().get(0);
	//		ChoiceNode choicecopyChild = choicecopy.getChoice(choiceChild.getName());
	//		testChoices(choicecopyChild, choiceChild);
	//		testParent(choicecopyChild, choicecopy, true);
	//		// children choice labels copied properly?
	//		testChoiceChildrenLabels(choicecopyChild, parentlabel, childlabel);
	//	}
	//
	//	@Test
	//	public void choicesParentParameterCopyTest(){
	//		MethodParameterNode copy = fPartCat1.getCopy();
	//		// parameters copied properly?
	//		testChoicesParentParameters(fPartCat1, copy, fLabel1, fLabel2);
	//		testParent(copy, fPartCat1.getParent(), true);
	//	}
	//
	//	public void testExpectedParameters(MethodParameterNode parameter, MethodParameterNode copy){
	//		testNode(parameter, copy);
	//		String choice = parameter.getDefaultValue();
	//		String choicecopy = copy.getDefaultValue();
	//		assertEquals(choice, choicecopy);
	////		testChoices(choice, choicecopy);
	////		testParent(choice, choicecopy.getParent(), false);
	//	}
	//
	//	@Test
	//	public void expectedParameterCopyTest(){
	//		MethodParameterNode copy = fExCat1.getCopy();
	//		testExpectedParameters(fExCat1, copy);
	//		testParent(copy, fExCat1.getParent(), true);
	//	}
	//
	//	public void testMethods(MethodNode method, MethodNode copy, String parentlabel, String childlabel){
	//		testNode(method, copy);
	//		// Test choices parent parameter
	//		MethodParameterNode partcat = method.getMethodParameters(false).get(0);
	//		MethodParameterNode copypartcat = copy.getMethodParameter(partcat.getName());
	//		testChoicesParentParameters(partcat, copypartcat, parentlabel, childlabel);
	//		testParent(copypartcat, copy, true);
	//		// Test expected parameter
	//		MethodParameterNode expcat = method.getMethodParameters(true).get(0);
	//		MethodParameterNode copyexpcat = copy.getMethodParameter(expcat.getName());
	//		testExpectedParameters(fExCat1, copyexpcat);
	//		testParent(copyexpcat, copy, true);
	//	}
	//
	//	@Test
	//	public void methodCopyTest(){
	//		MethodNode copy = fMethod1.getCopy();
	//		if(copy == null)System.out.println("COPY!!!");
	//		testMethods(fMethod1, copy, fLabel1, fLabel2);
	//		testParent(fMethod1, copy.getParent(), true);
	//	}
	//
	//	public void testClasses(ClassNode classnode, ClassNode copy, String parentlabel, String childlabel){
	//		testNode(classnode, copy);
	//
	//		MethodNode method = classnode.getMethods().get(0);
	//		MethodNode copymeth = copy.getMethod(method.getName(), method.getParametersTypes());
	//
	//		testMethods(method, copymeth, parentlabel, childlabel);
	//		testParent(copymeth, copy, true);
	//	}
	//
	//	@Test
	//	public void classCopyTest(){
	//		ClassNode copy = fClass1.getCopy();
	//		testClasses(fClass1, copy, fLabel1, fLabel2);
	//		testParent(copy, fClass1.getParent(), true);
	//	}
	//
	//	public void testRoots(RootNode root, RootNode copy, String parentlabel, String childlabel){
	//		testNode(root, copy);
	//
	//		ClassNode classnode = root.getClasses().get(0);
	//		ClassNode copyclass = copy.getClassModel(classnode.getName());
	//
	//		testClasses(classnode, copyclass, parentlabel, childlabel);
	//		testParent(copyclass, copy, true);
	//	}
	//
	//	@Test
	//	public void rootCopyTest(){
	//		RootNode copy = fRoot.getCopy();
	//		testRoots(fRoot, copy, fLabel1, fLabel2);
	//		this.testParent(copy, null, true);
	//	}
	//
}