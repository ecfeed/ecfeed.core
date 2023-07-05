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

import java.util.Arrays;
import java.util.List;

import com.ecfeed.core.model.AbstractNodeHelper;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ParametersParentNodeHelper;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.operations.GenericShiftOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnParameterOfMethodOperationShift extends GenericShiftOperation {

	private List<AbstractParameterNode> fParameters;

	public OnParameterOfMethodOperationShift(
			List<AbstractParameterNode> parameters, 
			IAbstractNode shifted, 
			boolean up,
			IExtLanguageManager extLanguageManager) {

		this(parameters, Arrays.asList(new IAbstractNode[]{shifted}), up, extLanguageManager);
	}

	public OnParameterOfMethodOperationShift(
			List<AbstractParameterNode> parameters, 
			List<? extends IAbstractNode> shifted, 
			boolean up, 
			IExtLanguageManager extLanguageManager) {
		this(parameters, shifted, 0, extLanguageManager);
		setShift(minAllowedShift(shifted, up));
	}

	public OnParameterOfMethodOperationShift(
			List<AbstractParameterNode> actualParameters, 
			List<? extends IAbstractNode> nodesToBeShifted, 
			int shift,
			IExtLanguageManager extLanguageManager) {
		
		super(actualParameters, nodesToBeShifted, shift, extLanguageManager);
		
		fParameters = actualParameters;
	}

	@Override
	public void execute() {
		
		BasicParameterNode firstActualParameterNode = (BasicParameterNode)fParameters.get(0);
		MethodNode methodNode = (MethodNode) firstActualParameterNode.getParent();

		List<? extends IAbstractNode> elementsToBeShifted = getShiftedElements();
		int shift = getShift();
		
		if (!shiftIsAllowed(elementsToBeShifted, shift)) {

			ExceptionHelper.reportRuntimeException(
					ClassNodeHelper.createMethodNameDuplicateMessage(
							methodNode.getClassNode(),  methodNode, false, getExtLanguageManager()));
		}
		
		List<Integer> indices = calculateIndices(fParameters, elementsToBeShifted);
		shiftElements(fParameters, indices, shift);
		
		for(TestCaseNode testCase : methodNode.getTestCases()){
			shiftElements(testCase.getTestData(), indices, shift);
		}
	}

	@Override
	public IModelOperation getReverseOperation(){
		return new OnParameterOfMethodOperationShift(fParameters, getShiftedElements(), -getShift(), getExtLanguageManager());
	}

	@Override
	protected boolean shiftIsAllowed(List<? extends IAbstractNode> shifted, int shift) {
		
		if(super.shiftIsAllowed(shifted, shift) == false) 
			return false;
		
		if(shifted.get(0) instanceof BasicParameterNode == false) 
			return false;
		
		BasicParameterNode basicParameterNode = (BasicParameterNode)shifted.get(0);
		MethodNode method = (MethodNode) basicParameterNode.getParent();
		
		List<String> parameterTypes = 
				ParametersParentNodeHelper.getParameterTypes(method, getExtLanguageManager());
		
		List<Integer> indices = calculateIndices(method.getParameters(), shifted);
		
		shiftElements(parameterTypes, indices, shift);

		ClassNode classNode = method.getClassNode();

		String methodName = AbstractNodeHelper.getName(method, getExtLanguageManager());

		MethodNode sibling = 
				ClassNodeHelper.findMethodByExtLanguage(
						classNode, 
						methodName, 
						getExtLanguageManager());

		if (sibling != null && sibling != method) {
			return false;
		}
		
		return true;
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

}
