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
	private CompositeParameterNode fLinkingContext;

	public BasicParameterDescription(
			String qualifiedName,
			BasicParameterNode basicParameterNode, 
			CompositeParameterNode linkingContext) {

		fQualifiedName = qualifiedName;
		fBasicParameterNode = basicParameterNode;
		fLinkingContext = linkingContext;
	}

	public String getQualifiedName() {
		return fQualifiedName;
	}

	public BasicParameterNode getBasicParameterNode() {
		return fBasicParameterNode;
	}

	public CompositeParameterNode getLinkingParameterNode() {
		return fLinkingContext;
	}

}
