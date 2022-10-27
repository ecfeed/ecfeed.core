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

public class ChoicesListHelper {

	public static boolean isMatchForListsOfChoices(List<ChoiceNode> choices, List<ChoiceNode> otherChoices) {

		if (choices.size() != otherChoices.size()){
			return false;
		}

		for (int index = 0; index < choices.size(); index++) {

			ChoiceNode choiceNode = choices.get(index);
			ChoiceNode choiceNodeToCompare = otherChoices.get(index);

			if (choiceNode.isMatch(choiceNodeToCompare) == false) {
				return false;
			}
		}

		return true;
	}

	public static void addChoice(ChoiceNode choice, List<ChoiceNode> choices, IChoicesParentNode parent) {

		addChoice(choice, choices, choices.size(), parent);
	}

	public static void addChoice(ChoiceNode choice, List<ChoiceNode> choices, int index, IChoicesParentNode parent) {

		choice.setParent(parent);
		choices.add(index, choice);
	}

	public static void addChoices(List<ChoiceNode> choicesToAdd, List<ChoiceNode> choices, IChoicesParentNode parent) {

		for (ChoiceNode choice : choicesToAdd) {
			addChoice(choice, choices, parent);
		}
	}

	//	public int getChoiceCount() {
	//
	//		return fChoices.size();
	//	}
	//	
	public static int getChoiceIndex(String choiceNameToFind, List<ChoiceNode> choices) {

		int index = 0;

		for (ChoiceNode choiceNode : choices) {
			if (choiceNode.getName().equals(choiceNameToFind)) {
				return index;
			}

			index++;
		}

		return -1;
	}

	public static boolean choiceExists(String choiceNameToFind, List<ChoiceNode> choices) {

		for (ChoiceNode choiceNode : choices) {
			if (choiceNode.getName().equals(choiceNameToFind)) {
				return true;
			}
		}

		return false;
	}

	public static boolean removeChoice(ChoiceNode choiceToRemove, List<ChoiceNode> choices) {

		if (choices.contains(choiceToRemove) && choices.remove(choiceToRemove)) {
			choiceToRemove.setParent(null);
			return true;
		}

		return false;
	}

	public static void replaceChoices(List<ChoiceNode> newChoices, List<ChoiceNode> choices, IChoicesParentNode parent) {

		choices.clear();
		choices.addAll(newChoices);

		for (ChoiceNode choiceNode : newChoices) {
			choiceNode.setParent(parent);
		}
	}

}
