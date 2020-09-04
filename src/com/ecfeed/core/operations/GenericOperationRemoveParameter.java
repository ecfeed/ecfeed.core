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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ModelOperationException;
import com.ecfeed.core.model.ParametersParentNode;
import com.ecfeed.core.utils.ExtLanguage;

public class GenericOperationRemoveParameter extends AbstractModelOperation{

	private ParametersParentNode fParametersParentNode;
	private AbstractParameterNode fAbstractParameterNode;
	private int fOriginalIndex;
	private ExtLanguage fExtLanguage;

	public GenericOperationRemoveParameter(ParametersParentNode target, AbstractParameterNode parameter, ExtLanguage extLanguage) {

		super(OperationNames.REMOVE_METHOD_PARAMETER, extLanguage);

		fParametersParentNode = target;
		fAbstractParameterNode = parameter;
		fExtLanguage = extLanguage;
	}

	@Override
	public void execute() throws ModelOperationException {

		setOneNodeToSelect(fParametersParentNode);
		fOriginalIndex = fParametersParentNode.getParameters().indexOf(fAbstractParameterNode);

		fParametersParentNode.removeParameter(fAbstractParameterNode);
		markModelUpdated();
	}

	public ExtLanguage getExtLanguage() {

		return fExtLanguage;
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new GenericOperationAddParameter(fParametersParentNode, fAbstractParameterNode, fOriginalIndex, false, getExtLanguage());
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
