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
//package com.ecfeed.core.operations.nodes;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.ecfeed.core.model.AbstractParameterNode;
//import com.ecfeed.core.model.BasicParameterNode;
//import com.ecfeed.core.model.MethodNode;
//import com.ecfeed.core.model.TestCaseNode;
//import com.ecfeed.core.operations.AbstractReverseOperation;
//import com.ecfeed.core.operations.CompositeOperation;
//import com.ecfeed.core.operations.GenericOperationRemoveParameter;
//import com.ecfeed.core.operations.IModelOperation;
//import com.ecfeed.core.operations.OperationNames;
//import com.ecfeed.core.utils.IExtLanguageManager;
//
//public class OnBasicParameterOperationRemoveOld extends CompositeOperation{
//
//	private String fParameterName;
//	private int fParameterIndex;
//
//	public OnBasicParameterOperationRemoveOld(
//			MethodNode methodNode, AbstractParameterNode parameter, boolean validate, IExtLanguageManager extLanguageManager) {
//
//		super(OperationNames.REMOVE_PARAMETER, true, methodNode, methodNode, extLanguageManager);
//
//		fParameterName = parameter.getName();
//
//		// TODO MO-RE - change composite operation into IModelOperation
//		addOperation(new RemoveBasicParameterOperationPrivate(methodNode, parameter, extLanguageManager));
//	}
//
//	@Override
//	public String toString() {
//
//		return "Operation remove parameter:" + fParameterName;
//	}
//
//	public OnBasicParameterOperationRemoveOld(MethodNode target, AbstractParameterNode parameter, IExtLanguageManager extLanguageManager) {
//		this(target, parameter, true, extLanguageManager);
//	}
//
//	private class RemoveBasicParameterOperationPrivate extends GenericOperationRemoveParameter{
//
//		private List<TestCaseNode> fOriginalTestCases;
//		private List<BasicParameterNode> fOriginalDeployedParameters;
//
//		public RemoveBasicParameterOperationPrivate(
//				MethodNode target,
//				AbstractParameterNode parameter,
//				IExtLanguageManager extLanguageManager) {
//
//			super(target, parameter, extLanguageManager);
//			
//			fOriginalTestCases = new ArrayList<>();
//			fOriginalDeployedParameters = new ArrayList<>();
//		}
//
//		@Override
//		public void execute() {
//
//			MethodNode targetMethodNode = getTargetMethod();
//
//			fParameterIndex = getParameter().getMyIndex();
//
//			fOriginalTestCases.clear();
//			fOriginalTestCases.addAll(targetMethodNode.getTestCases());
//			targetMethodNode.removeAllTestCases();
//
//			fOriginalDeployedParameters.clear();
//			fOriginalDeployedParameters.addAll(targetMethodNode.getDeployedMethodParameters());
//			targetMethodNode.removeAllDeployedParameters();
//
//			super.execute();
//		}
//
//		@Override
//		public IModelOperation getReverseOperation(){
//			return new ReverseOperation(getExtLanguageManager());
//		}
//
//		private MethodNode getTargetMethod(){
//			return (MethodNode) getOwnNode();
//		}
//
//		private class ReverseOperation extends AbstractReverseOperation {
//
//			public ReverseOperation(IExtLanguageManager extLanguageManager) {
//				super(RemoveBasicParameterOperationPrivate.this, extLanguageManager);
//			}
//
//			@Override
//			public void execute() {
//
//				MethodNode methodNode = getTargetMethod();
//
//				setOneNodeToSelect(methodNode);
//				
////				OnParameterOperationAddToParent onParameterOperationAddToParent = 
////						new OnParameterOperationAddToParent(
////						methodNode,
////						AbstractParameterNode abstractParameterNode,
////						int index,
////						IExtLanguageManager extLanguageManager)
//				
//				methodNode.replaceTestCases(fOriginalTestCases);
//				methodNode.setDeployedParameters(fOriginalDeployedParameters);
//				
//
//				//RemoveBasicParameterOperationPrivate.super.getReverseOperation().execute();
//			}
//
//			@Override
//			public IModelOperation getReverseOperation() {
//				return null;
//			}
//
//		}
//
//	}
//
//
//}
