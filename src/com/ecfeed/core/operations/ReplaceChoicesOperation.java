/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.IExtLanguageManager;

public class ReplaceChoicesOperation extends BulkOperation {

	public ReplaceChoicesOperation(
			AbstractParameterNode abstractParameterNode, 
			List<ChoiceNode> choices, 
			ITypeAdapterProvider adapterProvider,
			IExtLanguageManager extLanguageManager) {

		super("Replace choices", true, abstractParameterNode, abstractParameterNode, extLanguageManager);

		List<ChoiceNode> skipped = new ArrayList<ChoiceNode>();

		for (ChoiceNode choice : choices) {
			if (abstractParameterNode.getChoiceNames().contains(choice.getName())) {
				skipped.add(choice);
			} else {
				addOperation(new GenericOperationAddChoice(abstractParameterNode, choice, adapterProvider, true, extLanguageManager));
			}
		}

		addOperation(
				new GenericRemoveNodesOperation(
						abstractParameterNode.getChoices(), adapterProvider, true, abstractParameterNode, abstractParameterNode, extLanguageManager));

		for(ChoiceNode choice : skipped){
			addOperation(new GenericOperationAddChoice(abstractParameterNode, choice, adapterProvider, true, extLanguageManager));
		}
	}

}
