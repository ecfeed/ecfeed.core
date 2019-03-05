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

import java.util.ArrayList;
import java.util.List;

public class TestCase {

	List<ChoiceNode> fChoices;

	public TestCase() {

		fChoices = new ArrayList<>();
	}

	public TestCase(List<ChoiceNode> choices) {

		fChoices = choices;
	}

	public void add(ChoiceNode choiceNode) {

		fChoices.add(choiceNode);
	}

	public List<ChoiceNode> getListOfChoices() {
		return fChoices;
	}

	public TestCase makeClone() {

		TestCase result = new TestCase();

		for (ChoiceNode choiceNode : fChoices) {
			result.add(choiceNode.makeClone());
		}

		return result;
	}

}
