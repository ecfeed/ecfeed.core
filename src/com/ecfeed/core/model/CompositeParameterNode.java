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

import com.ecfeed.core.model.ConstraintNodeListHolder.ConstraintsItr;
import com.ecfeed.core.model.utils.ParametersLister;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public class CompositeParameterNode extends AbstractParameterNode implements IParametersAndConstraintsParentNode {

	public static final String COMPOSITE_PARAMETER_TYPE = "Structure";

	private ParametersLister fParametersHolder; 
	private ConstraintNodeListHolder fConstraintNodeListHolder;

	public CompositeParameterNode(
			String name,
			IModelChangeRegistrator modelChangeRegistrator) {

		this(name, null, modelChangeRegistrator);
	}

	public CompositeParameterNode(
			String name,
			AbstractParameterNode link,
			IModelChangeRegistrator modelChangeRegistrator) {

		super(name, modelChangeRegistrator);

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);

		setLinkToGlobalParameter(link);

		fParametersHolder = new ParametersLister(modelChangeRegistrator);
		fConstraintNodeListHolder = new ConstraintNodeListHolder(modelChangeRegistrator);
	}

	@Override
	public void setName(String name) {

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);

		super.setName(name);
	}

	@Override
	public String getNonQualifiedName() {

		return getName();
	}

	@Override
	public String toString() {

		return getName() + " : " + "Structure";
	}

	@Override
	public CompositeParameterNode makeClone() {
		CompositeParameterNode copy = new CompositeParameterNode(getName(), getLinkToGlobalParameter(), getModelChangeRegistrator());

		for (AbstractParameterNode parameter : getParameters()) {
			copy.addParameter((AbstractParameterNode) parameter.makeClone());
		}

		copy.setProperties(getProperties());
		copy.setParent(this.getParent());

		return copy;
	}

	@Override
	public List<IAbstractNode> getChildren() {

		List<IAbstractNode> children = new ArrayList<>();

		List<AbstractParameterNode> parameters = fParametersHolder.getParameters();
		children.addAll(parameters);

		List<ConstraintNode> constraintNodes = fConstraintNodeListHolder.getConstraintNodes();
		children.addAll(constraintNodes);

		return children;
	}

	@Override
	public boolean isMatch(IAbstractNode other) {

		if (other instanceof CompositeParameterNode == false) {
			return false;
		}

		CompositeParameterNode otherComposite = (CompositeParameterNode) other;

		if (!propertiesMatch(otherComposite)) {
			return false;
		}

		if (!fParametersHolder.isMatch(otherComposite.fParametersHolder)) {
			return false;
		}

		return super.isMatch(other);
	}

	public boolean propertiesMatch(CompositeParameterNode parameter) {

		if (!StringHelper.isEqual(getName(), parameter.getName())) {
			return false;
		}

		if (isLinked() != parameter.isLinked()) {
			return false;
		}

		if (isLinked()) {
			if (!StringHelper.isEqual(getLinkToGlobalParameter().getName(), parameter.getLinkToGlobalParameter().getName())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int getChildrenCount() {

		return fParametersHolder.getParametersCount();
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public void addParameter(AbstractParameterNode parameter) {

		fParametersHolder.addParameter(parameter, this);
	}

	@Override
	public void addParameter(
			AbstractParameterNode parameter, 
			AbstractParameterNode linkingContext) {

		fParametersHolder.addParameter(parameter, linkingContext, this);
	}

	@Override
	public void addParameter(
			AbstractParameterNode parameter, 
			AbstractParameterNode linkingContext,
			int index) {

		fParametersHolder.addParameter(parameter, linkingContext, index, this);
	}

	@Override
	public void addParameter(AbstractParameterNode parameter, int index) {

		fParametersHolder.addParameter(parameter, null, index, this);
	}

	@Override
	public void addParameters(List<AbstractParameterNode> parameters) {

		fParametersHolder.addParameters(parameters, this);
	}

	@Override
	public boolean removeParameter(AbstractParameterNode parameter) {

		return fParametersHolder.removeParameter(parameter);
	}

	@Override
	public void replaceParameters(List<AbstractParameterNode> parameters) {

		fParametersHolder.replaceParameters(parameters, this);
	}

	@Override
	public int getParametersCount() {

		return fParametersHolder.getParametersCount();
	}

	@Override
	public List<AbstractParameterNode> getParameters() {

		return fParametersHolder.getParameters();
	}

	@Override
	public AbstractParameterNode getParameter(int parameterIndex) {

		return fParametersHolder.getParameter(parameterIndex);
	}

	@Override
	public AbstractParameterNode findParameter(String parameterName) {

		return fParametersHolder.findParameter(parameterName);
	}

	@Override
	public int getParameterIndex(String parameterName) {

		return fParametersHolder.getParameterIndex(parameterName);
	}

	@Override
	public boolean parameterExists(String parameterName) {

		return fParametersHolder.parameterExists(parameterName);
	}

	@Override
	public boolean parameterExists(BasicParameterNode abstractParameterNode) {

		return fParametersHolder.parameterExists(abstractParameterNode);
	}

	@Override
	public List<String> getParameterTypes() {
		return new ArrayList<String>();
	}

	@Override
	public List<String> getParametersNames() {

		return fParametersHolder.getParametersNames();
	}

	@Override
	public String generateNewParameterName(String startParameterName) {

		return fParametersHolder.generateNewParameterName(startParameterName);
	}

	@Override
	public IParametersParentNode getParametersParent() {

		return (IParametersParentNode)getParent();
	}

	@Override
	public ConstraintsItr getIterator() {

		return fConstraintNodeListHolder.getIterator();
	}

	@Override
	public boolean hasNextConstraint(ConstraintsItr contIterator) {

		return fConstraintNodeListHolder.hasNextConstraint(contIterator);
	}

	@Override
	public ConstraintNode getNextConstraint(ConstraintsItr contIterator) {

		return fConstraintNodeListHolder.getNextConstraint(contIterator);
	}

	@Override
	public void removeConstraint(ConstraintsItr contIterator) {

		fConstraintNodeListHolder.removeConstraint(contIterator);
	}

	@Override
	public void addConstraint(ConstraintNode constraint) {

		fConstraintNodeListHolder.addConstraint(constraint, this);
	}

	@Override
	public void addConstraint(ConstraintNode constraint, int index) {

		fConstraintNodeListHolder.addConstraint(constraint, index, this);
	}

	@Override
	public List<ConstraintNode> getConstraintNodes() {

		return fConstraintNodeListHolder.getConstraintNodes();
	}

	@Override
	public List<ConstraintNode> getConstraintNodes(String name) {

		return fConstraintNodeListHolder.getConstraintNodes();
	}

	@Override
	public List<Constraint> getConstraints() {

		return fConstraintNodeListHolder.getConstraints();
	}

	@Override
	public void setConstraints(List<ConstraintNode> constraints) {

		fConstraintNodeListHolder.setConstraints(constraints);
	}

	@Override
	public List<Constraint> getConstraints(String name) {

		return fConstraintNodeListHolder.getConstraints(name);
	}

	@Override
	public Set<String> getNamesOfConstraints() {

		return fConstraintNodeListHolder.getConstraintsNames();
	}

	@Override
	public boolean removeConstraint(ConstraintNode constraint) {

		return fConstraintNodeListHolder.removeConstraint(constraint);
	}

	@Override
	public boolean isChoiceMentionedInConstraints(ChoiceNode choice) {

		return fConstraintNodeListHolder.isChoiceMentioned(choice);
	}

	@Override
	public Set<ConstraintNode> getMentioningConstraints(BasicParameterNode parameter) {

		return fConstraintNodeListHolder.getMentioningConstraints(parameter);
	}

	@Override
	public Set<ConstraintNode> getMentioningConstraints(BasicParameterNode parameter, String label) {

		return fConstraintNodeListHolder.getMentioningConstraints(parameter, label);
	}

	@Override
	public Set<ConstraintNode> getMentioningConstraints(ChoiceNode choice) {

		return fConstraintNodeListHolder.getMentioningConstraints(choice);
	}

	@Override
	public boolean isParameterMentionedInConstraints(BasicParameterNode parameter) {

		return fConstraintNodeListHolder.isParameterMentioned(parameter);
	}

	@Override
	public void replaceConstraints(List<ConstraintNode> constraints) {

		fConstraintNodeListHolder.replaceConstraints(constraints);
	}

	@Override
	public void removeAllConstraints() {

		fConstraintNodeListHolder.removeAllConstraints();
	}

	@Override
	public void removeMentioningConstraints(BasicParameterNode methodParameter) {

		fConstraintNodeListHolder.removeMentioningConstraints(methodParameter);
	}

	public String getType() {

		return COMPOSITE_PARAMETER_TYPE;
	}

	@Override
	public CompositeParameterNode getLinkDestination() {

		return (CompositeParameterNode) super.getLinkDestination();
	}

	@Override
	public List<IAbstractNode> getDirectChildren() {

		if (isLinked()) {
			return new ArrayList<>();
		}

		return getChildren();
	}

	@Override
	public boolean canAddChild(IAbstractNode child) {

		if (child instanceof AbstractParameterNode) {
			return true;
		}
		
		if (child instanceof ConstraintNode) {
			return AbstractNodeHelper.parentIsTheSame(child, this);
		}

		return false;
	}

}
