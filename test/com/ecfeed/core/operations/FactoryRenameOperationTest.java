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

import static org.junit.Assert.fail;

import org.junit.Test;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import com.ecfeed.core.utils.RegexHelper;
import com.ecfeed.core.utils.SimpleLanguageHelper;
import com.ecfeed.core.utils.TestHelper;

public class FactoryRenameOperationTest {

	@Test
	public void renameClassTest() {

		RootNode rootNode = new RootNode("Root", null);

		// add class1

		ClassNode classNode1 = new ClassNode("com.Class1", null);

		rootNode.addClass(classNode1);

		// add class2

		ClassNode classNode2 = new ClassNode("com.xx.Class2", null);

		rootNode.addClass(classNode2);

		// rename in simple mode - the same classNames

		final ExtLanguageManagerForSimple extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		IModelOperation modelOperation1 =
				FactoryRenameOperation.getRenameOperation(
						classNode2,
						"com.xx",
						"Class1",
						extLanguageManagerForSimple);

		try {
			modelOperation1.execute();
			fail();
		} catch (Exception e) {
		}

		// rename in java mode

		final ExtLanguageManagerForJava extLanguageManagerForJava = new ExtLanguageManagerForJava();

		IModelOperation modelOperation2 =
				FactoryRenameOperation.getRenameOperation(
						classNode2,
						"com.xx",
						"Class1",
						extLanguageManagerForJava);

		try {
			modelOperation2.execute();
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void renameMethodWithoutParametersTest() {

		RootNode rootNode = new RootNode("Root", null);

		// add class1

		ClassNode classNode = new ClassNode("com.Class1", null);
		rootNode.addClass(classNode);

		MethodNode methodNode1 = new MethodNode("method1", null);
		classNode.addMethod(methodNode1);

		MethodNode methodNode2 = new MethodNode("method2", null);
		classNode.addMethod(methodNode2);

		final ExtLanguageManagerForSimple extLanguageManagerForSimple = new ExtLanguageManagerForSimple();
		final ExtLanguageManagerForJava extLanguageManagerForJava = new ExtLanguageManagerForJava();

		// rename in simple mode - the same method name

		IModelOperation operation =
				FactoryRenameOperation.getRenameOperation(
						methodNode2,
						null,
						"method1",
						extLanguageManagerForSimple);

		try {
			operation.execute();
			fail();
		} catch (Exception e) {
		}

		// rename in java mode - the same method name

		operation =
				FactoryRenameOperation.getRenameOperation(
						methodNode2,
						null,
						"method1",
						extLanguageManagerForJava);

		try {
			operation.execute();
			fail();
		} catch (Exception e) {
		}

		// rename in simple mode - the other method name

		operation =
				FactoryRenameOperation.getRenameOperation(
						methodNode2,
						null,
						"method2b",
						extLanguageManagerForSimple);

		try {
			operation.execute();
		} catch (Exception e) {
			fail();
		}

		// rename in java mode - the other method name

		operation =
				FactoryRenameOperation.getRenameOperation(
						methodNode2,
						null,
						"method2c",
						extLanguageManagerForSimple);

		try {
			operation.execute();
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void renameMethodWithOneParameterTest() {

		final ExtLanguageManagerForSimple extLanguageManagerForSimple = new ExtLanguageManagerForSimple();
		final ExtLanguageManagerForJava extLanguageManagerForJava = new ExtLanguageManagerForJava();

		RootNode rootNode = new RootNode("Root", null);

		ClassNode classNode = new ClassNode("com.Class1", null);
		rootNode.addClass(classNode);

		// add method1 with int parameter

		MethodNode methodNode1 = new MethodNode("method1", null);
		classNode.addMethod(methodNode1);

		BasicParameterNode methodParameterNode1 =
				new BasicParameterNode("par1", "int", "0", false, null);
		methodNode1.addParameter(methodParameterNode1);

		// add method2 with long parameter

		MethodNode methodNode2 = new MethodNode("method2", null);
		classNode.addMethod(methodNode2);

		BasicParameterNode methodParameterNode2 =
				new BasicParameterNode("par1", "long", "0", false, null);
		methodNode2.addParameter(methodParameterNode2);

		// rename in simple mode - the same method name - should fail

		IModelOperation operation =
				FactoryRenameOperation.getRenameOperation(
						methodNode2,
						null,
						"method1",
						extLanguageManagerForSimple);

		try {
			operation.execute();
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, "Method", "method1", "already exists");
		}

		// rename in java mode - the same method name

		operation =
				FactoryRenameOperation.getRenameOperation(
						methodNode2,
						null,
						"method1",
						extLanguageManagerForJava);

		try {
			operation.execute();
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, "Method", "method1", "already exists");
		}

		// rename in simple mode - the other method name

		operation =
				FactoryRenameOperation.getRenameOperation(
						methodNode2,
						null,
						"method2b",
						extLanguageManagerForSimple);

		try {
			operation.execute();
		} catch (Exception e) {
			fail();
		}

		// rename in java mode - the other method name

		operation =
				FactoryRenameOperation.getRenameOperation(
						methodNode2,
						null,
						"method2c",
						extLanguageManagerForSimple);

		try {
			operation.execute();
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void renameParameterTest() {

		final ExtLanguageManagerForSimple extLanguageManagerForSimple = new ExtLanguageManagerForSimple();
		final ExtLanguageManagerForJava extLanguageManagerForJava = new ExtLanguageManagerForJava();

		RootNode rootNode = new RootNode("Root", null);

		ClassNode classNode = new ClassNode("com.Class1", null);
		rootNode.addClass(classNode);

		// add method1 with int parameter

		MethodNode methodNode1 = new MethodNode("method1", null);
		classNode.addMethod(methodNode1);

		BasicParameterNode methodParameterNode1 =
				new BasicParameterNode("par1", "int", "0", false, null);
		methodNode1.addParameter(methodParameterNode1);

		BasicParameterNode methodParameterNode2 =
				new BasicParameterNode("par2", "String", "0", false, null);
		methodNode1.addParameter(methodParameterNode2);

		// rename in simple mode - the same parameter name - should fail

		IModelOperation operation =
				FactoryRenameOperation.getRenameOperation(
						methodParameterNode2,
						null,
						"par1",
						extLanguageManagerForSimple);

		try {
			operation.execute();
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, OperationMessages.PARAMETER_WITH_THIS_NAME_ALREADY_EXISTS);
		}

		// rename in java mode - the same parameter name - should fail

		operation =
				FactoryRenameOperation.getRenameOperation(
						methodParameterNode2,
						null,
						"par1",
						extLanguageManagerForJava);

		try {
			operation.execute();
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, OperationMessages.PARAMETER_WITH_THIS_NAME_ALREADY_EXISTS);
		}

		// rename in simple mode - other parameter name - should suceed

		operation =
				FactoryRenameOperation.getRenameOperation(
						methodParameterNode2,
						null,
						"par1b",
						extLanguageManagerForSimple);

		try {
			operation.execute();
		} catch (Exception e) {
			fail();
		}

		// rename in simple mode - invalid name 1 - should fail

		operation =
				FactoryRenameOperation.getRenameOperation(
						methodParameterNode2,
						null,
						"par_1b",
						extLanguageManagerForSimple);

		try {
			operation.execute();
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, SimpleLanguageHelper.UNDERLINE_CHARS_ARE_NOT_ALLOWED);
		}

		// rename in simple mode - invalid name 2 - should fail

		operation =
				FactoryRenameOperation.getRenameOperation(
						methodParameterNode2,
						null,
						"par1b+",
						extLanguageManagerForSimple);

		try {
			operation.execute();
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, RegexHelper.SHOULD_CONTAIN_ALPHANUMERIC_CHARACTERS);
		}

		// rename in java mode - other parameter name - should succeed

		operation =
				FactoryRenameOperation.getRenameOperation(
						methodParameterNode2,
						null,
						"par1c",
						extLanguageManagerForJava);

		try {
			operation.execute();
		} catch (Exception e) {
			fail();
		}

		// rename in java mode - invalid name 1 - should fail

		operation =
				FactoryRenameOperation.getRenameOperation(
						methodParameterNode2,
						null,
						"par 1c",
						extLanguageManagerForJava);

		try {
			operation.execute();
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, "Spaces are not allowed in text");
		}

