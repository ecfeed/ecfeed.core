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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MethodParameterNodeTestForDetached {

	@Test
	public void attachDetachParameterNodeTest() {

		// TODO ADD CONSTRAINTS AND TEST CASES

		MethodNode methodNode = new MethodNode("method", null);

		// create and add parameter, choice

		String par1Name = "par1";

		addParameterToMethod(methodNode, par1Name, "String");		

		methodNode.detachParameterNode(par1Name);

		assertEquals(0, methodNode.getParametersCount());
		assertEquals(1, methodNode.getDetachedParametersCount());

		String newPar1Name = "newPar1";
		MethodParameterNode methodParameterNode = addParameterToMethod(methodNode, newPar1Name, "String");

		methodNode.attachParameterNode(par1Name, newPar1Name);

		assertEquals(1, methodNode.getParametersCount());
		assertEquals(0, methodNode.getDetachedParametersCount());
	}

	private MethodParameterNode addParameterToMethod(MethodNode methodNode, String name, String type) {

		MethodParameterNode methodParameterNode = new MethodParameterNode(name, type, "0", false, null);
		methodNode.addParameter(methodParameterNode);

		return methodParameterNode;
	}

}
