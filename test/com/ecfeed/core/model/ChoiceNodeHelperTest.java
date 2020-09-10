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


import com.ecfeed.core.utils.ExtLanguage;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChoiceNodeHelperTest {

	@Test
	public void createLabelTest() {

		MethodParameterNode methodParameterNode =
				new MethodParameterNode(
						"par1", null,	"int", "0", true);

		ChoiceNode choice = new ChoiceNode("choice_1", null, "MAX_VALUE");
		choice.setParent(methodParameterNode);

		String label = ChoiceNodeHelper.createLabel(choice, ExtLanguage.JAVA);
		assertEquals("choice_1 [MAX_VALUE]", label);

		label = ChoiceNodeHelper.createLabel(choice, ExtLanguage.SIMPLE);
		assertEquals("choice 1 [2147483647]", label);

		choice = new ChoiceNode("choice_1", null, "5");
		choice.setParent(methodParameterNode);

		label = ChoiceNodeHelper.createLabel(choice, ExtLanguage.JAVA);
		assertEquals("choice_1 [5]", label);
	}

}
