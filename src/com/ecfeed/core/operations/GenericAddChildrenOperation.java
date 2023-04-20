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

import java.util.Collection;
import java.util.Optional;

import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.NodeMapper;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.LogHelperCore;

public class GenericAddChildrenOperation extends CompositeOperation {

	public GenericAddChildrenOperation(
			IAbstractNode target, 
			Collection<? extends IAbstractNode> childrenToAdd, 
			boolean validate,
			Optional<NodeMapper> nodeMapper,
			IExtLanguageManager extLanguageManager) {

		this(target, childrenToAdd, -1, validate, nodeMapper, extLanguageManager);
	}

	public GenericAddChildrenOperation(
			IAbstractNode target, 
			Collection<? extends IAbstractNode> childrenToAdd, 
			int index, 
			boolean validate,
			Optional<NodeMapper> nodeMapper,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.ADD_CHILDREN, false, target, target, extLanguageManager);

		for (IAbstractNode child : childrenToAdd) {

			try {
				IModelOperation operation = createAddOperation(child, index, target, validate, nodeMapper);

				if (operation != null) {
					addOperation(operation);
				}
			} catch (Exception e) {
				LogHelperCore.logCatch(e);}

			index++;
		}
	}

	private IModelOperation createAddOperation(
			IAbstractNode child, 
			int index,
			IAbstractNode target,
			boolean validate,
			Optional<NodeMapper> nodeMapper) throws Exception {

		AddChildOperationCreator addChildOperationCreator = 
				new AddChildOperationCreator(child, index, validate, nodeMapper, getExtLanguageManager());

		IModelOperation operation = (IModelOperation)target.accept(addChildOperationCreator);

		return operation;
	}

	public boolean enabled(){
		return getOperations().isEmpty() == false;
	}
}
