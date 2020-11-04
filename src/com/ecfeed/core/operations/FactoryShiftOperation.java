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

import com.ecfeed.core.model.AbstractNode;
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
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class FactoryShiftOperation {
	
	private static class MoveUpDownOperationProvider implements IModelVisitor{

		private List<? extends AbstractNode> fShifted;
		private boolean fUp;
		private IExtLanguageManager fExtLanguageManager;

		public MoveUpDownOperationProvider(
				List<? extends AbstractNode> shifted, 
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
			if(fShifted.get(0) instanceof GlobalParameterNode){
				return new GenericShiftOperation(node.getParameters(), fShifted, fUp, fExtLanguageManager);
			}
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			if(fShifted.get(0) instanceof GlobalParameterNode){
				return new GenericShiftOperation(node.getParameters(), fShifted, fUp, fExtLanguageManager);
			}
			if(fShifted.get(0) instanceof MethodNode){
				return new GenericShiftOperation(node.getMethods(), fShifted, fUp, fExtLanguageManager);
			}
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			if(fShifted.get(0) instanceof MethodParameterNode){
				return new MethodParameterShiftOperation(node.getParameters(), fShifted, fUp, fExtLanguageManager);
			}
			if(fShifted.get(0) instanceof ConstraintNode){
				return new GenericShiftOperation(node.getConstraintNodes(), fShifted, fUp, fExtLanguageManager);
			}
			if(fShifted.get(0) instanceof TestCaseNode){
				return new GenericShiftOperation(node.getTestCases(), fShifted, fUp, fExtLanguageManager);
			}
			if(fShifted.get(0) instanceof TestSuiteNode){
				return new GenericShiftOperation(node.getTestSuites(), fShifted, fUp, fExtLanguageManager);
			}
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fUp, fExtLanguageManager);
			}
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fUp, fExtLanguageManager);
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

		private List<? extends AbstractNode> fShifted;
		private int fShift;
		private IExtLanguageManager fExtLanguageManager;

		public ShiftToIndexOperationProvider(List<? extends AbstractNode> shifted, int index, IExtLanguageManager extLanguageManager){
			fShifted = shifted;
			fShift = calculateShift(shifted, index);
			fExtLanguageManager = extLanguageManager;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			if(fShifted.get(0) instanceof ClassNode){
				return new GenericShiftOperation(node.getClasses(), fShifted, fShift, fExtLanguageManager);
			}
			if(fShifted.get(0) instanceof GlobalParameterNode){
				return new GenericShiftOperation(node.getParameters(), fShifted, fShift, fExtLanguageManager);
			}
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			if(fShifted.get(0) instanceof MethodNode){
				return new GenericShiftOperation(node.getMethods(), fShifted, fShift, fExtLanguageManager);
			}
			if(fShifted.get(0) instanceof GlobalParameterNode){
				return new GenericShiftOperation(node.getParameters(), fShifted, fShift, fExtLanguageManager);
			}
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			if(fShifted.get(0) instanceof MethodParameterNode){
				return new MethodParameterShiftOperation(node.getParameters(), fShifted, fShift, fExtLanguageManager);
			}
			if(fShifted.get(0) instanceof ConstraintNode){
				return new GenericShiftOperation(node.getConstraintNodes(), fShifted, fShift, fExtLanguageManager);
			}
			if(fShifted.get(0) instanceof TestCaseNode){
				return new GenericShiftOperation(node.getTestCases(), fShifted, fShift, fExtLanguageManager);
			}
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fShift, fExtLanguageManager);
			}
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			if(fShifted.get(0) instanceof ChoiceNode){
				return new GenericShiftOperation(node.getChoices(), fShifted, fShift, fExtLanguageManager);
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
				return new GenericShiftOperation(node.getChoices(), fShifted, fShift, fExtLanguageManager);
			}
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}
	}

	public static GenericShiftOperation getShiftOperation(
			List<? extends AbstractNode> shifted, 
			boolean up,
			IExtLanguageManager extLanguageManager) {
		
		AbstractNode parent = getParent(shifted);
		
		return getShiftOperation(
				parent, shifted, new MoveUpDownOperationProvider(shifted, up, extLanguageManager));
	}

	public static GenericShiftOperation getShiftOperation(
			List<? extends AbstractNode> shifted, int newIndex, IExtLanguageManager extLanguageManager) {
		AbstractNode parent = getParent(shifted);
		return getShiftOperation(parent, shifted, new ShiftToIndexOperationProvider(shifted, newIndex, extLanguageManager));
	}

	private static GenericShiftOperation getShiftOperation(AbstractNode parent, List<? extends AbstractNode> shifted, IModelVisitor provider) {
		if(parent == null || haveTheSameType(shifted) == false){
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}
		try{
			return (GenericShiftOperation)parent.accept(provider);
		}
		catch(Exception e){
			ExceptionHelper.reportRuntimeException(OperationMessages.OPERATION_NOT_SUPPORTED_PROBLEM);
			return null;
		}
	}

	private static int calculateShift(List<? extends AbstractNode> shifted, int newIndex) {
		int shift = newIndex - minIndexNode(shifted).getMyIndex();
		if(minIndexNode(shifted).getMyIndex() < newIndex){
			shift -= 1;
		}
		return shift;
	}

	private static AbstractNode minIndexNode(List<? extends AbstractNode> nodes){
		AbstractNode minIndexNode = nodes.get(0);
		for(AbstractNode node : nodes){
			minIndexNode = node.getMyIndex() < minIndexNode.getMyIndex() ? node : minIndexNode;
		}
		return minIndexNode;
	}

	private static boolean haveTheSameType(List<? extends AbstractNode> shifted) {
		if(shifted.size() == 0){
			return false;
		}
		Class<?> _class = shifted.get(0).getClass();
		for(AbstractNode node : shifted){
			if(node.getClass().equals(_class) == false){
				return false;
			}
		}
		return true;
	}

	private static AbstractNode getParent(List<? extends AbstractNode> nodes) {
		if(nodes.size() == 0){
			return null;
		}
		AbstractNode parent = nodes.get(0).getParent();
		if(parent == null){
			return null;
		}

		for(AbstractNode node : nodes){
			if(node.getParent() != parent){
				return null;
			}
		}
		return parent;
	}
}
