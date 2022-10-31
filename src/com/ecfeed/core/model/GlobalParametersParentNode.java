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

public abstract class GlobalParametersParentNode extends ParametersParentNode { // TODO MO-RE remove class

	public GlobalParametersParentNode(String name, IModelChangeRegistrator modelChangeRegistrator) {
		super(name, modelChangeRegistrator);
	}

	public List<BasicParameterNode> getGlobalParameters() {
		List<BasicParameterNode> result = new ArrayList<>();
		for(AbstractParameterNode parameter : getParameters()){
			result.add((BasicParameterNode)parameter);
		}
		return result;
	}

	public List<BasicParameterNode> getAllGlobalParametersAvailableForLinking() {
		List<BasicParameterNode> result = getAllGlobalParametersAvailableForLinking(getParent());
		result.addAll(getGlobalParameters());
		return result;
	}

	public BasicParameterNode findGlobalParameter(String qualifiedName){
		
		for (BasicParameterNode parameter : getAllGlobalParametersAvailableForLinking()) {
			
			String currentQualifiedName = parameter.getQualifiedName();
			
			if(currentQualifiedName.equals(qualifiedName)){
				return parameter;
			}
		}
		
		return null;
	}

	private List<BasicParameterNode> getAllGlobalParametersAvailableForLinking(IAbstractNode parent) {
		
		if(parent == null){
			return new ArrayList<BasicParameterNode>();
		}
		
		if (parent instanceof RootNode) {
			RootNode rootNode = (RootNode)parent;
			return rootNode.getGlobalParameters();
		}
		
		if (parent instanceof ClassNode) {
			ClassNode classNode = (ClassNode)parent;
			return classNode.getAllGlobalParametersAvailableForLinking();
		}
		
		if(parent instanceof GlobalParametersParentNode){
			return ((GlobalParametersParentNode)parent).getAllGlobalParametersAvailableForLinking();
		}
		
		if(parent.getParent() != null){
			return getAllGlobalParametersAvailableForLinking(parent.getParent());
		}
		
		return new ArrayList<BasicParameterNode>();
	}

	public BasicParameterNode getGlobalParameter(String parameterName) {
		if(findParameter(parameterName) != null){
			return (BasicParameterNode)findParameter(parameterName);
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
