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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ClassNodeHelper;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodNodeHelper;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.ParametersParentNode;
import com.ecfeed.core.utils.ExtLanguage;

public class GenericOperationRemoveParameter extends AbstractModelOperation{

	private ParametersParentNode fParametersParentNode;
	private AbstractParameterNode fAbstractParameterNode;
	private int fOriginalIndex;
	private ExtLanguage fViewMode;

	public GenericOperationRemoveParameter(ParametersParentNode target, AbstractParameterNode parameter, ExtLanguage extLanguage) {
		
		super(OperationNames.REMOVE_METHOD_PARAMETER, extLanguage);
		
		fParametersParentNode = target;
		fAbstractParameterNode = parameter;
		fViewMode = extLanguage;
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fParametersParentNode);
		fOriginalIndex = fParametersParentNode.getParameters().indexOf(fAbstractParameterNode);
		
		if (fParametersParentNode instanceof MethodNode) {
			
			MethodNode methodNode = (MethodNode)fParametersParentNode;
			verifyIfMethodSignatureIsUnique(methodNode, fOriginalIndex, fViewMode);
		}
		
		fParametersParentNode.removeParameter(fAbstractParameterNode);
		markModelUpdated();
	}

	public ExtLanguage getViewMode() {
		
		return fViewMode;
	}
	
	private static void verifyIfMethodSignatureIsUnique( // TODO SIMPLE-VIEW mode this to RemoveMethodParameterOperation
			MethodNode methodNode, 
			int indexOfParameterToRemove, 
			ExtLanguage extLanguage) throws ModelOperationException {

		ClassNode classNode = methodNode.getClassNode();

		List<String> parameterTypes = MethodNodeHelper.getMethodParameterTypes(methodNode, extLanguage);
		parameterTypes.remove(indexOfParameterToRemove);

		MethodNode foundMethodNode = ClassNodeHelper.findMethod(classNode, methodNode.getName(), parameterTypes, extLanguage);

		if (foundMethodNode == null) {
			return;
		}

		if (foundMethodNode == methodNode) {
			return;
		}

		ModelOperationException.report(ClassNodeHelper.generateMethodSignatureDuplicateMessage(
				classNode, foundMethodNode, extLanguage));
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new GenericOperationAddParameter(fParametersParentNode, fAbstractParameterNode, fOriginalIndex, false, getViewMode());
	}

	protected ParametersParentNode getOwnNode(){
		return fParametersParentNode;
	}

	protected AbstractParameterNode getParameter(){
		return fAbstractParameterNode;
	}

	protected int getOriginalIndex(){
		return fOriginalIndex;
	}

}
