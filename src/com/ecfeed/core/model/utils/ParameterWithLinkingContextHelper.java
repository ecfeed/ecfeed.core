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
import com.ecfeed.core.utils.SignatureHelper;

public class ParameterWithLinkingContextHelper {

	public static String createSignature(ParameterWithLinkingContext parameterWithLinkingContext) {
		
		AbstractParameterNode context = parameterWithLinkingContext.getLinkingContext();
		AbstractParameterNode parameter = parameterWithLinkingContext.getParameter();		
		
		return SignatureHelper.createSignatureOfParameterWithContext(parameter, context);
	}

}
