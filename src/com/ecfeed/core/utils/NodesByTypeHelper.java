/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.utils;

import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.IAbstractNode;

public class NodesByTypeHelper {

		public static NodesByType calculateNodesWithAllChildren(NodesByType initialNodes) {
			
			NodesByType children = new NodesByType();
			
			accumulateNodes(initialNodes.getClasses(), children);
			accumulateNodes(initialNodes.getMethods(), children);
			accumulateNodes(initialNodes.getBasicParameters(), children);
			accumulateNodes(initialNodes.getCompositeParameters(), children);
			accumulateNodes(initialNodes.getChoices(), children);
			accumulateNodes(initialNodes.getConstraints(), children);
			accumulateNodes(initialNodes.getTestSuites(), children);
			accumulateNodes(initialNodes.getTestCases(), children);
			
			NodesByType result = createSum(initialNodes, children);
			
			return result;
		}

		private static <T> void accumulateNodes(Set<T> nodes, NodesByType children) {
			
			for (T node : nodes) {
				accumulateNodes((IAbstractNode) node, children);
			}
		}
		
		private static void accumulateNodes(IAbstractNode parentNode, NodesByType inOutChildren) {
			
			inOutChildren.addNode(parentNode);
			
			List<IAbstractNode> children = parentNode.getDirectChildren();
			
			for (IAbstractNode child : children) {
				accumulateNodes(child, inOutChildren);
			}
		}

		public static NodesByType createSum(NodesByType nodes1, NodesByType nodes2) {
			
			NodesByType result = new NodesByType(nodes1);
			
			result.addTestCases(nodes2.getTestCases());
			result.addMethods(nodes2.getMethods());
			result.addBasicParameters(nodes2.getBasicParameters());
			result.addCompositeParameters(nodes2.getCompositeParameters());
			result.addChoices(nodes2.getChoices());
			result.addConstraints(nodes2.getConstraints());
			result.addTestSuites(nodes2.getTestSuites());
			result.addTestCases(nodes2.getTestCases());
			
			return result;
		}
		
}