		// rename in java mode - invalid name 1 - should fail

		operation =
				FactoryRenameOperation.getRenameOperation(
						methodParameterNode2,
						null,
						"p=ar1c",
						extLanguageManagerForJava);

		try {
			operation.execute();
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, RegexHelper.SHOULD_CONTAIN_ALPHANUMERIC_CHARACTERS);
		}

	}

	@Test
	public void renameChoiceForSimpleViewTest() {

		final ExtLanguageManagerForSimple extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		RootNode rootNode = new RootNode("root", null);

		ClassNode classNode = new ClassNode("class", null);
		rootNode.addClass(classNode);

		MethodNode  methodNode = new MethodNode("method",  null);
		classNode.addMethod(methodNode);

		BasicParameterNode methodParameterNode =
				new BasicParameterNode("par_1", "int", "0", false, null);
		methodNode.addParameter(methodParameterNode);

		ChoiceNode choiceNode1 = new ChoiceNode("choice_1", "1", null);
		methodParameterNode.addChoice(choiceNode1);

		ChoiceNode choiceNode2 = new ChoiceNode("choice_2", "2", null);
		methodParameterNode.addChoice(choiceNode2);

		// rename in java mode

		IModelOperation operation =
				FactoryRenameOperation.getRenameOperation(
						choiceNode2,
						null,
						"choice 1",
						extLanguageManagerForSimple);

		try {
			operation.execute();
		} catch (Exception e) {
			fail();
		}
	}

}
