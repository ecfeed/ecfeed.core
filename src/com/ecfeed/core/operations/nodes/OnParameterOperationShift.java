/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.operations.nodes;

import java.util.List;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.CompositeParameterNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.utils.MethodsWithResultsOfGenerations;
import com.ecfeed.core.operations.GenericShiftOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnParameterOperationShift extends GenericShiftOperation {

	List<? extends IAbstractNode> fNodesToBeShifted;
	private int fShift;
	private IExtLanguageManager fExtLanguageManager;

	private IParametersParentNode fParametersParentNode;
	private MethodsWithResultsOfGenerations fMethodsWithResultsOfGenerations;

	public OnParameterOperationShift(
			List<? extends IAbstractNode> nodesToBeShifted, 
			boolean up, 
			IExtLanguageManager extLanguageManager) {
		this(nodesToBeShifted, 0, extLanguageManager);

		setShift(minAllowedShift(nodesToBeShifted, up));
	}

	public OnParameterOperationShift(
			List<? extends IAbstractNode> nodesToBeShifted, 
			int shift,
			IExtLanguageManager extLanguageManager) {

		super(
				null,
				nodesToBeShifted, 
				shift, 
				extLanguageManager);

		fNodesToBeShifted = nodesToBeShifted;
		fShift = shift;
		fExtLanguageManager = extLanguageManager;

		fParametersParentNode = (IParametersParentNode) nodesToBeShifted.get(0).getParent();
		fMethodsWithResultsOfGenerations = new MethodsWithResultsOfGenerations();
	}

	@Override
	public void execute() {

		fShift = getShift();

		if (!shiftIsAllowed(fNodesToBeShifted, fShift)) {

			ExceptionHelper.reportRuntimeException("Shifting parameters is not allowed.");
			return;
		}

		IAbstractNode theFirstNodeToBeShifted = fNodesToBeShifted.get(0);
		List<MethodNode> methodNodes = MethodNodeHelper.findMentioningMethodNodes(theFirstNodeToBeShifted);

		fMethodsWithResultsOfGenerations.saveResultsForMethods(methodNodes);
		fMethodsWithResultsOfGenerations.clearResultsForAllMethods();


		List<AbstractParameterNode> parameters = fParametersParentNode.getParameters();
		List<Integer> fIndicesOfNodesToBeShifted = calculateIndices(parameters, fNodesToBeShifted);

		fParametersParentNode.shiftParameters(fIndicesOfNodesToBeShifted, fShift);


		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {

		return new ReverseShift();
	}

	@Override
	protected boolean shiftIsAllowed(List<? extends IAbstractNode> shifted, int shift) {

		if (!super.shiftIsAllowed(shifted, shift)) { 
			return false;
		}

		IAbstractNode firstShiftedNode = shifted.get(0);

		if (firstShiftedNode instanceof BasicParameterNode) 
			return true;

		if (firstShiftedNode instanceof CompositeParameterNode) 
			return true;

		return false;

		//		BasicParameterNode basicParameterNode = (BasicParameterNode)shifted.get(0);
		//		MethodNode method = (MethodNode) basicParameterNode.getParent();
		//
		//		List<String> parameterTypes = 
		//				ParametersParentNodeHelper.getParameterTypes(method, getExtLanguageManager());
		//
		//		List<Integer> indices = calculateIndices(method.getParameters(), shifted);
		//
		//		ShifterOfListElements.shiftElements(parameterTypes, indices, shift);
		//
		//		ClassNode classNode = method.getClassNode();
		//
		//		String methodName = AbstractNodeHelper.getName(method, getExtLanguageManager());
		//
		//		MethodNode sibling = 
		//				ClassNodeHelper.findMethodByExtLanguage(
		//						classNode, 
		//						methodName, 
		//						getExtLanguageManager());
		//
		//		if (sibling != null && sibling != method) {
		//			return false;
		//		}
		//
		//		return true;
	}

	@Override
	protected int minAllowedShift(List<? extends IAbstractNode> shifted, boolean up){
		int shift = up ? -1 : 1;
		while(shiftIsAllowed(shifted, shift) == false){
			shift += up ? -1 : 1;
			int borderIndex = (borderNode(shifted, shift) != null) ? borderNode(shifted, shift).getMyIndex() + shift : -1;
			if(borderIndex < 0 || borderIndex >= borderNode(shifted, shift).getMaxIndex()){
				return 0;
			}
		}
		return shift;
	}

	private class ReverseShift extends GenericShiftOperation {

		public ReverseShift() {

			super(
					null,
					fNodesToBeShifted, 
					fShift, 
					fExtLanguageManager);					
		}

		@Override
		protected boolean shiftIsAllowed(List<? extends IAbstractNode> shifted, int shift) {
			return true;
		}		

		@Override
		public void execute() {

			List<AbstractParameterNode> parameters = fParametersParentNode.getParameters();
			List<Integer> fIndicesOfNodesToBeShifted = calculateIndices(parameters, fNodesToBeShifted);
			fParametersParentNode.shiftParameters(fIndicesOfNodesToBeShifted, -fShift);

			fMethodsWithResultsOfGenerations.restoreResultsForAllMethods();

			markModelUpdated();
		}

		@Override
		public IModelOperation getReverseOperation() {
			return null;
		}

	}
}
