/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations.nodes;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.operations.CompositeOperation;
import com.ecfeed.core.operations.GenericOperationAddChoice;
import com.ecfeed.core.operations.GenericRemoveNodesOperation;
import com.ecfeed.core.operations.GenericRemoveNodesProcessorOfNodes;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.NodesByType;

public class OnChoicesOperationReplace extends CompositeOperation {

	public OnChoicesOperationReplace(
			BasicParameterNode abstractParameterNode, 
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

		GenericRemoveNodesProcessorOfNodes genericRemoveNodesProcessorOfNodes = 
				new GenericRemoveNodesProcessorOfNodes(
						abstractParameterNode.getChoices(), adapterProvider, true, extLanguageManager);
		
		NodesByType processedNodesToDelete = genericRemoveNodesProcessorOfNodes.getProcessedNodes();
		
		addOperation(
				new GenericRemoveNodesOperation(
						processedNodesToDelete,
						adapterProvider, 
						true, 
						abstractParameterNode, 
						abstractParameterNode, 
						extLanguageManager));

		for(ChoiceNode choice : skipped){
			addOperation(new GenericOperationAddChoice(abstractParameterNode, choice, adapterProvider, true, extLanguageManager));
		}
	}

}
