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

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.core.operations.FactoryRenameOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.RootOperationAddClass;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import com.ecfeed.core.utils.TestHelper;
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

	// TODO SIMPLE-VIEW rename parameter
}