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

public abstract class GlobalParametersParentNode extends ParametersParentNode {

	public GlobalParametersParentNode(String name, IModelChangeRegistrator modelChangeRegistrator) {
		super(name, modelChangeRegistrator);
	}

	public List<GlobalParameterNode> getGlobalParameters() {
		List<GlobalParameterNode> result = new ArrayList<>();
		for(AbstractParameterNode parameter : getParameters()){
			result.add((GlobalParameterNode)parameter);
		}
		return result;
	}

	public List<GlobalParameterNode> getAvailableGlobalParameters() {
		List<GlobalParameterNode> result = getAvailableGlobalParameters(getParent());
		result.addAll(getGlobalParameters());
		return result;
	}

	public GlobalParameterNode findGlobalParameter(String qualifiedName){
		for(GlobalParameterNode parameter : getAvailableGlobalParameters()){
			if(parameter.getQualifiedName().equals(qualifiedName)){
				return parameter;
			}
		}
		return null;
	}

	private List<GlobalParameterNode> getAvailableGlobalParameters(AbstractNode parent) {
		if(parent == null){
			return new ArrayList<GlobalParameterNode>();
		}
		else if(parent instanceof GlobalParametersParentNode){
			return ((GlobalParametersParentNode)parent).getAvailableGlobalParameters();
		}else if(parent.getParent() != null){
			return getAvailableGlobalParameters(parent.getParent());
		}else{
			return new ArrayList<GlobalParameterNode>();
		}
	}

	public GlobalParameterNode getGlobalParameter(String parameterName) {
		if(getParameter(parameterName) != null){
			return (GlobalParameterNode)getParameter(parameterName);
		}
		return null;
	}

	protected String getParentName(String qualifiedName){
		return qualifiedName.substring(0, qualifiedName.indexOf(":"));
	}

	protected String getParameterName(String qualifiedName){
		return qualifiedName.substring(qualifiedName.indexOf(":") + 1, qualifiedName.length());
	}
}
