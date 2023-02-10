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
	private static BasicParameterNode fExpParameter1;
	private static BasicParameterNode fPartParameter1;
	private static BasicParameterNode fPartParameter2;
	private static String fExpectedValue1;


	@BeforeClass
	public static void prepareModel(){
		fMethod = new MethodNode("method", null);
		fExpectedValue1 = "value1";

		fExpParameter1 = new BasicParameterNode("parameterE1", "type", "0", true, null);
		fExpParameter1.setDefaultValueString(fExpectedValue1);
		fPartParameter1 = new BasicParameterNode("parameterP1", "type", "0", false, null);
		fPartParameter2 = new BasicParameterNode("parameterP2", "type", "0", false, null);

		fMethod.addParameter(fPartParameter1);
		fMethod.addParameter(fExpParameter1);
		fMethod.addParameter(fPartParameter2);

	}

	@Test
	public void testAdapt(){
		ChoiceNode choice1 = new ChoiceNode("choice1", "", null);
		ChoiceNode statementChoice = new ChoiceNode("exp_choice", "statement expected value", null);
		ExpectedValueStatement testStatement = new ExpectedValueStatement(fExpParameter1, statementChoice, new JavaPrimitiveTypePredicate());

		List<ChoiceNode> testData = new ArrayList<>();
		testData.add(choice1);
		testData.add(new ChoiceNode("", fExpParameter1.getDefaultValue(), null));
		testData.add(choice1);

		testStatement.setExpectedValues(testData);

		assertTrue(testData.get(1).getValueString().equals(statementChoice.getValueString()));
	}

	@Test
	public void compareTest(){
		IPrimitiveTypePredicate predicate = new JavaPrimitiveTypePredicate();

		BasicParameterNode c1 = new BasicParameterNode("c", "type", "0", true, null);
		BasicParameterNode c2 = new BasicParameterNode("c", "type", "0", true, null);

		ChoiceNode p1 = new ChoiceNode("name", "value", null);
		ChoiceNode p2 = new ChoiceNode("name", "value", null);

		ExpectedValueStatement s1 = new ExpectedValueStatement(c1, p1, predicate);
		ExpectedValueStatement s2 = new ExpectedValueStatement(c2, p2, predicate);

		assertTrue(s1.isEqualTo(s2));
		c1.setName("c1");
		assertFalse(s1.isEqualTo(s2));
		c2.setName("c1");
		assertTrue(s1.isEqualTo(s2));

		s1.getChoice().setValueString("v1");
		assertFalse(s1.isEqualTo(s2));
		s2.getChoice().setValueString("v1");
		assertTrue(s1.isEqualTo(s2));

	}
}
