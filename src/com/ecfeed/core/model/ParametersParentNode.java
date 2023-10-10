///*******************************************************************************
// *
// * Copyright (c) 2016 ecFeed AS.                                                
// * All rights reserved. This program and the accompanying materials              
// * are made available under the terms of the Eclipse Public License v1.0         
// * which accompanies this distribution, and is available at                      
// * http://www.eclipse.org/legal/epl-v10.html 
// *  
// *******************************************************************************/
//
//package com.ecfeed.core.model;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public abstract class ParametersParentNode extends AbstractNode implements IParametersParentNode {
//
//	ParametersHolder fParametersHolder;
//
//	public ParametersParentNode(String name, IModelChangeRegistrator modelChangeRegistrator) {
//
//		super(name, modelChangeRegistrator);
//
//		fParametersHolder = new ParametersHolder(modelChangeRegistrator);
//	}
//
//	@Override
//	public List<IAbstractNode> getChildren() {
//
//		List<IAbstractNode> result = new ArrayList<>();
//		result.addAll(fParametersHolder.getParameters());
//
//		return result;
//	}
//
//	@Override
//	public int getChildrenCount() {
//
//		return fParametersHolder.getParametersCount();
//	}
//
//	@Override
//	public boolean isMatch(IAbstractNode node) {
//
//		if (node instanceof ParametersParentNode == false) {
//			return false;
//		}
//
//		ParametersParentNode comparedParent = (ParametersParentNode)node;
//
//		if (!fParametersHolder.isMatch(comparedParent.fParametersHolder)) {
//			return false;
//		}
//
//		return super.isMatch(node);
//	}
//
//	@Override
//	public void addParameter(BasicParameterNode parameter) {
//
//		fParametersHolder.addParameter(parameter, this);
//	}
//
//	@Override
//	public void addParameter(BasicParameterNode parameter, int index) {
//
//		fParametersHolder.addParameter(parameter, index, this);
//	}
//
//	@Override
//	public void addParameters(List<BasicParameterNode> parameters) {
//
//		fParametersHolder.addParameters(parameters, this);
//	}
//	
//	@Override
//	public boolean removeParameter(BasicParameterNode parameter) {
//
//		return fParametersHolder.removeParameter(parameter);
//	}
//
//	@Override
//	public List<BasicParameterNode> getParameters() {
//
//		return fParametersHolder.getParameters();
//	}
//
//	@Override
//	public int getParametersCount() {
//
//		return fParametersHolder.getParametersCount();
//	}	
//
//	@Override
//	public BasicParameterNode findParameter(String parameterNameToFind) {
//
//		return fParametersHolder.findParameter(parameterNameToFind);
//	}
//
//	@Override
//	public BasicParameterNode getParameter(int parameterIndex) {
//
//		return fParametersHolder.getParameter(parameterIndex);
//	}	
//
//	@Override
//	public int getParameterIndex(String parameterName) {
//
//		return fParametersHolder.getParameterIndex(parameterName);
//	}
//
//	public boolean parameterExists(String parameterName) {
//
//		return fParametersHolder.parameterExists(parameterName);
//	}
//
//	@Override
//	public boolean parameterExists(BasicParameterNode abstractParameterNode) {
//
//		return fParametersHolder.parameterExists(abstractParameterNode);
//	}
//
//	@Override
//	public List<String> getParameterTypes() {
//
//		return fParametersHolder.getParameterTypes();
//	}
//
//	@Override
//	public List<String> getParametersNames() {
//
//		return fParametersHolder.getParametersNames();
//	}
//
//	@Override
//	public void replaceParameters(List<BasicParameterNode> parameters) {
//
//		fParametersHolder.replaceParameters(parameters);
//	}
//
//	@Override
//	public String generateNewParameterName(String startParameterName) {
//
//		return fParametersHolder.generateNewParameterName(startParameterName);
//	}
//
//}
