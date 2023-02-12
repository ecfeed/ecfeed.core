/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNodeHelper;
import com.ecfeed.core.model.ParametersAndConstraintsParentNodeHelper;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;

public class ParametersContainerTest{

	@Test
	public void singleMethod() {

		RootNode rootNode = new RootNode("Root", null);

		// class node

		ClassNode classNode = RootNodeHelper.addClassNodeToRoot(rootNode, "Class", null);

		// method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		// parameter 1

		BasicParameterNode methodParameterNode1 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(methodNode, "MP1", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode1, "MC1", "MC1");


		BasicParameterNode methodParameterNode2 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(methodNode, "MP2", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode2, "MC2", "MC2");

		ParametersContainer parametersContainer = new ParametersContainer();

		List<String> parameterNames = parametersContainer.initialize(methodNode, false);

		assertEquals(2, parameterNames.size());

		assertEquals("MP1", parameterNames.get(0));
		assertEquals("MP2", parameterNames.get(1));

		BasicParameterNode resultParameter1 = parametersContainer.findBasicParameter("MP1", methodNode);
		BasicParameterNode resultParameter2 = parametersContainer.findBasicParameter("MP2", methodNode);

		assertEquals(methodParameterNode1, resultParameter1);
		assertEquals(methodParameterNode2, resultParameter2);
	}

}
