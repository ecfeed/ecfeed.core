/*******************************************************************************
 *
R * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ConstraintNodeListHolder {

	public static interface ConstraintsItr {
	}

	private List<ConstraintNode> fConstraintNodes;
	IModelChangeRegistrator fModelChangeRegistrator;

	public ConstraintNodeListHolder(IModelChangeRegistrator modelChangeRegistrator) {

		fModelChangeRegistrator = modelChangeRegistrator;
		fConstraintNodes = new ArrayList<>();
	}

	public ConstraintNodeListHolder.ConstraintsItr getIterator() {
		return new ConstraintsItrImpl(fConstraintNodes.iterator());
	}

	public boolean hasNextConstraint(ConstraintNodeListHolder.ConstraintsItr contIterator) {

		return ((ConstraintsItrImpl)contIterator).fIterator.hasNext();
	}

	public ConstraintNode getNextConstraint(ConstraintNodeListHolder.ConstraintsItr contIterator) {

		return ((ConstraintsItrImpl)contIterator).fIterator.next();
	}

	public void removeConstraint(ConstraintNodeListHolder.ConstraintsItr contIterator) {

		((ConstraintsItrImpl)contIterator).fIterator.remove();
		registerChange();
	}	

	public List<ConstraintNode> getConstraintNodes() { 
		return fConstraintNodes;
	}

	public int getConstraintListSize() { 
		return fConstraintNodes.size();
	}

	public void addConstraint(ConstraintNode constraint, IParametersAndConstraintsParentNode parentNode) {
		addConstraint(constraint, fConstraintNodes.size(), parentNode);
	}

	public void addConstraint(ConstraintNode constraint, int index, IParametersAndConstraintsParentNode parentNode) {
		constraint.setParent(parentNode); // TODO MO-RE remove this and put in calling methods (this is the convention)
		fConstraintNodes.add(index, constraint);
		registerChange();
	}

	public ConstraintNodeListHolder makeClone(
			IParametersAndConstraintsParentNode newParent, Optional<NodeMapper> nodeMapper) {

		ConstraintNodeListHolder cloneOfConstraintNodeListHolder = 
				new ConstraintNodeListHolder(fModelChangeRegistrator);

		for (ConstraintNode constraint : fConstraintNodes) {

			ConstraintNode clonedConstraint = constraint.makeClone(nodeMapper);
			clonedConstraint.setParent(newParent);

			if (clonedConstraint != null) {
				clonedConstraint.setParent(newParent);
				cloneOfConstraintNodeListHolder.addConstraint(clonedConstraint, newParent);
			}
		}

		return cloneOfConstraintNodeListHolder;
	}

	public List<Constraint> getConstraints() {

		List<Constraint> result = new ArrayList<Constraint>();

		for(ConstraintNode node : fConstraintNodes){
			result.add(node.getConstraint());
		}

		return result;
	}

	public List<Constraint> getConstraints(String name) {

		List<Constraint> constraints = new ArrayList<Constraint>();

		for(ConstraintNode node : fConstraintNodes){
			if(node.getName().equals(name)){
				constraints.add(node.getConstraint());
			}
		}

		return constraints;
	}

	public List<ConstraintNode> getConstraintNodes(String name) {

		List<ConstraintNode> constraintNodes = new ArrayList<ConstraintNode>();

		for(ConstraintNode constraintNode : fConstraintNodes){
			if(constraintNode.getName().equals(name)){
				constraintNodes.add(constraintNode);
			}
		}

		return constraintNodes;
	}

	public Set<String> getConstraintsNames() {

		Set<String> names = new HashSet<String>();

		for (ConstraintNode constraint : fConstraintNodes) {
			names.add(constraint.getName());
		}

		return names;
	}

	public boolean removeConstraint(ConstraintNode constraint) {

		constraint.setParent(null);
		boolean result = fConstraintNodes.remove(constraint);
		registerChange();

		return result;
	}

	public boolean isChoiceMentioned(ChoiceNode choice) {

		for (ConstraintNode constraint : fConstraintNodes) {

			if (constraint.mentions(choice)) {
				return true;
			}
		}

		return false;
	}

	public Set<ConstraintNode> getMentioningConstraints(BasicParameterNode parameter) {

		Set<ConstraintNode> result = new HashSet<ConstraintNode>();

		for (ConstraintNode constraint : fConstraintNodes) {

			if (constraint.mentions(parameter)) {
				result.add(constraint);
			}
		}

		return result;
	}

	public Set<ConstraintNode> getMentioningConstraints(BasicParameterNode parameter, String label) {

		Set<ConstraintNode> result = new HashSet<ConstraintNode>();

		for (ConstraintNode constraint : fConstraintNodes) {

			if (constraint.mentions(parameter, label)) {
				result.add(constraint);
			}
		}

		return result;
	}

	public Set<ConstraintNode> getMentioningConstraints(ChoiceNode choice) {

		Set<ConstraintNode> result = new HashSet<ConstraintNode>();

		for (ConstraintNode constraint : fConstraintNodes) {

			if (constraint.mentions(choice)) {
				result.add(constraint);
			}
		}

		return result;
	}

	public boolean isParameterMentioned(BasicParameterNode parameter) {

		for (ConstraintNode constraint : fConstraintNodes) {

			if (constraint.mentions(parameter)) {
				return true;
			}
		}

		return true;
	}

	public void replaceConstraints(List<ConstraintNode> constraints){

		fConstraintNodes.clear();
		fConstraintNodes.addAll(constraints);

		registerChange();
	}

	public void removeMentioningConstraints(BasicParameterNode methodParameter) {

		ArrayList<ConstraintNode> constraintsToDelete = new ArrayList<ConstraintNode>();  

		for(ConstraintNode constraint : fConstraintNodes){
			if (constraint.mentionsChoiceOfParameter(methodParameter)) {
				constraintsToDelete.add(constraint);
			}
		}

		for (ConstraintNode constraint : constraintsToDelete) {
			fConstraintNodes.remove(constraint);
		}

		registerChange();
	}

	public void removeAllConstraints() {

		fConstraintNodes.clear();
		registerChange();
	}

	public void registerChange() {

		if (fModelChangeRegistrator == null) {
			return;
		}

		fModelChangeRegistrator.registerChange();
	}

	private static class ConstraintsItrImpl implements ConstraintNodeListHolder.ConstraintsItr {

		Iterator<ConstraintNode> fIterator;

		ConstraintsItrImpl(Iterator<ConstraintNode> iterator) {
			fIterator = iterator;
		}

	}

	public void setConstraints(List<ConstraintNode> constraints) {

		fConstraintNodes.clear();
		fConstraintNodes.addAll(constraints);
	}

}