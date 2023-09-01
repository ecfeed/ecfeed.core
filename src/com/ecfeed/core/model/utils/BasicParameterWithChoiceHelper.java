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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.ChoiceNode;

public class BasicParameterWithChoiceHelper {

	public static List<ChoiceNode> convertToListOfChoices(
			List<BasicParameterWithChoice> parametersWithChoices) {

		Set<ChoiceNode> resultSet = new HashSet<>();

		for (BasicParameterWithChoice basicParameterWithChoice : parametersWithChoices) {
			resultSet.add(basicParameterWithChoice.getChoiceNode());
		}

		return new ArrayList<>(resultSet);
	}
}
