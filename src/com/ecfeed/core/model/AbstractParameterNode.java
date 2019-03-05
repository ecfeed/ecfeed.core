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
import java.util.Optional;
import java.util.Set;

import com.ecfeed.core.utils.JavaTypeHelper;

public abstract class AbstractParameterNode extends ChoicesParentNode {

	private String fType;
	private String fTypeComments;
	
	private Optional<String> fSuggestedType;

	public AbstractParameterNode(String name, IModelChangeRegistrator modelChangeRegistrator, String type) {
		super(name, modelChangeRegistrator);
		fSuggestedType = Optional.empty();
		fType = type;

		createDefaultProperties();
	}

	private void createDefaultProperties() {
		setPropertyDefaultValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE);
		setPropertyDefaultValue(NodePropertyDefs.PropertyId.PROPERTY_OPTIONAL);
	}

	@Override
	public AbstractParameterNode getParameter() {
		return this;
	}

	public ParametersParentNode getParametersParent(){
		return (ParametersParentNode)getParent();
	}

	@Override
	public int getMyIndex(){
		if(getParametersParent() == null){
			return -1;
		}
		return getParametersParent().getParameters().indexOf(this);
	}

	@Override
	public int getMaxIndex(){
		if(getParametersParent() != null){
			return getParametersParent().getParameters().size();
		}
		return -1;
	}

	public boolean isCorrectableToBeRandomizedType() {
		return JavaTypeHelper.isNumericTypeName(fType) || JavaTypeHelper.isStringTypeName(fType);
	}

	public String getType() {
		return fType;
	}

	public void setType(String type) {
		fType = type;
		registerChange();
	}

	public String getTypeComments() {
		return fTypeComments;
	}

	public void setTypeComments(String comments){
		fTypeComments = comments;
		registerChange();
	}

	public Optional<String> getSuggestedType() {
		return fSuggestedType;
	}

	public void setSuggestedType(String typeHidden) {
		fSuggestedType = Optional.ofNullable(typeHidden);
	}
	
	@Override
	public boolean isMatch(AbstractNode compared){
		if(compared instanceof AbstractParameterNode == false){
			return false;
		}
		AbstractParameterNode comparedParameter = (AbstractParameterNode)compared;
		if(comparedParameter.getType().equals(fType) == false){
			return false;
		}
		return super.isMatch(compared);
	}

	public abstract List<MethodNode> getMethods();
	public abstract Object accept(IParameterVisitor visitor) throws Exception;

	public abstract Set<ConstraintNode> getMentioningConstraints();
	public abstract Set<ConstraintNode> getMentioningConstraints(String label);

}
