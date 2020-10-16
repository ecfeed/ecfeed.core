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

import static org.junit.Assert.fail;

public class MethodOperationAddParameterTest {

	@Test
	public void test1() {

		RootNode rootNode = new RootNode("Root", null);

		ClassNode classNode = new ClassNode("com.Class1", null);
		rootNode.addClass(classNode);

		// method 1

		MethodNode methodNode1 = new MethodNode("method1", null);
		classNode.addMethod(methodNode1);

		// TODO SIMPLE-VIEW
//		MethodInterface methodInterface
//		MethodOperationAddParameter methodOperationAddParameter =
//				new MethodOperationAddParameter(
//				methodNode1,
//				MethodParameterNode methodParameterNode,
//		IExtLanguageManager extLanguageManager) {



	}

}
