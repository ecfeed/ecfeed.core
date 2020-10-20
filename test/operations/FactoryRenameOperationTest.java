/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package operations;

import com.ecfeed.core.model.*;
import com.ecfeed.core.operations.FactoryRenameOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationMessages;
import com.ecfeed.core.operations.RootOperationAddClass;
import com.ecfeed.core.utils.*;
import org.junit.Test;

import static org.junit.Assert.fail;

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

		MethodParameterNode methodParameterNode1 =
				new MethodParameterNode("par1", "int", "0", false, null);
		methodNode1.addParameter(methodParameterNode1);

		// add method2 with long parameter

		MethodNode methodNode2 = new MethodNode("method2", null);
		classNode.addMethod(methodNode2);

		MethodParameterNode methodParameterNode2 =
				new MethodParameterNode("par1", "long", "0", false, null);
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
		}

		// rename in java mode - the same method name - should succeed

		operation =
				FactoryRenameOperation.getRenameOperation(
						methodNode2,
						null,
						"method1",
						extLanguageManagerForJava);

		try {
			operation.execute();
		} catch (Exception e) {
			fail();
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

		MethodParameterNode methodParameterNode1 =
				new MethodParameterNode("par1", "int", "0", false, null);
		methodNode1.addParameter(methodParameterNode1);

		MethodParameterNode methodParameterNode2 =
				new MethodParameterNode("par2", "String", "0", false, null);
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
			TestHelper.checkExceptionMessage(e, JavaLanguageHelper.SPACES_ARE_NOT_ALLOWED_IN_NAME);
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

		MethodParameterNode methodParameterNode =
				new MethodParameterNode("par_1", "int", "0", false, null);

		ChoiceNode choiceNode1 = new ChoiceNode("choice_1", null, "1");
		methodParameterNode.addChoice(choiceNode1);

		ChoiceNode choiceNode2 = new ChoiceNode("choice_2", null, "2");
		methodParameterNode.addChoice(choiceNode2);

		// rename in java mode - invalid name 1 - should fail

		IModelOperation operation =
				FactoryRenameOperation.getRenameOperation(
						choiceNode2,
						null,
						"choice 1",
						extLanguageManagerForSimple);

		try {
			operation.execute();
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(e, FactoryRenameOperation.PARTITION_NAME_NOT_UNIQUE_PROBLEM);
		}
	}

}
