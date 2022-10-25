/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.implementation;

import java.util.List;

import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.utils.EcException;
import com.ecfeed.core.utils.LogHelperCore;
import com.ecfeed.core.utils.StrgList;

public abstract class AbstractModelImplementer implements IModelImplementer {

	private ImplementableVisitor fImplementableVisitor;
	private NodeImplementer fNodeImplementerVisitor;
	private IImplementationStatusResolver fStatusResolver;

	private class ImplementableVisitor implements IModelVisitor{
		@Override
		public Object visit(RootNode node) throws Exception {
			return isImplementableNode(node);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return isImplementableNode(node);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return isImplementableNode(node);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return isImplementableNode(node);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return isImplementableNode(node);
		}

		@Override
		public Object visit(TestSuiteNode node) throws Exception {
			return isImplementableNode(node);
		}
		
		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return isImplementableNode(node);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return isImplementableNode(node);
		}
	}

	private class NodeImplementer implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(TestSuiteNode node) throws Exception {
			return implement(node);
		}
		
		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return implement(node);
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return implement(node);
		}

	}

	public AbstractModelImplementer(IImplementationStatusResolver statusResolver) {
		fImplementableVisitor = new ImplementableVisitor();
		fNodeImplementerVisitor = new NodeImplementer();
		fStatusResolver = statusResolver;
	}

	@Override
	public boolean isImplementable(Class<? extends IAbstractNode> type){
		if(type.equals(RootNode.class) ||
				(type.equals(ClassNode.class))||
				(type.equals(MethodNode.class))||
				(type.equals(MethodParameterNode.class))||
				(type.equals(GlobalParameterNode.class))||
				(type.equals(TestCaseNode.class))||
				(type.equals(ChoiceNode.class))
				){
			return true;
		}
		return false;
	}

	@Override
	public boolean isImplementable(IAbstractNode node) {
		try {
			return (boolean)node.accept(fImplementableVisitor);
		} catch(Exception e) {
			LogHelperCore.logCatch(e);
		}
		
		return false;
	}

	@Override
	public boolean implement(IAbstractNode node) throws Exception {
		if (isImplementable(node)){
			return (boolean)node.accept(fNodeImplementerVisitor);
		}
		return false;
	}

	@Override
	public EImplementationStatus getImplementationStatus(IAbstractNode node) {
		return fStatusResolver.getImplementationStatus(node);
	}

	protected boolean implement(RootNode rootNode) throws Exception{
		implementRootGlobalParameters(rootNode);
		implementClasses(rootNode);
		return true;
	}

	private void implementRootGlobalParameters(RootNode rootNode) throws Exception {
		
		// TODO MO-RE remove with other implementers
//		for(AbstractParameterNode abstractParameterNode : rootNode.getParameters()){
//			
//			GlobalParameterNode globalParameterNode = (GlobalParameterNode)abstractParameterNode;  
//			if(isImplementableNode(abstractParameterNode) && getImplementationStatus(abstractParameterNode) != EImplementationStatus.IMPLEMENTED){
//				implement(abstractParameterNode);
//			}
//		}
	}

	private void implementClasses(RootNode rootNode) throws EcException, Exception {
		StrgList errorMessages = new StrgList();

		for(ClassNode classNode : rootNode.getClasses()){
			if(isImplementableNode(classNode) && getImplementationStatus(classNode) != EImplementationStatus.IMPLEMENTED){

				try {
					implement(classNode);
				} catch (Exception e) {
					errorMessages.add(e.getMessage());
				}
			}
		}

		if (!errorMessages.isEmpty()) {
			EcException.report(errorMessages.contentsToMultilineString());
		}
	}

	protected boolean implement(ClassNode classNode) throws Exception{
		for(GlobalParameterNode parameter : classNode.getGlobalParameters()){
			if(isImplementableNode(parameter) && getImplementationStatus(parameter) != EImplementationStatus.IMPLEMENTED){
				implement(parameter);
			}
		}
		if(classDefinitionImplemented(classNode) == false){
			implementClassDefinition(classNode);
		}

		implementMethods(classNode);
		return true;
	}

	private void implementMethods(ClassNode classNode) throws EcException {
		StrgList errorMessages = new StrgList();

		for(MethodNode method : classNode.getMethods()){
			if(isImplementableNode(method) && getImplementationStatus(method) != EImplementationStatus.IMPLEMENTED){

				try {
					implement(method);
				} catch (Exception e) {
					errorMessages.add(e.getMessage());
				}
			}
		}

		if (!errorMessages.isEmpty()) {
			EcException.report(errorMessages.contentsToMultilineString());
		}
	}

	protected boolean implement(MethodNode methodNode) throws Exception{
		for(MethodParameterNode parameter : methodNode.getMethodParameters()){
			if(isImplementableNode(parameter) && getImplementationStatus(parameter) != EImplementationStatus.IMPLEMENTED){
				implement(parameter);
			}
		}
		for(TestCaseNode testCase : methodNode.getTestCases()){
			if(isImplementableNode(testCase) && getImplementationStatus(testCase) != EImplementationStatus.IMPLEMENTED){
				implement(testCase);
			}
		}
		if(methodDefinitionImplemented(methodNode) == false){
			implementMethodDefinition(methodNode);
		}		
		return true;
	}

	protected boolean implement(AbstractParameterNode parameterNode) throws Exception{
		if(parameterDefinitionImplemented(parameterNode) == false){
			implementParameterDefinition(parameterNode);
		}
		for(ChoiceNode choice : parameterNode.getLeafChoices()){
			if(isImplementableNode(choice) && getImplementationStatus(choice) != EImplementationStatus.IMPLEMENTED){
				implement(choice);
				CachedImplementationStatusResolver.clearCache(choice);
			}
		}
		return true;
	}

	protected boolean implement(TestCaseNode testCaseNode) throws Exception{
		for(ChoiceNode choice : testCaseNode.getTestData()){
			if(isImplementableNode(choice) && getImplementationStatus(choice) != EImplementationStatus.IMPLEMENTED){
				implement(choice);
			}
		}
		return true;
	}

	protected boolean implement(ConstraintNode constraintNode) throws Exception{
		return false;
	}

	protected boolean implement(ChoiceNode choiceNode) throws Exception{
		if(parameterDefinitionImplemented(choiceNode.getParameter()) == false){
			implementParameterDefinition(choiceNode.getParameter());
		}
		if(choiceNode.isAbstract()){
			for(ChoiceNode leaf : choiceNode.getLeafChoices()){
				if(isImplementableNode(leaf) && getImplementationStatus(leaf) != EImplementationStatus.IMPLEMENTED){
					implement(leaf);
				}
			}
		}
		else{
			if(isImplementableNode(choiceNode) && getImplementationStatus(choiceNode) != EImplementationStatus.IMPLEMENTED){
				implementChoiceDefinition(choiceNode);
			}
		}
		return true;
	}

	protected boolean isImplementableNode(RootNode node){
		return hasImplementableNode(node.getClasses());
	}

	protected boolean isImplementableNode(ClassNode node) throws EcException {
		return hasImplementableNode(node.getMethods());
	}

	protected boolean isImplementableNode(MethodNode node) throws EcException {
		return hasImplementableNode(node.getParameters()) || hasImplementableNode(node.getTestCases());
	}

	protected boolean isImplementableNode(MethodParameterNode node){
		return hasImplementableNode(node.getChoices());
	}

	protected boolean isImplementableNode(GlobalParameterNode node){
		return hasImplementableNode(node.getChoices());
	}

	protected boolean isImplementableNode(ChoiceNode node){
		return hasImplementableNode(node.getChoices());
	}

	protected boolean isImplementableNode(TestSuiteNode node){
		return hasImplementableNode(node.getTestCaseNodes());
	}
	
	protected boolean isImplementableNode(TestCaseNode node){
		return hasImplementableNode(node.getTestData());
	}

	protected boolean hasImplementableNode(List<? extends IAbstractNode> nodes){
		for(IAbstractNode node : nodes){
			if(isImplementable(node)){
				return true;
			}
		}
		return false;
	}

	protected abstract boolean classDefinitionImplemented(ClassNode node);
	protected abstract boolean methodDefinitionImplemented(MethodNode node);
	protected abstract boolean parameterDefinitionImplemented(AbstractParameterNode node);

	protected abstract void implementClassDefinition(ClassNode node) throws Exception;
	protected abstract void implementMethodDefinition(MethodNode node) throws Exception;
	protected abstract void implementParameterDefinition(AbstractParameterNode node) throws Exception;
	protected abstract void implementChoiceDefinition(ChoiceNode node) throws Exception;
}
