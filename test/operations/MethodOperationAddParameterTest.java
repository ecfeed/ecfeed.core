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
import com.ecfeed.core.operations.MethodOperationAddParameter;
import com.ecfeed.core.operations.RootOperationAddClass;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.TestHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MethodOperationAddParameterTest {

	@Test
	public void addParameterForSingle() {

		IExtLanguageManager  extLanguageManagerForSimple = new ExtLanguageManagerForSimple();

		ClassNode classNode = new ClassNode("com.class1", null);

		// add method with int parameter

		MethodNode methodNode1 = new MethodNode("method", null);
		classNode.addMethod(methodNode1);

		MethodParameterNode methodParameterNode1 =
				new MethodParameterNode("arg0", "int", "0", false, null);

		MethodOperationAddParameter methodOperationAddParameter1 =
				new MethodOperationAddParameter(
					methodNode1,
					methodParameterNode1,
					extLanguageManagerForSimple);

		try {
			methodOperationAddParameter1.execute();
		} catch (Exception e) {
			fail();
		}

		// again add method with long parameter - should fail in simple mode

		MethodNode methodNode2 = new MethodNode("method", null);
		classNode.addMethod(methodNode2);

		MethodParameterNode methodParameterNode2 =
				new MethodParameterNode("arg0", "long", "0", false, null);

		MethodOperationAddParameter methodOperationAddParameter2 =
				new MethodOperationAddParameter(
						methodNode2,
						methodParameterNode2,
						extLanguageManagerForSimple);

		try {
			methodOperationAddParameter2.execute();
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(
					e,
					"class1",
					ClassNodeHelper.CONTAINS_METHOD_WITH_IDENTICAL_SIGNATURE);
		}
	}

	@Test
	public void addParameterForJava() {

		IExtLanguageManager  extLanguageManagerForJava = new ExtLanguageManagerForJava();

		ClassNode classNode = new ClassNode("com.class1", null);

		// add method with int parameter

		MethodNode methodNode1 = new MethodNode("method", null);
		classNode.addMethod(methodNode1);

		MethodParameterNode methodParameterNode1 =
				new MethodParameterNode("arg0", "int", "0", false, null);

		MethodOperationAddParameter methodOperationAddParameter1 =
				new MethodOperationAddParameter(
						methodNode1,
						methodParameterNode1,
						extLanguageManagerForJava);

		try {
			methodOperationAddParameter1.execute();
		} catch (Exception e) {
			fail();
		}

		// again add method with long parameter

		MethodNode methodNode2 = new MethodNode("method", null);
		classNode.addMethod(methodNode2);

		MethodParameterNode methodParameterNode2 =
				new MethodParameterNode("arg0", "long", "0", false, null);

		MethodOperationAddParameter methodOperationAddParameter2 =
				new MethodOperationAddParameter(
						methodNode2,
						methodParameterNode2,
						extLanguageManagerForJava);

		try {
			methodOperationAddParameter2.execute();
		} catch (Exception e) {
			fail();
		}

		// again add the second method with long parameter

		MethodNode methodNode3 = new MethodNode("method", null);
		classNode.addMethod(methodNode3);

		MethodParameterNode methodParameterNode3 =
				new MethodParameterNode("arg0", "long", "0", false, null);

		MethodOperationAddParameter methodOperationAddParameter3 =
				new MethodOperationAddParameter(
						methodNode3,
						methodParameterNode3,
						extLanguageManagerForJava);

		try {
			methodOperationAddParameter3.execute();
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(
					e,
					"com.class1",
					ClassNodeHelper.CONTAINS_METHOD_WITH_IDENTICAL_SIGNATURE);

		}
	}

}
