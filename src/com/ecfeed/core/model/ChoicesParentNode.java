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
import java.util.Set;

public abstract class ChoicesParentNode extends AbstractNode implements IChoicesParentNode { // TODO MO-RE remove class

	private List<ChoiceNode> fChoices;
	
	public ChoicesParentNode(String name, IModelChangeRegistrator modelChangeRegistrator) {
		super(name, modelChangeRegistrator);

		fChoices = new ArrayList<ChoiceNode>();
	}

	@Override
	public List<IAbstractNode> getChildren() {

		return new ArrayList<IAbstractNode>(getChoices());
	}

	@Override
	public int getChildrenCount() {

		return getChoiceCount();
	}

	@Override
	public boolean isMatch(IAbstractNode other) {

		if (other instanceof ChoicesParentNode == false) {
			return false;
		}

		ChoicesParentNode otherChoicesParentNode = (ChoicesParentNode)other;

		if (!ChoicesListHelper.isMatchForListsOfChoices(fChoices, otherChoicesParentNode.fChoices)) {
			return false;
		}

		boolean isMatch = super.isMatch(other);

		if (!isMatch) {
			return false;
		}
		return true;
	}

	@Override
	public void addChoice(ChoiceNode choiceToAdd) {

		ChoicesListHelper.addChoice(choiceToAdd, fChoices, this);
	}

	@Override
	public void addChoice(ChoiceNode choiceToAdd, int index) {

		ChoicesListHelper.addChoice(choiceToAdd, fChoices, index, this);
		registerChange();
	}

	@Override
	public void addChoices(List<ChoiceNode> choicesToAdd) {

		ChoicesListHelper.addChoices(choicesToAdd, fChoices, this);
		registerChange();
	}

	@Override
	public List<ChoiceNode> getChoices() {

		return fChoices;
	}

	@Override
	public int getChoiceCount() {

		return getChoices().size();
	}

	public List<ChoiceNode> getChoicesWithCopies() { // TODO MO-RE do we need this ?

		return getChoices();
	}

	@Override
	public ChoiceNode getChoice(String qualifiedName) {

		return (ChoiceNode)getChild(qualifiedName);
	}

	@Override
	public int getChoiceIndex(String choiceNameToFind) {

		return ChoicesListHelper.getChoiceIndex(choiceNameToFind, fChoices);
	}

	@Override
	public boolean choiceExistsAsDirectChild(String choiceNameToFind) {

		return ChoicesListHelper.choiceExists(choiceNameToFind, fChoices);
	}

	@Override
	public List<ChoiceNode> getLeafChoices() {

		return ChoiceNodeHelper.getLeafChoices(getChoices());
	}	

	@Override
	public List<ChoiceNode> getLeafChoicesWithCopies() { // TODO MO-RE do we need this ? rename ?

		return ChoiceNodeHelper.getLeafChoices(getChoicesWithCopies());
	}	

	@Override
	public Set<String> getAllChoiceNames() {

		return ChoiceNodeHelper.getChoiceNames(getAllChoices());
	}

	@Override
	public Set<String> getAllLabels() {

		return ChoiceNodeHelper.getAllLabels(getAllChoices());
	}

	@Override
	public Set<String> getLeafChoiceNames() {

		return ChoiceNodeHelper.getChoiceNames(getLeafChoices());
	}

	@Override
	public Set<ChoiceNode> getAllChoices() {

		return ChoiceNodeHelper.getAllChoices(getChoices());
	}

	@Override
	public Set<String> getChoiceNames() {

		return ChoiceNodeHelper.getChoiceNames(getChoices());
	}

	@Override
	public Set<ChoiceNode> getLabeledChoices(String label) {

		return ChoiceNodeHelper.getLabeledChoices(label, getChoices());
	}

	@Override
	public Set<String> getLeafLabels() { 

		return ChoiceNodeHelper.getLeafLabels(getLeafChoices());
	}

	@Override
	public Set<String> getLeafChoiceValues() {

		return ChoiceNodeHelper.getLeafChoiceValues(getLeafChoices());
	}

	@Override
	public boolean removeChoice(ChoiceNode choice) {

		boolean result = ChoicesListHelper.removeChoice(choice, fChoices);
		registerChange();
		return result;
	}

	@Override
	public void replaceChoices(List<ChoiceNode> newChoices) {
		
		ChoicesListHelper.replaceChoices(newChoices, fChoices, this);
		registerChange();
	}

	@Override
	public void clearChoices() {

		fChoices.clear();
		registerChange();
	}

}
