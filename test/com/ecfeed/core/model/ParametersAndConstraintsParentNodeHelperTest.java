/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Test;

import com.ecfeed.core.model.utils.BasicParameterWithChoice;

public class ParametersAndConstraintsParentNodeHelperTest {
	
	@Test
	public void getParametersWithChoicesForBasicParameterTest1() {
		
		RootNode rootNode = new RootNode("root", null);
		
		ClassNode classNode = RootNodeHelper.addNewClassNode(rootNode, "class", true, null);
		
		MethodNode methodNode = ClassNodeHelper.addNewMethod(classNode, "method", true, null);
		
		BasicParameterNode basicParameterNode = 
				MethodNodeHelper.addNewBasicParameter(methodNode, "par1", "String", "", true, null);
		
		BasicParameterNodeHelper.addNewChoice(basicParameterNode, "choice", "c1", false, true, null);
		
		List<BasicParameterWithChoice> parametersWithChoices =
				ParametersAndConstraintsParentNodeHelper.getParametersWithChoicesUsedInConstraintsForLocalTopParameter(
						basicParameterNode);
		
		assertEquals(0, parametersWithChoices.size());
	}

	

}
