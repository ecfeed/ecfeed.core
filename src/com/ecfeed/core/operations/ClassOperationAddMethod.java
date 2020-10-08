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

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.core.utils.IExtLanguageManager;

public class ClassOperationAddMethod extends AbstractModelOperation{

	private ClassNode fClassNode;
	private MethodNode fMethod;
	private int fIndex;

	private static final String UNEXPECTED_PROBLEM_WHILE_ADDING_ELEMENT = "Element could not be added to the model";

	public ClassOperationAddMethod(ClassNode target, MethodNode method, int index, IExtLanguageManager extLanguageManager) {
		super(OperationNames.ADD_METHOD, extLanguageManager);
		fClassNode = target;
		fMethod = method;
		fIndex = index;
	}

	public ClassOperationAddMethod(ClassNode target, MethodNode method, IExtLanguageManager extLanguageManager) {
		this(target, method, -1, extLanguageManager);
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fClassNode);
		List<String> problems = new ArrayList<String>();

		if(fIndex == -1){
			fIndex = fClassNode.getMethods().size();
		}

		generateUniqeNameForMethod(fMethod);

		String errorMessage = 
				ClassNodeHelper.verifyNewMethodSignatureIsValidAndUnique(
						fClassNode, 
						MethodNodeHelper.getName(fMethod, getExtLanguageManager()), 
						MethodNodeHelper.getMethodParameterTypes(fMethod, getExtLanguageManager()), 
						getExtLanguageManager());

		if (errorMessage != null){
			problems.add(errorMessage);
			ModelOperationException.report(StringHelper.convertToMultilineString(problems));
		}

		if(fClassNode.addMethod(fMethod, fIndex) == false){
			ModelOperationException.report(UNEXPECTED_PROBLEM_WHILE_ADDING_ELEMENT);
		}

		markModelUpdated();
	}

	private void generateUniqeNameForMethod(MethodNode methodNode) {

		IExtLanguageManager extLanguageManager = getExtLanguageManager();

		String methodNameInExtLanguage = MethodNodeHelper.getName(methodNode, extLanguageManager);
		List<String> parameterTypesInExtLanguage = MethodNodeHelper.getMethodParameterTypes(methodNode, extLanguageManager);

		String newNameInExtLanguage = 
				ClassNodeHelper.generateNewMethodName(
						fClassNode, methodNameInExtLanguage, parameterTypesInExtLanguage, extLanguageManager);

		MethodNodeHelper.setName(methodNode, newNameInExtLanguage, extLanguageManager);
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new ClassOperationRemoveMethod(fClassNode, fMethod, getExtLanguageManager());
	}

}
