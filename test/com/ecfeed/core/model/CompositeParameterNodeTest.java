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
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

public class CompositeParameterNodeTest {

	@Test
	public void basicTest() {
		
		String name = "parameterName";
		
		CompositeParameterNode compositeParameterNode = new CompositeParameterNode(name, null);
		
		assertEquals(name, compositeParameterNode.getName());
	}

	@Test
	public void getChilderTest() {
		
		String parameterName1 = "name1";
		String parameterName2 = "name2";
		
		BasicParameterNode basicParameterNode1 = new BasicParameterNode(parameterName1, "int", null);
		BasicParameterNode basicParameterNode2 = new BasicParameterNode(parameterName2, "int", null);
		
		CompositeParameterNode compositeParameterNode = new CompositeParameterNode(parameterName1, null);
		
		compositeParameterNode.addParameter(basicParameterNode1);
		compositeParameterNode.addParameter(basicParameterNode2);

		List<IAbstractNode>children = compositeParameterNode.getChildren();
		assertEquals(2, children.size());
	}
	
}
