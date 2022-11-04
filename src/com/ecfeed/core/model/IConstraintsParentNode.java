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

public interface IConstraintsParentNode extends IAbstractNode {
	
	public ConstraintNodeListHolder.ConstraintsItr getIterator();
	public boolean hasNextConstraint(ConstraintNodeListHolder.ConstraintsItr contIterator);
	public ConstraintNode getNextConstraint(ConstraintNodeListHolder.ConstraintsItr contIterator);
	public void removeConstraint(ConstraintNodeListHolder.ConstraintsItr contIterator);
	public void addConstraint(ConstraintNode constraint);
	public void addConstraint(ConstraintNode constraint, int index);
	public List<ConstraintNode> getConstraintNodes();
	public List<ConstraintNode> getConstraintNodes(String name);
	public List<Constraint> getConstraints();
	public List<Constraint> getConstraints(String name);
	public Set<String> getConstraintsNames();
	public boolean removeConstraint(ConstraintNode constraint);
	public boolean isChoiceMentionedInConstraints(ChoiceNode choice);
	public Set<ConstraintNode> getMentioningConstraints(BasicParameterNode parameter);
	public Set<ConstraintNode> getMentioningConstraints(BasicParameterNode parameter, String label);
	public Set<ConstraintNode> getMentioningConstraints(ChoiceNode choice);
	public boolean isParameterMentionedInConstraints(BasicParameterNode parameter);
	public void replaceConstraints(List<ConstraintNode> constraints);
	public void removeAllConstraints();
	public void removeMentioningConstraints(BasicParameterNode methodParameter);
	
}
