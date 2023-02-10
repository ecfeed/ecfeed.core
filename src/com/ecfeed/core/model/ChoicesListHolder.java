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

import java.util.ArrayList;
import java.util.List;

public class ChoicesListHolder {
	
	private List<ChoiceNode> fChoices;
	IModelChangeRegistrator fModelChangeRegistrator;
	
	public ChoicesListHolder(IModelChangeRegistrator modelChangeRegistrator) {
		
		fModelChangeRegistrator = modelChangeRegistrator;
		fChoices = new ArrayList<ChoiceNode>();
	}
	
	public List<ChoiceNode> getChoices() {

		return fChoices;
	}

	public boolean isMatch(ChoicesListHolder other) {

		if (fChoices.size() != other.fChoices.size()){
			return false;
		}

		for (int index = 0; index < fChoices.size(); index++) {

			ChoiceNode choiceNode = fChoices.get(index);
			ChoiceNode choiceNodeToCompare = other.fChoices.get(index);

			if (choiceNode.isMatch(choiceNodeToCompare) == false) {
				return false;
			}
		}

		return true;
	}

	public void addChoice(ChoiceNode choice, IChoicesParentNode parent) {

		addChoice(choice, fChoices.size(), parent);
		registerChange();
	}

	public void addChoice(ChoiceNode choice, int index, IChoicesParentNode parent) {

		choice.setParent(parent);
		fChoices.add(index, choice);
		registerChange();
	}

	public void addChoices(List<ChoiceNode> choicesToAdd, IChoicesParentNode parent) {

		for (ChoiceNode choice : choicesToAdd) {
			addChoice(choice, parent);
		}
		
		registerChange();
	}

	//	public int getChoiceCount() {
	//
	//		return fChoices.size();
	//	}
	//	
	public int getChoiceIndex(String choiceNameToFind) {

		int index = 0;

		for (ChoiceNode choiceNode : fChoices) {
			
			if (choiceNode.getName().equals(choiceNameToFind)) {
				return index;
			}

			index++;
		}

		return -1;
	}

	public boolean choiceExists(String choiceNameToFind) {

		for (ChoiceNode choiceNode : fChoices) {
			if (choiceNode.getName().equals(choiceNameToFind)) {
				return true;
			}
		}

		return false;
	}

	public boolean removeChoice(ChoiceNode choiceToRemove) {

		if (fChoices.contains(choiceToRemove) && fChoices.remove(choiceToRemove)) {
			choiceToRemove.setParent(null);
			registerChange();
			return true;
		}

		return false;
	}

	public void replaceChoices(List<ChoiceNode> newChoices, IChoicesParentNode parent) {

		fChoices.clear();
		fChoices.addAll(newChoices);

		for (ChoiceNode choiceNode : newChoices) {
			choiceNode.setParent(parent);
		}
		
		registerChange();
	}
	
	public void clearChoices() {

		fChoices.clear();
		registerChange();
	}

	public void registerChange() {

		if (fModelChangeRegistrator == null) {
			return;
		}

		fModelChangeRegistrator.registerChange();
	}
	
}
