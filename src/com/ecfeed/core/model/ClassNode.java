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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.utils.ParametersLister;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;

public class ClassNode extends AbstractNode implements IParametersParentNode {

	ParametersLister fParametersHolder;
	private List<MethodNode> fMethods;

	public ClassNode(String qualifiedName) {
		this(qualifiedName, null);
	}

	public ClassNode(String qualifiedName, IModelChangeRegistrator modelChangeRegistrator) {

		super(qualifiedName, modelChangeRegistrator);

		JavaLanguageHelper.verifyIsMatchWithJavaComplexIdentifier(qualifiedName);

		fParametersHolder = new ParametersLister(modelChangeRegistrator);
		fMethods = new ArrayList<MethodNode>();
	}

	@Override
	public List<IAbstractNode> getChildren(){

		List<IAbstractNode> children = new ArrayList<>();

		children.addAll(fParametersHolder.getParameters());
		children.addAll(fMethods);

		return children;
	}

	@Override
	public int getChildrenCount() {

		int parametetersSize = fParametersHolder.getParametersCount(); 
		int methodsSize = fMethods.size();

		return parametetersSize + methodsSize;
	}

	@Override
	public ClassNode makeClone(){
		ClassNode copy = new ClassNode(getName(), getModelChangeRegistrator());

		copy.setProperties(getProperties());

		for(BasicParameterNode parameter : getGlobalBasicParameters()){
			copy.addParameter(parameter.makeClone());
		}

		for(MethodNode method : fMethods){
			copy.addMethod(method.makeClone());
		}

		copy.setParent(getParent());
		return copy;
	}

	@Override
	public RootNode getRoot(){
		return (RootNode) getParent();
	}

	@Override
	public int getMaxChildIndex(IAbstractNode potentialChild){
		if(potentialChild instanceof BasicParameterNode) return getParameters().size();
		if(potentialChild instanceof MethodNode) return getMethods().size();
		return super.getMaxChildIndex(potentialChild);
	}

	public void setName(String qualifiedName) {

		JavaLanguageHelper.verifyIsMatchWithJavaComplexIdentifier(qualifiedName);

		super.setName(qualifiedName);
	}

	public int getMyClassIndex() {

		if (getParent() == null) {
			return -1;
		}

		int index = -1;

		for (IAbstractNode abstractNode : getParent().getChildren()) {

			if (abstractNode instanceof ClassNode) {
				index++;
			}

			if (abstractNode.equals(this)) {
				return index;
			}
		}

		return -1;
	}

	public boolean addMethod(MethodNode method) {
		return addMethod(method, fMethods.size());
	}

	public boolean addMethod(MethodNode method, int index) {

		if (findMethodWithTheSameName(method.getName()) != null) {

			ExceptionHelper.reportRuntimeException("Cannot add method. Method with the same name already exists.");
		}

		if (index >= 0 && index <= fMethods.size()) {
			fMethods.add(index, method);
			method.setParent(this);
			boolean result = fMethods.indexOf(method) == index;
			registerChange();
			return result;
		}

		return false;
	}

	public MethodNode findMethodWithTheSameName(String name) {

		List<MethodNode> methods = getMethods();

		for (MethodNode methodNode : methods) {

			if (methodNode.getName().equals(name)) {
				return methodNode;
			}
		}

		return null;
	}

	public List<MethodNode> getMethods() {
		return fMethods;
	}

	public boolean removeMethod(MethodNode method) {

		boolean result = fMethods.remove(method);
		registerChange();

		return result;
	}

