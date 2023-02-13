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
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.MethodParameterNodeHelper;
import com.ecfeed.core.model.ParametersAndConstraintsParentNodeHelper;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.RootNodeHelper;
import com.ecfeed.core.model.utils.ParametersContainer.ParameterType;

public class ParametersContainerTest {

	@Test
	public void localParameters() {

		RootNode rootNode = new RootNode("Root", null);

		// class node

		ClassNode classNode = RootNodeHelper.addClassNodeToRoot(rootNode, "Class", null);

		// method node

		MethodNode methodNode = ClassNodeHelper.addMethodToClass(classNode, "Method", null);

		// parameter 1 of method 

		BasicParameterNode methodParameterNode1 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(methodNode, "MP1", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode1, "MC1", "MC1");

		// parameter 2 of method

		BasicParameterNode methodParameterNode2 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(methodNode, "MP2", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(methodParameterNode2, "MC2", "MC2");

		// composite parameter

		CompositeParameterNode compositeParameterNode = MethodNodeHelper.addCompositeParameter(methodNode, "S1", null);

		// parameter 1 of composite

		BasicParameterNode basicParameterNode1 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(compositeParameterNode, "P1", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(basicParameterNode1, "C1", "C1");

		// parameter 2 of composite

		BasicParameterNode basicParameterNode2 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(compositeParameterNode, "P2", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(basicParameterNode2, "C2", "C2");

		// parameter 0 of composite

		BasicParameterNode basicParameterNode0 = 
				ParametersAndConstraintsParentNodeHelper.addBasicParameterToParent(compositeParameterNode, "P0", "String");

		MethodParameterNodeHelper.addChoiceToMethodParameter(basicParameterNode0, "C0", "C0");

		// parameters container

		ParametersContainer parametersContainer = new ParametersContainer();
		parametersContainer.calculateParametersData(methodNode, ParameterType.STANDARD);

		List<String> parameterNames = parametersContainer.getParameterNames();

		assertEquals(5, parameterNames.size());

		assertEquals("MP1", parameterNames.get(0));
		assertEquals("MP2", parameterNames.get(1));		
		assertEquals("S1:P0", parameterNames.get(2));
		assertEquals("S1:P1", parameterNames.get(3));
		assertEquals("S1:P2", parameterNames.get(4));

		BasicParameterNode resultMethodParameter1 = parametersContainer.findBasicParameter("MP1", methodNode);
		BasicParameterNode resultMethodParameter2 = parametersContainer.findBasicParameter("MP2", methodNode);

		assertEquals(methodParameterNode1, resultMethodParameter1);
		assertEquals(methodParameterNode2, resultMethodParameter2);

		BasicParameterNode resultParameter0 = parametersContainer.findBasicParameter("S1:P0", methodNode);
		BasicParameterNode resultParameter1 = parametersContainer.findBasicParameter("S1:P1", methodNode);
		BasicParameterNode resultParameter2 = parametersContainer.findBasicParameter("S1:P2", methodNode);

		assertEquals(basicParameterNode0, resultParameter0);
		assertEquals(basicParameterNode1, resultParameter1);
		assertEquals(basicParameterNode2, resultParameter2);
	}

}
