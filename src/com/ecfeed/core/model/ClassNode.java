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

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.JavaLanguageHelper;

public class ClassNode extends AbstractNode implements IParametersParentNode {
	
	ParametersHolder fParametersHolder;
	private List<MethodNode> fMethods;
	
	public ClassNode(String qualifiedName) {
		this(qualifiedName, null);
	}

	public ClassNode(String qualifiedName, IModelChangeRegistrator modelChangeRegistrator) {
		this(qualifiedName, modelChangeRegistrator, false, null);
	}
	
	public ClassNode(
			String qualifiedName, IModelChangeRegistrator modelChangeRegistrator, 
			boolean runOnAndroid, String androidBaseRunner) {

		super(qualifiedName, modelChangeRegistrator);

		JavaLanguageHelper.verifyIsMatchWithJavaComplexIdentifier(qualifiedName);

		fParametersHolder = new ParametersHolder(modelChangeRegistrator);
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

		for(GlobalParameterNode parameter : getGlobalParameters()){
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
		if(potentialChild instanceof GlobalParameterNode) return getParameters().size();
		if(potentialChild instanceof MethodParameterNode) return getParameters().size();
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

//	public String getAndroidRunner() {
//		return getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_ANDROID_RUNNER);
//	}
//
//	public void setAndroidRunner(String androidRunner) {
//		setPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_ANDROID_RUNNER, androidRunner);
//	}	
//
//	public boolean getRunOnAndroid() {
//		String value = getPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_RUN_ON_ANDROID);
//
//		if (value == null) {
//			return false;
//		}
//
//		return BooleanHelper.parseBoolean(value);
//	}
//
//	public void setRunOnAndroid(boolean runOnAndroid) {
//		setPropertyValue(NodePropertyDefs.PropertyId.PROPERTY_RUN_ON_ANDROID, BooleanHelper.toString(runOnAndroid));
//	}	

	public boolean addMethod(MethodNode method) {
		return addMethod(method, fMethods.size());
	}

	public boolean addMethod(MethodNode method, int index) {

		if (findMethodWithTheSameSignature(method.getName(), method.getParameterTypes()) != null) {

			ExceptionHelper.reportRuntimeException("Cannot add method. Method with identical signature already exists.");
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

	public MethodNode findMethodWithTheSameSignature(String name, List<String> parameterTypes) {

		for (MethodNode methodNode : getMethods()) {
			List<String> args = new ArrayList<String>();
			for (AbstractParameterNode arg : methodNode.getParameters()){
				args.add(arg.getType());
			}
			if (methodNode.getName().equals(name) && args.equals(parameterTypes)){
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

	@Override
	public List<MethodNode> getMethods(AbstractParameterNode parameter) {
		List<MethodNode> result = new ArrayList<MethodNode>();
		for(MethodNode method : getMethods()){
			for(MethodParameterNode methodParameter : method.getMethodParameters()){
				if(methodParameter.isLinked() && methodParameter.getLinkToGlobalParameter() == parameter){
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
	public void addParameters(List<MethodParameterNode> parameters) {
		
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
	public boolean parameterExists(AbstractParameterNode abstractParameterNode) {
		
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

	public List<GlobalParameterNode> getGlobalParameters() {

		List<GlobalParameterNode> result = new ArrayList<>();
		result.addAll(getParametersFromClass());
		
		return result;
	}

	private List<GlobalParameterNode> getParametersFromClass() {
		
		List<GlobalParameterNode> globalParameterNodes = new ArrayList<>();
		
		List<AbstractParameterNode> abstractParameters = getParameters();
		
		for (AbstractParameterNode abstractParameterNode : abstractParameters) {
			
			GlobalParameterNode globalParameterNode = (GlobalParameterNode)abstractParameterNode;
			
			globalParameterNodes.add(globalParameterNode);
		}
		
		return globalParameterNodes;
	}

	public List<GlobalParameterNode> getAllGlobalParametersAvailableForLinking() {

		List<GlobalParameterNode> result = new ArrayList<>();
		
		RootNode rootNode = (RootNode)getParent();
		result.addAll(rootNode.getGlobalParameters());
		
		result.addAll(getParametersFromClass());
		
		return result;
	}
	
	public GlobalParameterNode findGlobalParameter(String qualifiedName){
		
		List<GlobalParameterNode> globalParameters = getAllGlobalParametersAvailableForLinking();
		
		for (GlobalParameterNode parameter : globalParameters) {
			
			String currentQualifiedName = parameter.getQualifiedName();
			
			if(currentQualifiedName.equals(qualifiedName)){
				return parameter;
			}
		}
		
		return null;
	}

	
}
