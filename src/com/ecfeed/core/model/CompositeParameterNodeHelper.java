/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model;

import java.util.ArrayList;
import java.util.List;

public class CompositeParameterNodeHelper  {
	
	public static List<BasicParameterNode> getAllChildBasicParameters(CompositeParameterNode compositeParameterNode) {
		
		List<BasicParameterNode> result = new ArrayList<>();
		
		getAllChildBasicParametersRecursive(compositeParameterNode, result);
		
		return result;
	}
	
	private static void getAllChildBasicParametersRecursive(
			CompositeParameterNode compositeParameterNode, 
			List<BasicParameterNode> inOutBasicParameterNodes) {
		
		List<IAbstractNode> children = compositeParameterNode.getChildren();
		
		for (IAbstractNode abstractNode : children) {
			
			if (abstractNode instanceof BasicParameterNode) {
				inOutBasicParameterNodes.add((BasicParameterNode) abstractNode);
			}
			
			if (abstractNode instanceof CompositeParameterNode) {
				getAllChildBasicParametersRecursive((CompositeParameterNode) abstractNode, inOutBasicParameterNodes);
			}
		}
	}

}
