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

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;
import com.ecfeed.core.utils.SimpleLanguageHelper;

public abstract class AbstractParameterNode extends ChoicesParentNode {

	private String fType;
	private String fTypeComments;

	private Optional<String> fSuggestedType;

	public abstract List<MethodNode> getMethods();
	public abstract Object accept(IParameterVisitor visitor) throws Exception;

	public abstract Set<ConstraintNode> getMentioningConstraints();
	public abstract Set<ConstraintNode> getMentioningConstraints(String label);


	public AbstractParameterNode(String name, IModelChangeRegistrator modelChangeRegistrator, String type) { // TODO EX-AM move change registrator to the end
		super(name, modelChangeRegistrator);

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);

		fSuggestedType = Optional.empty();
		fType = type;

		createDefaultProperties();
	}

	@Override
	public void setName(String name) {

		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);

		super.setName(name);
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

	public boolean isCorrectableToBeRandomizedType() {
		return JavaLanguageHelper.isNumericTypeName(fType) || JavaLanguageHelper.isStringTypeName(fType);
	}

	public String getType() {
		return fType; 
	}

	public void setType(String type) {
		
		if (SimpleLanguageHelper.isSimpleType(type)) {
			ExceptionHelper.reportRuntimeException("Attempt to set invalid parameter type: " + type);
		}

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

	private void createDefaultProperties() {

		setPropertyDefaultValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE);
		setPropertyDefaultValue(NodePropertyDefs.PropertyId.PROPERTY_OPTIONAL);
	}

}
