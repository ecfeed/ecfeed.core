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
import java.util.Set;

public interface IChoicesParentNode extends IAbstractNode {

	public void addChoice(ChoiceNode choice);
	public void addChoice(ChoiceNode choice, int index);
	public void addChoices(List<ChoiceNode> choices);

	public int getChoiceCount();	
	public List<ChoiceNode> getChoices();
	public List<ChoiceNode> getChoicesWithCopies();
	public ChoiceNode getChoice(String qualifiedName);
	public int getChoiceIndex(String choiceNameToFind);
	public boolean choiceExistsAsDirectChild(String choiceNameToFind);
	public List<ChoiceNode> getLeafChoices();
	public List<ChoiceNode> getLeafChoicesWithCopies();
	public Set<String> getAllChoiceNames();
	public Set<String> getAllLabels();
	public Set<String> getLeafChoiceNames();
	public Set<ChoiceNode> getAllChoices();
	public Set<String> getChoiceNames();
	public Set<ChoiceNode> getLabeledChoices(String label);
	public Set<String> getLeafLabels();
	public Set<String> getLeafChoiceValues();
	public boolean removeChoice(ChoiceNode choice);
	public void replaceChoices(List<ChoiceNode> newChoices);
	public void clearChoices();
	
	public BasicParameterNode getParameter();
	public Object accept(IChoicesParentVisitor visitor) throws Exception;

}
