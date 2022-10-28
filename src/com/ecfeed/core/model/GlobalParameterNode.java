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
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class GlobalParameterNode extends AbstractParameterNode {

	public GlobalParameterNode(String name, String type, IModelChangeRegistrator modelChangeRegistrator) {
		super(name, type, modelChangeRegistrator);
	}

	//copy constructor creating a global parameter instance from other types, eg. MethodParameterNode
	public GlobalParameterNode(AbstractParameterNode source) {

		this(source.getName(), source.getType(), source.getModelChangeRegistrator());
		for(ChoiceNode choice : source.getChoices()){
			addChoice(choice.makeClone());
		}
	}

	@Override
	public GlobalParameterNode makeClone() {

		GlobalParameterNode copy = new GlobalParameterNode(getName(), getType(), getModelChangeRegistrator());

		copy.setProperties(getProperties());

		for(ChoiceNode choice : getChoices()){
			copy.addChoice(choice.makeClone());
		}
		return copy;
	}

	@Override
	public List<MethodNode> getMethods() {

		IParametersParentNode globalParametersParentNode = getParametersParent();

		if (globalParametersParentNode == null) {
			return null;
		}

		return globalParametersParentNode.getMethods(getParameter());
	}

	public List<BasicParameterNode> getLinkedMethodParameters(){

		List<BasicParameterNode> result = new ArrayList<>();
		List<MethodNode> methods = getMethods();

		if (methods == null) {
			return new ArrayList<>();

		}

		for(MethodNode method : methods) {
			result.addAll(method.getLinkers(this));
		}

		return result;
	}

	@Override
	public Object accept(IParameterVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public Object accept(IChoicesParentVisitor visitor) throws Exception{
		return visitor.visit(this);
	}

	public String getQualifiedName() {
		if(getParent() == getRoot() || getParent() == null){
			return getName();
		}
		return getParent().getName() + ":" + getName();
	}

	@Override
	public String getNonQualifiedName() {
		return getName();
	}

	@Override
	public String toString(){
		return getName() + ": " + getType();
	}

	@Override
	public IParametersParentNode getParametersParent(){
		return (IParametersParentNode)getParent();
	}

	@Override
	public Set<ConstraintNode> getMentioningConstraints() {
		Set<ConstraintNode> result = new HashSet<ConstraintNode>();
		for(BasicParameterNode parameter : getLinkedMethodParameters()){
			result.addAll(parameter.getMentioningConstraints());
		}
		return result;
	}

	@Override
	public Set<ConstraintNode> getMentioningConstraints(String label){
		Set<ConstraintNode> result = new HashSet<ConstraintNode>();
		for(BasicParameterNode parameter : getLinkedMethodParameters()){
			for(ConstraintNode constraint : parameter.getMentioningConstraints()){
				if(constraint.mentions(parameter, label)){
					result.add(constraint);
				}
			}
		}
		return result;
	}

	public List<ChoiceNode> getChoicesCopy() {
		List<ChoiceNode> copy = new ArrayList<>();
		for(ChoiceNode choice : getChoices()){
			copy.add(choice.makeClone());
		}
		return copy;
	}

}
