/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.model.utils;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.CompositeParameterNode;

public class BasicParameterDescription {

	private String fQualifiedName;
	private BasicParameterNode fBasicParameterNode;
	private CompositeParameterNode fLinkingCompositeParameterNode;

	public BasicParameterDescription(
			String qualifiedName,
			BasicParameterNode basicParameterNode, 
			CompositeParameterNode linkingCompositeParameterNode) {

		fQualifiedName = qualifiedName;
		fBasicParameterNode = basicParameterNode;
		fLinkingCompositeParameterNode = linkingCompositeParameterNode;
	}

	public String getQualifiedName() {
		return fQualifiedName;
	}

	public BasicParameterNode getBasicParameterNode() {
		return fBasicParameterNode;
	}

	public CompositeParameterNode getLinkingParameterNode() {
		return fLinkingCompositeParameterNode;
	}

}
