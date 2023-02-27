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

import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.IExtLanguageManager;
import com.ecfeed.core.utils.NodesByType;

public class GenericRemoveNodesOperation extends CompositeOperation {

	public GenericRemoveNodesOperation(
			NodesByType processedNodesToDelete,
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate,
			IAbstractNode nodeToSelect,
			IAbstractNode nodeToSelectAfterReverseOperation,
			IExtLanguageManager extLanguageManager) {

		super(OperationNames.REMOVE_NODES, 
				false,
				nodeToSelect,
				nodeToSelectAfterReverseOperation,
				extLanguageManager);

		List<IModelOperation> operations =  
				GenericRemoveNodesOperationsAccumulator.convertNodesToOperations(
						processedNodesToDelete,	extLanguageManager, typeAdapterProvider, validate);

		addChildOperations(operations);
	}

	private void addChildOperations(List<IModelOperation> operations) {
		for (IModelOperation modelOperation : operations) {
			addOperation(modelOperation);
		}
	}

	//	public Set<ConstraintNode> getAffectedConstraints() {
	//		return fGenericRemoveNodesProcessorOfNodes.getAffectedConstraints();
	//	}
	//
	//	public Set<TestCaseNode> getAffectedTestCases() {
	//		return fGenericRemoveNodesProcessorOfNodes.getAffectedTestCases();
	//	}

}
