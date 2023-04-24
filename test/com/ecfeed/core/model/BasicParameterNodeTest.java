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

import java.util.Optional;

import org.junit.Test;

public class BasicParameterNodeTest{
	
	@Test
	public void copyGlobalParameterTest(){
		BasicParameterNode parameter = new BasicParameterNode("parameter", "int", "0", false, null);
		ChoiceNode choice1 = new ChoiceNode("choice1", "1", null);
		ChoiceNode choice11 = new ChoiceNode("choice11", "11", null);
		ChoiceNode choice12 = new ChoiceNode("choice12", "12", null);
		ChoiceNode choice2 = new ChoiceNode("choice1", "2", null);
		ChoiceNode choice21 = new ChoiceNode("choice11", "21", null);
		ChoiceNode choice22 = new ChoiceNode("choice12", "22", null);
		choice1.addChoice(choice11);
		choice1.addChoice(choice12);
		choice1.addChoice(choice21);
		choice1.addChoice(choice22);
		parameter.addChoice(choice1);
		parameter.addChoice(choice2);

		NodeMapper nodeMapper = new NodeMapper();
		BasicParameterNode copy = parameter.makeClone(Optional.of(nodeMapper));
		AbstractParameterNodeHelper.compareParameters(parameter, copy);
	}

	@Test
	public void copyMethodParameterTest(){
		BasicParameterNode parameter = new BasicParameterNode("parameter", "int", "0", false, null);
		ChoiceNode choice1 = new ChoiceNode("choice1", "1", null);
		ChoiceNode choice11 = new ChoiceNode("choice11", "11", null);
		ChoiceNode choice12 = new ChoiceNode("choice12", "12", null);
		ChoiceNode choice2 = new ChoiceNode("choice1", "2", null);
		ChoiceNode choice21 = new ChoiceNode("choice11", "21", null);
		ChoiceNode choice22 = new ChoiceNode("choice12", "22", null);
		choice1.addChoice(choice11);
		choice1.addChoice(choice12);
		choice1.addChoice(choice21);
		choice1.addChoice(choice22);
		parameter.addChoice(choice1);
		parameter.addChoice(choice2);

		NodeMapper nodeMapper = new NodeMapper();
		BasicParameterNode copy = parameter.makeClone(Optional.of(nodeMapper));
		AbstractParameterNodeHelper.compareParameters(parameter, copy);
	}

	
}
