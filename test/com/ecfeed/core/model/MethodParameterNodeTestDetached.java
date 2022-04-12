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

import org.junit.Test;

public class MethodParameterNodeTestDetached {

	@Test
	public void attachDetachParameterNodeTest() {

		MethodNode methodNode = new MethodNode("method", null);

		// create and add parameter, choice

		String parameter1 = "par1";

		MethodParameterNode methodParameterNode = addParameterToMethod(methodNode, parameter1, "String");		

		methodNode.detachParameterNode(parameter1);
	}

	private MethodParameterNode addParameterToMethod(MethodNode methodNode, String name, String type) {

		MethodParameterNode methodParameterNode = new MethodParameterNode(name, type, "0", false, null);
		methodNode.addParameter(methodParameterNode);

		return methodParameterNode;
	}

}
