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

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.BasicParameterNodeHelper;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoiceNodeHelper;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.NodesByType;

public class GenericRemoveNodesProcessorOfChoices {

	public static void processChoices(
			Set<ChoiceNode> choiceNodes, 
			NodesByType inOutAffectedNodes) {

		for (ChoiceNode choiceNode : choiceNodes) {

			accumulateAffectedConstraints(choiceNode, inOutAffectedNodes);

			accumulateAffectedTestCases(choiceNode, inOutAffectedNodes);

			inOutAffectedNodes.addNode(choiceNode);
		}
	}
	
	private static void accumulateAffectedConstraints(
			IAbstractNode abstractNode, NodesByType inOutAffectedNodes) {

		if (abstractNode instanceof ChoiceNode) {

			List<ConstraintNode> mentioningConstraintNodes = 
					ChoiceNodeHelper.getMentioningConstraints((ChoiceNode) abstractNode);

			inOutAffectedNodes.addConstraints(mentioningConstraintNodes);
			return;
		} 

		if (abstractNode instanceof BasicParameterNode) {

			List<ConstraintNode> mentioningConstraintNodes = 
					BasicParameterNodeHelper.getMentioningConstraints((BasicParameterNode) abstractNode);

			inOutAffectedNodes.addConstraints(mentioningConstraintNodes);
			return;
		}
	}
	
	private static void accumulateAffectedTestCases(
			ChoiceNode choiceNode, NodesByType inOutAffectedNodes) {

		Set<TestCaseNode> mentioningTestCaseNodes = 
				ChoiceNodeHelper.getMentioningTestCases(choiceNode);

		inOutAffectedNodes.addTestCases(mentioningTestCaseNodes);
	}

}
