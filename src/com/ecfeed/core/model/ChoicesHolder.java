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

public class ChoicesHolder {

	private List<ChoiceNode> fChoices;
	private IModelChangeRegistrator fModelChangeRegistrator;
	
	public ChoicesHolder(IModelChangeRegistrator modelChangeRegistrator) {
		
		fModelChangeRegistrator = modelChangeRegistrator;
		fChoices = new ArrayList<ChoiceNode>();
	}

	public List<ChoiceNode> getChoices() {
		
		return fChoices;
	}

	public boolean isMatch(ChoicesHolder other) {

		List<ChoiceNode> otherChoices = other.getChoices();

		if (fChoices.size() != otherChoices.size()){
			return false;
		}

		for (int index = 0; index < fChoices.size(); index++) {

			ChoiceNode choiceNode = fChoices.get(index);
			ChoiceNode choiceNodeToCompare = otherChoices.get(index);

			if (choiceNode.isMatch(choiceNodeToCompare) == false) {
				return false;
			}
		}

		return true;
	}
	
	public void addChoice(ChoiceNode choice, IAbstractNode parent) {

		addChoice(choice, fChoices.size(), parent);
	}

	public void addChoice(ChoiceNode choice, int index, IAbstractNode parent) {

		fChoices.add(index, choice);
		choice.setParent(parent);
		registerChange();
	}
	
	public void addChoices(List<ChoiceNode> choices, IAbstractNode parent) {

		for (ChoiceNode choice : choices) {
			addChoice(choice, parent);
		}
	}
	
	public int getChoiceCount() {

		return fChoices.size();
	}
	
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
	
	public boolean removeChoice(ChoiceNode choice) {

		if (fChoices.contains(choice) && fChoices.remove(choice)) {
			choice.setParent(null);
			registerChange();
			return true;
		}

		return false;
	}
	
	public void replaceChoices(List<ChoiceNode> newChoices,  IAbstractNode parent) {

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

	
	private void registerChange() {

		if (fModelChangeRegistrator == null) {
			return;
		}

		fModelChangeRegistrator.registerChange();
	}

	
	
}
