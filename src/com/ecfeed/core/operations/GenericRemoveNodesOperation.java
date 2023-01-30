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
import com.ecfeed.core.utils.NodesByType;

public class GenericRemoveNodesOperation extends CompositeOperation {

	private GenericRemoveNodesProcessorOfNodes fGenericRemoveNodesProcessorOfNodes;

	public GenericRemoveNodesOperation(
			Collection<? extends IAbstractNode> nodesToRemove, 
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

		Set<IAbstractNode> fNodesToRemove = createSetOfNodesToRemove(nodesToRemove);

		NodesByType processedNodes = processNodes(fNodesToRemove, typeAdapterProvider, validate, extLanguageManager);

		List<IModelOperation> operations =  
				GenericRemoveNodesOperationsAccumulator.convertNodesToOperations(
						processedNodes, 
						extLanguageManager, typeAdapterProvider, validate);

		addChildOperations(operations);
	}

	private void addChildOperations(List<IModelOperation> operations) {
		for (IModelOperation modelOperation : operations) {
			addOperation(modelOperation);
		}
	}

	private NodesByType processNodes(
			Set<IAbstractNode> nodesToRemove,
			ITypeAdapterProvider typeAdapterProvider, 
			boolean validate,
			IExtLanguageManager extLanguageManager) {

		fGenericRemoveNodesProcessorOfNodes = 
				new GenericRemoveNodesProcessorOfNodes(
						nodesToRemove, typeAdapterProvider, validate, extLanguageManager);

		NodesByType processedNodes = fGenericRemoveNodesProcessorOfNodes.getProcessedNodes();
		return processedNodes;
	}

	private Set<IAbstractNode> createSetOfNodesToRemove(Collection<? extends IAbstractNode> nodesToRemove) {

		Set<IAbstractNode> setOfNodes = new HashSet<>(nodesToRemove);

		removeNodesWithAncestorsOnList(setOfNodes);

		return setOfNodes;
	}

	private static void removeNodesWithAncestorsOnList(Set<IAbstractNode> inOutNodesToRemove) {

		Iterator<IAbstractNode> iterator = inOutNodesToRemove.iterator();

		while (iterator.hasNext()) {

			IAbstractNode currentNode = iterator.next();

			List<IAbstractNode> ancestors = currentNode.getAncestors();

			for (IAbstractNode ancestor : ancestors) {

				if (inOutNodesToRemove.contains(ancestor)) {

					// node is deleted because ancestor will be remove with the whole sub-tree which includes current node 
					iterator.remove(); 
					break;
				}
			}
		}
	}

	public Set<ConstraintNode> getAffectedConstraints() {
		return fGenericRemoveNodesProcessorOfNodes.getAffectedConstraints();
	}

	public Set<TestCaseNode> getAffectedTestCases() {
		return fGenericRemoveNodesProcessorOfNodes.getAffectedTestCases();
	}

}
