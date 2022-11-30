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

//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//import com.ecfeed.core.utils.ExceptionHelper;
//import com.ecfeed.core.utils.JavaLanguageHelper;
//import com.ecfeed.core.utils.SimpleLanguageHelper;

public abstract class AbstractParameterNode extends AbstractNode {

	//	private String fType; TODO MO-RE remove unused code
	//	private String fTypeComments;
	//
	//	private Optional<String> fSuggestedType;

	//	public abstract boolean isGlobalParameter();
	//	
	//	public abstract List<MethodNode> getMethods();
	//	public abstract Object accept(IParameterVisitor visitor) throws Exception;
	//
	//	public abstract Set<ConstraintNode> getMentioningConstraints();
	//	public abstract Set<ConstraintNode> getMentioningConstraints(String label);

	public enum ParameterType {
		BASIC,
		COMPOSITE
	}


	public AbstractParameterNode(String name, IModelChangeRegistrator modelChangeRegistrator) {
		super(name, modelChangeRegistrator);

		//		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);
		//
		//		fSuggestedType = Optional.empty();
		//		fType = type;
		//
		//		createDefaultProperties();
	}

	//	@Override
	//	public void setName(String name) {
	//
	//		JavaLanguageHelper.verifyIsValidJavaIdentifier(name);
	//
	//		super.setName(name);
	//	}

	//	@Override
	//	public AbstractParameterNode getParameter() {
	//		return this;
	//	}
	//
	//	public IParametersParentNode getParametersParent() {
	//		
	//		return (IParametersParentNode)getParent();
	//	}

	//	@Override
	//	public int getMyIndex() {
	//		
	//		IParametersParentNode parametersParent = getParametersParent();
	//		
	//		if (parametersParent == null) {
	//			return -1;
	//		}
	//		
	//		List<AbstractParameterNode> parameters = parametersParent.getParameters();
	//		
	//		return parameters.indexOf(this);
	//	}

	//	@Override
	//	public int getMaxIndex(){
	//		if(getParametersParent() != null){
	//			return getParametersParent().getParameters().size();
	//		}
	//		return -1;
	//	}

	//	@Override
	//	public boolean isMatch(IAbstractNode compared){
	//		if(compared instanceof AbstractParameterNode == false){
	//			return false;
	//		}
	//		AbstractParameterNode comparedParameter = (AbstractParameterNode)compared;
	//		if(comparedParameter.getType().equals(fType) == false){
	//			return false;
	//		}
	//		return super.isMatch(compared);
	//	}

	//	public boolean isCorrectableToBeRandomizedType() {
	//		return JavaLanguageHelper.isNumericTypeName(fType) || JavaLanguageHelper.isStringTypeName(fType);
	//	}

	//	public String getType() {
	//		return fType; 
	//	}

	//	public void setType(String type) {
	//
	//		if (SimpleLanguageHelper.isSimpleType(type)) {
	//			ExceptionHelper.reportRuntimeException("Attempt to set invalid parameter type: " + type);
	//		}
	//
	//		fType = type;
	//		registerChange();
	//	}

	//	public String getTypeComments() {
	//		return fTypeComments;
	//	}

	//	public void setTypeComments(String comments){
	//		fTypeComments = comments;
	//		registerChange();
	//	}

	//	public Optional<String> getSuggestedType() {
	//		return fSuggestedType;
	//	}

	//	public void setSuggestedType(String typeHidden) {
	//		fSuggestedType = Optional.ofNullable(typeHidden);
	//	}

	//	private void createDefaultProperties() {
	//
	//		setPropertyDefaultValue(NodePropertyDefs.PropertyId.PROPERTY_WEB_ELEMENT_TYPE);
	//		setPropertyDefaultValue(NodePropertyDefs.PropertyId.PROPERTY_OPTIONAL);
	//	}

	public boolean isGlobalParameter() {

		IAbstractNode parent = getParent();

		if (parent instanceof MethodNode) {
			return false;
		}

		if (parent instanceof CompositeParameterNode) {
			return false;
		}

		return true;
	}

	public String getQualifiedName() {

		if (isGlobalParameter()) {

			if (getParent() == getRoot() || getParent() == null) {
				return getName();
			}

			return getParent().getName() + ":" + getName();
		} else {

			return getNonQualifiedName();
		}
	}

	//	public MethodNode getMethod() {
	//		
	//		IAbstractNode parent = getParent();
	//		
	//		if (parent instanceof MethodNode) {
	//			return (MethodNode) parent;
	//		}
	//
	//		return null;
	//	}

	public IParametersParentNode getParametersParent() {

		IAbstractNode parent = getParent();

		if (parent instanceof IParametersParentNode) {
			return (IParametersParentNode) parent;
		}

		return null;
	}	

}
