/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations;

import java.util.List;

import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.TestSuiteNode;
import com.ecfeed.core.operations.nodes.OnCompositeParameterOperationShift;
import com.ecfeed.core.operations.nodes.OnParameterOperationShift;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class FactoryShiftOperation {

	private static class MoveUpDownOperationProvider implements IModelVisitor{

		private List<? extends IAbstractNode> fShifted;
		private boolean fUp;
		private IExtLanguageManager fExtLanguageManager;

		public MoveUpDownOperationProvider(
				List<? extends IAbstractNode> shifted, 
				boolean up,
				IExtLanguageManager extLanguageManager) {
			fShifted = shifted;
			fUp = up;
			fExtLanguageManager = extLanguageManager;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			if(fShifted.get(0) instanceof ClassNode){
				return new GenericShiftOperation(node.getClasses(), fShifted, fUp, fExtLanguageManager);
			}

			IAbstractNode abstractNode = fShifted.get(0);

			if (abstractNode instanceof AbstractParameterNode && ((AbstractParameterNode)abstractNode).isGlobalParameter()) {
				return new GenericShiftOperation(node.getParameters(), fShifted, fUp, fExtLanguageManager);
			}

			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {


			IAbstractNode firstShiftedNode = fShifted.get(0);

			if ( ((firstShiftedNode instanceof AbstractParameterNode))
					&& ((AbstractParameterNode)firstShiftedNode).isGlobalParameter()) {

				return new GenericShiftOperation(node.getParameters(), fShifted, fUp, fExtLanguageManager);
			}

			if(firstShiftedNode instanceof MethodNode){
				return new GenericShiftOperation(node.getMethods(), fShifted, fUp, fExtLanguageManager);
			}

			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(MethodNode methodNode) throws Exception {
			
			if(fShifted.get(0) instanceof BasicParameterNode){
				return new OnParameterOperationShift(fShifted, fUp, fExtLanguageManager);
			}
			
			if(fShifted.get(0) instanceof CompositeParameterNode){
				return new OnCompositeParameterOperationShift(methodNode.getParameters(), fShifted, fUp, fExtLanguageManager);
			}
			
			if(fShifted.get(0) instanceof ConstraintNode){
				return new GenericShiftOperation(methodNode.getConstraintNodes(), fShifted, fUp, fExtLanguageManager);
			}
			
			if(fShifted.get(0) instanceof TestCaseNode){
				return new GenericShiftOperation(methodNode.getTestCases(), fShifted, fUp, fExtLanguageManager);
			}
			
			if(fShifted.get(0) instanceof TestSuiteNode){
				return new GenericShiftOperation(methodNode.getTestSuites(), fShifted, fUp, fExtLanguageManager);
			}
			
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(BasicParameterNode node) throws Exception {

			IAbstractNode abstractNode = fShifted.get(0);

			if(abstractNode instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fUp, fExtLanguageManager);
			}

			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(CompositeParameterNode node) throws Exception {

			IAbstractNode firstShiftedNode = fShifted.get(0);

			if(firstShiftedNode instanceof AbstractParameterNode){
				return new GenericShiftOperation(node.getParameters(), fShifted, fUp, fExtLanguageManager);
			}

			if(firstShiftedNode instanceof ConstraintNode){
				return new GenericShiftOperation(node.getConstraintNodes(), fShifted, fUp, fExtLanguageManager);
			}

			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(TestSuiteNode node) throws Exception {
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fUp, fExtLanguageManager);
			}
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

	}

	private static class ShiftToIndexOperationProvider implements IModelVisitor{

		private List<? extends IAbstractNode> fNodesToBeShifted;
		private int fShift;
		private IExtLanguageManager fExtLanguageManager;

		public ShiftToIndexOperationProvider(
				List<? extends IAbstractNode> nodesToBeShifted, 
				int index, 
				IExtLanguageManager extLanguageManager) {
			
			fNodesToBeShifted = nodesToBeShifted;
			fShift = calculateShift(nodesToBeShifted, index);
			fExtLanguageManager = extLanguageManager;
		}

		@Override
		public Object visit(RootNode rootNode) throws Exception {

			if (fNodesToBeShifted.get(0) instanceof ClassNode) {
				return new GenericShiftOperation(rootNode.getClasses(), fNodesToBeShifted, fShift, fExtLanguageManager);
			}

			//			if (fShifted.get(0) instanceof CompositeParameterNode) {
			//				return new GenericShiftOperation(
			//						rootNode.getCompositeParameterNodes(), fShifted, fShift, fExtLanguageManager);
			//			}

			IAbstractNode abstractNode = fNodesToBeShifted.get(0);

			if (abstractNode instanceof AbstractParameterNode 
					&& ((AbstractParameterNode)abstractNode).isGlobalParameter()) {

				List<AbstractParameterNode> parameters = rootNode.getParameters();
				return new GenericShiftOperation(parameters, fNodesToBeShifted, fShift, fExtLanguageManager);
			}
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			if(fNodesToBeShifted.get(0) instanceof MethodNode){
				return new GenericShiftOperation(node.getMethods(), fNodesToBeShifted, fShift, fExtLanguageManager);
			}

			IAbstractNode abstractNode = fNodesToBeShifted.get(0);

			if(abstractNode instanceof BasicParameterNode && ((BasicParameterNode)abstractNode).isGlobalParameter()){
				return new GenericShiftOperation(node.getParameters(), fNodesToBeShifted, fShift, fExtLanguageManager);
			}
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(MethodNode methodNode) throws Exception {
			
			IAbstractNode firstNodeToBeShifted = fNodesToBeShifted.get(0);
			
			if (firstNodeToBeShifted instanceof BasicParameterNode) {
				return new OnParameterOperationShift(fNodesToBeShifted, fShift, fExtLanguageManager);
			}
			
			if (firstNodeToBeShifted instanceof ConstraintNode) {
				return new GenericShiftOperation(methodNode.getConstraintNodes(), fNodesToBeShifted, fShift, fExtLanguageManager);
			}
			
			if (firstNodeToBeShifted instanceof TestCaseNode) {
				return new GenericShiftOperation(methodNode.getTestCases(), fNodesToBeShifted, fShift, fExtLanguageManager);
			}
			
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(BasicParameterNode node) throws Exception {

			if (node.isGlobalParameter()) {
				if(fNodesToBeShifted.get(0) instanceof ChoiceNode){
					return new GenericShiftOperation(node.getChoices(), fNodesToBeShifted, fShift, fExtLanguageManager);
				}
				ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
				return null;

			} else {
				if(fNodesToBeShifted.get(0) instanceof ChoiceNode){
					return new GenericShiftOperation(node.getChoices(), fNodesToBeShifted, fShift, fExtLanguageManager);
				}
				ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
				return null;
			}
		}

		@Override
		public Object visit(CompositeParameterNode node) throws Exception {
			ExceptionHelper.reportRuntimeException("TODO"); // TODO MO-RE
			return null;
		}

		@Override
		public Object visit(TestSuiteNode node) throws Exception {
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			if(fNodesToBeShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fNodesToBeShifted, fShift, fExtLanguageManager);
			}
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

	}

	public static GenericShiftOperation getShiftOperation(
			List<? extends IAbstractNode> shifted, 
			boolean up,
			IExtLanguageManager extLanguageManager) {

		IAbstractNode parent = getParent(shifted);

		return getShiftOperation(
				parent, shifted, new MoveUpDownOperationProvider(shifted, up, extLanguageManager));
	}

	public static GenericShiftOperation getShiftOperation(
			List<? extends IAbstractNode> nodesToBeShifted, 
			int newIndex, 
			IExtLanguageManager extLanguageManager) {
		
		IAbstractNode parent = getParent(nodesToBeShifted);
		return getShiftOperation(parent, nodesToBeShifted, new ShiftToIndexOperationProvider(nodesToBeShifted, newIndex, extLanguageManager));
	}

	private static GenericShiftOperation getShiftOperation(
			IAbstractNode parent, 
			List<? extends IAbstractNode> shifted, 
			IModelVisitor provider) {
		
		if (parent == null || haveTheSameType(shifted) == false) {
			return null;
		}
		
		try{
			return (GenericShiftOperation)parent.accept(provider);
		}
		catch(Exception e){
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}
	}

	private static int calculateShift(List<? extends IAbstractNode> shifted, int newIndex) {
		int shift = newIndex - minIndexNode(shifted).getMyIndex();
		if(minIndexNode(shifted).getMyIndex() < newIndex){
			shift -= 1;
		}
		return shift;
	}

	private static IAbstractNode minIndexNode(List<? extends IAbstractNode> nodes){
		IAbstractNode minIndexNode = nodes.get(0);
		for(IAbstractNode node : nodes){
			minIndexNode = node.getMyIndex() < minIndexNode.getMyIndex() ? node : minIndexNode;
		}
		return minIndexNode;
	}

	private static boolean haveTheSameType(List<? extends IAbstractNode> shifted) {
		if(shifted.size() == 0){
			return false;
		}
		Class<?> _class = shifted.get(0).getClass();
		for(IAbstractNode node : shifted){
			if(node.getClass().equals(_class) == false){
				return false;
			}
		}
		return true;
	}

	private static IAbstractNode getParent(List<? extends IAbstractNode> nodes) {

		if(nodes.size() == 0){
			return null;
		}
		IAbstractNode parent = nodes.get(0).getParent();
		if(parent == null){
			return null;
		}

		for(IAbstractNode node : nodes){
			if(node.getParent() != parent){
				return null;
			}
		}
		return parent;
	}
}
