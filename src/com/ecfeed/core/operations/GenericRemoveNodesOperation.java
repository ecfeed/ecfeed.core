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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.IAbstractNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.type.adapter.ITypeAdapterProvider;
import com.ecfeed.core.utils.IExtLanguageManager;

public class GenericRemoveNodesOperation extends CompositeOperation {

	private final Set<IAbstractNode> fSelectedNodes;

	private GenericRemoveNodesOperationsCreator fGenericRemoveNodesOperationsCreator;

	public GenericRemoveNodesOperation(
			Collection<? extends IAbstractNode> nodes, 
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

		fSelectedNodes = new HashSet<>(nodes);
		removeNodesWithAncestorsOnList();

		fGenericRemoveNodesOperationsCreator = 
				new GenericRemoveNodesOperationsCreator(
						fSelectedNodes, typeAdapterProvider, validate, extLanguageManager);

		List<IModelOperation> operations = fGenericRemoveNodesOperationsCreator.getOperations();
		
		for (IModelOperation modelOperation : operations) {
			addOperation(modelOperation);
		}
	}

	private void removeNodesWithAncestorsOnList() {

		Iterator<IAbstractNode> iterator = fSelectedNodes.iterator();

		while (iterator.hasNext()) {

			IAbstractNode currentNode = iterator.next();

			List<IAbstractNode> ancestors = currentNode.getAncestors();

			for (IAbstractNode ancestor : ancestors) {

				if (fSelectedNodes.contains(ancestor)) {

					// node is deleted because ancestor will be remove with the whole sub-tree which includes current node 
					iterator.remove(); 
					break;
				}
			}
		}
	}

	public Set<ConstraintNode> getAffectedConstraints() {
		return fGenericRemoveNodesOperationsCreator.getAffectedConstraints();
	}

	public Set<TestCaseNode> getAffectedTestCases() {
		return fGenericRemoveNodesOperationsCreator.getAffectedTestCases();
	}

}
