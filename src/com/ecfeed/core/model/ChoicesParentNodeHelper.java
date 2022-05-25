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

import java.util.List;

public abstract class ChoicesParentNodeHelper {

	public static void traverseSubTreesOfChoices(ChoicesParentNode srcParentNode, IChoiceNodeWorker choiceNodeWorker) {

		List<ChoiceNode> childChoiceNodes = srcParentNode.getChoices();

		if (childChoiceNodes.size() == 0) {
			return;
		}

		for (ChoiceNode choiceNode : childChoiceNodes) {

			choiceNodeWorker.doWork(choiceNode);

			traverseSubTreesOfChoices(choiceNode, choiceNodeWorker);
		}
	}

	public static void createCopyOfChoicesSubTrees(ChoicesParentNode srcParentNode, ChoicesParentNode dstParentNode) {

		List<ChoiceNode> childChoiceNodes = srcParentNode.getChoices();

		if (childChoiceNodes.size() == 0) {
			return;
		}

		for (ChoiceNode choiceNode : childChoiceNodes) {

			ChoiceNode clonedChoiceNode = choiceNode.makeClone();

			dstParentNode.addChoice(clonedChoiceNode);

			createCopyOfChoicesSubTrees(choiceNode, clonedChoiceNode);
		}
	}

	public static boolean isMatch(ChoicesParentNode choicesParentNode1, ChoicesParentNode choicesParentNode2) {

		if (choicesParentNode1 == null && choicesParentNode2 == null) {
			return true;
		}

		if (choicesParentNode1 == null && choicesParentNode2 != null) {
			return false;
		}

		if (choicesParentNode1 != null && choicesParentNode2 == null) {
			return false;
		}

		if (choicesParentNode1.isMatch(choicesParentNode2)) {
			return true;
		}

		return false;
	}

}
