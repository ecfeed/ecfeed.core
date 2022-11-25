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
import com.ecfeed.core.utils.JavaLanguageHelper;

public class CompositeParameterNode extends AbstractParameterNode implements IParametersAndConstraintsParentNode {
	
	public static final String COMPOSITE_PARAMETER_TYPE = "Structure";
	
	private ParametersHolder fParametersHolder; 

	public CompositeParameterNode(
			String name,
			IModelChangeRegistrator modelChangeRegistrator) {
		
		super(name, modelChangeRegistrator);

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);
		
		fParametersHolder = new ParametersHolder(modelChangeRegistrator);

		createDefaultProperties();
	}

	public boolean isGlobalParameter() {
		
		IAbstractNode parent = getParent();
		
		if (parent == null) {
			return false;
		}
		
		if (parent instanceof MethodNode) {
			return false;
		}
		
		return true;
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
		
		return new String(getName());
	}

	@Override
	public CompositeParameterNode makeClone() {
		CompositeParameterNode copy = 
				new CompositeParameterNode(getName(), getModelChangeRegistrator());

		copy.setProperties(getProperties());
		copy.setParent(this.getParent());

		copy.setParent(getParent());
		return copy;
	}

	@Override
	public List<IAbstractNode> getChildren() {
		
		List<IAbstractNode> children = new ArrayList<>();
		children.addAll(fParametersHolder.getParameters());
		
		return children;
	}

	@Override
	public boolean isMatch(IAbstractNode other) {
		
		if (other instanceof CompositeParameterNode == false) {
			return false;
		}
		
		CompositeParameterNode otherComposite = (CompositeParameterNode) other;
		
		if (!fParametersHolder.isMatch(otherComposite.fParametersHolder)) {
			return false;
		}
		
		return super.isMatch(other);
	}

	@Override
	public int getChildrenCount() {

		return fParametersHolder.getParametersCount();
	}

	private void createDefaultProperties() {
		// TODO MO-RE
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
	public void addParameter(AbstractParameterNode parameter, int index) {
		
		fParametersHolder.addParameter(parameter, index, this);
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
		
		fParametersHolder.replaceParameters(parameters);
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
	public AbstractParameterNode findParameter(String parameterNameToFind) {
		
		return fParametersHolder.findParameter(parameterNameToFind);
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
	
	public IParametersParentNode getParametersParent() {

		return (IParametersParentNode)getParent();
	}

	@Override
	public ConstraintsItr getIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasNextConstraint(ConstraintsItr contIterator) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ConstraintNode getNextConstraint(ConstraintsItr contIterator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeConstraint(ConstraintsItr contIterator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addConstraint(ConstraintNode constraint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addConstraint(ConstraintNode constraint, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ConstraintNode> getConstraintNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ConstraintNode> getConstraintNodes(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Constraint> getConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Constraint> getConstraints(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getConstraintsNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeConstraint(ConstraintNode constraint) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChoiceMentionedInConstraints(ChoiceNode choice) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<ConstraintNode> getMentioningConstraints(BasicParameterNode parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ConstraintNode> getMentioningConstraints(BasicParameterNode parameter, String label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ConstraintNode> getMentioningConstraints(ChoiceNode choice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isParameterMentionedInConstraints(BasicParameterNode parameter) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void replaceConstraints(List<ConstraintNode> constraints) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAllConstraints() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeMentioningConstraints(BasicParameterNode methodParameter) {
		// TODO Auto-generated method stub
		
	}
	
}
