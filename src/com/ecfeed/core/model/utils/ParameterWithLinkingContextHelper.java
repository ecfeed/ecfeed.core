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

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.AbstractParameterNodeHelper;
import com.ecfeed.core.model.BasicParameterNode;

public class ParameterWithLinkingContextHelper {

	public static BasicParameterNode findChoicesParentParameter(ParameterWithLinkingContext parameterWithLinkingContext) {

		BasicParameterNode basicParameterNode = (BasicParameterNode) parameterWithLinkingContext.getParameter();

		AbstractParameterNode link = basicParameterNode.getLinkToGlobalParameter();

		if (link == null) {
			return basicParameterNode;
		}

		return (BasicParameterNode) link;
	}

	public static void compareParametersWithLinkingContexts(
			ParameterWithLinkingContext parameterWithContext1, 
			ParameterWithLinkingContext parameterWithContext2) {

		AbstractParameterNodeHelper.compareParameters(
				parameterWithContext1.getParameter(), parameterWithContext2.getParameter());
		
		AbstractParameterNodeHelper.compareParameters(
				parameterWithContext1.getLinkingContext(), parameterWithContext2.getLinkingContext());
	}
	
}
