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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MethodDeployerTest {

	@Test
	public void deployNull() {

		try {
			MethodDeployer.deploy(null);
		} catch (Exception e) {
		}
	}
	
	@Test
	public void deployMethodWithoutParameters() {

		MethodNode sourceMethod = new MethodNode("method");

		MethodNode deployedMethod = MethodDeployer.deploy(sourceMethod);

		assertNotNull(deployedMethod);
		assertFalse(sourceMethod.hashCode() == deployedMethod.hashCode());
		assertEquals(sourceMethod.getName(), deployedMethod.getName());

		assertEquals(0, deployedMethod.getParameters().size());
	}

	@Test
	public void deployMethodWithOneParameter() {

		MethodNode sourceMethod = new MethodNode("method");
		MethodParameterNode methodParameterNode = new MethodParameterNode("parameter", "String", "A", true);
		sourceMethod.addParameter(methodParameterNode);

		MethodNode deployedMethod = MethodDeployer.deploy(sourceMethod);

		assertEquals(1, deployedMethod.getParameters().size());
		
		MethodParameterNode sourceParameter = (MethodParameterNode)sourceMethod.getParameters().get(0);
		MethodParameterNode deployedParameter = (MethodParameterNode)deployedMethod.getParameters().get(0);
		
		assertTrue(sourceParameter.hashCode() != deployedParameter.hashCode());

		assertEquals(sourceParameter.getName(), deployedParameter.getName());
		assertEquals(sourceParameter.getType(), deployedParameter.getType());
		assertEquals(sourceParameter.getDefaultValue(), deployedParameter.getDefaultValue());
		assertEquals(sourceParameter.isExpected(), deployedParameter.isExpected());
	}

}