	public Set<String> getTestCaseNames() {
		Set<String> suites = new HashSet<String>();

		for(MethodNode method : getMethods()){
			suites.addAll(method.getTestCaseNames());
		}

		return suites;
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public boolean isMatch(IAbstractNode other) {

		if (other instanceof ClassNode == false) {
			return false;
		}

		ClassNode otherClassNode = (ClassNode)other;

		if (!fParametersHolder.isMatch(otherClassNode.fParametersHolder)) {
			return false;
		}

		List<MethodNode> methodsToCompare = otherClassNode.getMethods();

		List<MethodNode> methods = getMethods();

		if(methods.size() != methodsToCompare.size()){
			return false;
		}

		for (int methodIndex = 0; methodIndex < methodsToCompare.size(); methodIndex++) {

			MethodNode methodNode = methods.get(methodIndex);
			MethodNode methodNodeToCompare = methodsToCompare.get(methodIndex);

			if (!methodNode.isMatch(methodNodeToCompare)) {
				return false;
			}
		}

		return super.isMatch(other);
	}

	public List<MethodNode> getChildMethods(BasicParameterNode parameter) {

		List<MethodNode> result = new ArrayList<MethodNode>();

		for (MethodNode method : getMethods()) {
			for(AbstractParameterNode methodParameter : method.getParameters()) {

				if (!(methodParameter instanceof BasicParameterNode)) {
					continue;
				}

				BasicParameterNode basicParameterNode = (BasicParameterNode) methodParameter;

				if (basicParameterNode.isLinked() && basicParameterNode.getLinkToGlobalParameter() == parameter) {
					result.add(method);
					break;
				}
			}
		}
		return result;
	}

	@Override
	public String getNonQualifiedName() {
		String[] nameNodeSplit = getName().split("\\.");
		return nameNodeSplit[nameNodeSplit.length - 1];
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

		fParametersHolder.replaceParameters(parameters, this);
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

	public List<BasicParameterNode> getGlobalBasicParameters() {

		List<BasicParameterNode> result = new ArrayList<>();

		result.addAll(getBasicParametersFromClass());
		
		return result;
	}

	private List<BasicParameterNode> getBasicParametersFromClass() {
		
		List<BasicParameterNode> globalParameterNodes = new ArrayList<>();

		List<AbstractParameterNode> abstractParameters = getParameters();

		for (AbstractParameterNode abstractParameterNode : abstractParameters) {

			if (abstractParameterNode instanceof BasicParameterNode) {
				globalParameterNodes.add((BasicParameterNode) abstractParameterNode);
			} else if (abstractParameterNode instanceof CompositeParameterNode) {
				globalParameterNodes.addAll(((CompositeParameterNode) abstractParameterNode).getNestedBasicParameters(true));
			}
			
		}
		
		return globalParameterNodes;
	}
	
	public List<CompositeParameterNode> getGlobalCompositeParameters() {

		List<CompositeParameterNode> result = new ArrayList<>();
		result.addAll(getCompositeParametersFromClass());
		
		return result;
	}

	private List<CompositeParameterNode> getCompositeParametersFromClass() {
		
		List<CompositeParameterNode> globalParameterNodes = new ArrayList<>();
		
		List<AbstractParameterNode> abstractParameters = getParameters();
		
		for (AbstractParameterNode abstractParameterNode : abstractParameters) {
			
			if (abstractParameterNode instanceof CompositeParameterNode) {
				globalParameterNodes.add((CompositeParameterNode) abstractParameterNode);
				globalParameterNodes.addAll(((CompositeParameterNode) abstractParameterNode).getNestedCompositeParameters(true));
			}

		}

		return globalParameterNodes;
	}

	public List<BasicParameterNode> getAllGlobalParametersAvailableForLinking() {

		List<BasicParameterNode> result = new ArrayList<>();

		RootNode rootNode = (RootNode)getParent();

		result.addAll(rootNode.getGlobalBasicParameters());
		
		result.addAll(getBasicParametersFromClass());
		
		return result;
	}

	public BasicParameterNode findGlobalParameter(String qualifiedName){

		List<BasicParameterNode> globalParameters = getAllGlobalParametersAvailableForLinking();

		for (BasicParameterNode parameter : globalParameters) {

			String currentQualifiedName = AbstractParameterNodeHelper.getQualifiedName(parameter);

			if(currentQualifiedName.equals(qualifiedName)){
				return parameter;
			}
		}

		return null;
	}
	
	@Override
	public List<IAbstractNode> getDirectChildren() {
		return getChildren();
	}
	
}
