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

import com.ecfeed.core.model.utils.ParametersLister;

public class RootNode extends AbstractNode implements IParametersParentNode {

	ParametersLister fParametersLister;
	private List<ClassNode> fClasses;
	private int fModelVersion;

	public RootNode(String name, IModelChangeRegistrator modelChangeRegistrator) {

		this(name, modelChangeRegistrator, ModelVersionDistributor.getCurrentSoftwareVersion());

		registerChange();
	}

	public RootNode(String name, IModelChangeRegistrator modelChangeRegistrator, int modelVersion) {

		super(name, modelChangeRegistrator);

		fParametersLister = new ParametersLister(modelChangeRegistrator);

		fClasses = new ArrayList<ClassNode>();
		fModelVersion = modelVersion;
	}

	@Override
	public List<IAbstractNode> getChildren(){
		List<IAbstractNode> children = new ArrayList<>(super.getChildren());
		children.addAll(fParametersLister.getReferenceToParameters());
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
	public RootNode makeClone(Optional<NodeMapper> nodeMapper) {

		RootNode cloneOfRootNode = new RootNode(getName(), getModelChangeRegistrator(), fModelVersion);

		cloneOfRootNode.setProperties(getProperties());

		for (AbstractParameterNode abstractParameterNode : getParameters()) {

			AbstractParameterNode clone = (AbstractParameterNode) abstractParameterNode.makeClone(nodeMapper);
			cloneOfRootNode.addParameter(clone);
		}

		for (ClassNode classnode : fClasses){
			cloneOfRootNode.addClass(classnode.makeClone(nodeMapper));
		}

		cloneOfRootNode.setParent(this.getParent());

		return cloneOfRootNode;
	}

	//	@Override
	//	public RootNode makeClone() {
	//		RootNode copy = new RootNode(getName(), getModelChangeRegistrator(), fModelVersion);
	//
	//		copy.setProperties(getProperties());
	//
	//		for (AbstractParameterNode abstractParameterNode : getParameters()) {
	//			
	//			BasicParameterNode globalParameterNode = (BasicParameterNode)abstractParameterNode;
	//			copy.addParameter(globalParameterNode.makeClone());
	//		}
	//
	//		for (ClassNode classnode : fClasses){
	//			copy.addClass(classnode.makeClone());
	//		}
	//		
	//		copy.setParent(this.getParent());
	//		return copy;
	//	}

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

	public List<CompositeParameterNode> getCompositeParameterNodes() {

		List<CompositeParameterNode> nodes = CompositeParameterNodeHelper.getChildCompositeParameterNodes(this);
		return nodes;
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

		if (!fParametersLister.isMatch(otherRootNode.fParametersLister)) {
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

		int countOfParameters = fParametersLister.getParametersCount();
		int countOfClasses = getClasses().size();

		return countOfParameters + countOfClasses;
	}

	@Override
	public void addParameter(AbstractParameterNode parameter) {

		fParametersLister.addParameter(parameter, this);
	}

	@Override
	public void addParameter(
			AbstractParameterNode parameter, 
			AbstractParameterNode linkingContext
			) {

		fParametersLister.addParameter(parameter, this);
	}

	@Override
	public void addParameter(
			AbstractParameterNode parameter, 
			AbstractParameterNode linkingContext,
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
	public AbstractParameterNode findParameter(String parameterNameToFind) {

		return fParametersLister.findParameter(parameterNameToFind);
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

		return fParametersLister.getParameterTypes();
	}

	@Override
	public List<String> getParametersNames() {

		return fParametersLister.getParametersNames();
	}

	@Override
	public String generateNewParameterName(String startParameterName) {

		return fParametersLister.generateNewParameterName(startParameterName);
	}

	public List<BasicParameterNode> getGlobalBasicParameters() {

		List<BasicParameterNode> globalParameterNodes = new ArrayList<>();

		List<AbstractParameterNode> abstractParameters = getParameters();

		for (AbstractParameterNode abstractParameterNode : abstractParameters) {

			if (abstractParameterNode instanceof BasicParameterNode) {
				BasicParameterNode globalParameterNode = (BasicParameterNode)abstractParameterNode;

				globalParameterNodes.add(globalParameterNode);
			}
		}

		return globalParameterNodes;
	}

	public List<CompositeParameterNode> getGlobalCompositeParameters() {

		List<CompositeParameterNode> globalParameterNodes = new ArrayList<>();

		List<AbstractParameterNode> abstractParameters = getParameters();

		for (AbstractParameterNode abstractParameterNode : abstractParameters) {

			if (abstractParameterNode instanceof CompositeParameterNode) {
				CompositeParameterNode globalParameterNode = (CompositeParameterNode) abstractParameterNode;

				globalParameterNodes.add(globalParameterNode);
				globalParameterNodes.addAll(globalParameterNode.getNestedCompositeParameters(true));
			}
		}

		return globalParameterNodes;
	}

	@Override
	public List<IAbstractNode> getDirectChildren() {
		return getChildren();
	}

	@Override
	public boolean canAddChild(IAbstractNode child) {

		if (child instanceof AbstractParameterNode) {
			return true;
		}

		if (child instanceof ClassNode) {
			return true;
		}

		return false;
	}

	@Override
	public void shiftParameters(List<Integer> indicesOfParameters, int shift) {
		
		fParametersLister.shiftElements(indicesOfParameters, shift);
	}

	@Override
	public void shiftOneParameter(int indexOfParameter, int shift) {
		
		fParametersLister.shiftOneElement(indexOfParameter, shift);
	}
	
}
