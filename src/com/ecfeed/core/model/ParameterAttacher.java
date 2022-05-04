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

import com.ecfeed.core.utils.ChoiceConversionItem;
import com.ecfeed.core.utils.ChoiceConversionList;
import com.ecfeed.core.utils.ExceptionHelper;

public class ParameterAttacher {

	public static void attach(
			MethodParameterNode srcMethodParameterNode, 
			ChoicesParentNode dstParameterForChoices,
			ChoiceConversionList choiceConversionList) {

		MethodNode methodNode = 
				attachInternal(srcMethodParameterNode, dstParameterForChoices, choiceConversionList);

		if (srcMethodParameterNode.isDetached()) {
			methodNode.removeDetachedParameter(srcMethodParameterNode);
		} 
	}

	public static void attachChoices(
			MethodParameterNode srcMethodParameterNode, 
			ChoicesParentNode dstParameterForChoices,
			ChoiceConversionList choiceConversionList) {

		MethodNode methodNode = 
				attachInternal(srcMethodParameterNode, dstParameterForChoices, choiceConversionList);
	} 

	private static MethodNode attachInternal(MethodParameterNode srcMethodParameterNode,
			ChoicesParentNode dstParameterForChoices, ChoiceConversionList choiceConversionList) {
		if (srcMethodParameterNode == null) {
			ExceptionHelper.reportRuntimeException("Empty method parameter.");
		}

		if (dstParameterForChoices == null) {
			ExceptionHelper.reportRuntimeException("Empty global parameter.");
		}

		if (choiceConversionList != null) {
			moveChoicesByConversionList(
					choiceConversionList, 
					srcMethodParameterNode, 
					dstParameterForChoices);
		}

		MethodNode methodNode = srcMethodParameterNode.getMethod();

		moveRemainingTopChoices(srcMethodParameterNode, dstParameterForChoices);

		methodNode.updateParameterReferencesInConstraints(
				srcMethodParameterNode, 
				dstParameterForChoices);
		return methodNode;
	}

	public static void moveChoicesByConversionList(
			ChoiceConversionList choiceConversionItems,
			MethodParameterNode srcParameterNode, 
			ChoicesParentNode dstParameterNode) {

		List<ChoiceConversionItem> sortedChoiceConversionItems = 
				choiceConversionItems.createSortedCopyOfConversionItems();

		MethodNode methodNode = srcParameterNode.getMethod();

		for (ChoiceConversionItem choiceConversionItem : sortedChoiceConversionItems) {

			ChoiceNode srcChoiceNode = srcParameterNode.getChoice(choiceConversionItem.getSrcName());

			if (srcChoiceNode == null) {
				ExceptionHelper.reportRuntimeException("Cannot find source choice.");
			}

			ChoiceNode dstChoiceNode = dstParameterNode.getChoice(choiceConversionItem.getDstName());

			if (dstChoiceNode == null) {
				ExceptionHelper.reportRuntimeException("Cannot find destination choice.");
			}

			moveChildChoices(srcChoiceNode, dstChoiceNode);

			methodNode.updateChoiceReferencesInTestCases(srcChoiceNode, dstChoiceNode);
			methodNode.updateChoiceReferencesInConstraints(srcChoiceNode, dstChoiceNode);

			// remove source choice

			ChoicesParentNode choicesParentNode = srcChoiceNode.getParent();
			choicesParentNode.removeChoice(srcChoiceNode);
		}
	}

	private static void moveChildChoices(ChoiceNode srcChoiceNode, ChoiceNode dstChoiceNode) {

		List<ChoiceNode> childChoices = srcChoiceNode.getChoices();

		for (ChoiceNode childChoice : childChoices) {

			dstChoiceNode.addChoice(childChoice);
		}
	}

	private static void moveRemainingTopChoices(
			MethodParameterNode srcMethodParameterNode,
			ChoicesParentNode dstParameterNode) {
		List<ChoiceNode> choiceNodes = srcMethodParameterNode.getChoices();

		for (ChoiceNode choiceNode : choiceNodes) {
			addChoiceWithUniqueName(choiceNode, dstParameterNode);
		}
	}

	private static void addChoiceWithUniqueName(ChoiceNode choiceNode, ChoicesParentNode methodParameterNode) {

		String orginalChoiceName = choiceNode.getName();

		if (!choiceNameExistsAmongChildren(orginalChoiceName, methodParameterNode)) {

			methodParameterNode.addChoice(choiceNode);
			return;
		}

		for (int postfixCounter = 1; postfixCounter < 999; postfixCounter++) {

			String tmpName = orginalChoiceName + "-" + postfixCounter;

			if (!choiceNameExistsAmongChildren(tmpName, methodParameterNode)) {

				choiceNode.setName(tmpName);
				methodParameterNode.addChoice(choiceNode);
				return;
			}
		}

		ExceptionHelper.reportRuntimeException("Cannot add choice to method parameter.");
	}

	private static boolean choiceNameExistsAmongChildren(String choiceName, ChoicesParentNode methodParameterNode) {

		List<ChoiceNode> choiceNodes = methodParameterNode.getChoices();

		for (ChoiceNode choiceNode : choiceNodes) {

			String currentChoiceName = choiceNode.getName();

			if (currentChoiceName.equals(choiceName)) {
				return true;
			}
		}

		return false;
	}


}