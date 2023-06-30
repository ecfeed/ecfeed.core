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

import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.NodeMapper;
import com.ecfeed.core.operations.AbstractModelOperation;
import com.ecfeed.core.operations.IModelOperation;
import com.ecfeed.core.operations.OperationNames;
import com.ecfeed.core.utils.IExtLanguageManager;

public class OnConstraintOperationReplaceReferences extends AbstractModelOperation{

	ConstraintNode fConstraintNode;
	NodeMapper fNodeMapper;
	NodeMapper.MappingDirection fMappingDirection;

	public OnConstraintOperationReplaceReferences(
			ConstraintNode constraintNode, 
			NodeMapper nodeMapper,
			NodeMapper.MappingDirection mappingDirection,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.REPLACE_STATEMENT, extLanguageManager);

		fConstraintNode = constraintNode;
		fNodeMapper = nodeMapper;
		fMappingDirection = mappingDirection;
	}

	@Override
	public void execute() {

		setOneNodeToSelect(fConstraintNode);
		fConstraintNode.replaceReferences(fNodeMapper, fMappingDirection);
		markModelUpdated();
	}

	@Override
	public IModelOperation getReverseOperation() {
		return new OnConstraintOperationReplaceReferences(
				fConstraintNode, 
				fNodeMapper,
				fNodeMapper.getReverseMappingDirection(fMappingDirection),
				getExtLanguageManager());
	}

}
