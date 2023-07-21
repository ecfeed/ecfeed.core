/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.serialization;

import java.util.HashMap;
import java.util.Map;

import com.ecfeed.core.model.AbstractNode;

public class ElementToNodeMapper {
	
	private final Map<nu.xom.Element, AbstractNode> fElementsToNodes = new HashMap<>();
	
	public void addMappings(nu.xom.Element xomElement, AbstractNode abstractNode) {
		
		fElementsToNodes.put(xomElement, abstractNode);
	}
	
	public AbstractNode getNode(nu.xom.Element xomElement) {
		
		return fElementsToNodes.get(xomElement);
	}

}
