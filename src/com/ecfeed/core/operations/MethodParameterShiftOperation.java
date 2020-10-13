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

import java.util.Arrays;
import java.util.List;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.IExtLanguageManager;

public class MethodParameterShiftOperation extends GenericShiftOperation {

	private List<AbstractParameterNode> fParameters;

	public MethodParameterShiftOperation(
			List<AbstractParameterNode> parameters, 
			AbstractNode shifted, 
			boolean up,
			IExtLanguageManager extLanguageManager) {

		this(parameters, Arrays.asList(new AbstractNode[]{shifted}), up, extLanguageManager);
	}

	public MethodParameterShiftOperation(
			List<AbstractParameterNode> parameters, 
			List<? extends AbstractNode> shifted, 
			boolean up, 
			IExtLanguageManager extLanguageManager) {
		this(parameters, shifted, 0, extLanguageManager);
		setShift(minAllowedShift(shifted, up));
	}

	public MethodParameterShiftOperation(
			List<AbstractParameterNode> parameters, 
			List<? extends AbstractNode> shifted, 
			int shift,
			IExtLanguageManager extLanguageManager) {
		super(parameters, shifted, shift, extLanguageManager);
		fParameters = parameters;
	}

	@Override
	public void execute() throws ModelOperationException {
		MethodNode method = ((MethodParameterNode)fParameters.get(0)).getMethod();

		if(shiftAllowed(getShiftedElements(), getShift()) == false){

			ModelOperationException.report(
					ClassNodeHelper.createMethodSignatureDuplicateMessage(
							method.getClassNode(),  method, getExtLanguageManager()));
		}
		List<Integer> indices = indices(fParameters, getShiftedElements());
		shiftElements(fParameters, indices, getShift());
		for(TestCaseNode testCase : method.getTestCases()){
			shiftElements(testCase.getTestData(), indices, getShift());
		}
	}

	@Override
	public IModelOperation getReverseOperation(){
		return new MethodParameterShiftOperation(fParameters, getShiftedElements(), -getShift(), getExtLanguageManager());
	}

	@Override
	protected boolean shiftAllowed(List<? extends AbstractNode> shifted, int shift){
		if(super.shiftAllowed(shifted, shift) == false) return false;
		if(shifted.get(0) instanceof MethodParameterNode == false) return false;
		MethodNode method = ((MethodParameterNode)shifted.get(0)).getMethod();
		List<String> parameterTypes = MethodNodeHelper.getParameterTypes(method, getExtLanguageManager());
		List<Integer> indices = indices(method.getParameters(), shifted);
		shiftElements(parameterTypes, indices, shift);

		ClassNode classNode = method.getClassNode();

		String methodName = MethodNodeHelper.getName(method, getExtLanguageManager());

		MethodNode sibling = 
				ClassNodeHelper.findMethodByExtLanguage(
						classNode, 
						methodName, 
						parameterTypes, 
						getExtLanguageManager());

		if(sibling != null && sibling != method){
			return false;
		}
		return true;
	}

	@Override
	protected int minAllowedShift(List<? extends AbstractNode> shifted, boolean up){
		int shift = up ? -1 : 1;
		while(shiftAllowed(shifted, shift) == false){
			shift += up ? -1 : 1;
			int borderIndex = (borderNode(shifted, shift) != null) ? borderNode(shifted, shift).getMyIndex() + shift : -1;
			if(borderIndex < 0 || borderIndex >= borderNode(shifted, shift).getMaxIndex()){
				return 0;
			}
		}
		return shift;
	}

}
