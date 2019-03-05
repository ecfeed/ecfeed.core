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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ecfeed.core.type.adapter.IPrimitiveTypePredicate;
import com.ecfeed.core.type.adapter.JavaPrimitiveTypePredicate;

public class ExpectedValueStatementTest{

	private static MethodNode fMethod;
	private static MethodParameterNode fExpParameter1;
	private static MethodParameterNode fPartParameter1;
	private static MethodParameterNode fPartParameter2;
	private static String fExpectedValue1;


	@BeforeClass
	public static void prepareModel(){
		fMethod = new MethodNode("method", null);
		fExpectedValue1 = "value1";

		fExpParameter1 = new MethodParameterNode("parameterE1", null, "type","0",  true);
		fExpParameter1.setDefaultValueString(fExpectedValue1);
		fPartParameter1 = new MethodParameterNode("parameterP1", null, "type","0",  false);
		fPartParameter2 = new MethodParameterNode("parameterP2", null, "type", "0", false);

		fMethod.addParameter(fPartParameter1);
		fMethod.addParameter(fExpParameter1);
		fMethod.addParameter(fPartParameter2);

	}

	@Test
	public void testAdapt(){
		ChoiceNode choice1 = new ChoiceNode("choice1", null, "");
		ChoiceNode statementChoice = new ChoiceNode("exp_choice", null, "statement expected value");
		ExpectedValueStatement testStatement = new ExpectedValueStatement(fExpParameter1, statementChoice, new JavaPrimitiveTypePredicate());

		List<ChoiceNode> testData = new ArrayList<>();
		testData.add(choice1);
		testData.add(new ChoiceNode("", null, fExpParameter1.getDefaultValue()));
		testData.add(choice1);

		testStatement.adapt(testData);

		assertTrue(testData.get(1).getValueString().equals(statementChoice.getValueString()));
	}

	@Test
	public void compareTest(){
		IPrimitiveTypePredicate predicate = new JavaPrimitiveTypePredicate();

		MethodParameterNode c1 = new MethodParameterNode("c", null, "type", "0", true);
		MethodParameterNode c2 = new MethodParameterNode("c", null, "type", "0", true);

		ChoiceNode p1 = new ChoiceNode("name", null, "value");
		ChoiceNode p2 = new ChoiceNode("name", null, "value");

		ExpectedValueStatement s1 = new ExpectedValueStatement(c1, p1, predicate);
		ExpectedValueStatement s2 = new ExpectedValueStatement(c2, p2, predicate);

		assertTrue(s1.compare(s2));
		c1.setFullName("c1");
		assertFalse(s1.compare(s2));
		c2.setFullName("c1");
		assertTrue(s1.compare(s2));

		s1.getCondition().setValueString("v1");
		assertFalse(s1.compare(s2));
		s2.getCondition().setValueString("v1");
		assertTrue(s1.compare(s2));

	}
}
