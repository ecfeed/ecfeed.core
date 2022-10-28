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

import static org.junit.Assert.fail;

import org.junit.Test;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.operations.MethodOperationRemoveParameter;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import com.ecfeed.core.utils.TestHelper;

public class MethodOperationRemoveParameterTest {

	@Test
	public void removeFirstParameter() {

		ClassNode classNode = new ClassNode("com.Class1", null);

		MethodNode methodNode2 = new MethodNode("method", null);
		classNode.addMethod(methodNode2);

		BasicParameterNode methodParameterNode = new BasicParameterNode("arg0", "int", "0", false, null);
		methodNode2.addParameter(methodParameterNode);

		MethodNode methodNode1 = new MethodNode("method", null);
		classNode.addMethod(methodNode1);

		MethodOperationRemoveParameter methodOperationRemoveParameter
				= new MethodOperationRemoveParameter(methodNode2, methodParameterNode, new ExtLanguageManagerForSimple());

		try  {
			methodOperationRemoveParameter.execute();
			fail();
		}  catch (Exception e) {
			TestHelper.checkExceptionMessage(
					e,
					"Class1",
					ClassNodeHelper.CONTAINS_METHOD_WITH_IDENTICAL_SIGNATURE);
		}
	}

	@Test
	public void removeSecondOfTwoParametersInJavaMode() {

		ClassNode classNode = new ClassNode("com.Class1", null);

		MethodNode methodNode1 = new MethodNode("method", null);
		classNode.addMethod(methodNode1);

		BasicParameterNode methodParameterNode1 = new BasicParameterNode("arg0", "int", "0", false, null);
		methodNode1.addParameter(methodParameterNode1);

		BasicParameterNode methodParameterNode2 = new BasicParameterNode("arg1", "long", "0", false, null);
		methodNode1.addParameter(methodParameterNode2);


		MethodNode methodNode2= new MethodNode("method", null);
		classNode.addMethod(methodNode2);

		BasicParameterNode methodParameterNode3 = new BasicParameterNode("arg0", "int", "0", false, null);
		methodNode2.addParameter(methodParameterNode3);


		MethodOperationRemoveParameter methodOperationRemoveParameter
				= new MethodOperationRemoveParameter(methodNode1, methodParameterNode2, new ExtLanguageManagerForJava());

		try  {
			methodOperationRemoveParameter.execute();
			fail();
		}  catch (Exception e) {
			TestHelper.checkExceptionMessage(
					e,
					"com.Class1",
					ClassNodeHelper.CONTAINS_METHOD_WITH_IDENTICAL_SIGNATURE);
		}
	}

	@Test
	public void removeSecondOfTwoParametersInSimpleMode() {

		ClassNode classNode = new ClassNode("com.Class1", null);

		MethodNode methodNode1 = new MethodNode("method", null);
		classNode.addMethod(methodNode1);

		BasicParameterNode methodParameterNode1 = new BasicParameterNode("arg0", "long", "0", false, null);
		methodNode1.addParameter(methodParameterNode1);

		BasicParameterNode methodParameterNode2 = new BasicParameterNode("arg1", "long", "0", false, null);
		methodNode1.addParameter(methodParameterNode2);


		MethodNode methodNode2= new MethodNode("method", null);
		classNode.addMethod(methodNode2);

		BasicParameterNode methodParameterNode3 = new BasicParameterNode("arg0", "int", "0", false, null);
		methodNode2.addParameter(methodParameterNode3);


		MethodOperationRemoveParameter methodOperationRemoveParameter
				= new MethodOperationRemoveParameter(methodNode1, methodParameterNode2, new ExtLanguageManagerForSimple());

		try  {
			methodOperationRemoveParameter.execute();
			fail();
		}  catch (Exception e) {
			TestHelper.checkExceptionMessage(
					e,
					"Class1",
					ClassNodeHelper.CONTAINS_METHOD_WITH_IDENTICAL_SIGNATURE,
					"method(Number)");
		}
	}

}
