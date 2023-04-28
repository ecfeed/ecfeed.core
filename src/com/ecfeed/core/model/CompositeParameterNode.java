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
import java.util.Optional;
import java.util.Set;

import com.ecfeed.core.model.ConstraintNodeListHolder.ConstraintsItr;
import com.ecfeed.core.model.utils.ParametersLister;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.StringHelper;

public class CompositeParameterNode extends AbstractParameterNode implements IParametersAndConstraintsParentNode {

	public static final String COMPOSITE_PARAMETER_TYPE = "Structure";

	private ParametersLister fParametersLister; 
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

		fParametersLister = new ParametersLister(modelChangeRegistrator);
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
	public CompositeParameterNode makeClone(Optional<NodeMapper> nodeMapper) {

		CompositeParameterNode copyOfCompositeParameter = 
				new CompositeParameterNode(getName(), getLinkToGlobalParameter(), getModelChangeRegistrator());

		cloneParameters(copyOfCompositeParameter, nodeMapper);

		cloneConstraints(copyOfCompositeParameter, nodeMapper);

		copyOfCompositeParameter.setProperties(getProperties());
		copyOfCompositeParameter.setParent(this.getParent());

		if (nodeMapper.isPresent()) {
			nodeMapper.get().addMappings(this, copyOfCompositeParameter);
		}

		return copyOfCompositeParameter;
	}

	private void cloneParameters(CompositeParameterNode copyOfCompositeParameter, Optional<NodeMapper> nodeMapper) {

		for (AbstractParameterNode parameter : getParameters()) {

			copyOfCompositeParameter.addParameter((AbstractParameterNode) parameter.makeClone(nodeMapper));
		}
	}

	private void cloneConstraints(CompositeParameterNode clonedCompositeParameterNode, Optional<NodeMapper> nodeMapper) {

		ConstraintNodeListHolder clonedConstraintHolder = 
				fConstraintNodeListHolder.makeClone(clonedCompositeParameterNode, nodeMapper);

		clonedCompositeParameterNode.fConstraintNodeListHolder = clonedConstraintHolder;
	}

	@Override
	public List<IAbstractNode> getChildren() {

		List<IAbstractNode> children = new ArrayList<>();

		List<AbstractParameterNode> parameters = fParametersLister.getReferenceToParameters();
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

		if (!fParametersLister.isMatch(otherComposite.fParametersLister)) {
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

		return fParametersLister.getParametersCount();
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public void addParameter(AbstractParameterNode parameter) {

		fParametersLister.addParameter(parameter, this);
	}

	@Override
	public void addParameter(
			AbstractParameterNode parameter, 
			AbstractParameterNode linkingContext // TODO MO-RE remove linking context from upper class/interface
			) {

		fParametersLister.addParameter(parameter, this);
	}

	@Override
	public void addParameter(
			AbstractParameterNode parameter, 
			AbstractParameterNode linkingContext,  // TODO MO-RE remove linking context from upper class/interface
			int index) {

		fParametersLister.addParameter(parameter, index, this);
	}

	@Override
	public void addParameter(AbstractParameterNode parameter, int index) {

		fParametersLister.addParameter(parameter, index, this);
	}

	@Override
	public void addParameters(List<AbstractParameterNode> parameters) {

		fParametersLister.addParameters(parameters, this);
	}

	@Override
	public boolean removeParameter(AbstractParameterNode parameter) {

		return fParametersLister.removeParameter(parameter);
	}

	@Override
	public void replaceParameters(List<AbstractParameterNode> parameters) {

		fParametersLister.replaceParameters(parameters, this);
	}

	@Override
	public int getParametersCount() {

		return fParametersLister.getParametersCount();
	}

	@Override
	public List<AbstractParameterNode> getParameters() {

		return fParametersLister.getReferenceToParameters();
	}

	@Override
	public AbstractParameterNode getParameter(int parameterIndex) {

		return fParametersLister.getParameter(parameterIndex);
	}

	@Override
	public AbstractParameterNode findParameter(String parameterName) {

		return fParametersLister.findParameter(parameterName);
	}

	@Override
	public int getParameterIndex(String parameterName) {

		return fParametersLister.getParameterIndex(parameterName);
	}

	@Override
	public boolean parameterExists(String parameterName) {

		return fParametersLister.parameterExists(parameterName);
	}

	@Override
	public boolean parameterExists(BasicParameterNode abstractParameterNode) {

		return fParametersLister.parameterExists(abstractParameterNode);
	}

	@Override
	public List<String> getParameterTypes() {
		return new ArrayList<String>();
	}

	@Override
	public List<String> getParametersNames() {

		return fParametersLister.getParametersNames();
	}

	@Override
	public String generateNewParameterName(String startParameterName) {

		return fParametersLister.generateNewParameterName(startParameterName);
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
