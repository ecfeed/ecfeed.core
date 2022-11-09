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

public class RootNode extends AbstractNode implements IParametersParentNode {

	ParametersHolder fParametersHolder;
	private List<ClassNode> fClasses;
	private int fModelVersion;

	public RootNode(String name, IModelChangeRegistrator modelChangeRegistrator) {

		this(name, modelChangeRegistrator, ModelVersionDistributor.getCurrentSoftwareVersion());

		registerChange();
	}

	public RootNode(String name, IModelChangeRegistrator modelChangeRegistrator, int modelVersion) {

		super(name, modelChangeRegistrator);
		
		fParametersHolder = new ParametersHolder(modelChangeRegistrator);

		fClasses = new ArrayList<ClassNode>();
		fModelVersion = modelVersion;
	}

	@Override
	public List<IAbstractNode> getChildren(){
		List<IAbstractNode> children = new ArrayList<>(super.getChildren());
		children.addAll(fParametersHolder.getParameters());
		children.addAll(fClasses);
		return children;
	}

	@Override
	public int getMaxChildIndex(IAbstractNode potentialChild){
		if(potentialChild instanceof BasicParameterNode) return getParameters().size();
		if(potentialChild instanceof BasicParameterNode) return getParameters().size();
		if(potentialChild instanceof ClassNode) return getClasses().size();
		return super.getMaxChildIndex(potentialChild);
	}

	@Override
	public RootNode makeClone() {
		RootNode copy = new RootNode(getName(), getModelChangeRegistrator(), fModelVersion);

		copy.setProperties(getProperties());

		for (BasicParameterNode abstractParameterNode : getParameters()) {
			
			BasicParameterNode globalParameterNode = (BasicParameterNode)abstractParameterNode;
			copy.addParameter(globalParameterNode.makeClone());
		}

		for (ClassNode classnode : fClasses){
			copy.addClass(classnode.makeClone());
		}
		
		copy.setParent(this.getParent());
		return copy;
	}

	@Override
	public String getNonQualifiedName() {
		return getName();
	}

	public boolean addClass(ClassNode node){
		return addClass(node, fClasses.size());
	}

	public boolean addClass(ClassNode node, int index){
		if(index >= 0 && index <= fClasses.size()){
			fClasses.add(index, node);
			node.setParent(this);
			boolean result = fClasses.indexOf(node) == index;
			registerChange();
			return result;
		}
		return false;
	}

	public List<ClassNode> getClasses() {
		return fClasses;
	}

	public int getModelVersion() {
		return fModelVersion;
	}

	public void setVersion(int modelVersion) {
		fModelVersion = modelVersion;
	}	

	public ClassNode getClass(String name) {
		for(ClassNode childClass : getClasses()){
			if(childClass.getName().equals(name)){
				return childClass;
			}
		}
		return null;
	}

	public boolean removeClass(ClassNode classNode){

		boolean result = fClasses.remove(classNode);
		registerChange();

		return result;
	}

	@Override
	public boolean isMatch(IAbstractNode other){
		
		if(other instanceof RootNode == false){
			return false;
		}

		RootNode otherRootNode = (RootNode)other;
		
		if (!fParametersHolder.isMatch(otherRootNode.fParametersHolder)) {
			return false;
		}
		
		if(getClasses().size() != otherRootNode.getClasses().size()){
			return false;
		}

		for (int classIndex = 0; classIndex < getClasses().size(); classIndex++) {
			
			if (getClasses().get(classIndex).isMatch(otherRootNode.getClasses().get(classIndex)) == false) {
				return false;
			}
		}

		return super.isMatch(otherRootNode);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception{
		return visitor.visit(this);
	}

	public List<MethodNode> getChildMethods(BasicParameterNode parameter) {
		List<MethodNode> result = new ArrayList<>();
		for(ClassNode classNode : getClasses()){
			result.addAll(classNode.getChildMethods(parameter));
		}
		return result;
	} 
	
	@Override
	public int getChildrenCount() {
		
		int countOfParameters = fParametersHolder.getParametersCount();
		int countOfClasses = getClasses().size();
		
		return countOfParameters + countOfClasses;
	}

	@Override
	public void addParameter(BasicParameterNode parameter) {
		
		fParametersHolder.addParameter(parameter, this);
	}

	@Override
	public void addParameter(BasicParameterNode parameter, int index) {
		
		fParametersHolder.addParameter(parameter, index, this);
	}
	
	@Override
	public void addParameters(List<BasicParameterNode> parameters) {
		
		fParametersHolder.addParameters(parameters, this);
	}

	@Override
	public boolean removeParameter(BasicParameterNode parameter) {
		
		return fParametersHolder.removeParameter(parameter);
	}

	@Override
	public void replaceParameters(List<BasicParameterNode> parameters) {
		
		fParametersHolder.replaceParameters(parameters);
	}

	@Override
	public int getParametersCount() {
		
		return fParametersHolder.getParametersCount();
	}

	@Override
	public List<BasicParameterNode> getParameters() {
		
		return fParametersHolder.getParameters();
	}

	@Override
	public BasicParameterNode getParameter(int parameterIndex) {
		
		return fParametersHolder.getParameter(parameterIndex);
	}

	@Override
	public BasicParameterNode findParameter(String parameterNameToFind) {
		
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
		
		return fParametersHolder.getParameterTypes();
	}

	@Override
	public List<String> getParametersNames() {
		
		return fParametersHolder.getParametersNames();
	}

	@Override
	public String generateNewParameterName(String startParameterName) {
		
		return fParametersHolder.generateNewParameterName(startParameterName);
	}
	
	public List<BasicParameterNode> getGlobalParameters() {
		
		List<BasicParameterNode> globalParameterNodes = new ArrayList<>();
		
		List<BasicParameterNode> abstractParameters = getParameters();
		
		for (BasicParameterNode abstractParameterNode : abstractParameters) {
			
			BasicParameterNode globalParameterNode = (BasicParameterNode)abstractParameterNode;
			
			globalParameterNodes.add(globalParameterNode);
		}
		
		return globalParameterNodes;
	}
}
