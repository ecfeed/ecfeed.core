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

import com.ecfeed.core.utils.EMathRelation;
import org.junit.Test;

import static org.junit.Assert.fail;

public class RelationStatementTest {

	@Test
	public void testStatementWithParameterCondition() {

		testCreateStatementWithParameterCondition("int", "int", true);
		testCreateStatementWithParameterCondition("int", "boolean", false);
		testCreateStatementWithParameterCondition("boolean", "int", false);
		testCreateStatementWithParameterCondition("boolean", "boolean", true);

		testCreateStatementWithParameterCondition("byte", "int", true);
		testCreateStatementWithParameterCondition("int", "byte", true);

		testCreateStatementWithParameterCondition("int", "short", true);
		testCreateStatementWithParameterCondition("short", "int", true);

		testCreateStatementWithParameterCondition("long", "int", true);
		testCreateStatementWithParameterCondition("int", "long", true);

		testCreateStatementWithParameterCondition("int", "float", true);
		testCreateStatementWithParameterCondition("float", "int", true);

		testCreateStatementWithParameterCondition("float", "double", true);
		testCreateStatementWithParameterCondition("double", "float", true);

		testCreateStatementWithParameterCondition("String", "String", true);
		testCreateStatementWithParameterCondition("String", "boolean", false);
		testCreateStatementWithParameterCondition("boolean", "String", false);

		testCreateStatementWithParameterCondition("char", "char", true);
		testCreateStatementWithParameterCondition("String", "char", true);
		testCreateStatementWithParameterCondition("char", "String", true);

		testCreateStatementWithParameterCondition("String", "double", false);
		testCreateStatementWithParameterCondition("double", "String", false);
	}

	private void testCreateStatementWithParameterCondition(String parameter1Type, String parameter2Type, boolean okExpected) {

		MethodNode methodNode = new MethodNode("method", null);

		MethodParameterNode methodParameterNode1 =
				new MethodParameterNode("par1", parameter1Type, null, false, null);
		methodNode.addParameter(methodParameterNode1);

		MethodParameterNode methodParameterNode2 =
				new MethodParameterNode("par2", parameter2Type, null, false, null);
		methodNode.addParameter(methodParameterNode2);

		if (okExpected) {
			try {
				RelationStatement.createRelationStatementWithParameterCondition(methodParameterNode1, EMathRelation.EQUAL, methodParameterNode2);
			} catch (Exception e) {
				fail();
			}
		} else {
			try {
				RelationStatement.createRelationStatementWithParameterCondition(methodParameterNode1, EMathRelation.EQUAL, methodParameterNode2);
				fail();
			} catch (Exception e) {
			}
		}
	}

}
