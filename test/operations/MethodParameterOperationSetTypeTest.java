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
import com.ecfeed.core.operations.MethodParameterOperationSetType;
import com.ecfeed.core.operations.RootOperationAddClass;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import com.ecfeed.core.utils.TestHelper;
import org.junit.Test;

import static org.junit.Assert.fail;

public class MethodParameterOperationSetTypeTest {

	@Test
	public void test1() {

		RootNode rootNode = new RootNode("Root", null);

		ClassNode classNode = new ClassNode("com.Class1", null);
		rootNode.addClass(classNode);

		// add method1 with int parameter

		MethodNode methodNode1 = new MethodNode("method", null);
		classNode.addMethod(methodNode1);

		MethodParameterNode methodParameterNode1 =
				new MethodParameterNode("par1", "int", "0", false, null);

		methodNode1.addParameter(methodParameterNode1);

		// add method2 with String parameter

		MethodNode methodNode2 = new MethodNode("method", null);
		classNode.addMethod(methodNode2);

		MethodParameterNode methodParameterNode2 =
				new MethodParameterNode("par1", "String", "x", false, null);

		methodNode2.addParameter(methodParameterNode2);

		// seting param of method2 to Number in simple mode

		try {
			MethodParameterOperationSetType methodParameterOperationSetType =
					new MethodParameterOperationSetType(
							methodParameterNode2, "Number", new ExtLanguageManagerForSimple(), null);
			methodParameterOperationSetType.execute();
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(
					e,
					"Class1",
					ClassNodeHelper.CONTAINS_METHOD_WITH_IDENTICAL_SIGNATURE);
		}

		// seting param of method1 to Text in simple mode

		try {
			MethodParameterOperationSetType methodParameterOperationSetType =
					new MethodParameterOperationSetType(
							methodParameterNode1, "Text", new ExtLanguageManagerForSimple(), null);
			methodParameterOperationSetType.execute();
			fail();
		} catch (Exception e) {
			TestHelper.checkExceptionMessage(
					e,
					"Class1",
					ClassNodeHelper.CONTAINS_METHOD_WITH_IDENTICAL_SIGNATURE);
		}

		// seting param of method1 to Logical in simple mode

//		try {
//			MethodParameterOperationSetType methodParameterOperationSetType =
//					new MethodParameterOperationSetType(
//							methodParameterNode1, "Logical", new ExtLanguageManagerForSimple(), null);
//			methodParameterOperationSetType.execute();
//		} catch (Exception e) {
//			fail();
//		}

		//  TODO SIMPLE-VIEW add more cases
	}

}
