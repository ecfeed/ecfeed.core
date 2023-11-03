/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.utils;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;

public class BasicParameterWithChoice {

	private BasicParameterNode fBasicParameterNode;
	private ChoiceNode fChoiceNode;

	@Override
	public String toString() {
		return fBasicParameterNode.getName() + "(" + fChoiceNode.getName() + ")";
	}
	
	public BasicParameterWithChoice(BasicParameterNode basicParameterNode, ChoiceNode choiceNode) {

		fBasicParameterNode = basicParameterNode;
		fChoiceNode = choiceNode;
	}

	public BasicParameterNode getBasicParameterNode() {
		return fBasicParameterNode;
	}

	public ChoiceNode getChoiceNode() {
		return fChoiceNode;
	}

}
