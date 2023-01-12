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
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.operations.RemoveBasicParameterOperation;
import com.ecfeed.core.utils.ExtLanguageManagerForJava;
import com.ecfeed.core.utils.ExtLanguageManagerForSimple;

public class RemoveBasicParameterOperationTest {

	@Test
	public void removeFirstParameter() {

		ClassNode classNode = new ClassNode("com.Class1", null);

		MethodNode methodNode2 = new MethodNode("method2", null);
		classNode.addMethod(methodNode2);

		BasicParameterNode methodParameterNode = new BasicParameterNode("arg0", "int", "0", false, null);
		methodNode2.addParameter(methodParameterNode);

		RemoveBasicParameterOperation methodOperationRemoveParameter
				= new RemoveBasicParameterOperation(methodNode2, methodParameterNode, new ExtLanguageManagerForSimple());

		try  {
			methodOperationRemoveParameter.execute();
		}  catch (Exception e) {
			fail();
		}
	}

	@Test
	public void removeParameterInJavaMode() {

		ClassNode classNode = new ClassNode("com.Class1", null);

		MethodNode methodNode1 = new MethodNode("method", null);
		classNode.addMethod(methodNode1);

		BasicParameterNode methodParameterNode1 = new BasicParameterNode("arg0", "int", "0", false, null);
		methodNode1.addParameter(methodParameterNode1);

		BasicParameterNode methodParameterNode2 = new BasicParameterNode("arg1", "long", "0", false, null);
		methodNode1.addParameter(methodParameterNode2);

		RemoveBasicParameterOperation methodOperationRemoveParameter
				= new RemoveBasicParameterOperation(methodNode1, methodParameterNode2, new ExtLanguageManagerForJava());

		try  {
			methodOperationRemoveParameter.execute();
		}  catch (Exception e) {
			fail();
		}
	}

}
