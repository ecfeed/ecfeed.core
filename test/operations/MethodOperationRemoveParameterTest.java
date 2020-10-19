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
import com.ecfeed.core.operations.MethodOperationRemoveParameter;
import com.ecfeed.core.operations.RootOperationAddClass;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;
import com.ecfeed.core.utils.TestHelper;
import org.junit.Test;

import static org.junit.Assert.fail;

public class MethodOperationRemoveParameterTest {

	@Test
	public void removeFirstParameter() {

		ClassNode classNode = new ClassNode("com.Class1", null);

		MethodNode methodNode2 = new MethodNode("method", null);
		classNode.addMethod(methodNode2);

		MethodParameterNode methodParameterNode = new MethodParameterNode("arg0", "int", "0", false, null);
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

		MethodParameterNode methodParameterNode1 = new MethodParameterNode("arg0", "int", "0", false, null);
		methodNode1.addParameter(methodParameterNode1);

		MethodParameterNode methodParameterNode2 = new MethodParameterNode("arg1", "long", "0", false, null);
		methodNode1.addParameter(methodParameterNode2);


		MethodNode methodNode2= new MethodNode("method", null);
		classNode.addMethod(methodNode2);

		MethodParameterNode methodParameterNode3 = new MethodParameterNode("arg0", "int", "0", false, null);
		methodNode2.addParameter(methodParameterNode3);


		MethodOperationRemoveParameter methodOperationRemoveParameter
				= new MethodOperationRemoveParameter(methodNode1, methodParameterNode2, new ExtLanguageManagerForJava());

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

}
