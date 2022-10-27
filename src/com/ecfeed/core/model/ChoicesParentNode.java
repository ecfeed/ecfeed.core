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

//	private List<ChoiceNode> fChoices;
	private ChoicesHolder fChoicesHolder;

	public ChoicesParentNode(String name, IModelChangeRegistrator modelChangeRegistrator) {
		super(name, modelChangeRegistrator);

		fChoicesHolder = new ChoicesHolder(modelChangeRegistrator);
	}

	@Override
	public List<IAbstractNode> getChildren() {

		return new ArrayList<IAbstractNode>(fChoicesHolder.getChoices());
	}

	@Override
	public int getChildrenCount() {

		return fChoicesHolder.getChoiceCount();
	}

	@Override
	public boolean isMatch(IAbstractNode other) {

		if (other instanceof ChoicesParentNode == false) {
			return false;
		}

		ChoicesParentNode otherChoicesParentNode = (ChoicesParentNode)other;

		if (!fChoicesHolder.isMatch(otherChoicesParentNode.fChoicesHolder)) {
			return false;
		}

		boolean isMatch = super.isMatch(other);

		if (!isMatch) {
			return false;
		}
		return true;
	}

	@Override
	public void addChoice(ChoiceNode choice) {

		fChoicesHolder.addChoice(choice, this);
	}

	@Override
	public void addChoice(ChoiceNode choice, int index) {

		fChoicesHolder.addChoice(choice, index, this);
	}

	@Override
	public void addChoices(List<ChoiceNode> choices) {

		fChoicesHolder.addChoices(choices, this);
	}

	@Override
	public List<ChoiceNode> getChoices() {

		return fChoicesHolder.getChoices();
	}

	@Override
	public int getChoiceCount() {

		return fChoicesHolder.getChoiceCount();
	}

	public List<ChoiceNode> getChoicesWithCopies() { // TODO MO-RE do we need this ?

		return fChoicesHolder.getChoices();
	}

	@Override
	public ChoiceNode getChoice(String qualifiedName) {

		return (ChoiceNode)getChild(qualifiedName);
	}

	@Override
	public int getChoiceIndex(String choiceNameToFind) {

		return fChoicesHolder.getChoiceIndex(choiceNameToFind);
	}

	@Override
	public boolean choiceExistsAsDirectChild(String choiceNameToFind) {

		return fChoicesHolder.choiceExists(choiceNameToFind);
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

		return fChoicesHolder.removeChoice(choice);
	}

	@Override
	public void replaceChoices(List<ChoiceNode> newChoices) {
		
		fChoicesHolder.replaceChoices(newChoices, this);
	}

	@Override
	public void clearChoices() {

		fChoicesHolder.clearChoices();
	}

}
