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

import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoiceNodeHelper;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.NodesByType;

public class GenericRemoveNodesProcessorOfChoices {

	public static void processChoices(Set<ChoiceNode> choiceNodes, NodesByType inOutAffectedNodes) {

		for (ChoiceNode choiceNode : choiceNodes) {

			processChoice(choiceNode, inOutAffectedNodes);
		}
	}

	private static void processChoice(ChoiceNode choiceNode, NodesByType inOutAffectedNodes) {

		List<ConstraintNode> calculatedConstraintNodesToDelete = ChoiceNodeHelper.getMentioningConstraints(choiceNode);

		List<TestCaseNode> calculatedTestCaseNodesToDelete = ChoiceNodeHelper.getMentioningTestCases(choiceNode);  
		
		inOutAffectedNodes.addConstraints(calculatedConstraintNodesToDelete);
		inOutAffectedNodes.addTestCases(calculatedTestCaseNodesToDelete);

		inOutAffectedNodes.addNode(choiceNode);
	}
}
