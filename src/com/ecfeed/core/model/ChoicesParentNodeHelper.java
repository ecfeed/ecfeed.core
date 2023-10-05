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

import com.ecfeed.core.model.utils.NodeNameHelper;
import com.ecfeed.core.operations.nodes.OnChoicesOperationSimpleRemoveAll;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.ParameterConversionItem;
import com.ecfeed.core.utils.ParameterConversionItemPartForChoice;
import com.ecfeed.core.utils.StringHelper;

public abstract class ChoicesParentNodeHelper {

	public static void traverseSubTreesOfChoices(IChoicesParentNode srcParentNode, IObjectWorker choiceNodeWorker) {

		List<ChoiceNode> childChoiceNodes = srcParentNode.getChoices();

		if (childChoiceNodes.size() == 0) {
			return;
		}

		for (ChoiceNode choiceNode : childChoiceNodes) {

			choiceNodeWorker.doWork(choiceNode);

			traverseSubTreesOfChoices(choiceNode, choiceNodeWorker);
		}
	}

	public static void createCopyOfChoicesSubTreesBetweenParameters(
			IChoicesParentNode srcParentNode, 
			IChoicesParentNode dstParentNode,
			ListOfModelOperations inOutReverseOperations,
			List<ParameterConversionItem> parameterConversionItems,
			IExtLanguageManager extLanguageManager) {

		createCopyOfChoicesSubtreesRecursive(srcParentNode, dstParentNode, parameterConversionItems);

		OnChoicesOperationSimpleRemoveAll reverseOperation = 
				new OnChoicesOperationSimpleRemoveAll(dstParentNode, extLanguageManager);

		inOutReverseOperations.add(reverseOperation);
	}

	private static void createCopyOfChoicesSubtreesRecursive(
			IChoicesParentNode srcParentNode, 
			IChoicesParentNode dstParentNode,
			List<ParameterConversionItem> inOutParameterConversionItems) {

		List<ChoiceNode> childChoiceNodes = srcParentNode.getChoices();

		if (childChoiceNodes.size() == 0) {
			return;
		}

		for (ChoiceNode choiceNode : childChoiceNodes) {

			ChoiceNode clonedChoiceNode = choiceNode.makeClone();
			clonedChoiceNode.clearChoices();

			dstParentNode.addChoice(clonedChoiceNode);

			if (inOutParameterConversionItems != null) {

				ParameterConversionItemPartForChoice srcPart = new ParameterConversionItemPartForChoice(choiceNode);
				ParameterConversionItemPartForChoice dstPart = new ParameterConversionItemPartForChoice(clonedChoiceNode);

				boolean isRandomized = choiceNode.isRandomizedValue();

				ParameterConversionItem parameterConversionItemForChoice = 
						new ParameterConversionItem(srcPart, dstPart, isRandomized);

				inOutParameterConversionItems.add(parameterConversionItemForChoice);
			}

			createCopyOfChoicesSubtreesRecursive(choiceNode, clonedChoiceNode, inOutParameterConversionItems);
		}
	}

	public static boolean isMatch(IChoicesParentNode choicesParentNode1, IChoicesParentNode choicesParentNode2) {

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

	public static String generateUniqueChoiceName(
			IChoicesParentNode choicesParentNode,
			String oldName,
			String availableName) {

		if (!NodeNameHelper.choiceNameCompliesWithNamingRules(oldName)) {
			ExceptionHelper.reportRuntimeException("Choice name is invalid.");
		}

		if (availableName != null 
				&& StringHelper.isEqual(oldName, availableName)) {

			return availableName;
		}

		String oldNameCore = StringHelper.removeFromNumericPostfix(oldName);

		for (int i = 1;   ; i++) {

			String newMethodName = oldNameCore + String.valueOf(i);

			if (availableName != null && StringHelper.isEqual(newMethodName, availableName)) {
				return availableName;
			}

			ChoiceNode choiceNode = ChoiceNodeHelper.findChoiceByName(choicesParentNode, newMethodName);

			if (choiceNode == null) {
				return newMethodName;
			}
		}
	}

}
